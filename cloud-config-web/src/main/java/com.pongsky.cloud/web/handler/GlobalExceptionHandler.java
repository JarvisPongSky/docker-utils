package com.pongsky.cloud.web.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pongsky.cloud.exception.DeleteException;
import com.pongsky.cloud.exception.DoesNotExistException;
import com.pongsky.cloud.exception.ExistException;
import com.pongsky.cloud.exception.FrequencyException;
import com.pongsky.cloud.exception.HttpException;
import com.pongsky.cloud.exception.InsertException;
import com.pongsky.cloud.exception.RemoteCallException;
import com.pongsky.cloud.exception.UpdateException;
import com.pongsky.cloud.exception.ValidationException;
import com.pongsky.cloud.model.annotation.Meaning;
import com.pongsky.cloud.response.GlobalResult;
import com.pongsky.cloud.response.enums.ResultCode;
import com.pongsky.cloud.utils.ip.IpUtils;
import com.pongsky.cloud.utils.jwt.dto.AuthInfo;
import com.pongsky.cloud.web.request.AuthUtils;
import com.pongsky.cloud.web.request.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

/**
 * ??????????????????
 *
 * @author pengsenhao
 * @create 2021-02-11
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper jsonMapper;

    /**
     * ?????????????????????????????????
     */
    private static final int BOUNDARY = 500;

    /**
     * ?????? param ????????????
     *
     * @param ex      ex
     * @param headers headers
     * @param status  status
     * @param request request
     * @return ?????? param ????????????
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)
                (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object result = getResult(ResultCode.BindException, getFieldMessages(ex.getBindingResult()),
                ex, httpServletRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * ?????? param ????????????
     *
     * @param ex      ex
     * @param headers headers
     * @param status  status
     * @param request request
     * @return ?????? param ????????????
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)
                (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object result = getResult(ResultCode.BindException, ex.getMessage(), ex, httpServletRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * ?????? body ????????????
     *
     * @param ex      ex
     * @param headers headers
     * @param status  status
     * @param request request
     * @return ?????? body ????????????
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)
                (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object result = getResult(ResultCode.MethodArgumentNotValidException,
                getFieldMessages(ex.getBindingResult()), ex, httpServletRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * ????????????????????????
     *
     * @param bindingResult bindingResult
     * @return ??????????????????
     */
    private String getFieldMessages(BindingResult bindingResult) {
        String escapeInterval = "\\.";
        String interval = ".";
        String listStart = "java.util.List<";
        StringBuilder stringBuilder = new StringBuilder("[ ");
        if (bindingResult.getTarget() == null) {
            bindingResult.getFieldErrors().forEach(error -> appendErrorMessage(stringBuilder,
                    error.getField(), error.getDefaultMessage()));
        } else {
            bindingResult.getFieldErrors().forEach(error -> {
                String filedName = error.getField();
                Field field = Arrays.stream(bindingResult.getTarget().getClass().getDeclaredFields())
                        .filter(f -> f.getName().equals(error.getField()))
                        .findFirst()
                        .orElse(null);
                if (field == null) {
                    appendErrorMessage(stringBuilder, filedName, error.getDefaultMessage());
                    return;
                }
                Meaning meaning = field.getAnnotation(Meaning.class);
                if (meaning != null) {
                    filedName = meaning.value();
                }
                if (!(filedName.split(escapeInterval).length > 1 && meaning != null)) {
                    appendErrorMessage(stringBuilder, filedName, error.getDefaultMessage());
                    return;
                }
                int i = filedName.lastIndexOf(interval, (filedName.lastIndexOf(interval) - 1)) + 1;
                String[] split = filedName.substring(i).split(escapeInterval);
                filedName = split[0].substring(0, filedName.lastIndexOf("["));
                String typeName = field.getGenericType().getTypeName();
                if (!(typeName.startsWith(listStart))) {
                    appendErrorMessage(stringBuilder, filedName, error.getDefaultMessage());
                    return;
                }
                typeName = typeName.substring(listStart.length(), typeName.lastIndexOf(">"));
                try {
                    Optional<Meaning> optionalMeaning = Arrays.stream(Class.forName(typeName).getDeclaredFields())
                            .filter(f -> f.getName().equals(split[1]))
                            .map(f -> f.getAnnotation(Meaning.class))
                            .findFirst();
                    if (optionalMeaning.isPresent()) {
                        filedName += optionalMeaning.get().value();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                appendErrorMessage(stringBuilder, filedName, error.getDefaultMessage());
            });
        }
        return stringBuilder.append("]").toString();
    }

    /**
     * ????????????????????????
     *
     * @param stringBuilder ??????????????????
     * @param filedName     ????????????
     * @param message       ??????????????????
     */
    private void appendErrorMessage(StringBuilder stringBuilder, String filedName, String message) {
        stringBuilder
                .append(filedName)
                .append(" ")
                .append(message)
                .append("; ");
    }

    /**
     * JSON ??????????????????
     *
     * @param ex      ex
     * @param headers headers
     * @param status  status
     * @param request request
     * @return JSON ??????????????????
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)
                (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object result = getResult(ResultCode.HttpMessageNotReadableException, null, ex, httpServletRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)
                (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object result = getResult(ResultCode.NoHandlerFoundException,
                httpServletRequest.getRequestURI() + " ???????????????", ex, httpServletRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatus status,
                                                                         WebRequest request) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)
                (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object result = getResult(ResultCode.HttpRequestMethodNotSupportedException,
                httpServletRequest.getMethod() + " ???????????????", ex, httpServletRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * ???????????????
     *
     * @param exception exception
     * @param request   request
     * @return ???????????????
     */
    @ExceptionHandler(value = DoesNotExistException.class)
    public Object doesNotExistException(DoesNotExistException exception, HttpServletRequest request) {
        return getResult(ResultCode.DoesNotExistException, exception.getLocalizedMessage(), exception, request);
    }

    /**
     * ????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????
     */
    @ExceptionHandler(value = {ValidationException.class, ConstraintViolationException.class})
    public Object validationException(Exception exception, HttpServletRequest request) {
        return getResult(ResultCode.ValidationException, exception.getLocalizedMessage(), exception, request);
    }

    /**
     * HTTP ????????????
     *
     * @param exception exception
     * @param request   request
     * @return HTTP ????????????
     */
    @ExceptionHandler(value = HttpException.class)
    public Object httpException(HttpException exception, HttpServletRequest request) {
        return getResult(ResultCode.HttpException, exception.getLocalizedMessage(), exception, request);
    }

    /**
     * ??????????????????
     *
     * @param exception exception
     * @param request   request
     * @return ??????????????????
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public Object accessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        if (AuthUtils.getUser(request).equals(AuthInfo.PUBLIC_INFO)) {
            // ?????????????????????
            return getResult(ResultCode.TokenExpiredException, "???????????????????????????????????????", exception, request);
        }
        return getResult(ResultCode.AccessDeniedException, null, exception, request);
    }

    /**
     * ?????????????????????
     *
     * @param exception exception
     * @param request   request
     * @return ?????????????????????
     */
    @ExceptionHandler(value = MultipartException.class)
    public Object multipartException(MultipartException exception, HttpServletRequest request) {
        return getResult(ResultCode.MultipartException, null, exception, request);
    }

    /**
     * ????????????????????????
     */
    @Value(value = "${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    /**
     * ????????????????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????????????????
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public Object maxUploadSizeExceededException(MaxUploadSizeExceededException exception, HttpServletRequest request) {
        return getResult(ResultCode.MaxUploadSizeExceededException,
                "???????????? " + maxFileSize + " ???????????????????????????????????????", exception, request);
    }

    /**
     * ??????????????????
     *
     * @param exception exception
     * @param request   request
     * @return ??????????????????
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Object illegalArgumentException(IllegalArgumentException exception, HttpServletRequest request) {
        return getResult(ResultCode.IllegalArgumentException, null, exception, request);
    }

    /**
     * ????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????
     */
    @ExceptionHandler(value = ExistException.class)
    public Object existException(ExistException exception, HttpServletRequest request) {
        return getResult(ResultCode.ExistException, null, exception, request);
    }

    /**
     * ????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????
     */
    @ExceptionHandler(value = FrequencyException.class)
    public Object frequencyException(FrequencyException exception, HttpServletRequest request) {
        return getResult(ResultCode.FrequencyException, null, exception, request);
    }

    /**
     * ????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????
     */
    @ExceptionHandler(value = InsertException.class)
    public Object insertException(InsertException exception, HttpServletRequest request) {
        return getResult(ResultCode.InsertException, null, exception, request);
    }

    /**
     * ????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????
     */
    @ExceptionHandler(value = UpdateException.class)
    public Object updateException(UpdateException exception, HttpServletRequest request) {
        return getResult(ResultCode.UpdateException, null, exception, request);
    }

    /**
     * ????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????
     */
    @ExceptionHandler(value = DeleteException.class)
    public Object deleteException(DeleteException exception, HttpServletRequest request) {
        return getResult(ResultCode.DeleteException, null, exception, request);
    }

    /**
     * ??????????????????
     *
     * @param exception exception
     * @return ??????????????????
     */
    @ExceptionHandler(value = RemoteCallException.class)
    public Object remoteCallException(RemoteCallException exception) {
        return exception.getResult();
    }

    /**
     * ????????????
     *
     * @param exception exception
     * @param request   request
     * @return ????????????
     */
    @ExceptionHandler(value = Exception.class)
    public Object exception(Exception exception, HttpServletRequest request) {
        return getResult(ResultCode.Exception, null, exception, request);
    }

    /**
     * ??????????????????????????????
     *
     * @param resultCode resultCode
     * @param message    message
     * @param exception  exception
     * @param request    request
     * @return ??????????????????????????????
     */
    private Object getResult(ResultCode resultCode, String message, Exception exception, HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        // ????????? getAttribute ???????????????????????? body ?????????????????????????????????????????????

        GlobalResult<Void> result = new GlobalResult<>(ip, resultCode, request.getRequestURI(), exception.getClass().getName());
        exception = getException(exception, 0);
        if (message != null) {
            result.setMessage(message);
        } else if (result.getMessage() == null) {
            if (exception.getLocalizedMessage() != null) {
                result.setMessage(exception.getLocalizedMessage());
            } else if (exception.getMessage() != null) {
                result.setMessage(exception.getMessage());
            }
        }
        log(exception, request, result);
        return result;
    }

    /**
     * ????????????????????????????????????
     */
    private static final int THROWABLE_COUNT = 10;

    /**
     * ????????????????????????
     *
     * @param exception ??????
     * @param number    ??????
     * @return ????????????????????????
     */
    private Exception getException(Exception exception, int number) {
        if (number > THROWABLE_COUNT) {
            return exception;
        }
        if (exception.getCause() != null) {
            return getException(exception, ++number);
        }
        return exception;
    }

    /**
     * ????????????????????????
     *
     * @param exception ??????
     * @param request   request
     * @param result    ??????????????????
     */
    private void log(Exception exception, HttpServletRequest request, GlobalResult<Void> result) {
        log.error("");
        log.error("Started exception");
        if (result.getCode() >= BOUNDARY) {
            log.error("request: methodURL [{}] methodType [{}] params [{}] body [{}]",
                    request.getRequestURI(),
                    request.getMethod(),
                    Optional.ofNullable(request.getQueryString()).orElse(""),
                    Optional.ofNullable(RequestUtils.getBody(request)).orElse(""));
            log.error("exception message: [{}]", result.getMessage());
            Arrays.asList(exception.getStackTrace()).forEach(stackTrace -> log.error(stackTrace.toString()));
        } else {
            log.error("exception message: [{}]", result.getMessage());
        }
        try {
            log.error("response: [{}]", jsonMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.error(e.getLocalizedMessage());
        }
        log.error("Ended exception");
    }

}

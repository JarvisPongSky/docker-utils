package com.pongsky.cloud.exception;

/**
 * 删除异常
 *
 * @author pengsenhao
 * @create 2021-02-12
 */
public class DeleteException extends RuntimeException {

    public DeleteException(String message) {
        super(message);
    }

    /**
     * 删除保存 SQL
     *
     * @param exceptionMessage 异常信息
     * @param deleteCount      删除总数
     */
    public static void validation(String exceptionMessage, Integer deleteCount) {
        validation(exceptionMessage, deleteCount, 1);
    }

    /**
     * 删除保存 SQL
     *
     * @param exceptionMessage 异常信息
     * @param deleteCount      删除总数
     * @param dataCount        数据总数
     */
    public static void validation(String exceptionMessage, Integer deleteCount, Integer dataCount) {
        if (!deleteCount.equals(dataCount)) {
            throw new DeleteException(exceptionMessage);
        }
    }

}

package com.pongsky.cloud;

import com.pongsky.cloud.config.DatabaseConfig;
import com.pongsky.cloud.config.JacksonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author pengsenhao
 * @create 2021-02-11
 */
@SpringBootApplication
@Import({
        JacksonConfig.class,
        DatabaseConfig.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

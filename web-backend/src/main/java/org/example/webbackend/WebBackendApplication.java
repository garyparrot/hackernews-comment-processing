package org.example.webbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebBackendApplication {

    public static void main(String[] args) {
        // TODO: There is an issue related to devtools hot reload. It will cause KafkaConsumer::consume
        //     : throwing an ClassCastException. this exception indicate that the casting between
        //     : org.example.domain.KeywordMatch and itself is not possible.
        //     : But giving closer investigate will reveal that both class are coming from two different
        //     : ClassLoader. This is because of devtool hot reload create a new classloader for runtime
        //     : replacement [0].
        //     :
        //     : Finding a solution like putting this class to 3rd dependency might be a nasty solution.
        //     : After all it's better than the hack you see below.
        //     :
        //     : [0]: https://docs.spring.io/spring-boot/docs/1.5.16.RELEASE/reference/html/using-boot-devtools.html#using-boot-devtools-restart
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(WebBackendApplication.class, args);
    }

}

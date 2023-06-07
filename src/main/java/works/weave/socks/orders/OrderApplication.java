package works.weave.socks.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}

// This is the main method, which is the entry point of the application. The SpringApplication.run() method will start the Spring application. We pass in the OrderApplication class, which is annotated with @SpringBootApplication and thus will start all the components under the package.

// The @SpringBootApplication annotation is equivalent to using @Configuration, @EnableAutoConfiguration and @ComponentScan with their default attributes:

// @Configuration tags the class as a source of bean definitions for the application context.
// @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
// @ComponentScan tells Spring to look for other components, configurations, and services in the the order package, allowing it to find the controllers.

// @EnableAsync enables Spring's ability to run @Async methods in a background thread pool. This is needed for the @Async annotation in the OrderService class to work.

// @EnableAsync annotation is used to enable Spring’s ability to run @Async methods in a background thread pool. 
// This is needed for the @Async annotation in the OrderService class to work.

// The @SpringBootApplication annotation is equivalent to using @Configuration, @EnableAutoConfiguration and @ComponentScan with their default attributes:

// @Configuration tags the class as a source of bean definitions for the application context.
// @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
// @ComponentScan tells Spring to look for other components, configurations, and services in the the order package, allowing it to find the controllers.

// @EnableAsync enables Spring's ability to run @Async methods in a background thread pool. 
// This is needed for the @Async annotation in the OrderService class to work.

// The @SpringBootApplication annotation is equivalent to using @Configuration, @EnableAutoConfiguration and @ComponentScan with their default attributes:

// @Configuration tags the class as a source of bean definitions for the application context.
// @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
// @ComponentScan tells Spring to look for other components, configurations, and services in the the order package, allowing it to find the controllers.

// @EnableAsync enables Spring's ability to run @Async methods in a background thread pool. 
// This is needed for the @Async annotation in the OrderService class to work.

// The @SpringBootApplication annotation is equivalent to using @Configuration, @EnableAutoConfiguration and @ComponentScan with their default attributes:

// @Configuration tags the class as a source of bean definitions for the application context.
// @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
// @ComponentScan tells Spring to look for other components, configurations, and services in the the order package, allowing it to find the controllers. 
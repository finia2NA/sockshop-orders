// The `MappedInterceptor` in Spring MVC is a mechanism for intercepting and processing requests and responses in a Spring Boot application. It allows you to define custom interceptors that can be applied to specific URL patterns.

// When you configure a `MappedInterceptor`, you specify the URL patterns to which the interceptor should be applied. Whenever a request matches one of these patterns, the interceptor's methods will be invoked.

// In the provided code, the `WebMvcConfig` class defines a `MappedInterceptor` bean called `myMappedInterceptor`. This interceptor is associated with the `HTTPMonitoringInterceptor`, which is another bean defined in the configuration.

// The purpose of this configuration is to apply the `HTTPMonitoringInterceptor` to all requests (`"/**"`). The `HTTPMonitoringInterceptor` can be used to perform monitoring, logging, or any other custom logic related to HTTP requests and responses.

// By intercepting requests and responses, you can add additional functionality such as logging, metrics collection, modifying the request or response, or performing authentication/authorization checks. The interceptor can be used to enhance the application's behavior and add cross-cutting concerns without modifying the individual controllers or endpoints.

package works.weave.socks.orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.MappedInterceptor;
import works.weave.socks.orders.middleware.HTTPMonitoringInterceptor;

@Configuration
public class WebMvcConfig {
    @Bean
    HTTPMonitoringInterceptor httpMonitoringInterceptor() {
        return new HTTPMonitoringInterceptor();
    }

    @Bean
    public MappedInterceptor myMappedInterceptor(HTTPMonitoringInterceptor interceptor) {
        return new MappedInterceptor(new String[]{"/**"}, interceptor);
    }
}

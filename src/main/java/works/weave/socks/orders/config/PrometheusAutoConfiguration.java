// This file is a Java configuration file that sets up Prometheus monitoring for a Spring Boot application. It defines two beans: a SpringBootMetricsCollector bean and a ServletRegistrationBean bean. The SpringBootMetricsCollector bean collects metrics from the application and registers them with Prometheus, while the ServletRegistrationBean bean exposes the metrics via a servlet. The file also includes some conditional annotations to ensure that the beans are only created if certain conditions are met.

// Prometheus is an open-source monitoring and alerting system that collects metrics from various sources, stores them, and provides a query language and visualization tools to analyze and alert on the data. It is commonly used for monitoring cloud-native applications and infrastructure.

// Generated using GPT4:
// In the provided code, there is a focus on application metrics collection and monitoring, which is one crucial aspect of resilience, as it helps in diagnosing issues and taking appropriate action when problems occur. However, there are still some improvements that could be made:

// 1. **Fail-safe mechanism for Bean Initialization**: If there is a failure during the creation of the `springBootMetricsCollector` bean or the `prometheusMetricsServletRegistrationBean`, the application might fail to start, impacting resilience. You could consider introducing a fallback mechanism in case of initialization errors.

//     Here is an example of how to do it for `springBootMetricsCollector`:

//     ```java
//     @Bean
//     @ConditionalOnMissingBean(SpringBootMetricsCollector.class)
//     SpringBootMetricsCollector springBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
//         try {
//             SpringBootMetricsCollector springBootMetricsCollector = new SpringBootMetricsCollector(publicMetrics);
//             springBootMetricsCollector.register();
//             return springBootMetricsCollector;
//         } catch(Exception e) {
//             // Log the exception and return a fallback
//             return null; // Returning null or a fallback dummy collector
//         }
//     }
//     ```

// 2. **Configuration properties validation**: You are directly using the value from the configuration `${prometheus.metrics.path:/metrics}` without validating it. It would be safer to validate these values before they are used, preventing potential errors and improving resilience.

//     Here is an example:

//     ```java
//     @Bean
//     @ConditionalOnMissingBean(name = "prometheusMetricsServletRegistrationBean")
//     ServletRegistrationBean prometheusMetricsServletRegistrationBean(@Value("${prometheus.metrics.path:/metrics}") String metricsPath) {
//         // Check if metricsPath is a valid path
//         if (!isValidPath(metricsPath)) {
//             throw new IllegalArgumentException("Invalid metrics path: " + metricsPath);
//         }
//         DefaultExports.initialize();
//         return new ServletRegistrationBean(new MetricsServlet(), metricsPath);
//     }
    
//     private boolean isValidPath(String path) {
//         // Implement your validation logic here
//     }
//     ```
   
// By focusing on these two aspects: fail-safe mechanism for Bean Initialization and validation of configuration properties, you can improve the resilience of this specific part of your application.


package works.weave.socks.orders.config;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.spring.boot.SpringBootMetricsCollector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@ConditionalOnClass(SpringBootMetricsCollector.class)
class PrometheusAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(SpringBootMetricsCollector.class)
    SpringBootMetricsCollector springBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
        SpringBootMetricsCollector springBootMetricsCollector = new SpringBootMetricsCollector
                (publicMetrics);
        springBootMetricsCollector.register();
        return springBootMetricsCollector;
    }

    @Bean
    @ConditionalOnMissingBean(name = "prometheusMetricsServletRegistrationBean")
    ServletRegistrationBean prometheusMetricsServletRegistrationBean(@Value("${prometheus.metrics" +
            ".path:/metrics}") String metricsPath) {
        DefaultExports.initialize();
        return new ServletRegistrationBean(new MetricsServlet(), metricsPath);
    }
}

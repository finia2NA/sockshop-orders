/*
 * Generated by GPT4:
 Looking at your code, here are a few ways to increase resilience:

1. Exception handling in `postHandle` method: The code within the `postHandle` method doesn't have any error handling mechanism. If an error occurs, such as a `NullPointerException` when calling `getAttribute(startTimeKey)`, it would cause the application to crash. To avoid this, you could add a try-catch block and handle the exceptions accordingly.

   ```java
   @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
        ModelAndView modelAndView) throws Exception {
        try {
            long start = (long) httpServletRequest.getAttribute(startTimeKey);
            long elapsed = System.nanoTime() - start;
            double seconds = (double) elapsed / 1000000000.0;
            String matchedUrl = getMatchingURLPattern(httpServletRequest);
            if (!matchedUrl.equals("")) {
                requestLatency.labels(
                    serviceName,
                    httpServletRequest.getMethod(),
                    matchedUrl,
                    Integer.toString(httpServletResponse.getStatus())).observe(seconds);
            }
        } catch (Exception e) {
            // Handle or log the exception here
        }
    }
   ```

2. Exception handling in `getMatchingURLPattern` method: If there are no matching conditions or the iterator has no next element, the code might throw an exception. Consider adding exception handling here too.

   ```java
   private String getMatchingURLPattern(HttpServletRequest httpServletRequest) {
        String res = "";
        try {
            for (PatternsRequestCondition pattern : getUrlPatterns()) {
                if (pattern.getMatchingCondition(httpServletRequest) != null &&
                    !httpServletRequest.getServletPath().equals("/error")) {
                    res = pattern.getMatchingCondition(httpServletRequest).getPatterns().iterator().next();
                    break;
                }
            }
        } catch (Exception e) {
            // Handle or log the exception here
        }
        return res;
    }
   ```

3. Handle Exceptions in `afterCompletion`: Right now the `afterCompletion` method is empty, but it's designed to contain logic that needs to execute after request completion, regardless of its outcome. For instance, this is a great place to handle and log any exceptions that occurred during the execution of the request handler. This can also improve the resilience of your application.

   ```java
   @Override
   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
       if (ex != null) {
           // log or handle exception
       }
   }
   ```

4. Thread safety for `urlPatterns`: The `urlPatterns` variable is not thread-safe. Multiple threads could execute the `getUrlPatterns()` method simultaneously and potentially cause race conditions. Consider synchronizing this part of the code to avoid potential issues.

   ```java
   private synchronized Set<PatternsRequestCondition> getUrlPatterns() {
       // existing code
   }
   ```
   
Remember, the error handling code should be meaningful and not simply swallow the exceptions. Good error handling includes logging the errors or taking specific actions when an error occurs.
 */
package works.weave.socks.orders.middleware;

import io.prometheus.client.Histogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.data.rest.webmvc.support.JpaHelper;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

public class HTTPMonitoringInterceptor implements HandlerInterceptor {
    static final Histogram requestLatency = Histogram.build()
            .name("http_request_duration_seconds")
            .help("Request duration in seconds.")
            .labelNames("service", "method", "path", "status_code")
            .register();

    private static final String startTimeKey = "startTime";
    @Autowired
    ResourceMappings mappings;
    @Autowired
    JpaHelper jpaHelper;
    @Autowired
    RepositoryRestConfiguration repositoryConfiguration;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;
    private Set<PatternsRequestCondition> urlPatterns;
    @Value("${spring.application.name:orders}")
    private String serviceName;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
            throws Exception {
        httpServletRequest.setAttribute(startTimeKey, System.nanoTime());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
            ModelAndView modelAndView) throws Exception {
        long start = (long) httpServletRequest.getAttribute(startTimeKey);
        long elapsed = System.nanoTime() - start;
        double seconds = (double) elapsed / 1000000000.0;
        String matchedUrl = getMatchingURLPattern(httpServletRequest);
        if (!matchedUrl.equals("")) {
            requestLatency.labels(
                    serviceName,
                    httpServletRequest.getMethod(),
                    matchedUrl,
                    Integer.toString(httpServletResponse.getStatus())).observe(seconds);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object o, Exception e) throws Exception {
    }

    private String getMatchingURLPattern(HttpServletRequest httpServletRequest) {
        String res = "";
        for (PatternsRequestCondition pattern : getUrlPatterns()) {
            if (pattern.getMatchingCondition(httpServletRequest) != null &&
                    !httpServletRequest.getServletPath().equals("/error")) {
                res = pattern.getMatchingCondition(httpServletRequest).getPatterns().iterator().next();
                break;
            }
        }
        return res;
    }

    private Set<PatternsRequestCondition> getUrlPatterns() {
        if (this.urlPatterns == null) {
            this.urlPatterns = new HashSet<>();
            requestMappingHandlerMapping.getHandlerMethods()
                    .forEach((mapping, handlerMethod) -> urlPatterns.add(mapping.getPatternsCondition()));
            RepositoryRestHandlerMapping repositoryRestHandlerMapping = new RepositoryRestHandlerMapping(mappings,
                    repositoryConfiguration);
            repositoryRestHandlerMapping.setJpaHelper(jpaHelper);
            repositoryRestHandlerMapping.setApplicationContext(applicationContext);
            repositoryRestHandlerMapping.afterPropertiesSet();
            repositoryRestHandlerMapping.getHandlerMethods()
                    .forEach((mapping, handlerMethod) -> urlPatterns.add(mapping.getPatternsCondition()));
        }
        return this.urlPatterns;
    }
}

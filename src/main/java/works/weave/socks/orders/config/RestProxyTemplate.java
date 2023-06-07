// To increase the resilience of the Spring Boot app and better handle application faults in the given code, here are a few improvements you can make:

// 1. Handle NumberFormatException: Currently, if the proxy port number cannot be parsed, an error is logged but no further action is taken. You can enhance the error handling by throwing an exception or taking appropriate action based on your application's requirements.

// ```java
// @PostConstruct
// public void init() {
//     if (host.isEmpty() || port.isEmpty()) {
//         return;
//     }
//     int portNr = -1;
//     try {
//         portNr = Integer.parseInt(port);
//     } catch (NumberFormatException e) {
//         throw new IllegalArgumentException("Invalid proxy port number", e);
//     }
//     // Rest of the code...
// }
// ```

// 2. Configure a connection timeout: By default, `SimpleClientHttpRequestFactory` does not have a connection timeout. You can set a reasonable timeout value to avoid long-running requests and handle cases where the remote server is not responding.

// ```java
// @PostConstruct
// public void init() {
//     // Existing code...

//     // Set connection timeout to 5 seconds (adjust the value as per your requirements)
//     factory.setConnectTimeout(5000);

//     // Rest of the code...
// }
// ```

// 3. Configure a read timeout: Similarly, you can also set a read timeout to handle situations where the remote server takes too long to respond or the response is delayed.

// ```java
// @PostConstruct
// public void init() {
//     // Existing code...

//     // Set read timeout to 10 seconds (adjust the value as per your requirements)
//     factory.setReadTimeout(10000);

//     // Rest of the code...
// }
// ```

// 4. Implement retry logic: You can consider adding retry logic to handle transient errors or temporary network issues. For example, you can use libraries like Spring Retry to automatically retry failed requests with configurable retry policies.

// ```java
// @PostConstruct
// public void init() {
//     // Existing code...

//     // Configure retry template with a fixed delay and maximum number of retries
//     RetryTemplate retryTemplate = new RetryTemplate();
//     FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
//     backOffPolicy.setBackOffPeriod(2000); // 2 seconds delay between retries
//     retryTemplate.setBackOffPolicy(backOffPolicy);
//     retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3)); // Retry 3 times
//     restTemplate.setRetryTemplate(retryTemplate);

//     // Rest of the code...
// }
// ```

// By incorporating these improvements, you can enhance the resilience of your Spring Boot app when dealing with application faults.

package works.weave.socks.orders.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
public final class RestProxyTemplate {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
      return new RestTemplate();
    }

    @Value("${proxy.host:}")
    private String host;

    @Value("${proxy.port:}")
    private String port;

    @PostConstruct
    public void init() {
        if (host.isEmpty() || port.isEmpty()) {
            return;
        }
        int portNr = -1;
        try {
            portNr = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            logger.error("Unable to parse the proxy port number");
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        InetSocketAddress address = new InetSocketAddress(host, portNr);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        factory.setProxy(proxy);

        restTemplate.setRequestFactory(factory);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}

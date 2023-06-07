/*
 * OOOH boy this seems like on that needs attention
 * GPT4 (aka my friend) says:
 * Based on the provided code, here are a few suggestions to increase resilience:

1. Exception Handling: Currently, the methods are throwing `InterruptedException` and `IOException`. While these exceptions are appropriate, they might not be comprehensive enough to cover all potential problems you might face during the execution. You could consider catching generic `Exception` or `RestClientException` in your methods and then either rethrowing it with additional context or logging it. This would prevent your application from crashing in case of unexpected issues.

2. Null Check: The code currently doesn't perform any null checks on the responses (`body`, `responseBody`). This could potentially cause `NullPointerExceptions` if the response is null. You should add null checks for the responses.

3. Logging: You are using debug level for logging, which might not output log messages depending on your log configuration. Consider using the appropriate log levels based on the importance of the log. For instance, errors could be logged at the error level.

4. Retry Mechanism: You are not implementing any retry mechanism in your code. If any of the HTTP calls fail due to transient errors like network issues, the entire operation will fail. Implementing a retry mechanism could increase the resilience of your application by making it more tolerant to temporary failures.

5. Timeout Configuration: Currently, there is no configuration for request timeouts. By adding a configuration for request timeouts, you can avoid scenarios where your application hangs indefinitely waiting for a response. You can add this configuration in your `RestProxyTemplate`.

```java
SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
requestFactory.setConnectTimeout(3000); // Connection timeout
requestFactory.setReadTimeout(3000); // Read timeout
this.halTemplate.setRequestFactory(requestFactory);
```

6. Health Checks: Although it might not directly be related to this specific code, adding health check mechanisms for your dependencies (e.g., the services you are communicating with) can significantly improve the resilience of your application. Health checks can be added in separate components or services.

Note: The implementation of these suggestions will depend on your specific requirements and the context of your application. Therefore, please take these suggestions as starting points rather than definite solutions.
 */

package works.weave.socks.orders.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import works.weave.socks.orders.config.RestProxyTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;

@Service
public class AsyncGetService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final RestProxyTemplate restProxyTemplate;

    private final RestTemplate halTemplate;

    @Autowired
    public AsyncGetService(RestProxyTemplate restProxyTemplate) {
        this.restProxyTemplate = restProxyTemplate;
        this.halTemplate = new RestTemplate(restProxyTemplate.getRestTemplate().getRequestFactory());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new Jackson2HalModule());
        MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter();
        halConverter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
        halConverter.setObjectMapper(objectMapper);
        halTemplate.setMessageConverters(Collections.singletonList(halConverter));
    }

    @Async
    public <T> Future<Resource<T>> getResource(URI url, TypeReferences.ResourceType<T> type)
            throws InterruptedException, IOException {
        RequestEntity<Void> request = RequestEntity.get(url).accept(HAL_JSON).build();
        LOG.debug("Requesting: " + request.toString());
        Resource<T> body = restProxyTemplate.getRestTemplate().exchange(request, type).getBody();
        LOG.debug("Received: " + body.toString());
        return new AsyncResult<>(body);
    }

    @Async
    public <T> Future<Resources<T>> getDataList(URI url, TypeReferences.ResourcesType<T> type)
            throws InterruptedException, IOException {
        RequestEntity<Void> request = RequestEntity.get(url).accept(HAL_JSON).build();
        LOG.debug("Requesting: " + request.toString());
        Resources<T> body = restProxyTemplate.getRestTemplate().exchange(request, type).getBody();
        LOG.debug("Received: " + body.toString());
        return new AsyncResult<>(body);
    }

    @Async
    public <T> Future<List<T>> getDataList(URI url, ParameterizedTypeReference<List<T>> type)
            throws InterruptedException, IOException {
        RequestEntity<Void> request = RequestEntity.get(url).accept(MediaType.APPLICATION_JSON).build();
        LOG.debug("Requesting: " + request.toString());
        List<T> body = restProxyTemplate.getRestTemplate().exchange(request, type).getBody();
        LOG.debug("Received: " + body.toString());
        return new AsyncResult<>(body);
    }

    @Async
    public <T, B> Future<T> postResource(URI uri, B body, ParameterizedTypeReference<T> returnType) {
        RequestEntity<B> request = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(body);
        LOG.debug("Requesting: " + request.toString());
        T responseBody = restProxyTemplate.getRestTemplate().exchange(request, returnType).getBody();
        LOG.debug("Received: " + responseBody);
        return new AsyncResult<>(responseBody);
    }
}

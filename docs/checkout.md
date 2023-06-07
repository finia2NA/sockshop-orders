# Manual check
## Things to check out
- [controllers/HealthCheckController](../src/main/java/works/weave/socks/orders/controllers/HealthCheckController.java): see if the health check is ok
- [controllers/OrdersController](../src/main/java/works/weave/socks/orders/controllers/OrdersController.java): Fix the TODO
- [config/PrometheusAutoConfiguration.java](../src/main/java/works/weave/socks/orders/config/PrometheusAutoConfiguration.java)
  - Prometheus is an open-source monitoring and alerting system that collects metrics from various sources, stores them, and provides a query language and visualization tools to analyze and alert on the data. It is commonly used for monitoring cloud-native applications and infrastructure.
  - Thus, maybe the monitoring could be improved
- [HTTPMonitoringInterceptor](../src/main/java/works/weave/socks/orders/middleware/HTTPMonitoringInterceptor.java)
  - Look at the AI generated advice, exception handlers would be nice

## Things that are fine

## General things that can be improved
- entities:
  - Field Level Validation: Apply field-level validation annotations, such as @NotBlank or @Size, to enforce constraints on the address fields. This can be useful to ensure that required fields are not empty and that fields have appropriate lengths.
  - Handle database connectivity issues, constraint violations, or optimistic locking exceptions.
  - Input Validation: Implement input validation for the address fields to ensure that they meet the required criteria. Validate the data types, lengths, and formats of the address components (e.g., number, street, city, postcode) to prevent invalid or malformed data from being stored.

# AI generated advice

# Promt
I am trying to increase the reslience of a Spring boot app, better dealing with Application Faults. Please tell me if there is anything I could improve in this respect in the following code.

Only mention changes that can be done to the code I give you. Do not give advice that cannot be implemented in this specific code. Do not give advice that is implemented at a different place in the application. For example, do not advice me to use a circuit breaker pattern in response to a file where this can not be implemented. In this case, simply say that nothing can be done in this file to increase resilience. Saying that nothing can be done is the correct answer when confronted with a file where nothing can be done to increase resilience. Do not give general advice for parts of the application that you are not given. 

Do not say something along the lines of "However, here are a few suggestions that can be considered to improve the resilience of the application in general:”. That is a wrong response.

Here is the code:


# General Advice 
- Exception Handling: Implement appropriate exception handling mechanisms throughout your application. Handle exceptions gracefully by providing meaningful error messages and returning appropriate HTTP status codes.
- Circuit Breaker Pattern: Use a circuit breaker library, such as Netflix Hystrix or resilience4j, to manage the flow of requests and prevent cascading failures.
- Retry Mechanisms: Implement retry logic for critical operations that may fail temporarily due to external dependencies. You can use libraries like resilience4j or Spring Retry to handle retries.
- Logging and Monitoring: Implement comprehensive logging and monitoring solutions to capture and track errors, exceptions, and system metrics. This will help you identify and diagnose issues promptly.
- Graceful Shutdown: Implement a graceful shutdown mechanism to ensure that the application completes any ongoing tasks or transactions before shutting down.
- Load Testing and Scalability: Perform load testing to identify bottlenecks and ensure your application can handle increased load. Consider scaling your application horizontally or vertically to handle increased traffic.




# Specifics:
- Adress and credit card validation
- logging of errors
- null safety
- timeouts
- implement retrys and circuit breaker
- unit tests

## Caveats:
As GPT4 says
> However, the enhancements mentioned above have more to do with the cleanliness and safety of the code, rather than improving resilience at an application level. The resilience of an application usually involves how the application deals with failures in its interactions with other components like databases, external APIs, or other microservices. So, while these changes can provide a certain level of resilience at the code level, they do not contribute much to the overall resilience of the application in terms of handling application faults, and increasing fault tolerance or redundancy.
so, while a lot of changes are recommended, only a few of them are actually relevant to the resilience of the application.

## Working with AI
- I started using GPT3.5 to get info on how spring works. This helped a lot to understand the control flow and layout of a spring project.
- I started by checking large files individually. Then, I pasted each file into chatgpt.
  - GPT3.5 was not the best, since it often gave advice that was general and not related to the code it was given, even with the promt clearly telling it not to.
  - GPT4 was a lot better in this, and gave good, specific advice. However, in small files with not a lot to fix, the advice was often more taylored to clean code and readability than to resilience.
    - The most common advice was null checks. As the AI itself says:
    > Note: Even if this code does not directly deal with any resources or services that could fail (network, database, etc.), making the code more robust to invalid inputs and avoiding null pointer exceptions can still be considered as improving the application's resilience.
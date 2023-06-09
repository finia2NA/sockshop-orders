To effectively manage network faults in programming, there are several methods, each addressing different potential issues. Here are some of them:

1. **Circuit Breaker Pattern**: The idea is to avoid a network or service in a known failure state to prevent system failure and unnecessary resource consumption. After a configured amount of failures or timeouts, the circuit breaker trips, and all further calls to the service through the circuit breaker will return with an error, without the actual call. After some recovery time, the circuit breaker allows a limited amount of test requests to pass through. If those requests succeed, the circuit breaker resumes normal operation. If they fail, the break is "open" again.

2. **Load Balancing**: This is a proactive method that distributes network traffic across multiple servers to ensure that no single server bears too much demand. By spreading the load, this method can greatly reduce the chances of any particular node failing due to overloading.

3. **Failover**: This is a method that allows a system to automatically switch to a standby server, system, or network if the primary system fails. It's a crucial part of a high-availability (HA) setup.

4. **Replication**: This involves duplicating data across multiple machines that can be used in case of a system crash. This can be done either in real-time (synchronous) or scheduled at regular intervals (asynchronous).

5. **Health Checks**: Regularly monitor the network to check for failures, either by sending simple requests or using more complex methods. This can help detect failures early and fix them before they cause serious problems.

6. **Bulkheading**: This isolates elements of an application into pools so that if one fails, the others will continue to function. It's similar to a ship's bulkhead; if one compartment fills with water, the others remain unaffected.

7. **Backpressure & Queueing**: When a service is fully loaded, instead of dropping requests or failing, it can push back, slowing down the flow of requests from the sender. Queues can help manage high loads and spikes in traffic, ensuring a smooth flow of data.

8. **Rate Limiting**: This is the process of controlling traffic to a server by limiting the number of requests that clients can make to that server within a specified time period.

9. **Idempotency**: This means that a client can make the same request repeatedly while producing the same result. This allows automatic retrying of network calls, without worrying about side effects of the same operation being executed multiple times.

10. **Caching**: Frequently accessed data or files are stored in a cache, reducing the need to access the network for information. This can significantly reduce the load on the network and improve the application's performance.

Incorporating these fault tolerance techniques in programming can make your applications more robust and reliable, and maintain a good user experience even when things go wrong.
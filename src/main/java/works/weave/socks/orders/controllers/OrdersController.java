/*
Genrated with GPT4:

In the given code, the following changes could be considered to enhance resilience:

1. Error handling: Currently, you are catching exceptions and re-throwing as `IllegalStateException`. Consider providing more specific custom exceptions and handle each exception type separately. This way, the system can react differently for different error types.

2. Timeout configuration: The current configuration is using the same timeout for all future tasks (`addressFuture`, `customerFuture`, `cardFuture`, `itemsFuture`). Each external call might require different timeout configurations, so consider having separate timeout configurations for each.

3. Null checks: You are performing a null check for `paymentResponse` but not for other Future responses (`addressFuture`, `customerFuture`, `cardFuture`, `itemsFuture`). To avoid NullPointerException, consider doing null checks after each `future.get()` call.

4. Non-blocking calls: Currently, `Future.get()` calls are blocking the execution of the program. To make your application more resilient, consider using `CompletableFuture` which can be composed and chained without blocking.

5. Validation: Your code includes a simple validation at the beginning of the `newOrder` method to ensure that the order details are not null. Consider adding more extensive validation for data quality, such as length checks, format checks, and range checks.

6. Retry Mechanism: For transient faults, implementing a retry mechanism on failure would help. This can be used in the external calls being made in the `newOrder` method. Note that excessive retries can lead to system overload, so be sure to implement a sensible strategy (like exponential backoff).

Please note that how you apply these suggestions depends on your specific use-case, requirements, and the overall design of your application.

**To my question of why Non-blocking calls might be advantageous, GPT4 responded:**
Yes, you are correct. Spring's servlet-based architecture can handle multiple requests concurrently on separate threads. Each HTTP request is handled in a separate thread, so blocking within a single request's processing does not block the handling of other requests.

However, within a single request's processing, using `Future.get()` is a blocking call which stops the execution of the thread until the result is available or a timeout occurs. This may not be optimal, especially when dealing with multiple external service calls within the same request processing as it's done in the `newOrder` method. 

On the other hand, `CompletableFuture` offers non-blocking operations within a single request's processing. You can use `CompletableFuture` to chain tasks in an asynchronous, non-blocking manner. This way, you can kick off all your external calls at once and then process the results as they come back, instead of waiting for each one to complete sequentially. 

The advantage of using `CompletableFuture` is more about efficiency and responsiveness within the processing of a single request rather than across multiple requests. If your service has to wait for multiple dependent calls to complete, using `CompletableFuture` can potentially reduce the overall processing time for a single request and make your application more responsive.
 */

package works.weave.socks.orders.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import works.weave.socks.orders.config.OrdersConfigurationProperties;
import works.weave.socks.orders.entities.*;
import works.weave.socks.orders.repositories.CustomerOrderRepository;
import works.weave.socks.orders.resources.NewOrderResource;
import works.weave.socks.orders.services.AsyncGetService;
import works.weave.socks.orders.values.PaymentRequest;
import works.weave.socks.orders.values.PaymentResponse;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RepositoryRestController
public class OrdersController {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrdersConfigurationProperties config;

    @Autowired
    private AsyncGetService asyncGetService;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Value(value = "${http.timeout:5}")
    private long timeout;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody CustomerOrder newOrder(@RequestBody NewOrderResource item) {
        try {

            if (item.address == null || item.customer == null || item.card == null || item.items == null) {
                throw new InvalidOrderException(
                        "Invalid order request. Order requires customer, address, card and items.");
            }

            LOG.debug("Starting calls");
            Future<Resource<Address>> addressFuture = asyncGetService.getResource(item.address,
                    new TypeReferences.ResourceType<Address>() {
                    });
            Future<Resource<Customer>> customerFuture = asyncGetService.getResource(item.customer,
                    new TypeReferences.ResourceType<Customer>() {
                    });
            Future<Resource<Card>> cardFuture = asyncGetService.getResource(item.card,
                    new TypeReferences.ResourceType<Card>() {
                    });
            Future<List<Item>> itemsFuture = asyncGetService.getDataList(item.items,
                    new ParameterizedTypeReference<List<Item>>() {
                    });
            LOG.debug("End of calls.");

            float amount = calculateTotal(itemsFuture.get(timeout, TimeUnit.SECONDS));

            // Call payment service to make sure they've paid
            PaymentRequest paymentRequest = new PaymentRequest(
                    addressFuture.get(timeout, TimeUnit.SECONDS).getContent(),
                    cardFuture.get(timeout, TimeUnit.SECONDS).getContent(),
                    customerFuture.get(timeout, TimeUnit.SECONDS).getContent(),
                    amount);
            // TODO: here, retry mechanism can be implemented
            LOG.info("Sending payment request: " + paymentRequest);
            Future<PaymentResponse> paymentFuture = asyncGetService.postResource(
                    config.getPaymentUri(),
                    paymentRequest,
                    new ParameterizedTypeReference<PaymentResponse>() {
                    });
            PaymentResponse paymentResponse = paymentFuture.get(timeout, TimeUnit.SECONDS);
            LOG.info("Received payment response: " + paymentResponse);
            if (paymentResponse == null) {
                throw new PaymentDeclinedException("Unable to parse authorisation packet");
            }
            if (!paymentResponse.isAuthorised()) {
                throw new PaymentDeclinedException(paymentResponse.getMessage());
            }

            // Ship
            String customerId = parseId(customerFuture.get(timeout, TimeUnit.SECONDS).getId().getHref());
            Future<Shipment> shipmentFuture = asyncGetService.postResource(config.getShippingUri(),
                    new Shipment(customerId), new ParameterizedTypeReference<Shipment>() {
                    });
            // TODO: here, retry and timeout mechanism can be implemented

            CustomerOrder order = new CustomerOrder(
                    null,
                    customerId,
                    customerFuture.get(timeout, TimeUnit.SECONDS).getContent(),
                    addressFuture.get(timeout, TimeUnit.SECONDS).getContent(),
                    cardFuture.get(timeout, TimeUnit.SECONDS).getContent(),
                    itemsFuture.get(timeout, TimeUnit.SECONDS),
                    shipmentFuture.get(timeout, TimeUnit.SECONDS),
                    Calendar.getInstance().getTime(),
                    amount);
            LOG.debug("Received data: " + order.toString());

            CustomerOrder savedOrder = customerOrderRepository.save(order);
            LOG.debug("Saved order: " + savedOrder);

            return savedOrder;
            // TODO: retries
        } catch (TimeoutException e) {
            throw new IllegalStateException("Unable to create order due to timeout from one of the services.", e);
        } catch (InterruptedException | IOException | ExecutionException e) {
            throw new IllegalStateException("Unable to create order due to unspecified IO error.", e);
        }
    }

    private String parseId(String href) {
        Pattern idPattern = Pattern.compile("[\\w-]+$");
        Matcher matcher = idPattern.matcher(href);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not parse user ID from: " + href);
        }
        return matcher.group(0);
    }

    // Pre-existing TODO: Add link to shipping
    // @RequestMapping(method = RequestMethod.GET, value = "/orders")
    // public @ResponseBody
    // ResponseEntity<?> getOrders() {
    // List<CustomerOrder> customerOrders = customerOrderRepository.findAll();
    //
    // Resources<CustomerOrder> resources = new Resources<>(customerOrders);
    //
    // resources.forEach(r -> r);
    //
    // resources.add(linkTo(methodOn(ShippingController.class,
    // CustomerOrder.getShipment::ge)).withSelfRel());
    //
    // // add other links as needed
    //
    // return ResponseEntity.ok(resources);
    // }

    private float calculateTotal(List<Item> items) {
        float amount = 0F;
        float shipping = 4.99F;
        amount += items.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
        amount += shipping;
        return amount;
    }

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public class PaymentDeclinedException extends IllegalStateException {
        public PaymentDeclinedException(String s) {
            super(s);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public class InvalidOrderException extends IllegalStateException {
        public InvalidOrderException(String s) {
            super(s);
        }
    }
}

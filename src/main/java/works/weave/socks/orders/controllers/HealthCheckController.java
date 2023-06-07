/*
 Based on the code provided, a few suggestions to improve resilience could be:

1. Improve Exception Handling: Currently, all exceptions are being caught and treated the same way, by setting the database status to "err". It would be beneficial to handle different types of exceptions differently. For example, network exceptions could be treated differently from MongoDB-specific exceptions. You could use more specific catch clauses, or examine the type of the caught exception within the catch block, to handle different types of exceptions appropriately.

```java
try {
    mongoTemplate.executeCommand("{ buildInfo: 1 }");
} catch (MongoException e) {
    database.setStatus("err");
} catch (Exception e) {
    // Potentially log a different status or message for unknown exceptions
    database.setStatus("unknown_err");
}
```

2. Logging Exceptions: It would be good to log the exception for further investigation of the issue when the database is not healthy. Currently, the exception is being caught but not logged.

```java
try {
    mongoTemplate.executeCommand("{ buildInfo: 1 }");
} catch (Exception e) {
    database.setStatus("err");
    // Use the appropriate logging framework to log the error.
    log.error("Database health check failed", e);
}
```

3. Use of @Autowired: Instead of using field injection with `@Autowired`, it is generally recommended to use constructor-based injection. This promotes immutability and enables easier unit testing because you can pass mock dependencies through the constructor.

```java
private final MongoTemplate mongoTemplate;

@Autowired
public HealthCheckController(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
}
```

4. Health Check Response: Instead of setting the database status to "err" when there is an exception, consider returning an HTTP 503 Service Unavailable response. This will make it easier for load balancers or Kubernetes to understand the health status of the service.

```java
if ("err".equals(database.getStatus())) {
    throw new ResponseStatusException(
        HttpStatus.SERVICE_UNAVAILABLE, "Database is unavailable"
    );
}
```

Please note that some of these changes might require additional changes in other parts of your application, so consider them in the context of your entire application design.
 */

package works.weave.socks.orders.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import works.weave.socks.orders.entities.HealthCheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HealthCheckController {

   @Autowired
   private MongoTemplate mongoTemplate;

   @ResponseStatus(HttpStatus.OK)
   @RequestMapping(method = RequestMethod.GET, path = "/health")
   public @ResponseBody Map<String, List<HealthCheck>> getHealth() {
      Map<String, List<HealthCheck>> map = new HashMap<String, List<HealthCheck>>();
      List<HealthCheck> healthChecks = new ArrayList<HealthCheck>();
      Date dateNow = Calendar.getInstance().getTime();

      HealthCheck app = new HealthCheck("orders", "OK", dateNow);
      HealthCheck database = new HealthCheck("orders-db", "OK", dateNow);

      try {
         mongoTemplate.executeCommand("{ buildInfo: 1 }");
      } catch (Exception e) {
         database.setStatus("err");
      }

      healthChecks.add(app);
      healthChecks.add(database);

      map.put("health", healthChecks);
      return map;
   }
}

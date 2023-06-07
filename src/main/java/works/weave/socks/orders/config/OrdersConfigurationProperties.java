// generated using GPT4:
// Based on your requirements and the provided code, here are a few things that could be done in this specific file to enhance resilience:

// 1. Handling `URISyntaxException`: `URI.create(String str)` method throws `URISyntaxException` if the given string violates RFC 2396. Even if you are confident about the structure of the URI being built, it's a good practice to handle this exception. This way, your application will not terminate abruptly in case of an unexpected input.

//     Here's how you can modify your `toUri()` method:
//     ```java
//     public URI toUri() {
//         try {
//             return new URI(wrapHTTP(hostname.toString() + domain.toString()) + endpoint);
//         } catch (URISyntaxException e) {
//             throw new IllegalArgumentException("Invalid URI", e);
//         }
//     }
//     ```

// 2. Null Check: Currently, the `ServiceUri` constructor does not validate if the passed `Hostname`, `Domain`, or `endpoint` are `null`. This may lead to `NullPointerException` in the `toUri` method. Validate these inputs in the constructor to prevent this.
//     ```java
//     private ServiceUri(Hostname hostname, Domain domain, String endpoint) {
//         if (hostname == null || domain == null || endpoint == null) {
//             throw new IllegalArgumentException("Hostname, domain and endpoint must not be null");
//         }
//         this.hostname = hostname;
//         this.domain = domain;
//         this.endpoint = endpoint;
//     }
//     ```

// 3. Empty Check: You could also add empty string checks for `Hostname` and `Domain`. If either of them is empty, it might not create a valid URI, and your `toUri` method would return an incorrect URI.
//     ```java
//     private Hostname(String hostname) {
//         if (hostname == null || hostname.isEmpty()) {
//             throw new IllegalArgumentException("Hostname must not be null or empty");
//         }
//         this.hostname = hostname;
//     }

//     private Domain(String domain) {
//         if (domain == null || domain.isEmpty()) {
//             throw new IllegalArgumentException("Domain must not be null or empty");
//         }
//         this.domain = domain;
//     }
//     ```

// 4. HTTPS instead of HTTP: If possible, consider using HTTPS protocol instead of HTTP to increase security. This would help secure your services by encrypting the information being sent between them.

//     To do this, modify `wrapHTTP` method:
//     ```java
//     private String wrapHTTP(String host) {
//         return "https://" + host;
//     }
//     ```

// These changes would make your code more resilient against unexpected or invalid inputs and exceptions that could cause your application to fail.

package works.weave.socks.orders.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties
public class OrdersConfigurationProperties {
    private String domain = "";

    public URI getPaymentUri() {
        return new ServiceUri(new Hostname("payment"), new Domain(domain), "/paymentAuth").toUri();
    }

    public URI getShippingUri() {
        return new ServiceUri(new Hostname("shipping"), new Domain(domain), "/shipping").toUri();
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    private class Hostname {
        private final String hostname;

        private Hostname(String hostname) {
            this.hostname = hostname;
        }

        @Override
        public String toString() {
            if (hostname != null && !hostname.equals("")) {
                return hostname;
            } else {
                return "";
            }
        }
    }

    private class Domain {
        private final String domain;

        private Domain(String domain) {
            this.domain = domain;
        }

        @Override
        public String toString() {
            if (domain != null && !domain.equals("")) {
                return "." + domain;
            } else {
                return "";
            }
        }
    }

    private class ServiceUri {
        private final Hostname hostname;
        private final Domain domain;
        private final String endpoint;

        private ServiceUri(Hostname hostname, Domain domain, String endpoint) {
            this.hostname = hostname;
            this.domain = domain;
            this.endpoint = endpoint;
        }

        public URI toUri() {
            return URI.create(wrapHTTP(hostname.toString() + domain.toString()) + endpoint);
        }

        private String wrapHTTP(String host) {
            return "http://" + host;
        }

        @Override
        public String toString() {
            return "ServiceUri{" +
                    "hostname=" + hostname +
                    ", domain=" + domain +
                    '}';
        }
    }
}

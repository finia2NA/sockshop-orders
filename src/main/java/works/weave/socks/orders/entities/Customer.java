/*
 Generated using GPT4:
 This code represents an Entity in a Spring Boot application using MongoDB for persistence. At the first glance, there seems to be no problematic code which could make the application non-resilient. 

However, here are some improvements specific to this code that might indirectly increase the resilience of the overall Spring Boot application:

1. **Validation**: You could add validation checks to setters and constructors to ensure that invalid data doesn't get persisted. For instance, you could check for null or empty strings for id, firstName, lastName and username fields. This may seem more of a validation aspect, but validating data at this level can prevent unexpected exceptions or faults from propagating and disrupting service.

```java
public void setFirstName(String firstName) {
    if (firstName == null || firstName.isEmpty()) {
        throw new IllegalArgumentException("firstName cannot be null or empty");
    }
    this.firstName = firstName;
}
```

2. **Defensive Copy**: In the getAddresses() and getCards() methods, you're returning a direct reference to the lists. This could potentially allow modification of these lists outside of this class which may lead to undesirable behavior or bugs. It might be beneficial to return a defensive copy of these lists to prevent such situations.

```java
public List<Address> getAddresses() {
    return new ArrayList<>(addresses);
}
```

3. **Immutable Entities**: You might want to consider making your entities immutable to increase the predictability of your application. This involves removing all setter methods and only setting properties via constructors. Immutable objects are inherently thread-safe and can't be changed once they're created, which can help in reducing bugs and improving resilience. 

Remember, these changes can help in increasing the overall robustness of the application, but resilience in a distributed system also heavily depends on how your application handles network errors, database connection errors, and other exceptions that may occur outside this entity class. That part of resilience won't be reflected in this class.
 */

package works.weave.socks.orders.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Customer {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String username;

    @DBRef(lazy = true)
    private List<Address> addresses = new ArrayList<>();

    @DBRef(lazy = true)
    private List<Card> cards = new ArrayList<>();

    public Customer() {
    }

    public Customer(String id, String firstName, String lastName, String username, List<Address> addresses,
            List<Card> cards) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.addresses = addresses;
        this.cards = cards;
    }

    public Customer(String firstName, String lastName, String username, List<Address> addresses, List<Card> cards) {
        this(null, firstName, lastName, username, addresses, cards);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", addresses=" + addresses +
                ", cards=" + cards +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Customer customer = (Customer) o;

        if (!getId().equals(customer.getId()))
            return false;
        return getUsername() != null ? getUsername().equals(customer.getUsername()) : customer.getUsername() == null;

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

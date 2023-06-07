/*
 Generated with GPT4:

 Looking at your code, it seems like you've developed a simple POJO (Plain Old Java Object) class, `Address`. This class is mainly used to hold data and doesn't have any particular business logic, operations or external connections that need to be resilient or fault-tolerant. This class doesn't perform any database or network operations or make any API calls, so there are no network faults, IO faults, or timeouts to handle.

However, there are small improvements that can be done to enhance the robustness of this class:

1. **Defensive programming**: To ensure that an `Address` object always has a valid state, consider adding some simple validation in your setters or constructor. For instance, you could check if a passed string is null or empty, and if so, throw an `IllegalArgumentException`. This makes sure that your application does not proceed with an invalid state. 

Example for `setNumber` method:
```java
public void setNumber(String number) {
    if (number == null || number.isEmpty()) {
        throw new IllegalArgumentException("Number cannot be null or empty");
    }
    this.number = number;
}
```
You could also add similar validations for other properties like `street`, `city`, `postcode` and `country` based on your requirements.

2. **Null safety in `equals` and `hashCode` methods**: In your current implementation, if the `id` field is `null`, calling `equals` or `hashCode` would result in a `NullPointerException`. Consider adding null checks in these methods to make them null-safe.

Here is an example how you could change it:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Address address = (Address) o;

    if (getId() != null) {
        return getId().equals(address.getId());
    }

    return false;
}

@Override
public int hashCode() {
    return getId() != null ? getId().hashCode() : 0;
}
```

These changes can help enhance the resilience of the `Address` class by ensuring it maintains a valid state and handles potential null values correctly, thus preventing unnecessary exceptions and application crashes.
 */
package works.weave.socks.orders.entities;

import org.springframework.data.annotation.Id;

public class Address {

    @Id
    private String id;

    private String number;
    private String street;
    private String city;
    private String postcode;
    private String country;

    public Address() {
    }

    public Address(String id, String number, String street, String city, String postcode, String country) {
        this.id = id;
        this.number = number;
        this.street = street;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }

    public Address(String number, String street, String city, String postcode, String country) {
        this(null, number, street, city, postcode, country);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Address address = (Address) o;

        return getId().equals(address.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}

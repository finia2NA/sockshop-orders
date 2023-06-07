/*
 * Generated using GPT4:
 
 Based on the code provided, it represents a simple Cart entity using Spring Data MongoDB annotations for mapping between Java objects and MongoDB. Here are a few suggestions that can improve the resilience of this specific piece of code:

1. Clone the List in getter methods: Returning a direct reference to mutable fields can be risky as it can be modified outside of your class, which can lead to unexpected results. This is often referred to as breaking encapsulation. In your case, for the method `getItems()`, you might want to return a new `ArrayList` containing all the items from the original list. The same applies to the `contents()` method. This makes sure the internal representation of your object is not modifiable from outside the class:

```java
public List<Item> getItems() {
    return new ArrayList<>(items);
}

public List<Item> contents() {
    return new ArrayList<>(items);
}
```

2. Null-check in setter methods: In the method `setItems()`, if a null value is passed, it might cause NullPointerException in other parts of the code. It could be better to do a null-check before setting the list:

```java
public void setItems(List<Item> items) {
    if (items == null) {
        throw new IllegalArgumentException("Items cannot be null");
    }
    this.items = items;
}
```

3. Null-check before adding and removing items: In the methods `add()` and `remove()`, you may want to check if the item is null before adding or removing it from the list. This could prevent NullPointerException and makes sure no null items can be added to the list:

```java
public Cart add(Item item) {
    if (item == null) {
        throw new IllegalArgumentException("Item cannot be null");
    }
    items.add(item);
    return this;
}

public Cart remove(Item item) {
    if (item == null) {
        throw new IllegalArgumentException("Item cannot be null");
    }
    items.remove(item);
    return this;
}
```

Remember, the goal is to make your code fail fast if something unexpected occurs. It's generally easier to deal with these issues immediately rather than letting the program run in an inconsistent state. The code modifications suggested here are trying to enforce this principle.
 */

package works.weave.socks.orders.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document
public class Cart {
    @NotNull
    private String customerId;
    @Id
    private String id;
    @DBRef
    private List<Item> items = new ArrayList<>();

    public Cart(String customerId) {
        this.customerId = customerId;
    }

    public Cart() {
        this(null);
    }

    public List<Item> contents() {
        return items;
    }

    public Cart add(Item item) {
        items.add(item);
        return this;
    }

    public Cart remove(Item item) {
        items.remove(item);
        return this;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", items=" + items +
                '}';
    }
}

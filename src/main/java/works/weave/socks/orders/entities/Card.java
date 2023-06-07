/*
 * TODO: credit card validation
 * 
 * Generated using GPT4:
 * Based on the provided code, there are a few things you could change to improve resilience in the context of this specific class:

1. **Null Safety**: The `equals()` and `hashCode()` methods assume `id` will never be null. If `id` is null, these methods will throw a NullPointerException. To prevent this, you could add null checks before calling methods on `id`. 

    ```java
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
    
        Card card = (Card) o;
    
        return id != null ? id.equals(card.getId()) : card.getId() == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    ```

2. **Input Validation**: For the `setLongNum()`, `setExpires()`, and `setCcv()` methods, consider adding some form of validation to make sure the input is valid. This could be as simple as checking for null or empty strings, or more complex like checking if the card number is valid or if the expiry date is in the future.

    ```java
    public void setLongNum(String longNum) {
        if (longNum == null || longNum.isEmpty()) {
            throw new IllegalArgumentException("Invalid card number");
        }
        this.longNum = longNum;
    }
    ```

3. **Immutability**: Immutable objects are inherently thread-safe and do not have synchronization issues. If `Card` instances should not change after being created, consider making the class immutable by removing setter methods and making fields final.

    ```java
    public final class Card {

        @Id
        private final String id;

        private final String longNum;
        private final String expires;
        private final String ccv;

        // constructors and getters remain the same

        // no setter methods
    }
    ```

Remember, these changes are for the context of this specific class. Other parts of your application may require different strategies for increasing resilience.

 */

package works.weave.socks.orders.entities;

import org.springframework.data.annotation.Id;

public class Card {

    @Id
    private String id;

    private String longNum;
    private String expires;
    private String ccv;

    public Card() {
    }

    public Card(String id, String longNum, String expires, String ccv) {
        this.id = id;
        this.longNum = longNum;
        this.expires = expires;
        this.ccv = ccv;
    }

    public Card(String longNum, String expires, String ccv) {
        this(null, longNum, expires, ccv);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", longNum='" + longNum + '\'' +
                ", expires='" + expires + '\'' +
                ", ccv='" + ccv + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        return getId().equals(card.getId());

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

    public String getLongNum() {
        return longNum;
    }

    public void setLongNum(String longNum) {
        this.longNum = longNum;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }
}

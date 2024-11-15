package Checkout;

import java.time.LocalDateTime;
import java.util.UUID;

public class Checkout {
    public UUID id;
    public UUID bookId;
    public UUID clientId;
    public LocalDateTime checkedOutAt;
    public LocalDateTime returnedAt;

    public Checkout(
            UUID id,
            UUID bookId,
            UUID clientId,
            LocalDateTime checkedOutAt,
            LocalDateTime returnedAt
    ) {
        this.id = id;
        this.bookId = bookId;
        this.clientId = clientId;
        this.checkedOutAt = checkedOutAt;
        this.returnedAt = returnedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public LocalDateTime getCheckedOutAt() {
        return checkedOutAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }
}

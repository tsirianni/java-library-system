package Checkout;

import java.util.UUID;

public class Checkout {
    public UUID id;
    public UUID book_id;
    public UUID client_id;

    public Checkout(UUID id, UUID book_id, UUID client_id) {
        this.id = id;
        this.book_id = book_id;
        this.client_id = client_id;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBook_id() {
        return book_id;
    }

    public UUID getClient_id() {
        return client_id;
    }
}

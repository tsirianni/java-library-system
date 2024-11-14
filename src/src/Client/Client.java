package Client;

import java.util.UUID;

public class Client {
    public UUID id;
    public String name;
    public String email;


    public Client(UUID id, String name, String email) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

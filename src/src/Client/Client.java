package Client;

import Client.DTOs.AddClientDTO;

import java.util.UUID;

public class Client {
    public UUID id;
    public String name;
    public String email;


    public Client(AddClientDTO data) {
        this.name = data.name();
        this.email = data.email();
        this.id = UUID.randomUUID();
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

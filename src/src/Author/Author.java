package Author;

import java.time.LocalDateTime;
import java.util.UUID;

public class Author {
    public UUID id;
    public String name;
    LocalDateTime dateOfBirth;

    public Author(UUID id, String name, LocalDateTime dateOfBirth) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }
}

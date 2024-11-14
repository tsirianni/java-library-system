package Book;

import java.time.LocalDateTime;
import java.util.UUID;

public class Book {
    public UUID id;
    public String title;
    public String author;
    public boolean available;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

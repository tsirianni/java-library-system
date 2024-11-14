package Book;

import Author.Author;

import java.time.LocalDateTime;
import java.util.UUID;

public class Book {
    public UUID id;
    public String title;
    public Author author;
    public boolean available;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public Book(
            UUID id, String title, Author author, boolean available, LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

package Book;

import Book.DTOs.AddBookDTO;
import utils.PrintColoured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class BookService {
    private AddBookDTO promptDTO(Scanner scanner) {
        PrintColoured.cyan("What is the book's title? ");
        String title = scanner.nextLine();
        PrintColoured.cyan("Who is the book's author? ");
        String author = scanner.nextLine();

        return new AddBookDTO(title, author);
    }

    public void addBook(Scanner scanner) throws Exception {
        AddBookDTO data = this.promptDTO(scanner);

        Path booksDBPath = Paths.get("src/resources/books.csv");
        List<String> books;
        try {
            books = Files.readAllLines(booksDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain books' records");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String content = "";
        for (String book : books) {
            String[] bookDetails = book.split(";");
            String bookTitle = bookDetails[1];
            if (Objects.equals(bookTitle, data.title())) {
                throw new IllegalArgumentException("There is already a book registered with this title");
            }

            content = content.concat(book.concat("\n"));
        }

        LocalDateTime today = LocalDateTime.now();
        Book newBook = new Book(UUID.randomUUID(), data.title(), data.author(), true, today, today);

        String formattedBook = this.formatRecord(newBook);
        Files.writeString(booksDBPath, content.concat(formattedBook));
    }

    private String formatRecord(Book newBook) {
        return String.format("%s;%s;%s;%b;%s;%s", newBook.getId(), newBook.getTitle(), newBook.getAuthor(),
                             newBook.isAvailable(), newBook.getCreatedAt(), newBook.getUpdatedAt()
        );
    }

    public void findAllBooks() throws Exception {
        Path booksDBPath = Paths.get("src/resources/books.csv");
        List<String> books;
        try {
            books = Files.readAllLines(booksDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain books' records");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        for (String book : books) {
            String[] bookDetails = book.split(";");
            PrintColoured.green(String.format("ID.: %-30s - Title.: %-25s - Author.: %-25s - Available.: %-10b",
                                              bookDetails[0],
                                              bookDetails[1], bookDetails[2], bookDetails[3]
            ));

        }
    }
}

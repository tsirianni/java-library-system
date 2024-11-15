package Book;

import Author.Author;
import Author.AuthorService;
import Book.DTOs.AddBookDTO;
import utils.PrintColoured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;

public class BookService {
    private final Path booksDBPath = Paths.get("src/resources/books.csv");
    private final AuthorService authorService = new AuthorService();

    private AddBookDTO promptDTO(Scanner scanner) throws Exception {
        PrintColoured.cyan("What is the book's title? ");
        String title = scanner.nextLine();
        this.authorService.findAllAuthors(true);
        PrintColoured.cyan("What is the book's author's ID? ");
        String authorId = scanner.nextLine();

        return new AddBookDTO(title, authorId);
    }

    public void addBook(Scanner scanner) throws Exception {
        AddBookDTO data = this.promptDTO(scanner);

        List<Author> authors = this.authorService.findAllAuthors(false);
        Optional<Author> existingAuthor = authors.stream().filter((author -> Objects.equals(
                author.getId(),
                UUID.fromString(data.authorID())
        ))).findFirst();

        if (existingAuthor.isEmpty()) {
            throw new IllegalArgumentException("No registered author was found with the provided name");
        }

        List<Book> books = this.findAllBooks(false);
        Optional<Book> existingBook =
                books.stream().filter(book -> Objects.equals(book.getTitle(), data.title()) && Objects.equals(
                        book.getAuthor().getId(),
                        UUID.fromString(data.authorID())
                )).findFirst();

        if (existingBook.isPresent()) {
            throw new IllegalArgumentException("There is already a book registered with this title and author");
        }

        LocalDateTime today = LocalDateTime.now();
        Book newBook = new Book(UUID.randomUUID(), data.title(), existingAuthor.get(), true, today, today);

        String formattedBook = this.formatRecord(newBook);
        Files.writeString(booksDBPath, formattedBook, StandardOpenOption.APPEND,
                          StandardOpenOption.CREATE
        );
    }

    public String formatRecord(Book newBook) {
        return String.format("%s;%s;%s;%b;%s;%s\n", newBook.getId(), newBook.getTitle(), newBook.getAuthor().getId(),
                             newBook.isAvailable(), newBook.getCreatedAt(), newBook.getUpdatedAt()
        );
    }

    public List<Book> findAllBooks(boolean shouldConsoleBooks) throws Exception {
        Path booksDBPath = Paths.get("src/resources/books.csv");
        List<String> bookRecords;
        try {
            bookRecords = Files.readAllLines(booksDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain books' records: " + IOEx.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Author> authors = this.authorService.findAllAuthors(false);
        List<Book> books = new ArrayList<>();

        for (String bookRecord : bookRecords) {
            String[] bookDetails = bookRecord.split(";");
            Optional<Author> bookAuthor = authors.stream().filter(author -> Objects.equals(
                    UUID.fromString(bookDetails[2]),
                    author.getId()
            )).findFirst();

            if (bookAuthor.isEmpty()) {
                throw new Exception("It was not possible to locate the author of one or more books");
            }

            Book book = new Book(UUID.fromString(bookDetails[0]), bookDetails[1], bookAuthor.get(),
                                 Boolean.parseBoolean(bookDetails[3]),
                                 LocalDateTime.parse(bookDetails[4]),
                                 LocalDateTime.parse(bookDetails[5])
            );

            books.add(book);

            if (shouldConsoleBooks) {
                PrintColoured.green(String.format("ID.: %-30s - Title.: %-25s - Author.: %-25s - Available.: %-10b",
                                                  book.getId(),
                                                  book.getTitle(), book.getAuthor().getName(), book.isAvailable()
                ));
            }
        }

        return books;
    }

    public Optional<Book> findBookById(UUID bookId) throws Exception {
        List<Book> books = this.findAllBooks(false);

        return books.stream().filter((bk) -> Objects.equals(bk.getId(), bookId)).findFirst();
    }
}

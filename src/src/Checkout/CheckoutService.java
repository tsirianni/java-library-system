package Checkout;

import Book.Book;
import Book.BookService;
import Checkout.DTOs.CheckoutBookDTO;
import Client.Client;
import Client.ClientService;
import utils.PrintColoured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class CheckoutService {
    private final BookService bookService = new BookService();
    private final ClientService clientService = new ClientService();

    private CheckoutBookDTO promptDTO(Scanner scanner) throws Exception {
        List<Book> books = bookService.findAllBooks(false);

        books.forEach(book -> {
            if (book.isAvailable()) {
                PrintColoured.green(String.format("ID.: %-30s - Title.: %-25s - Author.: %-25s",
                                                  book.getId(),
                                                  book.getTitle(), book.getAuthor().getName()
                ));
            }
        });
        PrintColoured.cyan("What book would you like to checkout? (book ID) ");


        String bookId = scanner.nextLine();
        PrintColoured.cyan("Insert your registered name: ");
        String clientName = scanner.nextLine();

        return new CheckoutBookDTO(bookId, clientName);
    }

    public void checkoutBook(Scanner scanner) throws Exception {
        CheckoutBookDTO data = this.promptDTO(scanner);
        List<Client> clients = this.clientService.findAllClients(false);
        List<Book> books = this.bookService.findAllBooks(false);

        Optional<Client> client =
                clients.stream().filter((cl) -> Objects.equals(cl.getName(), data.clientName())).findFirst();
        if (client.isEmpty()) {
            throw new IllegalArgumentException("No client was found with the provided name");
        }

        Optional<Book> book = books.stream().filter((bk) -> Objects.equals(
                bk.getId(),
                UUID.fromString(data.bookId())
        )).findFirst();

        if (book.isEmpty()) {
            throw new IllegalArgumentException("No book was found with the provided id");
        }

        Book bookObject = book.get();
        Client clientObject = client.get();

        Checkout checkout = new Checkout(UUID.randomUUID(), bookObject.getId(), clientObject.getId());
        String formattedRecord = this.formatRecord(checkout);
        Path checkoutsDBPath = Paths.get("src/resources/checkouts.csv");
        Path booksDBPath = Paths.get("src/resources/books.csv");

        // Already apologise for the horrible approach
        final String[] updateBookRecords = {""};
        books.forEach(bookRecord -> {
            if (bookRecord.getId() == checkout.getBook_id()) {
                bookRecord.setAvailable(false);
            }

            updateBookRecords[0] = updateBookRecords[0].concat(this.bookService.formatRecord(bookRecord));
        });

        try {
            Files.writeString(checkoutsDBPath, formattedRecord, StandardOpenOption.APPEND,
                              StandardOpenOption.CREATE
            );

            Files.writeString(booksDBPath, updateBookRecords[0]);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to checkout books: " + IOEx.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String formatRecord(Checkout checkout) {
        return String.format("%s;%s;%s\n", checkout.getId().toString(), checkout.getBook_id().toString(),
                             checkout.getClient_id().toString()
        );
    }
}

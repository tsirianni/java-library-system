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
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutService {
    private final BookService bookService = new BookService();
    private final ClientService clientService = new ClientService();
    private final Path checkoutsDBPath = Paths.get("src/resources/checkouts.csv");
    private final Path booksDBPath = Paths.get("src/resources/books.csv");

    private Optional<CheckoutBookDTO> promptDTO(Scanner scanner) throws Exception {
        PrintColoured.cyan("Would you like to see a list of available books? (yes|y|no|n)");
        String seeList = scanner.nextLine();
        Pattern seeListPattern = Pattern.compile("^(yes|y)$", Pattern.CASE_INSENSITIVE);
        Matcher showBookList = seeListPattern.matcher(seeList);

        if (showBookList.matches()) {
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
            return Optional.of(new CheckoutBookDTO(bookId, clientName));
        } else {
            PrintColoured.cyan("Operation cancelled.");
            return Optional.empty();
        }
    }

    public boolean checkoutBook(Scanner scanner) throws Exception {
        Optional<CheckoutBookDTO> dataOptional = this.promptDTO(scanner);
        boolean proceeded = false;

        if (dataOptional.isPresent()) {
            proceeded = true;
            CheckoutBookDTO data = dataOptional.get();

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

            Checkout checkout = new Checkout(UUID.randomUUID(), bookObject.getId(), clientObject.getId(),
                                             LocalDateTime.now(), null
            );

            String formattedRecord = this.formatRecord(checkout);

            // Already apologise for the horrible approach
            final String[] updateBookRecords = {""};
            books.forEach(bookRecord -> {
                if (bookRecord.getId() == checkout.getBookId()) {
                    bookRecord.setAvailable(false);
                }

                updateBookRecords[0] = updateBookRecords[0].concat(this.bookService.formatRecord(bookRecord));
            });

            try {
                Files.writeString(this.checkoutsDBPath, formattedRecord, StandardOpenOption.APPEND,
                                  StandardOpenOption.CREATE
                );

                Files.writeString(this.booksDBPath, updateBookRecords[0]);
            } catch (IOException IOEx) {
                throw new Exception("DB Error: Unable to checkout books: " + IOEx.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return proceeded;
    }

    private String formatRecord(Checkout checkout) {
        return String.format("%s;%s;%s;%s;%s\n", checkout.getId().toString(), checkout.getBookId().toString(),
                             checkout.getClientId().toString(), checkout.getCheckedOutAt(), checkout.getReturnedAt()
        );
    }
}

package Checkout;

import Book.Book;
import Book.BookService;
import Checkout.DTOs.CheckoutBookDTO;
import Checkout.DTOs.ReturnBookDTO;
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

    private Optional<CheckoutBookDTO> promptCheckoutDTO(Scanner scanner) throws Exception {
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
        Optional<CheckoutBookDTO> dataOptional = this.promptCheckoutDTO(scanner);
        boolean proceeded = false;

        if (dataOptional.isPresent()) {
            proceeded = true;
            CheckoutBookDTO data = dataOptional.get();
            Optional<Client> client = this.clientService.findClientByName(data.clientName());

            if (client.isEmpty()) {
                throw new IllegalArgumentException("No client was found with the provided name");
            }

            Optional<Book> book = this.bookService.findBookById(UUID.fromString(data.bookId()));
            if (book.isEmpty()) {
                throw new IllegalArgumentException("No book was found with the provided id");
            }

            Book bookObject = book.get();
            Client clientObject = client.get();
            Checkout checkout = new Checkout(
                    UUID.randomUUID(),
                    bookObject.getId(),
                    clientObject.getId(),
                    LocalDateTime.now(),
                    null
            );

            String formattedRecord = this.formatRecord(checkout);

            // Already apologise for the horrible approach
            List<Book> books = this.bookService.findAllBooks(false);
            final String[] updateBookRecords = {""};
            for (Book bk : books) {
                if (Objects.equals(bk.getId(), checkout.getBookId())) {
                    bk.setAvailable(false);
                }

                updateBookRecords[0] = updateBookRecords[0].concat(this.bookService.formatRecord(bk));
            }

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

    public List<Checkout> findAllClientCheckouts(UUID clientId, boolean shouldConsoleCheckouts, boolean ignoreReturned) throws Exception {
        List<String> checkoutRecords;
        try {
            checkoutRecords = Files.readAllLines(this.checkoutsDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain client's records");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Checkout> checkouts = new ArrayList<>();
        List<Book> books = this.bookService.findAllBooks(false);

        for (String checkoutRecord : checkoutRecords) {
            String[] checkoutDetails = checkoutRecord.split(";");
            UUID checkedOutBookClientId = UUID.fromString(checkoutDetails[2]);
            if (Objects.equals(checkedOutBookClientId, clientId)) {
                boolean hasReturnDate = !Objects.equals(checkoutDetails[4], "null");
                Checkout checkout = new Checkout(
                        UUID.fromString(checkoutDetails[0]),
                        UUID.fromString(checkoutDetails[1]),
                        checkedOutBookClientId,
                        LocalDateTime.parse(checkoutDetails[3]),
                        hasReturnDate ? LocalDateTime.parse(checkoutDetails[4]) : null
                );

                checkouts.add(checkout);
                if (shouldConsoleCheckouts) {

                    boolean shouldPrintBook = !ignoreReturned || (!hasReturnDate);
                    if (shouldPrintBook) {
                        Book checkoutBook =
                                books.stream().filter(book -> Objects.equals(book.getId(), checkout.getBookId())).findFirst().orElse(null);

                        String returnDateRepresentation = hasReturnDate ? "%td/%<tm/%<tY" : "";

                        PrintColoured.green(String.format("ID.: %-30s - Book: %-20s - Checked out at.: %td/%<tm/%<tY - Returned at.: " + returnDateRepresentation, checkout.getId(),
                                                          checkoutBook != null ? checkoutBook.getTitle() : "book details " + "unavailable",
                                                          checkout.getCheckedOutAt(), checkout.getReturnedAt()
                        ));
                    }
                }
            }
        }
        return checkouts;
    }

    public List<Checkout> findAllCheckouts(boolean shouldConsoleCheckouts) throws Exception {
        List<String> checkoutRecords;
        try {
            checkoutRecords = Files.readAllLines(this.checkoutsDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain client's records");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Checkout> checkouts = new ArrayList<>();
        List<Book> books = this.bookService.findAllBooks(false);

        for (String checkoutRecord : checkoutRecords) {
            String[] checkoutDetails = checkoutRecord.split(";");
            boolean hasReturnDate = !Objects.equals(checkoutDetails[4], "null");

            Checkout checkout = new Checkout(
                    UUID.fromString(checkoutDetails[0]),
                    UUID.fromString(checkoutDetails[1]),
                    UUID.fromString(checkoutDetails[2]),
                    LocalDateTime.parse(checkoutDetails[3]),
                    hasReturnDate ? LocalDateTime.parse(checkoutDetails[4]) : null
            );

            checkouts.add(checkout);

            if (shouldConsoleCheckouts) {
                Book checkoutBook =
                        books.stream().filter(book -> Objects.equals(book.getId(), checkout.getBookId())).findFirst().orElse(null);


                PrintColoured.green(String.format("ID.: %-30s - Book: %-20s", checkout.getId(),
                                                  checkoutBook != null ? checkoutBook.getTitle() : "book details " + "unavailable"
                ));
            }

        }
        return checkouts;
    }

    private Optional<ReturnBookDTO> promptReturnBookDTO(Scanner scanner) throws Exception {
        PrintColoured.cyan("Insert your name: ");
        String clientName = scanner.nextLine();

        Optional<Client> client = this.clientService.findClientByName(clientName);
        Optional<ReturnBookDTO> returnBookDTO;
        List<Checkout> clientCheckouts;

        if (client.isPresent()) {
            Client clientObject = client.get();
            clientCheckouts = this.findAllClientCheckouts(clientObject.getId(), true, true);
            if (clientCheckouts.isEmpty()) {
                PrintColoured.red("No checked out books have been found fo the given client");
                returnBookDTO = Optional.empty();
            } else {
                PrintColoured.cyan("Based on your check outs, which book would you like to return? (check out ID)");
                String checkoutId = scanner.nextLine();
                return Optional.of(new ReturnBookDTO(checkoutId));
            }
        } else {
            PrintColoured.red("No client has been found with the registered name");
            returnBookDTO = Optional.empty();
        }

        return returnBookDTO;
    }

    public boolean returnBook(Scanner scanner) throws Exception {
        Optional<ReturnBookDTO> promptOptional = this.promptReturnBookDTO(scanner);
        boolean proceeded = promptOptional.isPresent();

        if (proceeded) {
            ReturnBookDTO data = promptOptional.get();
            UUID checkoutId = UUID.fromString(data.checkoutId());
            UUID returnedBookId = null;
            List<Book> books = this.bookService.findAllBooks(false);

            List<Checkout> checkouts = this.findAllCheckouts(false);
            final String[] updatedCheckoutRecords = {""};

            for (Checkout checkout : checkouts) {
                if (Objects.equals(checkout.getId(), checkoutId)) {
                    checkout.setReturnedAt(LocalDateTime.now());
                    returnedBookId = checkout.getBookId();
                }

                updatedCheckoutRecords[0] = updatedCheckoutRecords[0].concat(this.formatRecord(checkout));
            }

            final String[] updateBookRecords = {""};
            for (Book book : books) {
                if (Objects.equals(book.getId(), returnedBookId)) {
                    book.setAvailable(true);
                }

                updateBookRecords[0] = updateBookRecords[0].concat(this.bookService.formatRecord(book));
            }

            try {
                Files.writeString(this.checkoutsDBPath, updatedCheckoutRecords[0]);
                Files.writeString(this.booksDBPath, updateBookRecords[0]);
            } catch (IOException IOEx) {
                throw new Exception("DB Error: Unable to checkout books: " + IOEx.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return proceeded;
    }
}

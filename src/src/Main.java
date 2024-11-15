import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Author.Author;
import Author.AuthorService;
import Book.Book;
import Book.BookService;
import Checkout.CheckoutService;
import Client.Client;
import Client.ClientService;
import utils.Actions;
import utils.PrintColoured;

public class Main {
    private final static ClientService clientService = new ClientService();
    private final static AuthorService authorService = new AuthorService();
    private final static BookService bookService = new BookService();
    private final static CheckoutService checkoutService = new CheckoutService();

    public static void main(String[] args) {
        boolean doAnother = true;
        Scanner scanner = new Scanner(System.in);

        while (doAnother) {
            try {
                init(scanner);
            } catch (Exception e) {
                PrintColoured.red("An exception has occurred: " + e.getMessage());
            } finally {
                doAnother = promptDoAnother(scanner);
            }
        }

        scanner.close();
    }

    public static void showMenu() {
        PrintColoured.cyan(String.format("%-30s --> %d", "Register a client:", 1));
        PrintColoured.cyan(String.format("%-30s --> %d", "List all client:", 2));
        PrintColoured.cyan(String.format("%-30s --> %d", "Register an author:", 3));
        PrintColoured.cyan(String.format("%-30s --> %d", "List all authors:", 4));
        PrintColoured.cyan(String.format("%-30s --> %d", "Register a book:", 5));
        PrintColoured.cyan(String.format("%-30s --> %d", "List all books:", 6));
        PrintColoured.cyan(String.format("%-30s --> %d", "Check out a book:", 7));
        PrintColoured.cyan(String.format("%-30s --> %d", "Return a book:", 8));
        PrintColoured.cyan(String.format("%-30s --> %d", "View my checked out books:", 9));
    }

    public static boolean promptDoAnother(Scanner scanner) {
        PrintColoured.cyan("Do another operation? (yes|y|no|n)");
        String doAnotherResponse = scanner.nextLine();
        Pattern doAnotherPattern = Pattern.compile("^(yes|y)$", Pattern.CASE_INSENSITIVE);
        Matcher doAnother = doAnotherPattern.matcher(doAnotherResponse);

        return doAnother.matches();
    }

    public static void init(Scanner scanner) throws Exception {
        PrintColoured.cyan("\nWhat would you like to do?\n");
        showMenu();
        PrintColoured.white("\nI would like to (number):");

        Actions chosenAction = Actions.fromCode(Integer.parseInt(scanner.nextLine()));

        switch (chosenAction) {
            case ADD_CLIENT:
                PrintColoured.green("You have chosen to register a client");
                Client newClient = clientService.addClient(scanner);
                PrintColoured.green(String.format("Client %s successfully registered", newClient.getName()));
                break;
            case LIST_CLIENTS:
                PrintColoured.green("You have chosen to list all clients");
                clientService.findAllClients(true);
                break;
            case ADD_AUTHOR:
                PrintColoured.green("You have chosen to register an author");
                Author author = authorService.addAuthor(scanner);
                PrintColoured.green(String.format("Author %s successfully registered", author.getName()));
                break;
            case LIST_AUTHORS:
                PrintColoured.green("You have chosen to list all authors");
                authorService.findAllAuthors(true);
                break;
            case ADD_BOOK:
                PrintColoured.green("You have chosen to register a book");
                Book newBook = bookService.addBook(scanner);
                PrintColoured.green(String.format("Book %s successfully registered", newBook.getTitle()));
                break;
            case LIST_BOOKS:
                PrintColoured.green("You have chosen to list all available books");
                bookService.findAllBooks(true);
                break;
            case CHECKOUT_BOOK:
                PrintColoured.green("You have chosen to checkout a book");
                boolean proceededWithCheckout = checkoutService.checkoutBook(scanner);
                if (proceededWithCheckout) {
                    PrintColoured.green("Book successfully checked out");
                } else {
                    PrintColoured.red("Unsuccessful book checkout");
                }
                break;
            case RETURN_BOOK:
                PrintColoured.green("You have chosen to return a checked-out book");
                boolean proceededWithReturn = checkoutService.returnBook(scanner);
                if (proceededWithReturn) {
                    PrintColoured.green("Book successfully checked out");
                } else {
                    PrintColoured.red("Unsuccessful book checkout");
                }
                break;
            case VIEW_CHECKOUT_HISTORY:
                PrintColoured.green("You have chosen to view your checkout history");
                PrintColoured.cyan("Insert your registered name: ");
                String clientName = scanner.nextLine();
                Optional<Client> client = clientService.findClientByName(clientName);

                if (client.isEmpty()) {
                    PrintColoured.red("No client found with the specified name");
                } else {
                    checkoutService.findAllClientCheckouts(client.get().getId(), true, false);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected action number: " + chosenAction);
        }
    }
}

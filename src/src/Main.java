import java.util.Scanner;

import Author.AuthorService;
import Book.BookService;
import Checkout.CheckoutService;
import Client.ClientService;
import utils.Actions;
import utils.PrintColoured;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ClientService clientService = new ClientService();
        AuthorService authorService = new AuthorService();
        BookService bookService = new BookService();
        CheckoutService checkoutService = new CheckoutService();

        PrintColoured.cyan("\nWhat would you like to do?\n");
        showMenu();
        PrintColoured.white("\nI would like to (number):");

        Actions chosenAction = Actions.fromCode(Integer.parseInt(scanner.nextLine()));

        switch (chosenAction) {
            case ADD_CLIENT:
                PrintColoured.green("You have chosen to register a client");
                clientService.addClient(scanner);
                PrintColoured.green("Client successfully registered");
                break;
            case LIST_CLIENTS:
                PrintColoured.green("You have chosen to list all clients");
                clientService.findAllClients(true);
                break;
            case ADD_AUTHOR:
                PrintColoured.green("You have chosen to register an author");
                authorService.addAuthor(scanner);
                PrintColoured.green("Author successfully registered");
                break;
            case LIST_AUTHORS:
                PrintColoured.green("You have chosen to list all authors");
                authorService.findAllAuthors(true);
                break;
            case ADD_BOOK:
                PrintColoured.green("You have chosen to register a book");
                bookService.addBook(scanner);
                PrintColoured.green("Book successfully registered");
                break;
            case LIST_BOOKS:
                PrintColoured.green("You have chosen to list all available books");
                bookService.findAllBooks(true);
                break;
            case CHECKOUT_BOOK:
                PrintColoured.green("You have chosen to checkout a book");
                checkoutService.checkoutBook(scanner);
                PrintColoured.green("Book successfully checked out");
                break;
            case RETURN_BOOK:
                PrintColoured.green("You have chosen to return a checked-out book");
                break;
            case VIEW_CHECKOUT_HISTORY:
                PrintColoured.green("You have chosen to view your checkout history");
            default:
                throw new IllegalStateException("Unexpected action number: " + chosenAction);
        }
    }

    public static void showMenu() {
        PrintColoured.cyan(String.format("%-25s --> %d", "Register a client:", 1));
        PrintColoured.cyan(String.format("%-25s --> %d", "List all client:", 2));
        PrintColoured.cyan(String.format("%-25s --> %d", "Register an author:", 3));
        PrintColoured.cyan(String.format("%-25s --> %d", "List all authors:", 4));
        PrintColoured.cyan(String.format("%-25s --> %d", "Register a book:", 5));
        PrintColoured.cyan(String.format("%-25s --> %d", "List all books:", 6));
        PrintColoured.cyan(String.format("%-25s --> %d", "Checkout a book:", 7));
    }
}

import java.util.Scanner;

import Client.ClientService;
import utils.Actions;
import utils.PrintColoured;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ClientService clientService = new ClientService();

        PrintColoured.cyan("\nWhat would you like to do?\n");
        showMenu();
        PrintColoured.white("\nI would like to (number):");

        Actions chosenAction = Actions.fromCode(Integer.parseInt(scanner.next()));

        switch (chosenAction) {
            case ADD_CLIENT:
                PrintColoured.green("You have chosen to register a client");
                clientService.addClient(scanner);
                PrintColoured.green("Client successfully registered");
                break;
            case LIST_CLIENTS:
                PrintColoured.green("You have chosen to list all clients");
                clientService.findAllClients();
            case ADD_BOOK:
                PrintColoured.green("You have chosen to add a book");
                break;
            case LIST_BOOKS:
                PrintColoured.green("You have chosen to list all available books");
                break;
            case RETURN_BOOK:
                PrintColoured.green("You have chosen to return a checked-out book");
                break;
            case CHECKOUT_BOOK:
                PrintColoured.green("You have chosen to checkout a book");
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
    }
}

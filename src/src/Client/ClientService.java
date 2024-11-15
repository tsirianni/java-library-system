package Client;

import Client.DTOs.AddClientDTO;
import utils.PrintColoured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ClientService {
    private final Path clientsDBPath = Paths.get("src/resources/clients.csv");

    private AddClientDTO promptDTO(Scanner scanner) {
        PrintColoured.cyan("What is the client's name? ");
        String name = scanner.nextLine();

        PrintColoured.cyan("What is the client's email address? ");
        String email = scanner.nextLine();

        return new AddClientDTO(name, email);
    }

    public Client addClient(Scanner scanner) throws Exception {
        AddClientDTO data = this.promptDTO(scanner);

        List<Client> clients = this.findAllClients(false);
        Optional<Client> existingClient = clients.stream().filter(client -> Objects.equals(
                client.getEmail(),
                data.email()
        )).findFirst();

        if (existingClient.isPresent()) {
            throw new IllegalArgumentException("The is already a client registered with the provided email " +
                                                       "address");
        }

        Client newClient = new Client(UUID.randomUUID(), data.name(), data.email());
        String formattedRecord = this.formatRecord(newClient);

        try {
            Files.writeString(this.clientsDBPath, formattedRecord, StandardOpenOption.APPEND,
                              StandardOpenOption.CREATE
            );
        } catch (IOException IOEx) {
            throw new Exception("DB Error: An error has occurred while attempting to register the new client " + IOEx.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return newClient;
    }

    private String formatRecord(Client newClient) {
        return String.format("%s;%s;%s\n", newClient.getId(), newClient.getName(), newClient.getEmail());
    }

    public List<Client> findAllClients(boolean shouldConsoleClients) throws Exception {

        List<String> clientRecords;
        try {
            clientRecords = Files.readAllLines(this.clientsDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain client's records");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Client> clients = new ArrayList<>();

        if (!clientRecords.isEmpty()) {
            for (String clientRecord : clientRecords) {
                String[] clientDetails = clientRecord.split(";");
                Client client = new Client(UUID.fromString(clientDetails[0]), clientDetails[1],
                                           clientDetails[2]
                );
                clients.add(client);

                if (shouldConsoleClients) {
                    PrintColoured.green(String.format("ID.: %-30s - Name.: %-25s - Email Address.: %s", client.getId(),
                                                      client.getName(), client.getEmail()
                    ));
                }
            }
        } else {
            if (shouldConsoleClients) {
                PrintColoured.yellow("There are no clients registered");
            }
        }

        return clients;
    }

    public Optional<Client> findClientByName(String clientName) throws Exception {
        List<Client> clients = this.findAllClients(false);

        return clients.stream().filter(client1 -> Objects.equals(client1.getName(), clientName)).findFirst();
    }
}

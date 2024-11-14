package Client;

import Client.DTOs.AddClientDTO;
import utils.PrintColoured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ClientService {
    private AddClientDTO promptDTO(Scanner scanner) {
        PrintColoured.cyan("What is the client's name? ");
        String name = scanner.next();

        PrintColoured.cyan("What is the client's email address? ");
        String email = scanner.next();

        return new AddClientDTO(name, email);
    }

    public void addClient(Scanner scanner) throws Exception {
        AddClientDTO data = this.promptDTO(scanner);

        Path clientsDBPath = Paths.get("src/resources/clients.csv");
        List<String> clients;
        try {
            clients = Files.readAllLines(clientsDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain client's records");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String content = "";
        for (String client : clients) {
            String[] clientDetails = client.split(";");
            String clientEmail = clientDetails[2];
            if (Objects.equals(clientEmail, data.email())) {
                throw new IllegalArgumentException("The is already a client registered with the provided email " +
                                                           "address");
            }
            content = content.concat(client.concat("\n"));
        }

        Client newClient = new Client(data);
        String formattedRecord = this.formatRecord(newClient);
        Files.writeString(clientsDBPath, content.concat(formattedRecord));
    }

    private String formatRecord(Client newClient) {
        return String.format("%s;%s;%s\n", newClient.getId(), newClient.getName(), newClient.getEmail());
    }

    public void findAllClients() throws Exception {
        Path clientsDBPath = Paths.get("src/resources/clients.csv");
        List<String> clients;
        try {
            clients = Files.readAllLines(clientsDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain client's records");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (String client : clients) {
            String[] clientDetails = client.split(";");
            PrintColoured.green(String.format("ID.: %-30s - Name.: %-25s - Email Address.: %s", clientDetails[0],
                                              clientDetails[1], clientDetails[2]
            ));
        }
    }
}

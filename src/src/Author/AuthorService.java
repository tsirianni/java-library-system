package Author;

import Author.DTOs.AddAuthorDTO;
import utils.PrintColoured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AuthorService {
    private final Path authorsDBPath = Paths.get("src/resources/authors.csv");

    private AddAuthorDTO promptDTO(Scanner scanner) {
        PrintColoured.cyan("What is the author's name? ");
        String name = scanner.nextLine();
        PrintColoured.cyan("What is the author's date of birth (dd/MM/yyyy)? ");
        String dateOfBirth = scanner.nextLine();

        return new AddAuthorDTO(name, dateOfBirth);
    }

    public Author addAuthor(Scanner scanner) throws Exception {
        AddAuthorDTO data = this.promptDTO(scanner);

        List<Author> authors = this.findAllAuthors(false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime parsedDateTime = LocalDate.parse(data.dateOfBirth(), formatter).atStartOfDay();

        Optional<Author> existingAuthor = authors.stream().filter((author -> Objects.equals(
                author.getName(),
                data.name()
        ) && Objects.equals(author.getDateOfBirth(), parsedDateTime))).findFirst();

        if (existingAuthor.isPresent()) {
            throw new IllegalArgumentException("There is already an author registered with the provided name and date" +
                                                       " of birth");
        }


        Author newAuthor = new Author(UUID.randomUUID(), data.name(), parsedDateTime);
        String formattedAuthor = this.formatRecord(newAuthor);

        try {
            Files.writeString(this.authorsDBPath, formattedAuthor, StandardOpenOption.APPEND,
                              StandardOpenOption.CREATE
            );
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to checkout books: " + IOEx.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return newAuthor;
    }

    public List<Author> findAllAuthors(boolean shouldConsoleAuthors) throws Exception {
        List<String> authorRecords;
        try {
            authorRecords = Files.readAllLines(this.authorsDBPath);
        } catch (IOException IOEx) {
            throw new Exception("DB Error: Unable to obtain authors' records" + IOEx.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Author> authors = new ArrayList<>();
        for (String authorRecord : authorRecords) {
            String[] authorDetails = authorRecord.split(";");
            Author author = new Author(UUID.fromString(authorDetails[0]), authorDetails[1],
                                       LocalDateTime.parse(authorDetails[2])
            );

            authors.add(author);

            if (shouldConsoleAuthors) {
                PrintColoured.green(String.format("ID.: %-30s - Name.: %-25s - Date of Birth.: %td/%<tm/%<tY",
                                                  author.getId().toString(), author.getName(),
                                                  author.getDateOfBirth()
                                    )
                );
            }
        }

        return authors;
    }

    private String formatRecord(Author author) {
        return String.format("%s;%s;%s\n", author.getId(), author.getName(), author.getDateOfBirth());
    }
}

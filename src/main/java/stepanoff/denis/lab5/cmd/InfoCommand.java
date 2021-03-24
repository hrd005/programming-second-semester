package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.time.format.DateTimeFormatter;

/**
 * Implementation of 'info' command.
 */
public class InfoCommand extends Command{

    {
        this.name = "info";
        this.description = ": print information about collection.";

        this.action = (String... a) -> {
            ConsoleWriter.println(
                    "\tCollection contains " + collection.size() + " tickets. They stored as Linked List.\n" +
                            "\tInitialization Date: " +
                            collection.getInitializationDate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) +
                            "\n\tLast saved modification Date: " +
                            collection.getModificationDate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) +
                            (collection.isUnsaved() ? "\n\tHave unsaved changes." : "."));
        };
    }
}

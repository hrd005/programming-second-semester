package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.Ticket;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.util.Optional;

/**
 * Implementation of 'remove_head' command.
 */
public class RemoveHeadCommand extends Command {

    {
        this.name = "remove_head";
        this.description = ": display and remove first element of the collection.";

        this.action = (String... s) -> {
            Optional<Ticket> head = Optional.ofNullable(this.collection.pop());
            if (head.isPresent()) {
                ConsoleWriter.println(head.get().toString());
            } else {
                ConsoleWriter.println("Collection is empty.", ConsoleWriter.Color.YELLOW);
            }
        };
    }
}

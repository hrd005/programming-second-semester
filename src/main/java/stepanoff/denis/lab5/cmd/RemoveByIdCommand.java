package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.Ticket;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.util.Optional;

import static stepanoff.denis.lab5.itil.ConsoleWriter.println;

/**
 * Implementation of 'remove_by_id' command.
 */
public class RemoveByIdCommand extends ParametrisedCommand {

    {
        this.name = "remove_by_id";
        this.description = ": remove element with specified id.";
        this.params = "id";

        this.action = (String... args) -> {
            if (args.length < 2) {
               println("An id should be provided.", ConsoleWriter.Color.RED);
                return;
            }
            int id = -1;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                println("An id should have integer value.", ConsoleWriter.Color.RED);
            }
            if (id == -1) {return;} // not possible

            Optional<Ticket> ticket = this.collection.take(id);
            if (ticket.isPresent()) {
                println("Ticket (id = " + id + ") removed.", ConsoleWriter.Color.GREEN);
            } else {
                println("No element with id '" + id + "' found.", ConsoleWriter.Color.YELLOW);
            }
        };
    }
}

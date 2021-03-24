package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.Ticket;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.io.IOException;
import java.util.List;

import static stepanoff.denis.lab5.itil.ConsoleWriter.printPrompt;
import static stepanoff.denis.lab5.itil.ConsoleWriter.println;

/**
 * Implementation of 'remove_greater' command.
 */
public class RemoveGreaterCommand extends ParametrisedCommand {

    {
        this.name = "remove_greater";
        this.params = "{ element }";
        this.description = ": remove elements greater than specified (sorting by name A-Z).";

        this.action = (String... s) -> {
            String name = "";
            try {
                do {
                    printPrompt("Ticket.Name = ", ConsoleWriter.Color.YELLOW);
                    name = this.input.readLine().trim();
                    if (name.isEmpty()) println("Empty input is forbidden.", ConsoleWriter.Color.RED);
                } while (name.isEmpty());
            } catch (IOException e) {/*...*/}

            if (name.isEmpty()) println("Unsuccessful input", ConsoleWriter.Color.RED);

            String finalName = name;
            List<Ticket> toRemove = this.collection.getFiltered((Ticket t) -> t.getName().compareTo(finalName) > 0);

            if (toRemove.isEmpty()) println("Nothing to remove.");
            else {
                toRemove.forEach((Ticket t) -> this.collection.take(t.getId()));
                println(toRemove.size() + " elements removed.");
            }
        };
    }
}

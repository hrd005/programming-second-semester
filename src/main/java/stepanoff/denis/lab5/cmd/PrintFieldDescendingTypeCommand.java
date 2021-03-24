package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.Ticket;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of 'print_field_descending_type' command.
 */
public class PrintFieldDescendingTypeCommand extends Command {

    {
        this.name = "print_field_descending_type";
        this.description = ": display type of all tickets in descending order (CHEAP->BUDGETARY->USUAL->VIP).";

        this.action = (String... s) -> {
            List<Ticket> tickets = this.collection.getSortedByTypeDesc();

            if (tickets.isEmpty()) {
                ConsoleWriter.println("Nothing to show.");
            } else {
                String out = tickets.stream()
                        .map((Ticket t) ->
                                String.format("Ticket(id = %d) %s", t.getId(), t.getType().name().toLowerCase()))
                        .collect(Collectors.joining("\n"));
                ConsoleWriter.println(out);
            }
        };
    }
}

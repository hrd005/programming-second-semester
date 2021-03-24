package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.Ticket;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of 'filter_greater_than_price' command.
 */
public class FilterGreaterThanPriceCommand extends ParametrisedCommand {

    {
        this.name = "filter_greater_than_price";
        this.description = ": show elements with price greater than specified value.";
        this.params = "price";

        this.action = (String... s) -> {
            try {
                double price = Double.parseDouble(s[1]);
                List<Ticket> tickets = this.collection.getFilteredGreaterThanPrice(price);

                String output;
                if (tickets.isEmpty()) output = "Nothing to show";
                else output = tickets.stream().map(Ticket::toString).collect(Collectors.joining(",\n"));

                ConsoleWriter.println(output);

            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                ConsoleWriter.println("Value provided is not a valid Number.", ConsoleWriter.Color.RED);
            }
        };
    }
}

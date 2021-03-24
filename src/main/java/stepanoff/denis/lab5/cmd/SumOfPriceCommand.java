package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.itil.ConsoleWriter;

/**
 * Implementation of 'sum_of_price' command.
 */
public class SumOfPriceCommand extends Command {

    {
        this.name = "sum_of_price";
        this.description = ": display the sum of prices of all elements in collection";

        this.action = (String... s) -> {
            double sum = this.collection.getSumOfPrice();
            ConsoleWriter.println(String.format("The sum of prices of all elements in collection: %.2f", sum));
        };
    }
}

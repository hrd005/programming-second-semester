package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.client.Main;
import stepanoff.denis.lab5.client.net.Request;
import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.common.net.NetException;
import stepanoff.denis.lab5.common.util.ConsoleWriter;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

                Future<List<TypedEntity>> resp = this.connector.manageRequest(
                        new Request(new CommandLabel(this.name)).add(new TypedEntity(price))
                );

                while (!resp.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();

                List<TypedEntity> ret = resp.get();
                if (ret.size() == 1 && ret.get(0).getType().equals(Integer.class)) {
                    ConsoleWriter.println("Nothing to show");
                    return;
                }

                String output = ret.stream()
                        .map((TypedEntity t) -> (Ticket) t.get())
                        .map(Ticket::toString)
                        .collect(Collectors.joining(",\n"));

                ConsoleWriter.println(output);

            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                ConsoleWriter.println("Value provided is not a valid Number.", ConsoleWriter.Color.RED);
            } catch (InterruptedException e) {
                Main.provideLogger().error(e.getMessage());
                ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
            } catch (ExecutionException e) {
                if (e.getCause().getClass().equals(NetException.class)) throw (NetException) e.getCause();
                else {
                    Main.provideLogger().error(e.getMessage());
                    ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
                }
            }
        };
    }
}

package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.client.Main;
import stepanoff.denis.lab5.client.net.Request;
import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.net.NetException;
import stepanoff.denis.lab5.common.util.ConsoleWriter;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static stepanoff.denis.lab5.common.util.ConsoleWriter.println;

/**
 * Implementation of 'sum_of_price' command.
 */
public class SumOfPriceCommand extends Command {

    {
        this.name = "sum_of_price";
        this.description = ": display the sum of prices of all elements in collection";

        this.action = (String... s) -> {
//            double sum = this.collection.getSumOfPrice();

            try {

                Future<List<TypedEntity>> ret = this.connector.manageRequest(
                        new Request(Main.provideCredentials(), new CommandLabel(this.name)).add(new TypedEntity(name))
                );

                while (!ret.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();

                double sum = (double) ret.get().get(0).get();

                if (sum < 0) {
                    println("Invalid credentials", ConsoleWriter.Color.RED);
                    return;
                }

                ConsoleWriter.println(String.format("The sum of prices of all elements in collection: %.2f", sum));
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

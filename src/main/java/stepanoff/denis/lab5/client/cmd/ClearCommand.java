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
 * <p>Implementation of 'clear' command.</p>
 */
public class ClearCommand extends Command {

    {
        this.name = "clear";
        this.description = ": removes all elements in collection.";

        this.action = (String... a) -> {
            try {
                Future<List<TypedEntity>> resp = this.connector.manageRequest(
                        new Request(Main.provideCredentials(), new CommandLabel(this.name))
                );

                while (!resp.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();
                if ((Integer) resp.get().get(0).get() == 1)
                    println("Collection cleared", ConsoleWriter.Color.GREEN);
                else if ((Integer) resp.get().get(0).get() == 5)
                    println("Invalid credentials", ConsoleWriter.Color.RED);
                else
                    println("Server hadn't held request correctly", ConsoleWriter.Color.RED);
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
        }; //this.collection.clear();
    }
}

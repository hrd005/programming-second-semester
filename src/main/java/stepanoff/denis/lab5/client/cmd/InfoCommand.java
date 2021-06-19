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
 * Implementation of 'info' command.
 */
public class InfoCommand extends Command{

    {
        this.name = "info";
        this.description = ": print information about collection.";

        this.action = (String... a) -> {
//            ConsoleWriter.println(
//                    "\tCollection contains " + collection.size() + " tickets. They stored as Linked List.\n" +
//                            "\tInitialization Date: " +
//                            collection.getInitializationDate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) +
//                            "\n\tLast saved modification Date: " +
//                            collection.getModificationDate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) +
//                            (collection.isUnsaved() ? "\n\tHave unsaved changes." : "."));

            try {
                Future<List<TypedEntity>> resp = this.connector.manageRequest(
                        new Request(Main.provideCredentials(), new CommandLabel(this.name))
                );
                while (!resp.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();

                if (resp.get().get(0).getType().equals(Integer.class) && (Integer) resp.get().get(0).get() == 5)
                    println("Invalid credentials", ConsoleWriter.Color.RED);

                ConsoleWriter.println((String) resp.get().get(0).get());
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

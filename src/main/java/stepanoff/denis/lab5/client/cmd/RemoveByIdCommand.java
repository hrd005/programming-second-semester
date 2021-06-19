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

import static stepanoff.denis.lab5.common.util.ConsoleWriter.println;

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
//
//            Optional<Ticket> ticket = this.collection.take(id);
//            if (ticket.isPresent()) {
//                println("Ticket (id = " + id + ") removed.", ConsoleWriter.Color.GREEN);
//            } else {
//                println("No element with id '" + id + "' found.", ConsoleWriter.Color.YELLOW);
//            }

            try {
                Future<List<TypedEntity>> resp = this.connector.manageRequest(
                        new Request(Main.provideCredentials(), new CommandLabel(this.name)).add(new TypedEntity(id))
                );

                while (!resp.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();

                if (resp.get().get(0).getType().equals(Ticket.class)) {
                    ConsoleWriter.println("Ticket deleted", ConsoleWriter.Color.GREEN);
                } else {
                    if ((Integer) resp.get().get(0).get() == 5) {
                        println("Invalid credentials", ConsoleWriter.Color.RED);
                        return;
                    }
                    ConsoleWriter.println("Server haven't help request correctly or such ticket is not found", ConsoleWriter.Color.RED);
                }
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

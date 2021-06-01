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

/**
 * Implementation of 'remove_head' command.
 */
public class RemoveHeadCommand extends Command {

    {
        this.name = "remove_head";
        this.description = ": display and remove first element (sorting by name alphabetically) of the collection.";

        this.action = (String... s) -> {
//            Optional<Ticket> head = Optional.ofNullable(this.collection.pop());
//            if (head.isPresent()) {
//                ConsoleWriter.println(head.get().toString());
//            } else {
//                ConsoleWriter.println("Collection is empty.", ConsoleWriter.Color.YELLOW);
//            }

            try {
                Future<List<TypedEntity>> resp = this.connector.manageRequest(
                        new Request(new CommandLabel(this.name))
                );

                while (!resp.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();

                TypedEntity ret = resp.get().get(0);

                if (ret.getType().equals(Ticket.class))
                    ConsoleWriter.println(ret.get().toString());
                else
                    ConsoleWriter.println("Collection is empty.", ConsoleWriter.Color.YELLOW);

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

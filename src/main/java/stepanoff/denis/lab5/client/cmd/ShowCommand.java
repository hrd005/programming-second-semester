package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.client.Main;
import stepanoff.denis.lab5.client.net.Request;
import stepanoff.denis.lab5.client.net.ServerConnector;
import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.common.net.NetException;
import stepanoff.denis.lab5.common.util.ConsoleWriter;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Implementation of 'show' command.
 */
public class ShowCommand extends Command {

    {
        this.name = "show";
        this.description = ": print all elements in collection.";

        this.action = (String... a) -> {
            try {
                Future<List<TypedEntity>> resp = new ServerConnector().manageRequest(
                        new Request(new CommandLabel(this.name))
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

                ret.stream()
                        .map((TypedEntity e) -> (Ticket) e.get())
                        .map(Ticket::toString)
                        .forEach(ConsoleWriter::println);

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
        }; //ConsoleWriter.println(collection.toString());
    }
}

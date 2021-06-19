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

import static stepanoff.denis.lab5.common.util.ConsoleWriter.printPrompt;
import static stepanoff.denis.lab5.common.util.ConsoleWriter.println;

/**
 * Implementation of 'remove_greater' command.
 */
public class RemoveGreaterCommand extends ParametrisedCommand {

    {
        this.name = "remove_greater";
        this.params = "{ element }";
        this.description = ": remove elements greater than specified (sorting by name A-Z).";

        this.action = (String... s) -> {
            String name = "";
            try {
                do {
                    printPrompt("Ticket.Name = ", ConsoleWriter.Color.YELLOW);
                    name = this.input.readLine().trim();
                    if (name.isEmpty()) println("Empty input is forbidden.", ConsoleWriter.Color.RED);
                } while (name.isEmpty());
            } catch (IOException e) {/*...*/}

            if (name.isEmpty()) println("Unsuccessful input", ConsoleWriter.Color.RED);
//
//            String finalName = name;
//            List<Ticket> toRemove = this.collection.getFiltered((Ticket t) -> t.getName().compareTo(finalName) > 0);
//
//            if (toRemove.isEmpty()) println("Nothing to remove.");
//            else {
//                toRemove.forEach((Ticket t) -> this.collection.take(t.getId()));
//                println(toRemove.size() + " elements removed.");
//            }

            try {
                Future<List<TypedEntity>> ret = this.connector.manageRequest(
                        new Request(Main.provideCredentials(), new CommandLabel(this.name)).add(new TypedEntity(name))
                );

                while (!ret.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();

                int remCol = (int) ret.get().get(0).get();

                if (remCol < 0) println("Invalid credentials", ConsoleWriter.Color.RED);
                else println(remCol + " elements removed.");
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

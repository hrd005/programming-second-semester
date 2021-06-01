package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.client.Main;
import stepanoff.denis.lab5.client.net.Request;
import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.common.net.NetException;
import stepanoff.denis.lab5.common.util.ConsoleWriter;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Implementation of 'print_field_descending_type' command.
 */
public class PrintFieldDescendingTypeCommand extends Command {

    {
        this.name = "print_field_descending_type";
        this.description = ": display type of all tickets in descending order (CHEAP->BUDGETARY->USUAL->VIP).";

        this.action = (String... s) -> {
//            List<Ticket> tickets = this.collection.getSortedByTypeDesc();
//
//            if (tickets.isEmpty()) {
//                ConsoleWriter.println("Nothing to show.");
//            } else {
//                String out = tickets.stream()
//                        .map((Ticket t) ->
//                                String.format("Ticket(id = %d) %s", t.getId(), t.getType().name().toLowerCase()))
//                        .collect(Collectors.joining("\n"));
//            }
            try {
                Future<List<TypedEntity>> resp = this.connector.manageRequest(
                        new Request(new CommandLabel("show"))
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

                String out = ret.stream()
                        .map((TypedEntity t) -> (Ticket) t.get())
                        .sorted(Comparator.comparing(Ticket::getType))
                        .map((Ticket t) ->
                                String.format("Ticket(id = %d) %s", t.getId(), t.getType().name().toLowerCase()))
                        .collect(Collectors.joining("\n"));
                ConsoleWriter.println(out);

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
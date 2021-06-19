package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.client.Main;
import stepanoff.denis.lab5.client.net.Request;
import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.data.*;
import stepanoff.denis.lab5.common.net.NetException;
import stepanoff.denis.lab5.common.util.ConsoleWriter;
import stepanoff.denis.lab5.common.util.TypedEntity;
import stepanoff.denis.lab5.server.dataio.file.InvalidFileException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static stepanoff.denis.lab5.common.util.ConsoleWriter.getColored;
import static stepanoff.denis.lab5.common.util.ConsoleWriter.println;

/**
 * <p>Implementation of 'add_if_max' command.</p>
 */
public class AddIfMaxCommand extends AddCommand {

    {
        this.name = "add_if_max";
        this.description = ": add element if the specified element is greater than maximal element in collection.";

        this.action = (String... a) -> {

            Ticket.Builder tBuilder = Ticket.newTicket();
            Coordinates.Builder cBuilder = new Coordinates.Builder();
            Venue.Builder vBuilder = Venue.newVenue();
            try {
                String name = this.getValidPrompt("Name = ", tBuilder::name);
                String xCoord = this.getValidPrompt("Coordinates.X = ", cBuilder::x);
                String yCoord =  this.getValidPrompt("Coordinates.Y = ", cBuilder::y);
                String price = this.getValidPrompt("Price = ", tBuilder::price);
                String type = this.getValidPrompt(getColored("Type", ConsoleWriter.Color.GREEN) + " (" +
                                Stream.of(TicketType.values())
                                        .map((TicketType s) -> getColored(s.name().toLowerCase(), ConsoleWriter.Color.CYAN))
                                        .collect(Collectors.joining(" | "))
                                + ") " + getColored("= ", ConsoleWriter.Color.GREEN),
                        tBuilder::ticketType
                );
                String venueName = this.getValidPrompt("Venue.Name = ", vBuilder::name);
                String venueCapacity = this.getValidPrompt("Venue.Capacity = ", vBuilder::capacity);
                String venueType = this.getValidPrompt(getColored("Venue.Type", ConsoleWriter.Color.GREEN) + " (" +
                                Stream.of(VenueType.values())
                                        .map((VenueType s) -> getColored(s.name().toLowerCase(), ConsoleWriter.Color.CYAN))
                                        .collect(Collectors.joining(" | "))
                                + ") " + getColored("= ", ConsoleWriter.Color.GREEN),
                        vBuilder::venueType);

                Ticket _new = tBuilder
                        .name(name)
                        .coordinates(
                                cBuilder
                                        .x(xCoord)
                                        .y(yCoord)
                        )
                        .price(price)
                        .ticketType(type)
                        .venue(
                                vBuilder
                                        .name(venueName)
                                        .capacity(venueCapacity)
                                        .venueType(venueType)
                        ).build();

                Future<List<TypedEntity>> ret = connector.manageRequest(
                        new Request(
                                Main.provideCredentials(),
                                new CommandLabel(this.name)
                        ).add(new TypedEntity(_new))
                );

                while (!ret.isDone()) {
                    Thread.sleep(50);
                    System.out.print('.');
                }
                System.out.println();
                int ret_status = (Integer) ret.get().get(0).get();
                if (ret_status == 1)
                    println("Ticket added", ConsoleWriter.Color.GREEN);
                else if (ret_status == 2)
                    println("Ticket not added (not maximal ticket in collection)", ConsoleWriter.Color.YELLOW);
                else if ((Integer) ret.get().get(0).get() == 5)
                    println("Invalid credentials", ConsoleWriter.Color.RED);
                else
                    println("Server hadn't held request correctly", ConsoleWriter.Color.RED);

            } catch (IOException e) {
                Main.provideLogger().warn(e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new InvalidFileException("Illegal arguments for " + this.name + " provided.");
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

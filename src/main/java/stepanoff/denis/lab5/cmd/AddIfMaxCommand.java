package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.*;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static stepanoff.denis.lab5.itil.ConsoleWriter.getColored;
import static stepanoff.denis.lab5.itil.ConsoleWriter.println;

/**
 * <p>Implementation of 'add_if_max' command.</p>
 */
public class AddIfMaxCommand extends AddCommand {

    {
        this.name = "add_if_max";
        this.description = ": add element if the specified element is greater than maximal element in collection.";

        this.action = (String... a) -> {
            Optional<Ticket> max = this.collection.getMax();

            Ticket.Builder tBuilder = Ticket.newTicket(this.collection.getTicketIdManager());
            String name = "";
            try {
                name = this.getValidPrompt("Name = ", tBuilder::name);
            } catch (IOException e) { /*...*/ }

            boolean shouldAdd = max.isPresent() & !name.isEmpty() ? name.compareTo(max.get().getName()) > 0 : !name.isEmpty();

            if (shouldAdd) {
                try {
                    Coordinates.Builder cBuilder = new Coordinates.Builder();
                    Venue.Builder vBuilder = Venue.newVenue(this.collection.getVenueIdManager());
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

                    this.collection.add(
                            tBuilder.name(name)
                                    .coordinates(
                                            cBuilder.x(xCoord)
                                                    .y(yCoord)
                                    )
                                    .price(price)
                                    .ticketType(type)
                                    .venue(
                                            vBuilder.name(venueName)
                                                    .capacity(venueCapacity)
                                                    .venueType(venueType)
                                    )
                                    .build()
                    );

                    println("Ticket added", ConsoleWriter.Color.GREEN);
                } catch (IOException e) { /*...*/ }
            } else {
                println("No sense to continue input -- element will not be added.");
            }
        };
    }
}

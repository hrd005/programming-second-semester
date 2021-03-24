package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.*;
import stepanoff.denis.lab5.io.InvalidFileException;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static stepanoff.denis.lab5.itil.ConsoleWriter.*;

/**
 * <p>Implementation of 'add' command.</p>
 */
public class AddCommand extends ParametrisedCommand {

    {
        this.name = "add";
        this.description = ": add new element to collection.";
        this.params = "{ element }";

        action = (String... a) -> {
            Ticket.Builder tBuilder = Ticket.newTicket(this.collection.getTicketIdManager());
            Coordinates.Builder cBuilder = new Coordinates.Builder();
            Venue.Builder vBuilder = Venue.newVenue(this.collection.getVenueIdManager());
            try {
                String name = this.getValidPrompt("Name = ", tBuilder::name);
                String xCoord = this.getValidPrompt("Coordinates.X = ", cBuilder::x);
                String yCoord =  this.getValidPrompt("Coordinates.Y = ", cBuilder::y);
                String price = this.getValidPrompt("Price = ", tBuilder::price);
                String type = this.getValidPrompt(getColored("Type", Color.GREEN) + " (" +
                        Stream.of(TicketType.values())
                                .map((TicketType s) -> getColored(s.name().toLowerCase(), Color.CYAN))
                                .collect(Collectors.joining(" | "))
                                + ") " + getColored("= ", Color.GREEN),
                        tBuilder::ticketType
                );
                String venueName = this.getValidPrompt("Venue.Name = ", vBuilder::name);
                String venueCapacity = this.getValidPrompt("Venue.Capacity = ", vBuilder::capacity);
                String venueType = this.getValidPrompt(getColored("Venue.Type", Color.GREEN) + " (" +
                        Stream.of(VenueType.values())
                                .map((VenueType s) -> getColored(s.name().toLowerCase(), Color.CYAN))
                                .collect(Collectors.joining(" | "))
                        + ") " + getColored("= ", Color.GREEN),
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

                println("Ticket added", Color.GREEN);

            } catch (IOException ignored) {}
            catch (IllegalArgumentException e) {
                throw new InvalidFileException("Illegal arguments for " + this.name + " provided.");
            }
        };
    }

    /**
     *
     * @param prompt Text that should be printed as input invitation
     * @param consumer A function which validates user input and throws either NumberFormatException
     *                 for not number input, either IllegalArgumentException in other cases.
     * @return Validated input
     * @throws IOException in case of reading this.input cause IOException
     */
    protected String getValidPrompt(String prompt, Consumer<String> consumer) throws IOException {
        String s;

        if (repeatedInput) {
            do {
                printPrompt(prompt, Color.GREEN);
                s = this.input.readLine().trim();
                try {
                    consumer.accept(s);
                } catch (NumberFormatException e) {
                    println("\tInput should be a valid number.", Color.RED);
                    s = null;
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().startsWith("No enum"))
                        println("\tNo such constant. Please, enter one of suggested.", Color.RED);
                    else println("\t" + e.getMessage(), Color.RED);
                    s = null;
                }
            } while (s == null);

            return s;
        } else {
            return ((s = this.input.readLine()) != null ? s : "").trim();
        }
    }
}

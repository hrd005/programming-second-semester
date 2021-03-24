package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.*;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static stepanoff.denis.lab5.itil.ConsoleWriter.*;

/**
 * Implementation of 'update' command.
 */
public class UpdateCommand extends ParametrisedCommand {

    {
        this.name = "update";
        this.description = ": update element with a specified id.";
        this.params = "id { element }";

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

            Optional<Ticket> ticket = this.collection.look(id);
            ticket.ifPresent((Ticket t) -> {

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

                    tBuilder = t.builder();
                    if (!name.isEmpty()) tBuilder.name(name);
                    if (!(xCoord.isEmpty() && yCoord.isEmpty())) {
                        double x = xCoord.isEmpty()? t.getCoordinates().getX() : Double.parseDouble(xCoord);
                        double y = yCoord.isEmpty()? t.getCoordinates().getY() : Double.parseDouble(yCoord);
                        tBuilder.coordinates(x, y);
                    }
                    if (!price.isEmpty()) tBuilder.price(price);
                    if (!type.isEmpty()) tBuilder.ticketType(type);
                    if (!(venueName.isEmpty() && venueCapacity.isEmpty() && venueType.isEmpty())) {
                        vBuilder = t.getVenue().builder();
                        if (!venueName.isEmpty()) vBuilder.name(venueName);
                        if (!venueCapacity.isEmpty()) vBuilder.capacity(venueCapacity);
                        if (!venueType.isEmpty()) vBuilder.venueType(venueType);

                        tBuilder.venue(vBuilder);
                    }

                    if (!t.equals(tBuilder.build())) {
                        this.collection.take(t.getId());
                        this.collection.add(tBuilder.build());
                        println("Ticket updated", Color.GREEN);
                    } else {
                        println("Nothing to update", Color.GREEN);
                    }

                } catch (IOException ignored) {}
            });

            if (!ticket.isPresent()) {
                println("No element with id '" + id + "' found.", Color.YELLOW);
            }
        };
    }

    /**
     * @param prompt a text should be printed as invitation to user to input
     * @param consumer a validation function
     * @see AddCommand for full explanation of validation function
     * @return valid input or empty string.
     * @throws IOException in case this.input throws IOException.
     */
    private String getValidPrompt(String prompt, Consumer<String> consumer) throws IOException {
        String s;

        if (repeatedInput) {
            do {
                printPrompt(prompt, Color.GREEN);
                s = this.input.readLine().trim();

                if (s.isEmpty()) {
                    println("\033[F\033[" + prompt.length() + "C(assuming not changed)", Color.CYAN);
                    return s;
                }

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
        } else return this.input.readLine().trim();
    }
}

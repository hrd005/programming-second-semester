package stepanoff.denis.lab5.server;

import org.slf4j.Logger;
import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.server.dataio.FileIO;
import stepanoff.denis.lab5.common.dataio.FileReadingException;
import stepanoff.denis.lab5.common.util.ConsoleWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class represents the collection the program operates and provides some API to efficiently write commands.
 */
public class Collection {

    private final Logger logger = Main.provideLogger();

    private final LinkedList<Ticket> list = new LinkedList<>();
    private File file;
    private LocalDate initializationDate = null;
    private LocalDate modificationDate = null;

    private boolean isUnsaved = false;

    /**
     * @param file File where Collection data is located.
     */
    public Collection(File file) {

        this.file = file;
        Path path = file.toPath();
        BasicFileAttributes attributes = null;
        try {
            attributes = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            logger.warn("Failed to read Collection info -- " + e.getMessage());
        }

        if (attributes != null) {
            this.initializationDate = LocalDate.from(attributes.creationTime().toInstant().atZone(ZoneId.systemDefault()));
            this.modificationDate = LocalDate.from(attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()));
        }
    }

    /**
     * Add element to Collection
     * Collection will become unsaved
     * @param ticket element
     */
    public void add(Ticket ticket) {
        this.add(ticket, false);
    }

    /**
     * Add element to Collection
     * @param ticket element
     * @param silent true -- collection will not become unsaved
     *               false -- collection will become unsaved.
     */
    public void add(Ticket ticket, boolean silent) {
        //if (!silent) this.isUnsaved = true;

        if (!this.getTicketIdManager().isIdAppropriate(ticket.getId())) {
            logger.warn("Doubled Ticket ID " + ticket.getId() + ". Element ignored.");
            ConsoleWriter.println("Specified Ticket ID is already present.\n\tElement is not added.", ConsoleWriter.Color.RED);
            return;
        }

        if (!this.getVenueIdManager().isIdAppropriate(ticket.getVenue().getId())) {
            logger.warn("Doubled Venue ID " + ticket.getVenue().getId() + ". Element ignored.");
            ConsoleWriter.println("Specified Venue ID is already present. \n\tElement is not added.", ConsoleWriter.Color.RED);
            return;
        }
        if (!silent) {
            this.isUnsaved = true;
            logger.info("Element added.");
        }
        this.list.add(ticket);
    }

    /**
     * Get and remove element
     * @param id id of Ticket (element)
     * @return Optional Ticket with specified id.
     */
    public Optional<Ticket> take(int id) {
        Optional<Ticket> requested = this.list.stream().filter((Ticket t) -> t.getId() == id).findFirst();
        requested.ifPresent((Ticket t) -> {
            this.isUnsaved = true;
            this.list.remove(t);
            logger.info("Element removed.");
        });

        return requested;
    }

    /**
     * Get but not remove element
     * @param id id of Ticket (element)
     * @return Optional Ticket with specified id.
     */
    public Optional<Ticket> look(int id) {
        return this.list.stream().filter((Ticket t) -> t.getId() == id).findFirst();
    }

    /**
     * Get and remove first element. Equivalent to take(0)
     * @return Optional Ticket
     */
    public Optional<Ticket> pop() {
        this.isUnsaved = !this.list.isEmpty() || this.isUnsaved;
        if (this.isUnsaved) logger.info("Element removed.");
        return this.list.stream().sorted().findFirst();
    }

    /**
     * Get Maximal element
     * @return Optional Ticket
     */
    public Optional<Ticket> getMax() {
        return this.list.stream().max(Comparator.naturalOrder());
    }

    /**
     * Remove all elements
     */
    public void clear() {
        this.isUnsaved = !this.list.isEmpty() || this.isUnsaved;
        this.list.clear();
        logger.info("Collection cleared.");
    }

    /**
     * @return sum of prices of all Tickets in collection
     */
    public double getSumOfPrice() {
        return this.list.stream().mapToDouble(Ticket::getPrice).sum();
    }

    /**
     * Get List of filtered elements
     * @param predicate filter
     * @return Filtered List
     */
    public List<Ticket> getFiltered(Predicate<Ticket> predicate) {
        return this.list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Shortcut for command. Filters element greater than specified price
     * @param price price
     * @return Filtered List
     */
    public List<Ticket> getFilteredGreaterThanPrice(double price) {
        return this.getFiltered((Ticket t) -> t.getPrice() > price);
    }

    /**
     * Get List of elements of collection sorted.
     * @param comparator comparing function
     * @return Sorted List
     */
    public List<Ticket> getSorted(Comparator<Ticket> comparator) {
        return this.list.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Shortcut for command. Sort elements by Ticket.type in enum ordinal.
     * @return Sorted List
     */
    public List<Ticket> getSortedByTypeDesc() {
        return this.getSorted(Comparator.comparing(Ticket::getType));
    }

    /**
     * @return Time when file with collection data was created
     */
    public LocalDate getInitializationDate() {
        return this.initializationDate;
    }

    /**
     * @return Time when file with collection data was modified last time
     */
    public LocalDate getModificationDate() {
        return this.modificationDate;
    }

    /**
     * @return number of elements in collection
     */
    public int size() {
        return this.list.size();
    }

    /**
     * @return status of changes
     */
    public boolean isUnsaved() {
        return isUnsaved;
    }

    /**
     * @return IdManager for Tickets
     * @see IdManager
     */
    public IdManager getTicketIdManager() {
        return new IdManager(this.list.stream().mapToLong(Ticket::getId));
    }

    /**
     * @return IdManager for Venues
     * @see IdManager
     */
    public IdManager getVenueIdManager() {
        return new IdManager(this.list.stream().mapToLong((Ticket t) -> t.getVenue().getId()));
    }

    /**
     * @return File where collection data supposed to be stored.
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Convert collection to plain List
     * @return LinkedList containing all elements of collection
     */
    public LinkedList<Ticket> asList() {
        return this.list;
    }

    /**
     * Update collection from specified File
     * @param file new file
     * @throws FileReadingException if not possible to read file correctly.
     */
    public void reload(File file) throws FileReadingException {
        Collection newCol = FileIO.getReader().parseFile(file.getAbsolutePath());
        this.clear();
        this.list.addAll(newCol.asList());
        this.file = newCol.getFile();
        this.initializationDate = newCol.initializationDate;
        this.modificationDate = newCol.modificationDate;
        this.isUnsaved = false;
    }

    /**
     * Make a copy of collection
     * @return the same collection
     */
    public Collection copy() {
        Collection newCol = new Collection(this.file);
        this.list.forEach((Ticket t) -> newCol.list.add(t.copy()));
        newCol.initializationDate = this.initializationDate;
        newCol.modificationDate = this.modificationDate;
        newCol.isUnsaved = this.isUnsaved;

        return newCol;
    }

    /**
     * @return String representation
     */
    @Override
    public String toString() {
        return this.list.isEmpty()? "Nothing to show"
                : this.list.stream().map(Ticket::toString).collect(Collectors.joining(",\n"));
    }
}

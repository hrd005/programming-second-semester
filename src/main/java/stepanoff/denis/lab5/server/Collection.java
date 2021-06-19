package stepanoff.denis.lab5.server;

import org.slf4j.Logger;
import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.server.dataio.DataSource;
import stepanoff.denis.lab5.server.dataio.file.FileIO;
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
    private final DataSource source;
//    private LocalDate initializationDate = null;
//    private LocalDate modificationDate = null;

    private boolean isUnsaved = false;

    public Collection(DataSource source) {
        this.source = source;
    }

    /**
     * Add element to Collection
     * Collection will become unsaved
     * @param ticket element
     * @return whether element added or not
     */
    public boolean add(Ticket ticket) {
        return this.add(ticket, false);
    }

    /**
     * Add element to Collection
     * @param ticket element
     * @param silent true -- collection will not become unsaved
     *               false -- collection will become unsaved.
     * @return whether element added or not
     */
    public synchronized boolean add(Ticket ticket, boolean silent) {
        //if (!silent) this.isUnsaved = true;

//        if (!this.getTicketIdManager().isIdAppropriate(ticket.getId())) {
//            logger.warn("Doubled Ticket ID " + ticket.getId() + ". Element ignored.");
//            ConsoleWriter.println("Specified Ticket ID is already present.\n\tElement is not added.", ConsoleWriter.Color.RED);
//            return false;
//        }
//
//        if (!this.getVenueIdManager().isIdAppropriate(ticket.getVenue().getId())) {
//            logger.warn("Doubled Venue ID " + ticket.getVenue().getId() + ". Element ignored.");
//            ConsoleWriter.println("Specified Venue ID is already present. \n\tElement is not added.", ConsoleWriter.Color.RED);
//            return false;
//        }

        if (this.source.isCommitNewSupported() && !silent) {
            Ticket toAdd = this.source.commitNew(ticket);
            if (toAdd == null) return false;
            this.list.add(toAdd);
        } else {
            this.list.add(ticket);
        }

        if (!silent) {
            this.isUnsaved = true;
            logger.info("Element added.");
        }
        return true;
    }

    /**
     * Get and remove element
     * @param id id of Ticket (element)
     * @return Optional Ticket with specified id.
     */
    public synchronized Optional<Ticket> take(int id) {
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
    public synchronized Optional<Ticket> look(int id) {
        return this.list.stream().filter((Ticket t) -> t.getId() == id).findFirst();
    }

    /**
     * Get and remove first element. Equivalent to take(0)
     * @return Optional Ticket
     */
    public synchronized Optional<Ticket> pop() {
        this.isUnsaved = !this.list.isEmpty() || this.isUnsaved;
        if (this.isUnsaved) logger.info("Element removed.");
        return this.list.stream().sorted().findFirst();
    }

    /**
     * Get Maximal element
     * @return Optional Ticket
     */
    public synchronized Optional<Ticket> getMax() {
        return this.list.stream().max(Comparator.naturalOrder());
    }

    /**
     * Remove all elements
     */
    public synchronized void clear(int uid) {

        List<Ticket> toRem = this.list.stream()
                .filter((Ticket t) -> t.getOwnerId() == uid)
                .collect(Collectors.toList());

        this.isUnsaved = this.isUnsaved || !toRem.isEmpty();

        this.list.removeAll(toRem);

        logger.info("Your elements in collection removed.");
    }

    private synchronized void clear() {
        this.list.clear();
    }

    /**
     * @return sum of prices of all Tickets in collection
     */
    public synchronized double getSumOfPrice() {
        return this.list.stream().mapToDouble(Ticket::getPrice).sum();
    }

    /**
     * Get List of filtered elements
     * @param predicate filter
     * @return Filtered List
     */
    public synchronized List<Ticket> getFiltered(Predicate<Ticket> predicate) {
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
    public synchronized List<Ticket> getSorted(Comparator<Ticket> comparator) {
        return this.list.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Shortcut for command. Sort elements by Ticket.type in enum ordinal.
     * @return Sorted List
     */
    public List<Ticket> getSortedByTypeDesc() {
        return this.getSorted(Comparator.comparing(Ticket::getType));
    }

//    /**
//     * @return Time when file with collection data was created
//     */
//    public LocalDate getInitializationDate() {
//        return this.initializationDate;
//    }

//    /**
//     * @return Time when file with collection data was modified last time
//     */
//    public LocalDate getModificationDate() {
//        return this.modificationDate;
//    }

    /**
     * @return number of elements in collection
     */
    public synchronized int size() {
        return this.list.size();
    }

    /**
     * @return status of changes
     */
    public synchronized boolean isUnsaved() {
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

//    /**
//     * @return File where collection data supposed to be stored.
//     */
//    public File getFile() {
//        return this.file;
//    }

    /**
     * Convert collection to plain List
     * @return LinkedList containing all elements of collection
     */
    public synchronized LinkedList<Ticket> asList() {
        return this.list;
    }

//    /**
//     * Update collection from specified File
//     * @param file new file
//     * @throws FileReadingException if not possible to read file correctly.
//     */
    public synchronized void reload() {
        Collection newCol = this.source.readAll();
        this.clear();
        this.list.addAll(newCol.asList());
//        this.file = newCol.getFile();
//        this.initializationDate = newCol.initializationDate;
//        this.modificationDate = newCol.modificationDate;
        this.isUnsaved = false;
    }

    /**
     * Make a copy of collection
     * @return the same collection
     */
    public synchronized Collection copy() {
        Collection newCol = new Collection(this.source);
        this.list.forEach((Ticket t) -> newCol.list.add(t.copy()));
//        newCol.initializationDate = this.initializationDate;
//        newCol.modificationDate = this.modificationDate;
        newCol.isUnsaved = this.isUnsaved;

        return newCol;
    }

    /**
     * @return String representation
     */
    @Override
    public synchronized String toString() {
        return this.list.isEmpty()? "Nothing to show"
                : this.list.stream().map(Ticket::toString).collect(Collectors.joining(",\n"));
    }
}

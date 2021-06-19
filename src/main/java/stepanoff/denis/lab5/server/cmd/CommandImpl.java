package stepanoff.denis.lab5.server.cmd;

import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.common.data.Venue;
import stepanoff.denis.lab5.common.util.TypedEntity;
import stepanoff.denis.lab5.server.dataio.DataSourceFactory;
import stepanoff.denis.lab5.server.dataio.SecurityManager;
import stepanoff.denis.lab5.server.dataio.SecurityManagerFactory;
import stepanoff.denis.lab5.server.dataio.file.FileIO;
import stepanoff.denis.lab5.server.dataio.file.FileWriter;
import stepanoff.denis.lab5.server.dataio.file.FileWritingException;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In this class you can find definitions of all commands (request) client can send.
 */
public class CommandImpl extends CommandSet {

    {
        this.registerCommand(
                new CommandLabel("show"),
                (collection, argument, auth) -> {
                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(5));
                        return ret;
                    }

                    if (argument == null) return collection.asList()
                            .stream()
                            .map(TypedEntity::new)
                            .collect(Collectors.toCollection(LinkedList::new));
                    else {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        Optional<Ticket> t = collection.look((Integer) argument.getValue());
                        if (t.isPresent())
                            ret.add(new TypedEntity(t.get()));
                        else ret.add(new TypedEntity(0));

                        return ret;
                    }
                }
        );

        this.registerCommand( new Command(new CommandLabel("add")) {{
            this.action = (collection, argument, auth) -> {

                SecurityManager sm = SecurityManagerFactory.getDefault();
                int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                if (uid <= 0) {
                    LinkedList<TypedEntity> ret = new LinkedList<>();
                    ret.add(new TypedEntity(5));
                    return ret;
                }

                Ticket recvd = (Ticket) argument.getValue();

                Ticket valid = Ticket.existingTicket(
                        -1,
                        recvd.getCreationDate())
                        .coordinates(recvd.getCoordinates())
                        .name(recvd.getName())
                        .price(recvd.getPrice())
                        .ticketType(recvd.getType())
                        .venue(Venue.existing(
                                -1)
                                .capacity(recvd.getVenue().getCapacity())
                                .name(recvd.getVenue().getName())
                                .venueType(recvd.getVenue().getType())
                                .build())
                        .owner(uid)
                        .build();

                boolean isAdd = collection.add(valid);

                LinkedList<TypedEntity> ret = new LinkedList<>();
                ret.add(new TypedEntity(isAdd ? 1 : 0));
                return ret;
            };
        }});

        this.registerCommand( new Command(new CommandLabel("add_if_max")) {{
            this.action = (collection, argument, auth) -> {

                SecurityManager sm = SecurityManagerFactory.getDefault();
                int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                if (uid <= 0) {
                    LinkedList<TypedEntity> ret = new LinkedList<>();
                    ret.add(new TypedEntity(5));
                    return ret;
                }

                LinkedList<TypedEntity> ret = new LinkedList<>();

                Ticket recvd = (Ticket) argument.getValue();

                Optional<Ticket> max = collection.getMax();
                if (max.isPresent() && recvd.getName().compareTo(max.get().getName()) > 0) {
                    ret.add(new TypedEntity(2));
                    return ret;
                }

                Ticket valid = Ticket.existingTicket(
                        -1,
                        recvd.getCreationDate())
                        .coordinates(recvd.getCoordinates())
                        .name(recvd.getName())
                        .price(recvd.getPrice())
                        .ticketType(recvd.getType())
                        .venue(Venue.existing(
                                -1)
                                .capacity(recvd.getVenue().getCapacity())
                                .name(recvd.getVenue().getName())
                                .venueType(recvd.getVenue().getType())
                                .build())
                        .owner(uid)
                        .build();

                boolean isAdd = collection.add(valid);

                ret.add(new TypedEntity(isAdd ? 1 : 0));
                return ret;
            };
        }});

        this.registerCommand(
                new CommandLabel("clear"),
                (collection, argument, auth) -> {

                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(5));
                        return ret;
                    }

                    collection.clear(uid);

                    LinkedList<TypedEntity> ret = new LinkedList<>();
                    ret.add(new TypedEntity(1));
                    return ret;
                }
        );

        this.registerCommand(
                new CommandLabel("filter_greater_than_price"),
                (collection, argument, auth) -> {
                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(5));
                        return ret;
                    }

                    return collection
                            .getFilteredGreaterThanPrice((Double) argument.getValue())
                            .stream()
                            .map(TypedEntity::new)
                            .collect(Collectors.toCollection(LinkedList::new));
                }
        );

        this.registerCommand(
                new CommandLabel("info"),
                (collection, argument, auth) -> {

                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(5));
                        return ret;
                    }

                    LinkedList<TypedEntity> ret = new LinkedList<>();

                    String s = "\tCollection contains " + collection.size() + " tickets. They stored as Linked List.\n" +
//                            "\tInitialization Date: " +
//                            collection.getInitializationDate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) +
//                            "\n\tLast saved modification Date: " +
//                            collection.getModificationDate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) +
                            (collection.isUnsaved() ? "\n\tHave unsaved changes." : "");

                    ret.add(new TypedEntity(s));
                    return ret;
                }
        );

        this.registerCommand(
                new CommandLabel("remove_by_id"),
                (collection, argument, auth) -> {

                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(5));
                        return ret;
                    }

                    LinkedList<TypedEntity> ret = new LinkedList<>();

                    int id = (int) argument.getValue();

                    Optional<Ticket> t = collection.look(id);
                    if (t.isPresent()) {
                        if (t.get().getOwnerId() != uid) {
                            ret.add(new TypedEntity(5));
                            return ret;
                        }
                        Optional<Ticket> ticket = collection.take(id);
                        ticket.ifPresent(value -> ret.add(new TypedEntity(value)));
                    } else
                        ret.add(new TypedEntity(0));

                    return ret;
                }
        );

        this.registerCommand(
                new CommandLabel("remove_greater"),
                (collection, argument, auth) -> {

                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(-1));
                        return ret;
                    }

                    List<Ticket> toRemove =
                            collection.getFiltered((Ticket t) ->
                                    t.getName().compareTo((String) argument.getValue()) > 0
                                            && t.getOwnerId() == uid);

                    toRemove.forEach((Ticket t) -> collection.take(t.getId()));

                    LinkedList<TypedEntity> ret = new LinkedList<>();
                    ret.add(new TypedEntity(toRemove.size()));
                    return ret;
                }
        );

        this.registerCommand(
                new CommandLabel("remove_head"),
                (collection, argument, auth) -> {
                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(5));
                        return ret;
                    }

                    LinkedList<TypedEntity> ret = new LinkedList<>();

                    Optional<Ticket> head = collection.pop();
                    if (head.isPresent()) {
                        if (head.get().getOwnerId() != uid) {
                            collection.add(head.get(), true);
                            ret.add(new TypedEntity(5));
                            return ret;
                        }
                        ret.add(new TypedEntity(head.get()));
                    }
                    else ret.add(new TypedEntity(0));

                    return ret;
                }
        );

        this.registerCommand(
                new CommandLabel("sum_of_price"),
                (collection, argument, auth) -> {
                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(-1));
                        return ret;
                    }

                    LinkedList<TypedEntity> ret = new LinkedList<>();
                    ret.add(new TypedEntity(collection.getSumOfPrice()));
                    return ret;
                }
        );

        this.registerCommand(
                new CommandLabel("update"),
                (collection, argument, auth) -> {
                    SecurityManager sm = SecurityManagerFactory.getDefault();
                    int uid = sm.authenticate(auth.getLogin(), auth.getPass());

                    if (uid <= 0) {
                        LinkedList<TypedEntity> ret = new LinkedList<>();
                        ret.add(new TypedEntity(5));
                        return ret;
                    }

                    LinkedList<TypedEntity> ret = new LinkedList<>();

                    Ticket toUpdate = (Ticket) argument.getValue();
                    Optional<Ticket> updating = collection.take(toUpdate.getId());
                    if (updating.isPresent() && !updating.get().equals(toUpdate) && updating.get().getOwnerId() == uid) {
                        boolean isAdd = collection.add(toUpdate, true);
                        ret.add(new TypedEntity(isAdd ? 1 : 0));
                    } else if (!updating.isPresent()) {
                        ret.add(new TypedEntity(0));
                    } else if (updating.get().equals(toUpdate) || updating.get().getOwnerId() != uid) {
                        collection.add(updating.get(), true);
                        ret.add(new TypedEntity(2));
                    }

                    return ret;
                }
        );

        this.registerCommand(
                new CommandLabel("save"),
                (collection, argument, auth) -> {
                    if (!collection.isUnsaved()) {
                        System.out.println("Collection contains no unsaved changes.");
                        return null;
                    }

//                    FileWriter writer = FileIO.getWriter();
//                    File saved = collection.getFile();
//                    try {
//                        writer.write(collection, saved);
//                    } catch (FileWritingException e) {
//                        System.out.println(e.getMessage());
//                        saved = new File("temp" + Instant.now().toEpochMilli());
//                        System.out.println("Trying to save to " + saved.getPath());
//
//                        try {
//                            writer.write(collection, saved);
//                        } catch (FileWritingException e1) {
//                            System.out.println(e1.getMessage());
//                            return null;
//                        }
//                    }
//
//                    try {
//                        collection.reload(collection.getFile());
//                    } catch (Exception e) {
//                        System.out.println("Could not update current state: " + e.getMessage());
//                    }

                    DataSourceFactory.getDefault().writeAll(collection);
                    collection.reload();

                    System.out.println("Collection saved.");

                    return null;
                }
        );

        this.registerCommand(
                new CommandLabel("register"),
                (collection, argument, auth) -> {
                    SecurityManager sm = SecurityManagerFactory.getDefault();

                    LinkedList<TypedEntity> ret = new LinkedList<>();
                    ret.add(new TypedEntity(sm.register(auth.getLogin(), auth.getPass())));
                    return ret;
                }
        );
    }
}

package stepanoff.denis.lab5.server.dataio;

import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.common.data.Venue;
import stepanoff.denis.lab5.server.Collection;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DataBase implements DataSource, SecurityManager {

    private final static String DB_HOST = "localhost";
    private final static String DB_NAME = "studs";
    private final static String DB_USER = "postgres";
    private final static String DB_PASS = "root";

    private final static String SELECT_TICKETS = "select * from ticket;";
    private final static String SELECT_VENUES = "select * from venue;";
    private final static String SELECT_USERS = "select login from users";
    private final static String AUTHENTICATE = "select id from users where login = ? and pass = ? ;";
    private final static String COMMIT_VENUE = "insert into venue values (default, ?, ?, ?::venue_type) returning id;";
    private final static String COMMIT_TICKET = "insert into ticket values (default, ?, ?, ?, ?, ?, ?::ticket_type, ?, ?) returning id;";
    private final static String REGISTER = "insert into users values(default, ?, ?);";
    private final static String CLEAR_VENUE = "delete from venue";
    private final static String CLEAR_TICKET = "delete from ticket";
    private final static String COMMIT_TICKET_ID = "insert into ticket values (?, ?, ?, ?, ?, ?, ?::ticket_type, ?, ?);";
    private final static String COMMIT_VENUE_ID = "insert into venue values (?, ?, ?, ?::venue_type);";

    private PreparedStatement selectTickets;
    private PreparedStatement selectVenues;
    private PreparedStatement selectUsers;
    private PreparedStatement authenticate;
    private PreparedStatement commitVenue;
    private PreparedStatement commitTicket;
    private PreparedStatement register;
    private PreparedStatement clearVenue;
    private PreparedStatement clearTicket;
    private PreparedStatement commitTicketId;
    private PreparedStatement commitVenueId;

    public DataBase() {
        String url = "jdbc:postgresql://" + DB_HOST + "/" + DB_NAME;
        try {
            Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASS);

            this.selectTickets = connection.prepareStatement(SELECT_TICKETS);
            this.selectVenues = connection.prepareStatement(SELECT_VENUES);
            this.selectUsers = connection.prepareStatement(SELECT_USERS);
            this.authenticate = connection.prepareStatement(AUTHENTICATE);
            this.commitVenue = connection.prepareStatement(COMMIT_VENUE);
            this.commitTicket = connection.prepareStatement(COMMIT_TICKET);
            this.register = connection.prepareStatement(REGISTER);
            this.clearTicket = connection.prepareStatement(CLEAR_TICKET);
            this.clearVenue = connection.prepareStatement(CLEAR_VENUE);
            this.commitTicketId = connection.prepareStatement(COMMIT_TICKET_ID);
            this.commitVenueId = connection.prepareStatement(COMMIT_VENUE_ID);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection readAll() {
        try {
            LinkedList<Venue> venues = new LinkedList<>();
            LinkedList<Ticket> tickets = new LinkedList<>();

            ResultSet rsV = selectVenues.executeQuery();
            while (rsV.next()) {
                venues.add(Venue.existing(rsV.getLong("id"))
                        .name(rsV.getString("name"))
                        .capacity(rsV.getInt("capacity"))
                        .venueType(rsV.getString("type"))
                        .build());
            }
            rsV.close();

            ResultSet rsT = selectTickets.executeQuery();
            while (rsT.next()) {
                long venueId = rsT.getLong("venue_id");
                tickets.add(
                        Ticket.existingTicket(
                                rsT.getInt("id"),
                                Date.from(rsT.getTimestamp("creation_date").toInstant()))
                .name(rsT.getString("name"))
                .coordinates(rsT.getDouble("coords_x"), rsT.getDouble("coords_y"))
                .price(rsT.getDouble("price"))
                .ticketType(rsT.getString("type"))
                .venue(venues.stream().filter((Venue v) -> v.getId() == venueId).findFirst().get())
                .owner(rsT.getInt("owner_id"))
                .build());
            }
            rsT.close();

            Collection collection = new Collection(this);
            tickets.forEach((Ticket t) -> collection.add(t, true));

            return collection;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void writeAll(Collection collection) {
        List<Ticket> tickets = collection.asList();
        try {
            this.clearVenue.executeUpdate();
            this.clearTicket.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tickets.forEach( (Ticket t) -> {
                    try {
                        this.commitTicketId.setInt(1, t.getId());
                        this.commitTicketId.setString(2, t.getName());
                        this.commitTicketId.setDouble(3, t.getCoordinates().getX());
                        this.commitTicketId.setDouble(4, t.getCoordinates().getY());
                        this.commitTicketId.setTimestamp(5, Timestamp.from(t.getCreationDate().toInstant()));
                        this.commitTicketId.setDouble(6, t.getPrice());
                        this.commitTicketId.setString(7, t.getType().toString());
                        this.commitTicketId.setLong(8, t.getVenue().getId());
                        this.commitTicketId.setInt(9, t.getOwnerId());

                        this.commitVenueId.setLong(1, t.getVenue().getId());
                        this.commitVenueId.setString(2, t.getVenue().getName());
                        this.commitVenueId.setInt(3, t.getVenue().getCapacity());
                        this.commitVenueId.setString(4, t.getVenue().getType().toString());

                        this.commitTicketId.executeUpdate();
                        this.commitVenueId.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            this.commitTicketId.clearParameters();
                            this.commitVenueId.clearParameters();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public Ticket commitNew(Ticket ticket) {
        try {
            this.commitVenue.setString(1, ticket.getVenue().getName());
            this.commitVenue.setInt(2, ticket.getVenue().getCapacity());
            this.commitVenue.setString(3, ticket.getVenue().getType().name());
            ResultSet rsV = this.commitVenue.executeQuery();
            rsV.next();
            long venueId = rsV.getLong("id");
            rsV.close();
            Venue v = ticket.getVenue().builder().id(venueId).build();

            this.commitTicket.setString(1, ticket.getName());
            this.commitTicket.setDouble(2, ticket.getCoordinates().getX());
            this.commitTicket.setDouble(3, ticket.getCoordinates().getY());
            this.commitTicket.setTimestamp(4, Timestamp.from(ticket.getCreationDate().toInstant()));
            this.commitTicket.setDouble(5, ticket.getPrice());
            this.commitTicket.setString(6, ticket.getType().name());
            this.commitTicket.setLong(7, venueId);
            this.commitTicket.setInt(8, ticket.getOwnerId());
            ResultSet rsT = this.commitTicket.executeQuery();
            rsT.next();
            int ticketId = rsT.getInt("id");
            rsT.close();

            return ticket.builder().id(ticketId).venue(v).build();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                this.commitTicket.clearParameters();
                this.commitVenue.clearParameters();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean isCommitNewSupported() {
        return true;
    }

    @Override
    public int authenticate(String login, String pass) {
        try {
            this.authenticate.setString(1, login);
            this.authenticate.setString(2, this.hash(pass));

            ResultSet rs = this.authenticate.executeQuery();
            if (!rs.next()) {
                rs.close();
                return -1;
            }
            int id = rs.getInt("id");
            rs.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int register(String login, String pass) {
        try {
            List<String> uNames = new LinkedList<>();
            ResultSet rs1 = this.selectUsers.executeQuery();
            while (rs1.next()) {
                uNames.add(rs1.getString("login"));
            }

            if (!uNames.contains(login)) {
                this.register.setString(1, login);
                this.register.setString(2, hash(pass));
                this.register.executeUpdate();
                return 1;
            } else {
                return 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] messageDigest = md.digest(s.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null; // NEVER
    }
}

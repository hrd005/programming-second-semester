//package stepanoff.denis.lab5.server.dataio.file;
//
//import stepanoff.denis.lab5.common.data.*;
//import stepanoff.denis.lab5.common.dataio.FileReadingException;
//import stepanoff.denis.lab5.server.Collection;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Scanner;
//
///**
// * Implementation reading collection data from CSV
// */
//class CsvFileReader implements FileReader {
//
//    private static final int COLUMNS_COUNT = 11;
//
//    @Override
//    public Collection parseFile(String filename) throws FileReadingException {
//
//        File file = this.getReadableFile(filename);
//
//        Collection collection = new Collection(file);
//
//        Scanner scanner = this.createScanner(file);
//        scanner.useDelimiter(System.lineSeparator());
//
//        String s = scanner.next();
//        //int columns = this.parseString(s).length;
//        int columns = COLUMNS_COUNT; // we just have to have so many columns
//
//        // First row -- a title row
//
//        scanner.forEachRemaining((String line) -> {
//            String[] parts = this.parseString(line);
//            //System.out.println(Arrays.toString(parts));
//
//            if (parts.length != columns)
//                throw new InvalidFileException("Incorrect columns count: " + parts.length + " instead of "
//                        + columns + " in line:\n\t" + line);
//
//            Ticket ticket;
//            try {
//                ticket = this.createEntity(parts);
//            } catch (Exception e) {
//                throw new InvalidFileException("Invalid data provided: " + e.getMessage(), e);
//            }
//
//            collection.add(ticket, true);
//        });
//
//        scanner.close();
//
//        return collection;
//    }
//
//    private File getReadableFile(String filename) throws FileReadingException {
//        File file = new File(filename);
//        if (!file.exists()) {
//            throw new FileReadingException("File Not Found: " + filename + "\n");
//        }
//        if (!file.isFile()) {
//            throw new FileReadingException(filename + " is not a file.\n");
//        }
//        if (!file.canRead()) {
//            throw new FileReadingException("Reading file " + filename + " is not permitted.\n");
//        }
//
//        return file;
//    }
//
//    private Scanner createScanner(File source) throws FileReadingException {
//        try {
//            return new Scanner(source);
//        } catch (FileNotFoundException e) {
//            throw new FileReadingException("File Not Found: " + source.getPath() + "\n");
//        }
//    }
//
//    private String[] parseString(String s) {
//
//        ArrayList<String> list = new ArrayList<>();
//
//        StringBuilder sb = new StringBuilder();
//
//        boolean ignoreCommas = false;
//
//        char[] chars = s.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//
//            char c = chars[i];
//
//            switch (c) {
//
//                case ',':
//                    if (!ignoreCommas) {
//                        list.add(sb.toString());
//                        sb = new StringBuilder();
//                    } else {
//                        sb.append(c);
//                    }
//                    break;
//
//                case '\"':
//                    if ((chars.length - 1 == i || chars[i+1] != '\"')) {
//                        ignoreCommas = !ignoreCommas;
//
//                        if (sb.length() != 0 && sb.charAt(sb.length() - 1) != '"' && ignoreCommas) {
//                            throw new InvalidFileException("Unexpected '\"' in line:\n\t " + s);
//                        }
//
//                        if (!ignoreCommas) {
//                            if (chars.length - 1 == i || chars[i+1] == ',') {
//                                list.add(sb.toString());
//                                sb = new StringBuilder();
//                                i += 1;
//                            } else throw new InvalidFileException("Unexpected '" + chars[i+1] + "' in line:\n\t " + s);
//                        }
//                    } else {
//                        sb.append(c);
//                        i += 1;
//                    }
//                    break;
//
//                default:
//                    sb.append(c);
//            }
//        }
//
//        if (sb.length() > 0 || chars[chars.length - 1] == ',') {
//            list.add(sb.toString());
//        }
//
//        return list.toArray(new String[0]);
//    }
//
//    private Ticket createEntity(String[] columns) {
//
//        int ticketId = Integer.parseInt(columns[0]);
//        String name = columns[1];
//        double xCoord = Double.parseDouble(columns[2]);
//        double yCoord = Double.parseDouble(columns[3]);
//        Date creationDate = Date.from(Instant.from(LocalDateTime.parse(columns[4]).atZone(ZoneId.systemDefault())));
//        double price = Double.parseDouble(columns[5]);
//        TicketType ticketType = TicketType.valueOf(columns[6].toUpperCase());
//        long venueId = Long.parseLong(columns[7]);
//        String venueName = columns[8];
//        int capacity = Integer.parseInt(columns[9]);
//        VenueType venueType = VenueType.valueOf(columns[10].toUpperCase());
//
//        Venue venue = Venue.existing(venueId)
//                .name(venueName)
//                .capacity(capacity)
//                .venueType(venueType)
//                .build();
//
//        return Ticket.existingTicket(ticketId, creationDate)
//                .name(name)
//                .coordinates(xCoord, yCoord)
//                .price(price)
//                .ticketType(ticketType)
//                .venue(venue)
//                .build();
//    }
//}

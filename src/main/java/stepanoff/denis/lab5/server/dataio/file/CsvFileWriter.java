package stepanoff.denis.lab5.server.dataio.file;

import stepanoff.denis.lab5.server.Collection;
import stepanoff.denis.lab5.common.data.Ticket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Implementation writing file in CSV
 */
public class CsvFileWriter implements FileWriter {

    @Override
    public void write(Collection collection, File file) throws FileWritingException {
//        File file = collection.getFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new FileWritingException("An error while creating new file occurred.");
            }
        }

        if (!file.canWrite()) throw new FileWritingException("No rights to write in file " + file.getPath());

        try (PrintWriter writer = new PrintWriter(file)) {

            String head = "id,name,coordinates.x,coordinates.y,creationDate,price,type," +
                    "venue.id,venue.name,venue.capacity,venue.type";

            writer.println(head);

            collection.asList().forEach((Ticket t) -> {
                String line = this.combineString(t);
                writer.println(line);
            });

        } catch (FileNotFoundException e) {
            throw new FileWritingException("File disappeared while writing.");
        }
    }

    private String combineString(Ticket ticket) {
        return ticket.getId() + "," +
                this.formatStr(ticket.getName()) + "," +
                ticket.getCoordinates().getX() + "," +
                ticket.getCoordinates().getY() + "," +
                ticket.getCreationDate().toInstant()
                        .atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," +
                ticket.getPrice() + "," +
                ticket.getType().name().toLowerCase() + "," +
                ticket.getVenue().getId() + "," +
                this.formatStr(ticket.getVenue().getName()) + "," +
                ticket.getVenue().getCapacity() + "," +
                ticket.getVenue().getType().name().toLowerCase();
    }

    private String formatStr(String s) {
        String ret = s.replaceAll("\"", "\"\"");
        return ret.contains("\"") || ret.contains(",") ? "\"" + ret + "\"" : ret;
    }
}

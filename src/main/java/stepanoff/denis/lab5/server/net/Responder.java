package stepanoff.denis.lab5.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class encapsulates module of response writing.
 */
public class Responder {

    private static final Logger logger = LoggerFactory.getLogger("Responder");
    private final Socket socket;

    /**
     * Create a Responder for specified connection
     * @param socket -- socket connected to a client
     */
    public Responder(Socket socket) {
        this.socket = socket;
    }

    /**
     * Send calculated data
     * @param data -- LinkedList containing all the data you want to save to client encapsulated in Typed Entities.
     *             Note that all entities will be sent in order of their serialized size.
     * @see TypedEntity
     */
    public void writeResponse(LinkedList<TypedEntity> data) {

        if (data == null) {
            logger.warn("Nothing to response. Stop processing.");
            return;
        }
        if (data.size() == 0) data.add(new TypedEntity(0));
                                          // <- Common Signal for all request that something went wrong

        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

//        if (data.size() == 1 && !data.peekFirst().getType().equals(Ticket.class)) {
//            dos.writeInt(1);
//            ObjectOutputStream oos = new ObjectOutputStream(dos);
//            oos.writeObject(data.pollFirst());
//            oos.flush();
//        } else {                                                     <- JUST SEND THEM IN ONE WAY
            dos.writeInt(data.size());

            LinkedList<ByteBuffer> objects = new LinkedList<>();

            for (TypedEntity e : data) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(e);
                oos.flush();
                objects.add(ByteBuffer.wrap(bos.toByteArray()));
            }

            List<ByteBuffer> sortedObjects = objects.stream()
                    .sorted(Comparator.comparing(ByteBuffer::capacity))
                    .collect(Collectors.toList());

            int sum = 0;
            for (ByteBuffer bytes : sortedObjects) {
                int length = bytes.capacity();
                sum += length;
                dos.writeInt(length);
                dos.write(bytes.array());
            }

            logger.info(sum + " bytes sent.");
            //  }
            dos.flush();
            os.flush();
            socket.close();
        } catch (IOException e) {
            logger.error("IO error -- " + e.getMessage());
        }
    }
}

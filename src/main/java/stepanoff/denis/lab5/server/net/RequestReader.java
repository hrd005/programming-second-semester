package stepanoff.denis.lab5.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stepanoff.denis.lab5.common.util.Authentication;
import stepanoff.denis.lab5.server.cmd.CommandArgument;
import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.data.Ticket;
import stepanoff.denis.lab5.common.util.TypedEntity;
import stepanoff.denis.lab5.server.Main;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * This class encapsulates request reading and processing module.
 */
public class RequestReader {

    private final static Logger logger = LoggerFactory.getLogger("RequestProcessor");

    private final Socket socket;

    /**
     * Creates Request Reader for specified connection
     * @param socket -- socket connected to connection with client
     */
    public RequestReader(Socket socket) {
        this.socket = socket;
    }

    /**
     * Parse request (and call Executor to process it)
     * After parsing Responder will be called automatically
     */
    public void process() {
        LinkedList<TypedEntity> received = new LinkedList<>();
        try {
            InputStream is = socket.getInputStream();
            logger.debug("IS received.");

            DataInputStream dis = new DataInputStream(is);
            logger.debug("DIS received");

            dis.readInt();
            logger.debug("Int read");

            ObjectInputStream ois = new ObjectInputStream(is);
            logger.debug("OIS received");
            int count = ois.readInt();
            logger.debug("Count received");
            for (int i = 0; i < count; i++) {
                received.add((TypedEntity) ois.readObject());
                logger.debug("Object read.");
            }
        } catch (IOException e) {
            logger.error("IO error -- " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.error("Serialization fault -- " + e.getMessage());
        }

        if (received.isEmpty() || !received.getFirst().getType().equals(Authentication.class)) {
            logger.error("Invalid request received. Stop processing.");
            return;
        }
        if (received.size() < 2) return; // Just to calm down IDE warning. Situation is handled in previous 'if'
        Authentication auth = (Authentication) received.pollFirst().get();
        if (!received.getFirst().getType().equals(CommandLabel.class)) {
            logger.error("Invalid request received. Stop processing.");
            return;
        }
        CommandLabel command = (CommandLabel) received.pollFirst().get();
        logger.info(command.label + " command requested.");

        LinkedList<TypedEntity> response = null;
        if (received.size() == 0) {
            response = Main.provideExecutor().execute(command, null, auth);
        } else {
            TypedEntity arg = received.pollFirst();
            if (arg.getType().equals(Ticket.class)) {
                response = Main.provideExecutor().execute(command, new CommandArgument(arg.get()), auth);
            }
            if (arg.getType().equals(Double.class)) {
                response = Main.provideExecutor().execute(command, new CommandArgument(arg.get()), auth);
            }
            if (arg.getType().equals(Integer.class)) {
                response = Main.provideExecutor().execute(command, new CommandArgument(arg.get()), auth);
            }
            if (arg.getType().equals(String.class)) {
                response = Main.provideExecutor().execute(command, new CommandArgument(arg.get()), auth);
            }
        }

        new Responder(this.socket).writeResponse(response);
    }
}

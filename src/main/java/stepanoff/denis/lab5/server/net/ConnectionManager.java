package stepanoff.denis.lab5.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class encapsulates module of accepting connections.
 */
public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger("Acceptor");
    private static final int PORT = 3080;
    private final ServerSocket serverSocket = new ServerSocket(PORT);
    private boolean running = true;

    /**
     * @throws IOException in case of problems of starting listening on port 3080
     */
    public ConnectionManager() throws IOException {
    }

    /**
     * Main server cycle
     */
    public void start() {
        logger.info("Listening on port " + PORT);
        while (running) {

            try {
                Socket socket = serverSocket.accept();
                logger.info("Accepted connection from " + socket.getInetAddress().getHostName());
                new RequestReader(socket).process();

            } catch (IOException e) {
                logger.warn("Failed to accept connection: " + e.getMessage());
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.warn("Failed to stop listening.");
        }
    }

    /**
     * Just in case you want to stop server part
     * @throws IOException in case of problems with closing sockets.
     */
    public void stop() throws IOException {
        this.running = false;
        this.serverSocket.close();
    }
}

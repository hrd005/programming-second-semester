package stepanoff.denis.lab5.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stepanoff.denis.lab5.client.cmd.CommandStore;
import stepanoff.denis.lab5.client.cmd.ExecutorService;
import stepanoff.denis.lab5.client.cmd.MaxRecursionDepthException;
import stepanoff.denis.lab5.client.net.ServerConnector;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger("ClientMain");

    public static void main(String... args) {

        ServerConnector connector = new ServerConnector();

        try {
            ExecutorService.start(System.in, CommandStore.CLIENT_DEFAULT, connector);
        } catch (MaxRecursionDepthException e) {
            logger.error("Recursion on STDIN detected?");
        }
    }

    public static Logger provideLogger() {
        return logger;
    }
}

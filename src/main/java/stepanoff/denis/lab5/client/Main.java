package stepanoff.denis.lab5.client;

import jdk.internal.jline.internal.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stepanoff.denis.lab5.client.cmd.CommandStore;
import stepanoff.denis.lab5.client.cmd.ExecutorService;
import stepanoff.denis.lab5.client.cmd.MaxRecursionDepthException;
import stepanoff.denis.lab5.client.net.Request;
import stepanoff.denis.lab5.client.net.ServerConnector;
import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.util.Authentication;
import stepanoff.denis.lab5.common.util.ConsoleWriter;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger("ClientMain");
    private static Authentication auth = null;

    public static void main(String... args) {

        ServerConnector connector = new ServerConnector();

        do {
            System.out.println("Register or login? (r|s)");
            String l = System.console().readLine();

            if ("register".contains(l)) {
                ConsoleWriter.printPrompt("Login: ");
                String login = System.console().readLine();
                ConsoleWriter.printPrompt("Password: ");
                String pass = new String(System.console().readPassword());

                Future<List<TypedEntity>> resp =
                        connector.manageRequest(
                                new Request(new Authentication(login, pass), new CommandLabel("register"))
                        );

                int status = 0;
                try {
                    status = (int) resp.get().get(0).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if (status == 0) {
                    System.out.println("Server error. Try again later.");
                } else if (status == 1) {
                    System.out.println("Successfully registered.");
                    auth = new Authentication(login, pass);
                } else if (status == 2) {
                    System.out.println("This user is already registered.");
                }
            } else if("login".contains(l)) {
                ConsoleWriter.printPrompt("Login: ");
                String login = System.console().readLine();
                ConsoleWriter.printPrompt("Password: ");
                String pass = new String(System.console().readPassword());

                Future<List<TypedEntity>> resp =
                        connector.manageRequest(
                                new Request(new Authentication(login, pass), new CommandLabel("info"))
                        );

                int status = 0;
                try {
                    status = (int) resp.get().get(0).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if (status == 0) {
                    System.out.println("Server error. Try again later.");
                } else if (status == 1) {
                    System.out.println("Logged in.");
                    auth = new Authentication(login, pass);
                } else if (status == 5) {
                    System.out.println("Invalid credentials.");
                }
            }
        } while (auth == null);
        try {
            ExecutorService.start(System.in, CommandStore.CLIENT_DEFAULT, connector);
        } catch (MaxRecursionDepthException e) {
            logger.error("Recursion on STDIN detected?");
        }
    }

    public static Logger provideLogger() {
        return logger;
    }

    public static Authentication provideCredentials() {
        return auth;
    }
}

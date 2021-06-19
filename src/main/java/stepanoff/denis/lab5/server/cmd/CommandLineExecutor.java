package stepanoff.denis.lab5.server.cmd;

import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.server.Main;
import stepanoff.denis.lab5.server.net.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

/**
 * This class is command line interpreter for server command line
 */
public class CommandLineExecutor {

    private final BufferedReader input;
    private ConnectionManager connectionManager;

    /**
     * Constructs Executor
     * @param is -- Input Stream that will be read
     * @param manager -- Connection Manager (server side representation) you are currently running
     */
    public CommandLineExecutor(InputStream is, ConnectionManager manager) {
        this.input = new BufferedReader(new InputStreamReader(is));
        this.connectionManager = manager;
    }

    /**
     * Main interpreting cycle
     * @throws IOException in case of problems with reading provided Input Stream
     * or in case of problems with provided or reinstated ConnectionManager
     */
    public void cycle() throws IOException {

        String line = this.input.readLine();
        while (line != null) {

            String[] parts = Stream.of(line.split(" "))
                    .map(String::trim)
                    .filter((String s) -> !s.isEmpty())
                    .toArray(String[]::new);

            if (parts.length == 0) {
                line = this.input.readLine();
                continue;
            }

            String command = parts[0];

            switch (command) {
                case "help":
                    System.out.println("\tsave -- save collection to file\n\texit -- save collection to file and stop server");
                    break;
                case "save":
                    connectionManager.stop();
                    this.doSave();
                    connectionManager = new ConnectionManager();
                    Thread server = new Thread(() -> connectionManager.start());
                    server.start();
                    break;
                case "exit":
                    connectionManager.stop();
                    this.doSave();
                    this.doExit();
                    break;
            }

            line = this.input.readLine();
        }
        connectionManager.stop();
        this.doSave();
        this.doExit();
    }

    private void doSave() {
        Main.provideExecutor().await();
        Main.provideExecutor().execute(new CommandLabel("save"), null, null);
    }

    private void doExit() {
        Main.provideExecutor().await();
        System.exit(0);
    }
}

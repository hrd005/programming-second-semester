package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.client.Main;
import stepanoff.denis.lab5.client.net.ServerConnector;
import stepanoff.denis.lab5.common.dataio.FileIO;
import stepanoff.denis.lab5.common.dataio.FileReadingException;
import stepanoff.denis.lab5.common.net.NetException;
import stepanoff.denis.lab5.common.util.ConsoleWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * Interpreter for client cmd
 */
public class ExecutorService {

    private final BufferedReader input;
    private final HashMap<String, stepanoff.denis.lab5.client.cmd.Command> commandSet = new HashMap<>(16);
    private final ServerConnector connector;
    private final Stack<String> callStack = new Stack<>();
    private final ExecutorService parent;
    private final String identifier;

    private ExecutorService(InputStream inputStream, ServerConnector connector) throws MaxRecursionDepthException {
        this(inputStream, connector, ".", null);
    }

    private ExecutorService(InputStream inputStream,
                            ServerConnector connector,
                            String identifier,
                            ExecutorService parent)
            throws MaxRecursionDepthException {
        this.input = new BufferedReader(new InputStreamReader(inputStream));
        this.connector = connector;
        this.parent = parent;
        this.identifier = identifier;
        if (!this.registerExecutor(identifier))
            throw new MaxRecursionDepthException("Recursive script " + identifier + " stopped.");
    }

    private void registerCommand(stepanoff.denis.lab5.client.cmd.Command command) {
        command.setConnector(this.connector);
        command.setInput(this.input);
        command.setMode(this.isRoot());
//        if (command instanceof ExecutionCommand) ((ExecutionCommand) command).setCaller(this);
        this.commandSet.put(command.getName(), command);
        if (command instanceof ExecuteScriptCommand) {
            this.commandSet.put(command.getName(),
                    new stepanoff.denis.lab5.client.cmd.Command() {{ this.action = (String... s) -> executeScript(s); }});
        }
        if (command instanceof ExitCommand) {
            this.commandSet.put(command.getName(),
                    new stepanoff.denis.lab5.client.cmd.Command() {{ this.action = (String... s) -> exit(); }});
        }
    }

    private boolean registerExecutor(String identifier) {
        if (this.parent != null) {
            return this.parent.registerExecutor(identifier);
        }

        //long recursionDepth = this.callStack.stream().filter((String s) -> s.equals(identifier)).count();
        if (!this.getCallStack().contains(identifier)) {
            this.callStack.push(identifier);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if script is running normally
     * @param identifier -- identifier of current script
     * @return true -- CallStack is valid
     *         false -- CallStack is corrupted
     */
    public boolean checkCallStack(String identifier) {
        String lastExecutor = this.getLastExecutor();
        if (!lastExecutor.equals(identifier)) {
            //ConsoleWriter.println("Unexpected errors during script execution.", ConsoleWriter.Color.RED);
            this.getCallStack().clear();
            this.getCallStack().push(".");
            return false;
        } else return true;
    }

    private Stack<String> getCallStack() {
        return this.isRoot() ? this.callStack : this.parent.getCallStack();
    }

    private String getLastExecutor() {
        return this.isRoot() ? this.callStack.peek() : this.parent.getLastExecutor();
    }

    private void cycle() throws IOException {

        if (isRoot()) ConsoleWriter.printPrompt();

        String line = this.input.readLine();
        while (line != null) {

            if (!this.checkCallStack(this.identifier)) {
                Main.provideLogger().warn(this.identifier + " is recursive.");
                return;
            }

            String[] parts = Stream.of(line.split(" "))
                    .map(String::trim)
                    .filter((String s) -> !s.isEmpty())
                    .toArray(String[]::new);

            if (parts.length == 0) {
                if (isRoot()) ConsoleWriter.printPrompt();
                line = this.input.readLine();
                continue;
            }

            Command action = this.commandSet.get(parts[0]);
            if (action == null)
                 if (isRoot())
                     ConsoleWriter.println("Unknown command '" + parts[0] +
                             "'. Try 'help' to list all commands.", ConsoleWriter.Color.YELLOW);
                 else ConsoleWriter.println("Unknown command '" + parts[0] +
                         "' missed.");
            else {
                try {
                    action.getAction().execute(parts);
                } catch (ExitInScriptException e) {
                    break;
                } catch (NetException e) {
                    ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
                    Main.provideLogger().warn(e.getMessage());
                } catch (Exception e) {
                    Main.provideLogger().error(e.getMessage());
                    ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
                    return;
                }
            }

            if (isRoot()) ConsoleWriter.printPrompt();
            line = this.input.readLine();
        }
    }

    /**
     * Check if this executor is root (reading STDIN)
     * @return true if root, false if not
     */
    public boolean isRoot() {
        return this.parent == null;
    }

    /**
     * Start interpreter
     * @param inputStream -- Input Stream to read commands from
     * @param commandStore -- set of commands
     * @param connector -- connector to server
     * @throws MaxRecursionDepthException if recursion detected
     */
    public static void start(InputStream inputStream, CommandStore commandStore, ServerConnector connector)
            throws MaxRecursionDepthException {
        ExecutorService executorService = new ExecutorService(inputStream, connector);
        commandStore.forEach(executorService::registerCommand);
        try {
            executorService.cycle();
        } catch (IOException e) {
            Main.provideLogger().error(e.getMessage());
        }
    }

    /**
     * Start a new interpreter
     * @param inputStream -- Input Stream to read commands from
     * @param commandStore -- set of commands
     * @param connector -- connector to server
     * @param identifier -- identifier of this interpreter
     * @throws MaxRecursionDepthException if recursion detected
     */
    public void start(InputStream inputStream, CommandStore commandStore, ServerConnector connector, String identifier)
            throws MaxRecursionDepthException {
        ExecutorService executorService = new ExecutorService(inputStream, connector, identifier, this);

        commandStore.forEach(executorService::registerCommand);
        try {
            executorService.cycle();
        } catch (IOException e) {
            Main.provideLogger().warn(e.getMessage());
        }
    }

    /**
     * Realisation of execute_script command
     * @param s -- command params
     */
    public void executeScript(String... s) {
        if (s.length <= 1 || s[1].isEmpty()) {
            ConsoleWriter.println("Script file is not specified.", ConsoleWriter.Color.RED);
            return;
        }

        String identifier = s[1];
        //Collection col = this.isRoot() ? this.collection.copy() : this.collection;
        CommandStore commandStore = CommandStore.CLIENT_DEFAULT;
        try {
            InputStream inputStream = FileIO.createInputStream(s[1]);

            //Collection modified =
            this.start(inputStream, commandStore, this.connector, identifier);
            if (this.checkCallStack(identifier))
                this.getCallStack().pop();

        } catch (MaxRecursionDepthException e) {
            Main.provideLogger().error("Recursive script stopped.");
            ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.YELLOW);
            this.getCallStack().clear();
            this.getCallStack().push(".");
        } catch (FileReadingException e) {
            Main.provideLogger().warn(e.getMessage());
            ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
            this.getCallStack().clear();
        }
    }

    /**
     * implementation od exit command
     */
    public void exit() {
        if (isRoot()) new ExitCommand().getAction().execute();
        else throw new ExitInScriptException();
    }
}

package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.Collection;
import stepanoff.denis.lab5.io.FileIO;
import stepanoff.denis.lab5.io.FileReadingException;
import stepanoff.denis.lab5.io.InvalidFileException;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.stream.Stream;

public class ExecutorService {

    private final BufferedReader input;
    private final HashMap<String, Command> commandSet = new HashMap<>(16);
    private Collection collection;
    private final Stack<String> callStack = new Stack<>();
    private final ExecutorService parent;

    private ExecutorService(InputStream inputStream, Collection collection) throws MaxRecursionDepthException {
        this(inputStream, collection, ".", null);
    }

    private ExecutorService(InputStream inputStream,
                            Collection collection,
                            String identifier,
                            ExecutorService parent)
            throws MaxRecursionDepthException {
        this.input = new BufferedReader(new InputStreamReader(inputStream));
        this.collection = collection;
        this.parent = parent;
        if (!this.registerExecutor(identifier))
            throw new MaxRecursionDepthException("Recursive script " + identifier + " stopped.");
    }

    private void registerCommand(Command command) {
        command.setCollection(this.collection);
        command.setInput(this.input);
        command.setMode(this.isRoot());
//        if (command instanceof ExecutionCommand) ((ExecutionCommand) command).setCaller(this);
        this.commandSet.put(command.getName(), command);
        if (command instanceof ExecuteScriptCommand) {
            this.commandSet.put(command.getName(),
                    new Command() {{ this.action = (String... s) -> executeScript(s); }});
        }
        if (command instanceof ExitCommand) {
            this.commandSet.put(command.getName(),
                    new Command() {{ this.action = (String... s) -> exit(); }});
        }
    }

    private boolean registerExecutor(String identifier) {
        if (this.parent != null) {
            return this.parent.registerExecutor(identifier);
        }

        long recursionDepth = this.callStack.stream().filter((String s) -> s.equals(identifier)).count();
        if (recursionDepth < 50) {
            this.callStack.push(identifier);
            return true;
        } else {
            return false;
        }
    }

    public void onChildExecutorFinished(String identifier, Collection modified) {
        String lastExecutor = this.getLastExecutor();
        if (!lastExecutor.equals(identifier) || modified == null) {
            ConsoleWriter.println("Errors during script execution. Fallback changes.", ConsoleWriter.Color.RED);
            this.getCallStack().clear();
            this.getCallStack().push(".");
        }
        if (lastExecutor.equals(identifier) && modified != null) this.collection = modified;
        this.commandSet.forEach((String key, Command value) -> {
            value.setCollection(this.collection);
        });
    }

    private Stack<String> getCallStack() {
        return this.isRoot() ? this.callStack : this.parent.getCallStack();
    }

    private String getLastExecutor() {
        return this.isRoot() ? this.callStack.pop() : this.parent.getLastExecutor();
    }

    private Collection cycle() throws IOException {

        if (isRoot()) ConsoleWriter.printPrompt();

        String line = this.input.readLine();
        while (line != null) {
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
                } catch (Exception e) {
                    ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
                    return null;
                }
            }

            if (isRoot()) ConsoleWriter.printPrompt();
            line = this.input.readLine();
        }

        return this.collection;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public static void start(InputStream inputStream, CommandSet commandSet, Collection collection)
            throws MaxRecursionDepthException {
        ExecutorService executorService = new ExecutorService(inputStream, collection);
        commandSet.forEach(executorService::registerCommand);
        try {
            executorService.cycle();
        } catch (Exception ignored) {
        }
    }

    public Collection start(InputStream inputStream, CommandSet commandSet, Collection collection, String identifier)
            throws MaxRecursionDepthException {
        ExecutorService executorService = new ExecutorService(inputStream, collection, identifier, this);

        commandSet.forEach(executorService::registerCommand);
        try {
            return executorService.cycle();
        } catch (Exception ignored) {

        }
        return null;
    }

    public void executeScript(String... s) {
        if (s.length <= 1 || s[1].isEmpty()) {
            ConsoleWriter.println("Script file is not specified.", ConsoleWriter.Color.RED);
            return;
        }

        String identifier = s[1];
        Collection col = this.isRoot() ? this.collection.copy() : this.collection;
        CommandSet commandSet = CommandSet.DEFAULT;
        try {
            InputStream inputStream = FileIO.createInputStream(s[1]);

            Collection modified = this.start(inputStream, commandSet, col, identifier);
            this.onChildExecutorFinished(identifier, modified);

        } catch (MaxRecursionDepthException e) {
            ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.YELLOW);
            this.getCallStack().push(identifier);
            this.onChildExecutorFinished(identifier, this.collection);
        } catch (FileReadingException e) {
            ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
            this.onChildExecutorFinished(identifier, null);
        }
    }

    public void exit() {
        if (isRoot()) new ExitCommand().getAction().execute();
        else throw new ExitInScriptException();
    }
}

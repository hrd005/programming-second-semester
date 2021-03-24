package stepanoff.denis.lab5.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * <p>Represents a set of commands ExecutorService must work with.</p>
 * <p>HelpCommand is added automatically.</p>
 * @see HelpCommand
 */
public class CommandSet {

    /**
     * Default command set. (from task)
     * Contains InfoCommand, ShowCommand, AddCommand, UpdateCommand, RemoveByIdCommand, ClearCommand, SaveCommand,
     * ExecuteScriptCommand, ExitCommand, RemoveHeadCommand, AddIfMaxCommand, RemoveGreaterCommand,
     * SumOfPriceCommand, FilterGreaterThanPriceCommand, PrintFieldDescendingTypeCommand
     */
    public static final CommandSet DEFAULT = new CommandSet(
            new InfoCommand(),
            new ShowCommand(),
            new AddCommand(),
            new UpdateCommand(),
            new RemoveByIdCommand(),
            new ClearCommand(),
            new SaveCommand(),
            new ExecuteScriptCommand(),
            new ExitCommand(),
            new RemoveHeadCommand(),
            new AddIfMaxCommand(),
            new RemoveGreaterCommand(),
            new SumOfPriceCommand(),
            new FilterGreaterThanPriceCommand(),
            new PrintFieldDescendingTypeCommand()
    );

    private final ArrayList<Command> commands;

    /**
     * @param commands commands should be included in set
     */
    public CommandSet(Command... commands) {
        this.commands = new ArrayList<>(Arrays.asList(commands));
        this.commands.add(new HelpCommand(this));
    }

    /**
     * Just in case of necessity to perform action for each command in set.
     * @param action action that consumes Command
     */
    protected void forEach(Consumer<Command> action) {
        commands.forEach(action);
    }
}

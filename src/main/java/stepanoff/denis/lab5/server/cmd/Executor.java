package stepanoff.denis.lab5.server.cmd;

import stepanoff.denis.lab5.common.cmd.CommandLabel;
import stepanoff.denis.lab5.common.util.TypedEntity;
import stepanoff.denis.lab5.server.Collection;

import java.util.LinkedList;

/**
 * This class executes commands from client
 */
public class Executor {

    private final Collection collection;
    private final CommandSet commandSet = new CommandImpl();

    /**
     * Initialize Executor with Collection
     * @see Collection
     * @param collection -- collection provided
     */
    public Executor(Collection collection) {
        this.collection = collection;
    }

    /**
     * Execute command
     * @param command -- Command with at least action and (in necessary) argument provided
     * @return Linked List with Typed Entities to send client
     * @see TypedEntity
     */
    public LinkedList<TypedEntity> execute(Command command) {
        CommandAction action = command.getAction();
        return action.execute(this.collection, command.getArgument());
    }

    /**
     * Execute command
     * @param label -- Command name to execute
     * @param argument -- Argument (if necessary) for a command
     * @return Linked List with Typed Entities to send client
     * @see TypedEntity
     */
    public LinkedList<TypedEntity> execute(CommandLabel label, CommandArgument argument) {
        return this.execute(this.commandSet.getCommand(label, argument));
    }
}

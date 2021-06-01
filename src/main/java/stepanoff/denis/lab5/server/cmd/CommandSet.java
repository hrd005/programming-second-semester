package stepanoff.denis.lab5.server.cmd;

import stepanoff.denis.lab5.common.cmd.CommandLabel;

import java.util.HashMap;

/**
 * This class matches requests and their actions
 */
public class CommandSet {

    private final HashMap<CommandLabel, CommandAction> commands = new HashMap<>();

    /**
     * Get command by label
     * @param label -- string with request
     * @return Command or null if no such command available
     */
    public Command getCommand(String label) {
        return this.getCommand(new CommandLabel(label));
    }

    /**
     * Get command by label with argument provided
     * @param label -- string with request
     * @param argument -- argument provided
     * @return Command or null if no such command available
     */
    public Command getCommand(String label, CommandArgument argument) {
        return this.getCommand(new CommandLabel(label), argument);
    }

    /**
     * Get command by label with argument provided
     * @param label -- CommandLabel with request
     * @param argument -- argument provided
     * @return Command or null if no such command available
     */
    public Command getCommand(CommandLabel label, CommandArgument argument) {
        Command command = this.getCommand(label);
        command.setArgument(argument);
        return command;
    }

    /**
     * Get command by label with argument provided
     * @param label -- CommandLabel with request
     * @return Command or null if no such command available
     */
    public Command getCommand(CommandLabel label) {
        if (!this.commands.containsKey(label)) return null;
        return new Command(label) {
            {
                this.action = commands.get(label);
            }
        };
    }

    /**
     * Add command to set
     * @param label -- CommandLabel associated
     * @param action -- action to perform on call
     */
    public void registerCommand(CommandLabel label, CommandAction action) {
        this.commands.put(label, action);
    }

    /**
     * Add command to send
     * @param command -- command with label and action specified
     */
    public void registerCommand(Command command) {
        this.commands.put(command.getLabel(), command.getAction());
    }
}

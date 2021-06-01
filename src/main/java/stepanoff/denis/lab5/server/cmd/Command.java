package stepanoff.denis.lab5.server.cmd;

import stepanoff.denis.lab5.common.cmd.CommandLabel;

/**
 * Abstract command for server
 */
public class Command {

    private final CommandLabel label;

    /**
     * Get label (a request string) associated with this command
     * @return label
     * @see CommandLabel
     */
    public CommandLabel getLabel() {
        return this.label;
    }

    private CommandArgument argument;

    /**
     * Get command argument
     * @return argument
     * @see CommandArgument
     */
    public CommandArgument getArgument() {
        return this.argument;
    }

    /**
     * Set command argument
     * @param argument -- argument encapsulated in CommandArgument
     * @see CommandArgument
     */
    public void setArgument(CommandArgument argument) {
        this.argument = argument;
    }

    protected CommandAction action;

    /**
     * Get command action
     * @return action
     * @see CommandAction
     */
    public CommandAction getAction() {
        return this.action;
    }

    /**
     * Construct a Command with specified label
     * @param label -- specified label
     */
    public Command(CommandLabel label) {
        this.label = label;
    }

    /**
     * Construct a Command and provide argument
     * @param label -- specified label
     * @param argument -- provided argument for executing
     */
    public Command(CommandLabel label, CommandArgument argument) {
        this(label);
        this.setArgument(argument);
    }
}

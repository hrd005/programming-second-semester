package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.data.Collection;
import stepanoff.denis.lab5.itil.ConsoleWriter;

import java.io.BufferedReader;

/**
 * Parent class for all commands.
 */
public abstract class Command {

    /**
     * A String used to identify command in user input
     */
    protected String name;

    /**
     * @return String used to identify command in user input
     */
    public String getName() {
        return this.name;
    }

    /**
     * Action a command is supposed to do.
     */
    protected Action action;

    /**
     * @return Action a command is supposed to do.
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * String describes command action to build help.
     */
    protected String description;

    /**
     * @return help string for this command.
     */
    public String getHelp() {
        return ConsoleWriter.getColored(String.format("%-31s", this.name), ConsoleWriter.Color.CYAN) + this.description;
    }

    /**
     * <p>Collection the program works with currently.</p>
     * <p>Can be useful in writing command action.</p>
     */
    protected Collection collection;

    /**
     * @param collection is a collection the command will be working on.
     */
    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    /**
     * <p>Input channel the program currently working with.</p>
     * <p>Can be useful in writing command action.</p>
     */
    protected BufferedReader input;

    /**
     * @param input is a BufferedReader which command supposed to read during execution.
     */
    public void setInput(BufferedReader input) {
        this.input = input;
    }

    /**
     * Used to define if necessary to ask user again for input or it is just script.
     */
    protected boolean repeatedInput;

    /**
     * @param repeatedInput true -- user supposed to be asked again for invalid input
     *                      false -- program will not ask for valid input again
     */
    public void setMode(boolean repeatedInput) {
        this.repeatedInput = repeatedInput;
    }

    /**
     * <p>A representation of command action, can be used as lambda.</p>
     * <p>A single method -- execute -- receives array of String parameters, [0] contains the command name itself.
     * Existing of other indexes is not guaranteed and depends on validity of user input.</p>
     */
    @FunctionalInterface
    public interface Action {
        void execute(String... args);
    }
}

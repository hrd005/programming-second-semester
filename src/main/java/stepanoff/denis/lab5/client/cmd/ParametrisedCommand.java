package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.common.util.ConsoleWriter;

import static stepanoff.denis.lab5.common.util.ConsoleWriter.getColored;

/**
 * Overrides standard getHelp() method to make it uses command parameters.
 */
public abstract class ParametrisedCommand extends Command {

    /**
     * Command parameters (String description) for help building.
     */
    protected String params;

    /**
     * @return help string for this command.
     */
    @Override
    public String getHelp() {
        return String.format("%-49s", getColored(this.name, ConsoleWriter.Color.CYAN) + " "
                + getColored(this.params, ConsoleWriter.Color.PURPLE)) + this.description;
    }
}

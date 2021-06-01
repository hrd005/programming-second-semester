package stepanoff.denis.lab5.client.cmd;

import stepanoff.denis.lab5.common.util.ConsoleWriter;

/**
 * Implementation of 'help' command. This command is added to CommandSet automatically.
 * @see CommandStore
 */
public class HelpCommand extends Command {

    private final CommandStore commandStore;

    /**
     * @param commandStore - CommandSet for which help should be built.
     */
    public HelpCommand(CommandStore commandStore) {
        this.name = "help";
        this.description = ": list all the commands.";
        this.commandStore = commandStore;

        this.action =
                (String... args) -> this.commandStore.forEach(
                        (Command c) -> ConsoleWriter.println(c.getHelp())
                );
    }
}

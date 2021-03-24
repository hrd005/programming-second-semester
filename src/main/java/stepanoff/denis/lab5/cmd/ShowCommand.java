package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.itil.ConsoleWriter;

/**
 * Implementation of 'show' command.
 */
public class ShowCommand extends Command {

    {
        this.name = "show";
        this.description = ": print all elements in collection.";

        this.action = (String... a) -> ConsoleWriter.println(collection.toString());
    }
}

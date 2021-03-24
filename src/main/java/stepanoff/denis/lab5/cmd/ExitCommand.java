package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.itil.ConsoleWriter;

/**
 * Implementation of 'exit' command in user console. Implementation for scripts located in ExecutorService
 * @see ExecutorService
 */
public class ExitCommand extends Command {

    {
        this.name = "exit";
        this.description = ": quit application without saving changes.";
        this.action = (String... a) -> {
            ConsoleWriter.println("See you soon! ;-)", ConsoleWriter.Color.CYAN);
            System.exit(0);
        };
    }
}

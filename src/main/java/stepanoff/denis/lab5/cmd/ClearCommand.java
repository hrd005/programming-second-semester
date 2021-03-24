package stepanoff.denis.lab5.cmd;

/**
 * <p>Implementation of 'clear' command.</p>
 */
public class ClearCommand extends Command {

    {
        this.name = "clear";
        this.description = ": removes all elements in collection.";

        this.action = (String... a) -> this.collection.clear();
    }
}

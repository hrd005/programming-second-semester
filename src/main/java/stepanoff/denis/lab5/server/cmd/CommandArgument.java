package stepanoff.denis.lab5.server.cmd;

/**
 * This class encapsulates argument for command
 * @see Command
 */
public class CommandArgument {

    private final Object value;
    public CommandArgument(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }
}

package stepanoff.denis.lab5.common.cmd;

import java.io.Serializable;

/**
 * Encapsulates request string
 */
public class CommandLabel implements Serializable {

    public final String label;

    public CommandLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommandLabel)) return false;
        return label.equals(((CommandLabel) o).label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}

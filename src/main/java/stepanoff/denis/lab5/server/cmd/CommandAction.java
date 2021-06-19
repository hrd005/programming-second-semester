package stepanoff.denis.lab5.server.cmd;

import stepanoff.denis.lab5.common.util.Authentication;
import stepanoff.denis.lab5.common.util.TypedEntity;
import stepanoff.denis.lab5.server.Collection;

import java.util.LinkedList;

/**
 * Interface for action of Command
 * @see Command
 */
public interface CommandAction {

    LinkedList<TypedEntity> execute(Collection collection, CommandArgument argument, Authentication auth);

}

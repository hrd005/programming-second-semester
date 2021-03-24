package stepanoff.denis.lab5.cmd;

import stepanoff.denis.lab5.itil.ConsoleWriter;

/**
 * Implementation of 'help' command. This command is added to CommandSet automatically.
 * @see CommandSet
 */
public class HelpCommand extends Command {

    private final CommandSet commandSet;

    /**
     * @param commandSet - CommandSet for which help should be built.
     */
    public HelpCommand(CommandSet commandSet) {
        this.name = "help";
        this.description = ": list all the commands.";
        this.commandSet = commandSet;

        this.action =
                (String... args) -> this.commandSet.forEach(
                        (Command c) -> ConsoleWriter.println(c.getHelp())
                );
    }

    //{


//        this.action = (String... args) -> ConsoleWriter.println(
//                ConsoleWriter.getColored("help", ConsoleWriter.Color.CYAN)
//                        + ": list all the commands.\n" +
//                ConsoleWriter.getColored("info", ConsoleWriter.Color.CYAN)
//                        + ": print information about stored collection.\n" +
//                ConsoleWriter.getColored("show", ConsoleWriter.Color.CYAN)
//                        + ": print all the elements of collection.\n" +
//                ConsoleWriter.getColored("add", ConsoleWriter.Color.CYAN)
//                        + " " + ConsoleWriter.getColored("{ element }", ConsoleWriter.Color.PURPLE)
//                        + ": add new element to collection.\n" +
//                ConsoleWriter.getColored("update", ConsoleWriter.Color.CYAN)
//                        + " " + ConsoleWriter.getColored("id { element }", ConsoleWriter.Color.PURPLE)
//                        + ": update element with specified id.\n" +
//                ConsoleWriter.getColored("remove_by_id", ConsoleWriter.Color.CYAN)
//                        + " " + ConsoleWriter.getColored("id", ConsoleWriter.Color.PURPLE)
//                        + ": remove element with specified id.\n" +
//                ConsoleWriter.getColored("clear", ConsoleWriter.Color.CYAN)
//                        + ": clear collection.\n"
//        );
   // }
}

package stepanoff.denis.lab5.cmd;

/**
 * Just empty command. Used to mark, that command should be executed by ExecutorService and to build help.
 * Implementation located in ExecutorService.executeScript() because of high coherency between action and executor entity.
 * @see ExecutorService
 */
public class ExecuteScriptCommand extends ParametrisedCommand {

    {
        this.name = "execute_script";
        this.params = "file_name";
        this.description = ": read and execute commands from file.";

//        this.action = (String... s) -> {
//            if (s.length <= 1 || s[1].isEmpty()) {
//                println("Script file is not specified.", ConsoleWriter.Color.RED);
//                return;
//            }
//
//            try {
//                InputStream inputStream = FileIO.createInputStream(s[1]);
//                Collection col = this.caller.isRoot() ? this.collection.copy() : this.collection;
//                CommandSet commandSet = CommandSet.DEFAULT;
//                String identifier = s[1];
//
//                Collection modified = this.caller.start(inputStream, commandSet, col, identifier);
//                if (modified != null) {
//                    this.caller.onChildExecutorFinished(identifier, modified);
//                }
//
//            } catch (MaxRecursionDepthException | FileReadingException e) {
//                println(e.getMessage(), ConsoleWriter.Color.RED);
//            }
//        };
    }
}

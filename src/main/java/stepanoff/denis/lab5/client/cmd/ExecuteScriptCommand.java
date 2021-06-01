package stepanoff.denis.lab5.client.cmd;

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
    }
}

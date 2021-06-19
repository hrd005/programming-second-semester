package stepanoff.denis.lab5.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stepanoff.denis.lab5.server.cmd.CommandLineExecutor;
import stepanoff.denis.lab5.server.cmd.Executor;
import stepanoff.denis.lab5.server.dataio.DataSourceFactory;
import stepanoff.denis.lab5.server.net.ConnectionManager;
import stepanoff.denis.lab5.common.util.ConsoleWriter;
import stepanoff.denis.lab5.server.dataio.file.FileIO;
import stepanoff.denis.lab5.server.dataio.file.FileReader;
import stepanoff.denis.lab5.common.dataio.FileReadingException;

import java.io.IOException;

/**
 * Welcome to the Server-side code
 * It is a Main class and an entry point for server
 */
public class Main {

    private static Executor executor;

    private static final Logger logger = LoggerFactory.getLogger("ServerMain");

    public static void main(String... args) {

        logger.info("Server starting...");

//        if (args.length == 0) {
//            ConsoleWriter.println("The data file name has not been passed.\nStop execution", ConsoleWriter.Color.RED);
//            logger.error("The data file name has not been passed.\nStop execution");
//            System.exit(-1);
//        }

        //FileReader fileReader = FileIO.getReader();
        Collection collection = DataSourceFactory.getDefault().readAll();
//        try {
//            //collection = fileReader.parseFile(args[0]);
//        } catch (FileReadingException e) {
//            logger.error("Fatal Errors during File reading" + e.getMessage());
//            ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
//            return;
//        }

        executor = new Executor(collection);
        try {
            ConnectionManager cnmgr = new ConnectionManager();
            Thread server = new Thread(cnmgr::start);
            server.start();
            logger.info("Server started.");
            new CommandLineExecutor(System.in, cnmgr).cycle();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Get configured executor.
     * @return Executor
     */
    public static Executor provideExecutor() {
        return executor;
    }

    /**
     * Get Main Server logger
     * @return Logger
     */
    public static Logger provideLogger() {
        return logger;
    }
}

package stepanoff.denis.lab5;

import stepanoff.denis.lab5.cmd.CommandSet;
import stepanoff.denis.lab5.cmd.ExecutorService;
import stepanoff.denis.lab5.cmd.MaxRecursionDepthException;
import stepanoff.denis.lab5.data.Collection;
import stepanoff.denis.lab5.io.FileIO;
import stepanoff.denis.lab5.io.FileReader;
import stepanoff.denis.lab5.io.FileReadingException;
import stepanoff.denis.lab5.itil.ConsoleWriter;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            ConsoleWriter.println("The data file name has not been passed.\nStop execution", ConsoleWriter.Color.RED);
            System.exit(-1);
        }

        FileReader fileReader = FileIO.getReader();
        Collection collection;
        try {
            collection = fileReader.parseFile(args[0]);

            ExecutorService.start(System.in, CommandSet.DEFAULT, collection);
        } catch (FileReadingException | MaxRecursionDepthException e) {
            e.printStackTrace();
        }
    }
}

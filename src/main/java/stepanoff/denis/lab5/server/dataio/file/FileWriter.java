package stepanoff.denis.lab5.server.dataio.file;

import stepanoff.denis.lab5.server.Collection;

import java.io.File;

/**
 * Abstract writer for collection data
 */
public interface FileWriter {

    /**
     * Write all data to file
     * @param collection collection should be saved to file
     * @param file file where data supposed to be stored
     * @throws FileWritingException when it is impossible to write in specified file
     */
    void write(Collection collection, File file) throws FileWritingException;
}

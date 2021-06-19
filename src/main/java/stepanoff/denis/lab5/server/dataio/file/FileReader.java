package stepanoff.denis.lab5.server.dataio.file;

import stepanoff.denis.lab5.common.dataio.FileReadingException;
import stepanoff.denis.lab5.server.Collection;

/**
 * Abstract Reader for collection data
 */
public interface FileReader {

    /**
     * Read all data
     * @param filename String with filename/filepath where data is stored
     * @return Collection with all data
     * @throws FileReadingException if it is ot possible to read from specified file
     * @see Collection
     */
    Collection parseFile(String filename) throws FileReadingException;
}

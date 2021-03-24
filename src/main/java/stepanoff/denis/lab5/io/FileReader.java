package stepanoff.denis.lab5.io;

import stepanoff.denis.lab5.data.Collection;

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

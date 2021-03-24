package stepanoff.denis.lab5.io;

import java.io.*;

/**
 * IO Utilities
 */
public class FileIO {

    /**
     * Get Reader for collection data
     * @return FileWriter for current data format
     */
    public static FileReader getReader() {
        return new CsvFileReader();
    }

    /**
     * Get Writer for collection data
     * @return FileReader for current data format
     */
    public static FileWriter getWriter() {
        return new CsvFileWriter();
    }

    /**
     * Create input stream for a random file
     * @param filename string with filename/filepath
     * @return InputStream for this file
     * @throws FileReadingException if not possible to create InputStream
     */
    public static InputStream createInputStream(String filename) throws FileReadingException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileReadingException("File Not Found: " + filename + "\n");
        }
        if (!file.isFile()) {
            throw new FileReadingException(filename + " is not a file.\n");
        }
        if (!file.canRead()) {
            throw new FileReadingException("Reading file " + filename + " is not permitted.\n");
        }

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileReadingException("File Not Found: " + filename + "\n");
        }
    }
}

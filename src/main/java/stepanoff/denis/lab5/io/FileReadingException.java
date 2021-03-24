package stepanoff.denis.lab5.io;

/**
 * Exception for cases when something happened with reading file
 */
public class FileReadingException extends Exception {

    public FileReadingException(String msg) {
        super(msg);
    }
}

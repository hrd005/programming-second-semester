package stepanoff.denis.lab5.io;

/**
 * Exception for cases when file is corrupted.
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String msg) {
        super(msg);
    }
    public InvalidFileException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

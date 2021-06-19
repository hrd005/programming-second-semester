package stepanoff.denis.lab5.server.dataio.file;

/**
 * Exception for cases when something happened with writing file
 */
public class FileWritingException extends Exception {
    public FileWritingException(String msg) {
        super(msg);
    }
}

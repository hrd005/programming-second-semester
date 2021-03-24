package stepanoff.denis.lab5.data;

/**
 * Exception signalizes that not all required fields in Builder are initialized.
 */
public class NotInitializedEntityException extends RuntimeException {

    public NotInitializedEntityException(String msg) {
        super(msg);
    }
}

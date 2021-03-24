package stepanoff.denis.lab5.cmd;

/**
 * Marker exception signalizing that recursion is too deep.
 */
public class MaxRecursionDepthException extends Exception {

    public MaxRecursionDepthException(String msg) {
        super(msg);
    }
}

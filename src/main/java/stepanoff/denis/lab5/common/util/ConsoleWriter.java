package stepanoff.denis.lab5.common.util;

/**
 * Utility class supposed to organize println's
 */
public class ConsoleWriter {

    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * Print string to standard out stream
     * @param str String will be printed out
     */
    public static void println(String str) {
        System.out.println(str);
    }

    /**
     * Print string to standard out stream colorized
     * @param str String will be printed out
     * @param color Color will be used
     * @see Color
     */
    public static void println(String str, Color color) {
        System.out.println(color.ansiCode + str + ANSI_RESET);
    }

    /**
     * Print invitation to type command
     */
    public static void printPrompt() {
        printPrompt("> ");
    }

    /**
     * Print custom invitation to input
     * @param prompt String will be printed out
     */
    public static void printPrompt(String prompt) {
        System.out.print(prompt);
    }

    /**
     * Print colored custom invitation to input
     * @param prompt String will be printed out
     * @param color Color will be used
     * @see Color
     */
    public static void printPrompt(String prompt, Color color) {
        System.out.print(color.ansiCode + prompt + ANSI_RESET);
    }

    /**
     * Get String with necessary ANSI codes of colors
     * @param s string
     * @param color color
     * @return string with ANSI codes of specified color
     */
    public static String getColored(String s, Color color) {
        return color.ansiCode + s + ANSI_RESET;
    }

    /**
     * Colors available.
     */
    public enum Color {

        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        private final String ansiCode;

        Color(String ansiCode) {
            this.ansiCode = ansiCode;
        }
    }
}

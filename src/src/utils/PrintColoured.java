package utils;

public class PrintColoured {

    // ANSI escape codes for colors
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";

    public static void printColored(String message, String color) {
        System.out.println(color + message + RESET);
    }

    public static void red(String message) {
        printColored(message, RED);
    }

    public static void green(String message) {
        printColored(message, GREEN);
    }

    public static void yellow(String message) {
        printColored(message, YELLOW);
    }

    public static void blue(String message) {
        printColored(message, BLUE);
    }

    public static void cyan(String message) {
        printColored(message, CYAN);
    }

    public static void white(String message) {
        printColored(message, WHITE);
    }
}


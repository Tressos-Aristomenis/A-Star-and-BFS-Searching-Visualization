package exception;

public class FileFormatException extends Exception {
    public FileFormatException(String line, int lineNumber) {
        System.err.println("ERROR: File could not be formatted properly. Check the syntax.");
        System.err.println("HINT: Check line " + "(" + lineNumber + ") | " + line);		// specifies the line number and the line that cause the error.
        System.exit(-1);
    }
}

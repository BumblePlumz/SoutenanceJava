package projet;

public class CommandeException extends RuntimeException {
    public CommandeException(Exception ex)
    {
        super(ex);
    }
    public CommandeException(String message) {
        super(message);
    }
}

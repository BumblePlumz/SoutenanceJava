package projet;

/**
 * Classe de gestion d'erreur des commandes
 * @author Nguyen Nicolas
 * @version 1.00
 */
class CommandeException extends RuntimeException {
    public CommandeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

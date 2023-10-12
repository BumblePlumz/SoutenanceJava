package projet;
/**
 * Classe de gestion d'erreur de la fenêtre : paramètres
 * @author Nguyen Nicolas
 * @version 1.00
 */
public class GUIParametreException extends RuntimeException {
    public GUIParametreException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

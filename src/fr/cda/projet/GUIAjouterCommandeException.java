package projet;
/**
 * Classe de gestion d'erreur de la fenêtre : ajouter commande
 * @author Nguyen Nicolas
 * @version 1.00
 */
public class GUIAjouterCommandeException extends RuntimeException {
    public GUIAjouterCommandeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

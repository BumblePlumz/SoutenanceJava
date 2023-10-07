package projet;

import java.util.*;

/**
 * Classe de définition d'une commande
 *
 * @author Nguyen Nicolas
 * @version 0.05
 */
public class Commande
{
    // Les caracteristiques d'une commande
    //
    private int     numero;         // numero de la commande
    private String  date;           // date de la commande. Au format JJ/MM/AAAA
    private String  client;         // nom du client
    private List<String> references; // les references des produits de la commande
    private boolean livrer = false;
    private String raison = "";

    public Commande(int numero, String date, String client) {
        this.numero = numero;
        this.date = date;
        this.client = client;
        this.references = new ArrayList<>();
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public boolean isLivrer() {
        return livrer;
    }

    public void setLivrer(boolean livrer) {
        this.livrer = livrer;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public void ajoutRaison(String raison)  {
        this.raison += raison;
    }

    /**
     * On met les références en forme pour les afficher
     * @param bool true pour afficher l'attribut raison
     * @return une chaîne de caractère contenant les références d'une commande
     * @author Nguyen Nicolas
     */
    private String refsToString(boolean bool){
        StringBuilder sb = new StringBuilder();
        String alinea = "\t";
        String n = "\n";
        if (references != null){
            sb.append(n);
            sb.append(alinea);
            sb.append(n);
            for (String s : references) {
                String[] strings = s.split("=");
                String ref = "Réference   : "+strings[0];
                String quantite = "Quantité    : "+strings[1];

                sb.append(alinea);
                sb.append(ref);
                sb.append(n);
                sb.append(alinea);
                sb.append(quantite);
                sb.append(n);
                sb.append(alinea);
                sb.append(n);
            }
        }
        // L'affichage doit-il prendre en compte les raisons
        if (!bool){
            sb.append("=================================================");
        }else{
            // On vérifie que la commande n'est pas livrée
            if (!livrer){
                System.out.println("Début de la mise en forme");
                String[] raisons = raison.split(";");

                if (raison.length() > 0){
                    for (int i = 0; i < raisons.length; i++) {
                        if (!raisons[i].isEmpty()){
                            System.out.println("Raison "+i+" : "+raisons[i]);
                            String[] donnees = raisons[i].split("=");
                            sb.append("Il manque "+donnees[1]+" "+donnees[0]);
                            sb.append(n);
                        }
                    }
                }else{
                    Site.logger.info("Une erreur s'est produite dans la mise en forme des raisons d'une commande");
                }
            }else{
                sb.append("La commande a été livrée");
                sb.append(n);
            }
            sb.append("=================================================");
        }
        return sb.toString();
    }
    public String toStringLivrable(){
        return String.format(" Commande : %-5d \n Date     : %-15s \n Client   : %-15s %s", numero, date, client, refsToString(true));
    }
    @Override
    public String toString() {
        return String.format(" Commande : %-5d \n Date     : %-15s \n Client   : %-15s %s", numero, date, client, refsToString(false));
    }
}
/**
 * Classe de gestion d'erreur des commandes
 *
 * @author Nguyen Nicolas
 * @version v1
 */
class CommandeException extends RuntimeException {
    public CommandeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
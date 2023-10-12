package projet;

import java.util.*;

/**
 * Classe de définition d'une commande
 *
 * @author Nguyen Nicolas
 * @version 1.00
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

    public Commande(int numero, String date, String client, List<String> references) {
        this.numero = numero;
        this.date = date;
        this.client = client;
        this.references = references;
    }

    public String getDate() {
        return date;
    }

    public String getClient() {
        return client;
    }

    public int getNumero() {
        return numero;
    }

    public List<String> getReferences() {
        return references;
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

    public String[] getSplitRaison() {
        return raison.split(";");
    }
    public void setRaison(String raison) {
        this.raison = raison;
    }

    public void ajoutRaison(String raison)  {
        this.raison += raison;
    }

    /**
     * Récupérer la mise en forme de la commande pour la sauvegarder
     * @param commande la commande que l'on souhaite mettre en forme
     * @param compteur le compteur d'index des commandes
     * @return String la mise en forme
     */
    public String formatSauvegardeCommande(Commande commande) {
        StringBuilder sb = new StringBuilder();
        for (String ref : commande.getReferences()) {
            sb.append(commande.getNumero());
            sb.append(";");
            sb.append(commande.getDate());
            sb.append(";");
            sb.append(commande.getClient());
            sb.append(";");
            sb.append(ref);
            sb.append(";");
            if (commande.isLivrer()){
                sb.append("true");
            }else{
                sb.append("false");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Récupérer la mise en forme des références, des raisons et de l'état livrable.
     * @param afficherRaison true pour afficher l'attribut raison
     * @param afficherLivrer true pour afficher l'attribut livrer
     * @return une chaîne de caractère contenant les références d'une commande
     */
    private String refsToString(boolean afficherRaison, boolean afficherLivrer){
        StringBuilder sb = new StringBuilder();

        // Afficher les références au format d'affichage
        if (references != null){
            formatAfficherReference(sb);
        }else{
            Site.logger.fatal("Une corruption de commande s'est produite ! Une vérification est nécessaire !");
        }

        // L'affichage, doit-il prendre en compte les raisons
        if (afficherRaison){
            formatAfficherRaison(sb);
        }

        // L'affichage, doit-il prendre en compte l'état livrable de la commande
        if (afficherLivrer){
            formatAfficherLivrer(sb);
        }

        // Clotûre de l'affichage
        sb.append("=================================================");
        return sb.toString();
    }

    /**
     * Récupérer la mise en forme des références de la commande pour l'affichage
     * @param sb StringBuilder
     * @return StringBuilder formatter pour l'affichage
     */
    private StringBuilder formatAfficherReference(StringBuilder sb) {
        String alinea = "\t";
        String n = "\n";

        sb.append(n);
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

        return sb;
    }

    /**
     * Récupérer la mise en forme d'une raison au format : "Il manque [référence] [quantité]"
     * @param sb StringBuilder
     * @return StringBuilder contenant l'affichage de la raison.
     */
    private StringBuilder formatAfficherRaison(StringBuilder sb) {
        // On vérifie que la commande n'est pas livrée
        if (!livrer) {
            String[] raisons = getSplitRaison();
            // On vérifie que des raisons exitent
            if (raisons.length > 0){
                for (int i = 0; i < raisons.length; i++) {
                    // On vérifie que les raisons ne sont pas vides
                    if (!raisons[i].isEmpty()){
                        String[] donnees = raisons[i].split("=");
                        String texte = "Il manque "+donnees[1]+" "+donnees[0];
                        sb.append(texte);
                        sb.append("\n");
                    }
                }
            }else{
                Site.loggerInfo.info("Une erreur s'est produite dans la mise en forme des raisons d'une commande");
            }
        }

        return sb;
    }

    /**
     * Récupérer le bon affichage pour la commande, à savoir si elle est livré/prête/non livrer
     * @param sb StringBuilder
     * @return StringBuilder contenant l'état de la commande
     */
    private StringBuilder formatAfficherLivrer(StringBuilder sb) {
        // L'affichage, doit-il prendre en compte l'annotation livrer
        if (livrer){
            sb.append("La commande a été livrée");
            sb.append("\n");
        }else{
            if (this.raison.isEmpty()){
                sb.append("La commande est prête à être livrée");
                sb.append("\n");
            }else{
                sb.append("!!! La commande n'a pas été livrée !!!");
                sb.append("\n");
            }
        }
        return sb;
    }

    /**
     * Surcharge de la méthode toString
     * @param afficherRaison Afficher les raisons
     * @param afficherLivrer Afficher l'état de 'livrer' de la commande
     * @return String formatter pour l'affichage
     */
    public String toString(Boolean afficherRaison, Boolean afficherLivrer){
        return String.format(" Commande : %-5d \n Date     : %-15s \n Client   : %-15s %s", numero, date, client, refsToString(afficherRaison, afficherLivrer));
    }

    @Override
    public String toString() {
        return String.format(" Commande : %-5d \n Date     : %-15s \n Client   : %-15s %s", numero, date, client, refsToString(false, true));
    }
}
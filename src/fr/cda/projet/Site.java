package projet;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.*;


// TODO Enlever les sout

/**
 * Classe de gestion des données du site de vente
 *
 * @author Vendor & Nguyen Nicolas
 * @version 0.05
 */
public class Site
{
    public static final Logger logger = LogManager.getLogger(Site.class); // Gestion des logs
    private final List<Produit> stock = new ArrayList<>();       // Les produits du stock
    private final List<Commande> commandes = new ArrayList<>();  // Les bons de commande


    // Constructeur
    //
    public Site()
    {
        try{
            // lecture du fichier resources/Produits.txt | pour chaque ligne on créer un Produit que l'on ajoute a stock
            initialiserStock("src/fr/resources/Produits.txt");

            // lecture du fichier resources/Commandes.txt |  pour chaque ligne on créer une Commande que l'on ajoute à commandes ou l'on ajoute la une référence d'un produit a une commande existante
            initialiserCommandes("src/fr/resources/Commandes.txt");

            // Calcul des stocks
            calculInitialStock();
        }catch (IndexOutOfBoundsException e){
            logger.error("Une boucle sur une collection a générée une erreur", e);
        }catch(NullPointerException e){
            logger.error("Une collection n'a pas pu être chargée correctement", e);
        }catch(CommandeException e){
            logger.error(e.getMessage(), e.getCause(), e.getStackTrace());
        }
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public List<Produit> getStock() {
        return stock;
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere tous les produits du stock
     * @return retourne un toString de tous les produits
     * @author Nguyen Nicolas
     */
    public String listerTousProduits() {
        // L'utilisation d'un stringbuilder est moins coûteuse à grande échelle.
        StringBuilder sb = new StringBuilder();
        for(Produit prod : stock){
            sb.append(prod.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere toutes les commandes
     * @return retourne un toString de toutes les commandes
     * @author Nguyen Nicolas
     */
    public String listerToutesCommandes()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < commandes.size(); i++) {
            if (i > 0){
                sb.append("\n");
                sb.append(commandes.get(i));
            }
        }
        return sb.toString();
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere une commande
     * @param numero Numéro d'une commande
     * @return Retourne un toString de la commande
     * @author Nguyen Nicolas
     */
    public String listerCommande(int numero)
    {
        StringBuilder sb = new StringBuilder();
        try {
            if (numero > 0 && numero < commandes.size()){
                sb.append(commandes.get(numero).toStringLivrable());
            }else{
                logger.info("Une recherche hors champs a été détectée");
            }
        }catch (IndexOutOfBoundsException e){
            logger.error("Une recherche a provoquée une erreur car l'index n'existe pas", e);
        }catch (NullPointerException e){
            logger.error("Une recherche a provoquée une erreur car la liste de commande n'existe pas ou est vide", e);
        }
        return sb.toString();
    }

    /**
     * Chargement du fichier resources/Produits.txt
     * @param nomFichier fichier de sauvegarde des données de la classe Produit
     * @author vendor
     */
    private void initialiserStock(String nomFichier)
    {
        String[] lignes = Terminal.lireFichierTexte(nomFichier);
        try{
            for(String ligne :lignes) {
                String[] champs = ligne.split("[;]",4);
                String reference = champs[0];
                String nom = champs[1];
                double prix = Double.parseDouble(champs[2]);
                int quantite =  Integer.parseInt(champs[3]);
                Produit p = new Produit(reference,
                                        nom,
                                        prix,
                                        quantite
                                        );
                stock.add(p);
            }
        }catch (NullPointerException e){
            logger.error("Une erreur est survenu dans l'initialisation des stock", e);
        }
    }

    /**
     * Chargement du fichier resources/Commande.txt
     * @param nomFichier fichier de sauvegarde des données de la classe Command
     * @author Nguyen Nicolas
     */
    private void initialiserCommandes(String nomFichier) {
        String[] lignes = Terminal.lireFichierTexte(nomFichier);
        commandes.add(0, null);
        // On vérifie qu'il n'y a pas eu d'erreur si le chargement du fichier
        if (lignes == null){ throw new CommandeException("Une erreur s'est produite dans le chargement du fichier de données des commandes", new NullPointerException()); }

        // On boucle sur les lignes du fichier
        for (String ligne : lignes){
            // On éclate la ligne pour récupérer les informations
            String[] champs = ligne.split(";");

            // On vérifie que les données ne sont pas corrompues
            if (champs.length != 4){ throw new CommandeException("Une corruption de donnée s'est produite dans l'initialisation des commandes", new IndexOutOfBoundsException()); }

            // On récupère le numéro de la commande et on découpe les données
            int numero = Integer.parseInt(champs[0]);
            String date = champs[1];
            String client = champs[2];
            String ref = champs[3];

            // On dirige la donnée au bon endroit (Si la commande existe on ajoute sinon on créer)
            if (numero > 0 && numero < commandes.size() ){
                Commande commande = commandes.get(numero);
                commande.getReferences().add(ref);
            }else{
                Commande commande = new Commande(numero, date, client);
                commande.getReferences().add(ref);
                commandes.add(commande);
            }
        }
    }

    /**
     * Afficher toutes les commandes non livrées
     * @return liste sous forme de string
     * @author Nguyen Nicolas
     */
    public String listerToutesCommandesNonLivrer() {
        StringBuilder sb = new StringBuilder();
        try{
            for (Commande commande : commandes) {
                if (commande != null && !commande.isLivrer()){
                    sb.append("\n");
                    sb.append(commande.toStringLivrable());
                }
            }
        }catch (NullPointerException e){
            logger.error("Une erreur s'est produite dans l'affichage de toutes les commandes non livrées", e);
        }
        return sb.toString();
    }

    /**
     * Calcul initial des commandes livrer et des stocks restant ainsi que des quantitées manquante pour valider une commande
     * @author Nguyen Nicolas
     */
    public void calculInitialStock() {

        // Pour toutes les commandes
        for (Commande commande : commandes) {
            if (commande != null && !commande.isLivrer()){
                commande.setRaison("");
                List<String> refs = commande.getReferences();
                int refValider = 0;

                // Pour toutes les références d'une commande
                for (String ref : refs) {
                    String[] refParts = ref.split("=");
                    String reference = refParts[0];
                    int quantite = Integer.parseInt(refParts[1]);
                    // Vérification de la corrumption des données
                    if (refParts.length != 2) { throw new CommandeException("Une corruption de donnée s'est produite dans des références de commande", new IndexOutOfBoundsException()); }

                    // Pour tous les produits
                    for (Produit produit : stock) {
                        if (produit.getReference().equals(reference)){
                            // Vérification de la disponibilité
                            if (!produit.isCalculQuantite(quantite)){
                                formatRaison(commande, quantite, produit);
                                logger.info("La commande : "+ commande.getNumero()+" n'a pas été validée");
                            }else{
                                // Soustraction de la quantitée
                                produit.soutraireStock(quantite);
                                logger.info("Retiré du stock : "+quantite+" "+produit.getReference());
                                refValider++;
                            }
                        }
                    }
                }
                // Si on a validé autant de référence qu'il en existe on peut livrer la commande
                if (refValider == commande.getReferences().size()){
                    commande.setLivrer(true);
                    logger.info("La commande : "+commande.getNumero()+" a été validée");
                }
            }
        }
    }

    public void reCalculerStock(Commande commande){
        String[] raisons = commande.getRaison().split("=");
        // On boucle sur les references de la commande
        for(String reference : commande.getReferences()){
            String[] refs = reference.split("=");
            // On vérifie que la référence est dans la liste des raisons
            if (refs[0].equals(raisons[0])){
                // On boucle sur les produits
                for (Produit produit : stock) {
                    // On vérifie que la référence du produit est équivalente à la référence de la raison
                    if (produit.getReference().equals(raisons[0])){
                        produit.soutraireStock(Integer.parseInt(refs[1]));
                        logger.info("Retiré du stock : "+refs[1]+" "+produit.getReference());
                    }
                }
            }
        }
    }

    private void formatRaison(Commande commande, int quantite, Produit produit) {
        StringBuilder sb = new StringBuilder();

        // Mise en forme de la donnée raison
        String raison = produit.miseEnFormeRaison(quantite);
        sb.append(raison);
        sb.append(";");

        // Vérification pour savoir si une raison existe ou non
        if (commande.getRaison().isEmpty()){
            commande.setRaison(sb.toString());
        }else{
            commande.ajoutRaison(sb.toString());
        }
    }
}
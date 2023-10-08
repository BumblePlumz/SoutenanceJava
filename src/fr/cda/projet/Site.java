package projet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.*;

/**
 * Classe de gestion des données du site de vente
 *
 * @author Vendor & Nguyen Nicolas
 * @version 1.00
 */
public class Site
{
    public static final Logger logger = LogManager.getLogger(Site.class); // Gestion des logs d'erreurs
    public static final Logger loggerInfo = LogManager.getLogger("info");
    public static int indexCommande = 1;
    public static int indexStock = 0;
    private final List<Produit> stock = new ArrayList<>();       // Les produits du stock
    private final List<Commande> commandes = new ArrayList<>();  // Les bons de commande

    /**
     * Constructeur
     * L'initialisation du programme passe par l'utilisation du constructeur de la classe Site
     */
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

    private void initialiserIndexCommande() {

    }

    public int getIndexCommande() {
        return indexCommande;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public List<Produit> getStock() {
        return stock;
    }

    /**
     * Chargement du fichier resources/Produits.txt
     * @param nomFichier fichier de sauvegarde des données de la classe Produit
     * @exception NullPointerException la procédure peut générer une exception
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
                this.indexStock++;
            }
        }catch (NullPointerException e){
            logger.error("Une erreur est survenu dans l'initialisation des stock", e);
        }
    }

    /**
     * Chargement du fichier resources/Commande.txt
     * @param nomFichier fichier de sauvegarde des données de la classe Command
     * @exception CommandeException la procédure peut générer une exception
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
            if (champs.length < 4 || champs.length > 5){ throw new CommandeException("Une corruption de donnée s'est produite dans l'initialisation des commandes", new IndexOutOfBoundsException()); }

            // On récupère le numéro de la commande et on découpe les données
            int numero = Integer.parseInt(champs[0]);
            String date = champs[1];
            String client = champs[2];
            String ref = champs[3];
            boolean livrer = false;
            if (champs.length == 5){
                livrer = champs[4].equals("true");
            }

            // On dirige la donnée au bon endroit (Si la commande existe on ajoute sinon on créer)
            if (numero > 0 && numero < commandes.size() ){
                Commande commande = commandes.get(numero);
                commande.getReferences().add(ref);
            }else{
                Commande commande = new Commande(numero, date, client);
                if (champs.length == 5){
                    commande.setLivrer(livrer);
                }
                commande.getReferences().add(ref);
                commandes.add(commande);
                this.indexCommande++;
            }
        }
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
    public String listerToutesCommandes(boolean calcul)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < commandes.size(); i++) {
            if (i > 0){
                if (calcul){
                    // On calcul la commande pour être sur que les données raison sont à jour
                    calculStock(commandes.get(i));
                }else{
                    estValide(commandes.get(i));
                }
                sb.append("\n");
                sb.append(commandes.get(i).toString(true, true));
            }
        }
        return sb.toString();
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere une commande
     * @param numero Numéro d'une commande
     * @return Retourne un toString de la commande
     * @exception IndexOutOfBoundsException la méthode peut générer une exception
     * @exception NullPointerException la méthode peut générer une exception
     * @author Nguyen Nicolas
     */
    public String listerCommande(int numero, boolean calcul)
    {
        StringBuilder sb = new StringBuilder();
        try {
            if (numero > 0 && numero < commandes.size()){
                if (calcul){
                    // On calcul la commande pour être sur que les données raison sont à jour
                    calculStock(commandes.get(numero));
                }else{
                    estValide(commandes.get(numero));
                }
                sb.append(commandes.get(numero).toString(true, true));
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
     * Afficher toutes les commandes non livrées
     * @return liste sous forme de string
     * @author Nguyen Nicolas
     */
    public String listerToutesCommandesNonLivrer() {
        StringBuilder sb = new StringBuilder();
        try{
            for (Commande commande : commandes) {
                if (commande != null && !commande.isLivrer()){
                    // On calcul la commande pour être sur que les données raison sont à jour
                    estValide(commande);
                    sb.append("\n");
                    sb.append(commande.toString(true, true));
                }
            }
        }catch (NullPointerException e){
            logger.error("Une erreur s'est produite dans l'affichage de toutes les commandes non livrées", e);
        }
        return sb.toString();
    }

    /**
     * Calcul initial des commandes livrer et des stocks restant ainsi que des quantitées manquante pour valider une commande
     * @exception CommandeException la procédure peut générer une exception
     * @author Nguyen Nicolas
     */
    public void calculInitialStock() {
        // Pour toutes les commandes
        for (Commande commande : commandes) {
            if (commande != null && !commande.isLivrer()){
                calculStock(commande);
            }
        }
    }

    /**
     * Calculer et ajuster le stock par rapport a une commande (Si les stocks le permettent livre une commande et soustrait les stocks)
     * @param commande Une commande spécifique
     * @exception CommandeException gestion de la corruption des données
     * @exception NumberFormatException gestion de la corruption des données
     * @author Nguyen Nicolas
     */
    public void calculStock(Commande commande) {
        Boolean estValide = estValide(commande);

        // Si on a validé autant de référence qu'il en existe on peut livrer la commande
        if (estValide){
            loggerInfo.info("Commande : "+commande.getNumero()+" validée.");
            soustraireStock(commande);
            loggerInfo.info("-------------------------------------------------------");
        }
    }

    /**
     * Retirer les éléments référant de la commande du stock pour la livrer
     * @param commande la commande en cours de gestion
     * @author Nguyen Nicolas
     */
    private void soustraireStock(Commande commande) {
        List<String> refs = commande.getReferences();

        // On boucle sur les références stockées pour faire les modifications de stock
        for (String ref : refs) {
            String[] refParts = ref.split("=");
            String reference = refParts[0];
            int quantite = Integer.parseInt(refParts[1]);

            for (Produit produit : stock){
                if (produit.getReference().equals(reference)) {
                    // Soustraction de la quantitée
                    produit.soutraireStock(quantite);
                    loggerInfo.info("\tRetiré du stock : "+quantite+" "+produit.getReference());
                }
            }
        }
        commande.setLivrer(true);
    }

    /**
     * On vérifie si une commande peut être validée en rapport avec les quantitées de produits en stock
     * @param commande commande par laquelle on vérifie l'état des stocks de ses références
     * @return boolean Est-elle valide pour livraison
     */
    private boolean estValide(Commande commande) {
        commande.setRaison("");
        List<String> refs = commande.getReferences();
        int compteur = 0;
        boolean resultat = false;
        // Pour toutes les références d'une commande on vérifie l'état des stocks
        for (String ref : refs) {
            try{
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
                        }else{
                            compteur++;
                        }
                    }
                }
            }catch(NumberFormatException e){
                logger.fatal("Une erreur est survenu lors d'un calcul de stock", e);
            }
        }
        if (compteur == commande.getReferences().size()){
            commande.setRaison("");
            resultat = true;
        }
        return resultat;
    }

    /**
     * Création et/ou sauvegarde d'une chaine de caractère de type 'raison'
     * @param commande la commande a modifier
     * @param quantite la quantite demandée
     * @param produit le produit concernant la commmande et la quantité
     * @author Nguyen Nicolas
     */
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

    /**
     * Sauvegarder les données des commandes dans le fichier commandes.txt
     * @exception IOException Erreur dans la gestion d'un fichier
     * @author Nguyen Nicolas
     */
    public void sauvegarderCommandes(){
        try {
            FileWriter fileWriter = new FileWriter("src/fr/resources/Commandes.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Commande commande : commandes) {
                if (commande != null){
                    StringBuilder sb = new StringBuilder();
                    int compteur = 0;
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
                        if (compteur < (commande.getReferences().size()-1)){
                            sb.append("\n");
                        }
                        compteur++;
                    }
                    bufferedWriter.write(sb.toString());
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        }catch (IOException e){
            throw new CommandeException("Erreur dans la sauvegarde des commandes", e.getCause());
        }
    }

    /**
     * Sauvegarde des données du stock
     * @exception IOException Erreur dans la gestion d'un fichier
     * @author Nguyen Nicolas
     */
    public void sauvegarderStock(){
        try {
            FileWriter fileWriter = new FileWriter("src/fr/resources/Produits.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Produit produit : stock) {
                StringBuilder sb = new StringBuilder();
                sb.append(produit.getReference());
                sb.append(";");
                sb.append(produit.getNom());
                sb.append(";");
                sb.append(produit.getPrix());
                sb.append(";");
                sb.append(produit.getQuantite());

                bufferedWriter.write(sb.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }catch (IOException e){
            throw new CommandeException(e.getMessage(), e.getCause());
        }
    }
}
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
    private final String stockFilePath = "src/fr/resources/Produits.txt";
    private final String commandesFilePath= "src/fr/resources/Commandes.txt";

    /**
     * Constructeur
     * L'initialisation du programme passe par l'utilisation du constructeur de la classe Site
     */
    public Site()
    {
        try{
            // lecture du fichier resources/Produits.txt | pour chaque ligne, on crée un Produit que l'on ajoute a stock
            initialiserStock(stockFilePath);

            // lecture du fichier resources/Commandes.txt | pour chaque ligne, on crée une Commande que l'on ajoute à commandes ou l'on ajoute la une référence d'un produit a une commande existante
            initialiserCommandes(commandesFilePath);

            // On vérifie les commandes et génère les attributs raisons des commandes
            initialisationDesReferences();

        }catch(ProduitException e){
            logger.error("Une collection n'a pas pu être chargée correctement", e);
        }catch(CommandeException e){
            logger.error(e.getMessage(), e.getCause(), e.getStackTrace());
        }
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
                Site.indexStock++;
            }
        }catch (NullPointerException e){
            logger.error("Une erreur est survenu dans l'initialisation des stock", e);
            throw new ProduitException(e.getMessage(), e.getCause());
        }catch (IndexOutOfBoundsException e){
            logger.error("Une erreur est survenu dans l'initialisation des stock", e);
            throw new ProduitException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Chargement du fichier resources/Commande.txt
     * @param nomFichier fichier de sauvegarde des données de la classe Command
     * @exception CommandeException la procédure peut générer une exception
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
            boolean livrer = champs[4].equals("true");

            // On dirige la donnée au bon endroit (Si la commande existe on ajoute sinon on créer)
            if (numero > 0 && numero < commandes.size() ){
                // On récupère la commande pour ajouter la référence
                Commande commande = commandes.get(numero);
                commande.getReferences().add(ref);
            }else{
                // On instancie une nouvelle commande
                Commande commande = new Commande(numero, date, client);
                commande.setLivrer(livrer);
                commande.getReferences().add(ref);
                commandes.add(commande);
                Site.indexCommande++;
            }
        }
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere tous les produits du stock
     * @return retourne un toString de tous les produits
     */
    public String listerTousProduits() {
        // L'utilisation d'un stringbuilder est moins coûteuse à grande échelle.
        StringBuilder sb = new StringBuilder();
        for(Produit produit : stock){
            sb.append(produit.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere toutes les commandes
     * @param calcul true si on veut faire la livraison de toute les commandes possibles dans l'ordre d'enregistrement
     * @return retourne un toString de toutes les commandes
     */
    public String listerToutesCommandes(boolean calcul)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < commandes.size(); i++) {
            if (calcul){
                // On calcule la commande pour être sûr que les données raison sont à jour
                calculStock(commandes.get(i));
            }else{
                // On valide juste les commandes pour voir si les stocks sont disponibles sans les livrer
                estValide(commandes.get(i));
            }
            sb.append("\n");
            sb.append(commandes.get(i).toString(true, true));
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
                    // On calcule la commande pour être sûr que les données raison sont à jour
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
                    // On calcule la commande pour être sûr que les données raison sont à jour
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
     * Générer les attributs raisons des commandes
     */
    public void initialisationDesReferences() {
        for (Commande commande : commandes) {
            if (commande != null && !commande.isLivrer()){
                estValide(commande);
            }
        }
    }

    /**
     * Calculer et ajuster le stock par rapport a une commande (Si les stocks le permettent livre une commande et soustrait les stocks)
     * @param commande Une commande spécifique
     * @exception CommandeException gestion de la corruption des données
     * @exception NumberFormatException gestion de la corruption des données
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
     * @exception NumberFormatException peut générer une erreur
     * @return boolean Est-elle valide pour livraison
     */
    private boolean estValide(Commande commande) {
        commande.setRaison("");
        List<String> refs = commande.getReferences();
        int nombreDeReferenceDisponibleEnStock = 0;

        // Pour toutes les références d'une commande on vérifie l'état des stocks
        for (String ref : refs) {
            try{
                String[] refParts = ref.split("=");

                // Vérification de la corrumption des données
                if (refParts.length != 2) { throw new CommandeException("Une corruption de donnée s'est produite dans des références de commande", new IndexOutOfBoundsException()); }

                String reference = refParts[0];
                int quantite = Integer.parseInt(refParts[1]);

                // Attribut de vérification pour savoir si un produit a été trouvé.
                boolean referanceEnStock = false;

                // Pour tous les produits
                for (Produit produit : stock) {
                    if (produit.getReference().equals(reference)){
                        // Vérification de la disponibilité
                        if (!produit.isCalculQuantite(quantite)){
                            formatAjoutOuNouvelleRaison(commande, quantite, produit);
                        }else{
                            referanceEnStock = true;
                        }
                    }
                }
                if (referanceEnStock){
                    nombreDeReferenceDisponibleEnStock++;
                }
            }catch(NumberFormatException e){
                logger.fatal("Une erreur est survenu lors d'un calcul de stock", e);
            }
        }
        return nombreDeReferenceDisponibleEnStock == commande.getReferences().size();
    }

    /**
     * Création et/ou sauvegarde d'une chaine de caractère de type 'raison'
     * @param commande la commande a modifier
     * @param quantite la quantite demandée
     * @param produit le produit concernant la commmande et la quantité
     */
    private void formatAjoutOuNouvelleRaison(Commande commande, int quantite, Produit produit) {
        StringBuilder sb = new StringBuilder();

        // Mise en forme de la donnée raison
        sb.append(produit.formatStockageObjetRaison(quantite));
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
     */
    public void sauvegarderCommandes(){
        try {
            FileWriter fileWriter = new FileWriter("src/fr/resources/Commandes.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            StringBuilder sb = new StringBuilder();

            // On recherche la dernière commande pour ne pas stocker de retour à la ligne.
            int indexCommande = 0;

            for (Commande commande : commandes) {
                String c = commande.formatSauvegardeCommande(commande, indexCommande);
                if (!c.isEmpty()){
                    sb.append(c);
                }
                indexCommande++;
                bufferedWriter.write(sb.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }catch (IOException e){
            throw new CommandeException("Erreur dans la sauvegarde des commandes", e.getCause());
        }
    }

    /**
     * Sauvegarde des données du stock
     * @exception IOException Erreur dans la gestion d'un fichier
     */
    public void sauvegarderStock(){
        try {
            FileWriter fileWriter = new FileWriter("src/fr/resources/Produits.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            StringBuilder sb = new StringBuilder();

            for (Produit produit : stock) {
                sb.append(produit.formatSauvegardeProduit());
            }

            bufferedWriter.write(sb.toString());
            bufferedWriter.newLine();
            bufferedWriter.close();
        }catch (IOException e){
            throw new CommandeException(e.getMessage(), e.getCause());
        }
    }

}
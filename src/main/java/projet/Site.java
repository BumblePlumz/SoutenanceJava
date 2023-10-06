package projet;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.*;

// Classe de definition du site de vente
// TODO Enlever les sout
public class Site
{
    private static final Logger logger = LogManager.getLogger(Site.class);
    private ArrayList<Produit> stock;       // Les produits du stock
    private ArrayList<Commande> commandes;  // Les bons de commande


    // Constructeur
    //
    public Site()
    {
        stock = new ArrayList<Produit>();
        commandes = new ArrayList<Commande>();

        // lecture du fichier resources/Produits.txt
        //  pour chaque ligne on créer un Produit que l'on ajoute a stock
        initialiserStock("src/main/resources/Produits.txt");

        // lecture du fichier resources/Commandes.txt
        // pour chaque ligne on créer une Commande que l'on ajoute à commandes ou l'on ajoute la une référence d'un produit a une commande existante
        initialiserCommandes("src/main/resources/Commandes.txt");
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere tous les produits du stock
     * @return retourne un toString de tous les produits
     */
    public String listerTousProduits()
    {
        String res="";
        for(Produit prod : stock)
            res=res+prod.toString()+"\n";

        return res;
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere toutes les commandes
     * @return retourne un toString de toutes les commandes
     */
    public String listerToutesCommandes()
    {
        String res="Cette methode n'est pas codee\n";
        res=res+"Elle doit retourner les commandes\n";
        res=res+"Les commandes sont concatenes dans une chaine";

        return res;
    }

    /**
     * Methode qui retourne sous la forme d'une chaine de caractere une commande
     * @param numero Numéro d'une commande
     * @return Retourne un toString de la commande
     */
    public String listerCommande(int numero)
    {
        String res="Cette methode n'est pas codee\n";
        res=res+"Numero de commande : "+numero+"\n";
        res=res+"Elle doit retourner le contenu d'une commande\n";
        
        return res;
    }

    /**
     * Chargement du fichier resources/Produits.txt
     * @param nomFichier fichier de sauvegarde des données de la classe Produit
     */
    private void initialiserStock(String nomFichier)
    {
        String[] lignes = Terminal.lireFichierTexte(nomFichier);
        for(String ligne :lignes)
            {
                System.out.println(ligne);
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
    }

    /**
     *
     * @param nomFichier fichier de sauvegarde des données de la classe Command
     */
    private void initialiserCommandes(String nomFichier) {
        // TODO
    }
}
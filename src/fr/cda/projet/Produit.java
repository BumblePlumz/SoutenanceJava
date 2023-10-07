package projet;

import util.Calcul;

/**
 * Classe de gestion des produits
 *
 * @author Vendor & Nguyen Nicolas
 * @version 0.05
 */
public class Produit
{
    // Les caracteristiques d'un Produit
    //
    private String  reference;      // reference du produit
    private String  nom;            // nom du produit
    private double  prix;           // prix du produit
    private int     quantite;       // quantité du produit

    // Constructeur
    //
    public Produit(String reference,
                   String nom,
                   double prix,
                   int quantite)
    {
        this.reference = reference;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }

    public String getReference() {
        return reference;
    }

    public void ajoutQuantite(int quantite){
        this.quantite += quantite;
    }

    /**
     * Savoir si une quantitée est retirable du stock
     * @param nombre quantitée à retirer
     * @return vrai ou faux
     */
    public boolean isCalculQuantite(int nombre){
        return Calcul.soustraction(this.quantite, nombre, false) >= 0;
    }

    /**
     * Retirer une quantitée de produit du stock
     * @param nombre quantitée à retirer du stock
     */
    public void soutraireStock(int nombre){
        this.quantite -= nombre;
    }

    /**
     * Mise en forme par le calcul de la donnée au format REF=X
     * @param quantiteLivrable la quantité demandée
     * @return un String qui affiche les quantités manquantes
     */
    public String miseEnFormeRaison(int quantiteLivrable){
        return reference+"="+Calcul.soustraction(quantite, quantiteLivrable, true);
    }

    /**
     * Affichage de l'objet en chaîne de caractère
     * @return objet.toString()
     */
    @Override
    public String toString()
    {
        String sPrix;
        if (prix < 10) {
            sPrix = String.format("0%.2f", prix);
        } else {
            sPrix = String.format("%.2f", prix);
        }
        return String.format("%-15s %-53s %-5s %3d",reference,nom,sPrix,quantite);
    }
}
package projet;

import ihm.*;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.Arrays;

// Classe de definition de l'IHM principale du compte
//

/**
 * Classe de définition de l'IHM principale du compte
 *
 * @author Vendor & Nguyen Nicolas
 * @version 0.01
 */
public class GUISite implements FormulaireInt
{
    // Protected permet l'accès au méthode de site dans les enfants donc les fenêtres de modification
    protected Site site;

    // Constructeur
    //
    public GUISite(Site site)
    {
        this.site = site;

        // Creation du formulaire
        Formulaire form = new Formulaire("Site de vente",this,1100,730);
        
        //  Creation des elements de l'IHM
        //
        form.setPosition(20, 20);
        form.addLabel("Afficher tous les produits du stock");
        form.addButton("AFF_STOCK","Tous le stock");
        form.addLabel("");
        form.addLabel("Afficher tous les bons de commande");
        form.addButton("AFF_COMMANDES","Toutes les commandes");
        form.addLabel("");
        form.addText("NUM_COMMANDE","Numero de commande : ",true,"1");
        form.addButton("AFF_COMMANDE","Afficher");
        form.setPosition(100, 181);
        form.addButton("MODIF_COMMANDE", "Modifier");
        form.setPosition(20, 205);
        form.addLabel("");
        form.addButton("AFF_LIVRER", "Afficher les commandes non livrées");
        form.addLabel("");
        form.addButton("LIVER_COMMANDE", "Livrer");
        form.addLabel("");
        form.addButton("CALC_VENTE", "Calculer les ventes");

        form.setPosition(150, 600);
        form.addButton("FERMER", "Quitter");

        form.setPosition(400,0);
        form.addZoneText("RESULTATS","Resultats",
                            true,
                            "",
                            600,700);

        // Affichage du formulaire
        form.afficher();
    }

    /**
     * Methode appelée quand on clique sur un bouton
     * @param form Le formulaire dans lequel se trouve le bouton
     * @param nomSubmit Le nom du bouton qui a �t� utilis�.
     */
    public void submit(Formulaire form,String nomSubmit)
    {
        // Gestion de l'affichage
        switch(nomSubmit){
            case "AFF_STOCK":
                form.setValeurChamp("RESULTATS", "");
                String rsStock = site.listerTousProduits();
                form.setValeurChamp("RESULTATS",rsStock);
                break;
            case "AFF_COMMANDES":
                form.setValeurChamp("RESULTATS", "");
                String rsCommandes = site.listerToutesCommandes();
                form.setValeurChamp("RESULTATS",rsCommandes);
                break;
            case "AFF_COMMANDE":
                form.setValeurChamp("RESULTATS", "");
                String numStr = form.getValeurChamp("NUM_COMMANDE");
                int num = Integer.parseInt(numStr);
                String rsCommande = site.listerCommande(num);
                form.setValeurChamp("RESULTATS",rsCommande);
                break;
            case"AFF_LIVRER":
                form.setValeurChamp("RESULTATS", "");
                String rsLivrable = site.listerToutesCommandesNonLivrer();
                form.setValeurChamp("RESULTATS", rsLivrable);
                break;
            case "MODIF_COMMANDE":
                String commande = form.getValeurChamp("RESULTATS");

                // On récupère l'identifiant de la commande
                String[] parties = commande.split(":");
                String[] partie = parties[1].split(" ");
                int identifiant = Integer.parseInt(partie[1].trim());
                if (!site.getCommandes().get(identifiant).isLivrer()){
                    try{
                        GUIModifierCommande ihm = new GUIModifierCommande(this, site.getCommandes().get(identifiant), site.getStock());
                    }catch (IllegalStateException e){
                        Site.logger.error("Une erreur est survenue lors de la validation du formulaire de modification ", e);
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "La commande a déjà été livrée", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "LIVRER_COMMANDE":
                break;
            case "CALC_VENTE":
                break;
            case "FERMER":
                form.fermer();
                break;
        }
    }

}
package projet;

import ihm.*;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

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
        form.setPosition(180, 181);
        form.addButton("CALC_COMMANDE", "prix");
        form.setPosition(20, 205);
        form.addLabel("");
        form.addButton("AFF_LIVRER", "Afficher les commandes non livrées");
        form.addLabel("");
        form.addButton("CALC_VENTE", "Calculer les ventes");
        form.addLabel("");
        form.addButton("SAUVER", "Sauvegarder");
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
        try{
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
                    try{
                        int identifiantModif = getIdentifiant(form);
                        // Si la commande n'est pas livrée
                        if (!site.getCommandes().get(identifiantModif).isLivrer()){
                                GUIModifierCommande ihm = new GUIModifierCommande(this, site.getCommandes().get(identifiantModif), site.getStock());
                        }else{
                            JOptionPane.showMessageDialog(null, "La commande a déjà été livrée", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }catch (IllegalStateException e){
                        site.logger.error("Une erreur est survenue lors de la validation du formulaire de modification ", e);
                    }catch (ArrayIndexOutOfBoundsException e){
                        JOptionPane.showMessageDialog(null, "Veuillez affichez une commande avant d'essayer de la traiter", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        site.logger.error("Une demande de traitement a été faite sans l'affichage préalable", e);
                    }
                    break;
                case "CALC_COMMANDE":
                    try{
                        int identifiantPrix = getIdentifiant(form);
                        // Si la commande est livrée
                        if (site.getCommandes().get(identifiantPrix).isLivrer()){
                            Commande commande = site.getCommandes().get(identifiantPrix);
                            Map<Commande, List<String>> map = afficherPrixCommande(commande);
                            form.setValeurChamp("RESULTATS", map.get(commande).get(0));
                        }else{
                            JOptionPane.showMessageDialog(null, "La commande n'a pas été livrée", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        JOptionPane.showMessageDialog(null, "Veuillez affichez une commande avant d'essayer de la traiter", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        Site.logger.error("Une demande de traitement a été faite sans l'affichage préalable", e);
                    }catch(NumberFormatException e){
                        JOptionPane.showMessageDialog(null, "Veuillez affichez une commande avant d'essayer de la traiter", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        Site.logger.error("Une demande de traitement a été faite sans l'affichage préalable", e);
                    }
                    break;
                case "CALC_VENTE":
                    StringBuilder sb = new StringBuilder();
                    Map<Commande, List<String>> liste;
                    double prixTotal = 0;
                    for (Commande commande : site.getCommandes()){
                        System.out.println(commande);
                        if (commande != null && commande.isLivrer()){
                            liste = afficherPrixCommande(commande);
                            prixTotal += Double.parseDouble(liste.get(commande).get(1));
                            sb.append(liste.get(commande).get(0));
                            sb.append("\n");
                            sb.append("--------------------------------------");
                            sb.append("\n");
                        }
                    }
                    sb.append("=================================");
                    sb.append("\n");
                    String stringPrixTotal = "SOMME DE TOUTE LES COMMANDES : "+prixTotal;
                    sb.append(stringPrixTotal);
                    form.setValeurChamp("RESULTATS", sb.toString());
                    break;
                case "SAUVER":
                    break;
                case "FERMER":
                    form.fermer();
                    break;
                default:
                    throw new IllegalArgumentException("Erreur critique merci de relancer le programme");
            }
        }catch (IllegalStateException e){
            Site.logger.fatal(e.getMessage(), e);
        }
    }

    /**
     * Récupérer l'affichage des prix d'une commande
     * @param commande La commande traitée
     * @return map<Commande, List<String>> Pour la liste 0 : récupère l'affichage en String | 1 : récupère le prix total de la commande
     * @author Nguyen Nicolas
     */
    private Map<Commande, List<String>> afficherPrixCommande(Commande commande) {
        // Attributs
        Map<Commande, List<String>> map = new HashMap<>();
        List<String> liste = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        double total = 0;

        // Affichage titre
        String titre = "COMMANDE : "+ commande.getNumero();
        sb.append(titre);
        sb.append("\n");

        // Pour toutes les références
        for (String ref : commande.getReferences()) {
            String[] reference = ref.split("=");
            // Pour tous les produits
            for (Produit produit : site.getStock()) {
                if (produit.getReference().equals(reference[0])){
                    // Mise en forme de la référence pour l'alignement des données dans l'IHM
                    String refToString = reference[0];
                    for (int i = 0; i < 15; i++) {
                        if (refToString.length() < 15){
                            refToString += " ";
                        }
                    }
                    // Mise en forme des références, quantités et prix de la commande
                    String texte = refToString+reference[1]+" : "+produit.getPrix();
                    sb.append("\t\t\t");
                    sb.append(texte);
                    sb.append("\n");
                    // Calcul du prix total
                    total += produit.getPrix() * (Integer.parseInt(reference[1]));
                }
            }
        }
        // Finition de l'affichage avec le total
        String prixTotal = "SOMME : "+total+" euros";
        sb.append(prixTotal);
        // Sauvegarde des données dans la hashmap
        liste.add(sb.toString());
        liste.add(Double.toString(total));
        map.put(commande, liste);
        return map;
    }

    /**
     * Retourne l'identifiant de la commande en cours d'affichage
     * @param form formulaire actuellement affiché
     * @return int retourne l'identifiant de la commande affichée
     * @author Nguyen Nicolas
     */
    private int getIdentifiant(Formulaire form) {
        String commande = form.getValeurChamp("RESULTATS");
        String[] parties = commande.split(":");
        String[] partie = parties[1].split(" ");
        int identifiant = Integer.parseInt(partie[1].trim());
        return identifiant;
    }

}
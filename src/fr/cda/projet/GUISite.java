package projet;

import ihm.*;

import javax.swing.*;
import java.util.*;

/**
 * Classe de définition de l'IHM principale du compte
 *
 * @author Vendor & Nguyen Nicolas
 * @version 1.00
 */
public class GUISite implements FormulaireInt
{
    // Protected permet l'accès aux méthodes de site dans les enfants donc les fenêtres de modifications
    protected Site site;
    // Formulaire placé en attribut pour l'accès des enfants à celui-ci et l'actualisation des affichages
    protected Formulaire form = new Formulaire("Site de vente",this,1100,730);
    private JCheckBox calculCheckBox;

    /**
     * Constructeur de l'ihm GUISite
     * @param site le site qui va servir de controleur pour les données de l'affichage
     */
    public GUISite(Site site)
    {
        this.site = site;
        
        // Affichage des données
        form.setPosition(20, 20);
        form.addLabel("Afficher tous les produits du stock");
        form.addButton("AFF_STOCK","Tous le stock");

        form.addLabel("");
        form.addLabel("Afficher tous les bons de commande");
        form.addButton("AFF_COMMANDES","Toutes les commandes");
        form.addLabel("");

        // Sélection et affichage d'un commande et ses actions possibles (modification/calcul)
        form.addText("NUM_COMMANDE","Numero de commande : ",true,"1");
        form.addButton("AFF_COMMANDE","Afficher");
        form.setPosition(100, 181);
        form.addButton("MODIF_COMMANDE", "Modifier");
        form.setPosition(180, 181);
        form.addButton("CALC_COMMANDE", "prix");
        form.setPosition(232, 181);
        form.addButton("LIVRER", "Livrer");

        // Affichage des commandes non livrées
        form.setPosition(20, 205);
        form.addLabel("");
        form.addButton("AFF_LIVRER", "Afficher les commandes non livrées");

        // Calculer les ventes totales
        form.addLabel("");
        form.addButton("CALC_VENTE", "Calculer les ventes");
        form.addLabel("");
        form.addButton("AJOUT_COMMANDE", "Ajouter un bon de commande");
        form.setPosition(137, 600);

        // Sauvegarder
        form.addButton("SAUVER", "Sauvegarder");
        form.setPosition(150, 640);

        // Fermer le programme
        form.addButton("FERMER", "Quitter");

        // Affichage livraison automatique
        form.setPosition(190, 105);
        calculCheckBox = new JCheckBox("Livraison automatique");
        JPanel panel = new JPanel();
        panel.add(calculCheckBox);
        form.addPanel(panel, 160, 40);

        form.setPosition(400,0);
        form.addZoneText("RESULTATS","Resultats",
                            false,
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
                    Boolean livraisonAutomatiqueOnOff = calculCheckBox.isSelected();
                    form.setValeurChamp("RESULTATS", "");
                    String rsCommandes = site.listerToutesCommandes(livraisonAutomatiqueOnOff);
                    form.setValeurChamp("RESULTATS",rsCommandes);
                    break;
                case "AFF_COMMANDE":
                    form.setValeurChamp("RESULTATS", "");
                    String numStr = form.getValeurChamp("NUM_COMMANDE");
                    int num = Integer.parseInt(numStr);
                    String rsCommande = site.listerCommande(num, false);
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
                            // Ouverture de l'ihm modification de commande
                            new GUIModifierCommande(this, site.getCommandes().get(identifiantModif));
                        }else{
                            JOptionPane.showMessageDialog(null, "La commande a déjà été livrée", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        }
                        String reAffichageCommande = site.listerCommande(identifiantModif, false);
                        form.setValeurChamp("RESULTATS",reAffichageCommande);
                    }catch (IllegalStateException e){
                        Site.logger.error("Une erreur est survenue lors de la validation du formulaire de modification ", e);
                        throw new GUISiteException("Une demande de traitement a été faite sans l'affichage préalable", e.getCause());
                    }catch (ArrayIndexOutOfBoundsException e){
                        JOptionPane.showMessageDialog(null, "Veuillez affichez une commande valide avant d'essayer de la traiter", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        Site.logger.error("Une demande de traitement a été faite sans l'affichage préalable", e);
                        throw new GUISiteException("Une demande de traitement a été faite sans l'affichage préalable", e.getCause());
                    }catch (NumberFormatException e){
                        JOptionPane.showMessageDialog(null, "Veuillez affichez une commande valide avant d'essayer de la traiter", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        Site.logger.error("Une demande de traitement a été faite sans l'affichage préalable", e);
                        throw new GUISiteException("Une demande de traitement a été faite sans l'affichage préalable", e.getCause());
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
                        throw new GUISiteException("Une demande de traitement a été faite sans l'affichage préalable", e.getCause());
                    }catch(NumberFormatException e){
                        JOptionPane.showMessageDialog(null, "Veuillez affichez une commande avant d'essayer de la traiter", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        Site.logger.error("Une demande de traitement a été faite sans l'affichage préalable", e);
                        throw new GUISiteException("Une demande de traitement a été faite sans l'affichage préalable", e.getCause());
                    }
                    break;
                case "LIVRER":
                    try{
                        int id = this.getIdentifiant(form);
                        Commande commandeAffichee = site.getCommandes().get(id);
                        site.calculStock(commandeAffichee);
                        form.setValeurChamp("RESULTATS", site.listerCommande(id, false));
                    }catch (NumberFormatException e){
                        JOptionPane.showMessageDialog(null, "Veuillez affichez une commande avant d'essayer de la traiter", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                        Site.logger.error("Une demande de traitement a été faite sans l'affichage préalable", e);
                        throw new GUISiteException("Une demande de traitement a été faite sans l'affichage préalable", e.getCause());
                    }
                    break;
                case "CALC_VENTE":
                    StringBuilder sb = new StringBuilder();
                    Map<Commande, List<String>> liste;
                    double prixTotal = 0;
                    for (Commande commande : site.getCommandes()){
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
                case "AJOUT_COMMANDE":
                    try{
                        new GUIAjouterCommande(this);
                    }catch (GUIAjouterCommandeException e){
                        Site.logger.error(e.getMessage(), e.getCause());
                    }
                    break;
                case "SAUVER":
                    try{
                        site.sauvegarderCommandes();
                        site.sauvegarderStock();
                    }catch (CommandeException e){
                        Site.logger.fatal(e.getMessage(), e);
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue dans la sauvegarde des fichiers, merci de contacter un administrateur", "Erreur critique !", JOptionPane.INFORMATION_MESSAGE);
                    }finally {
                        JOptionPane.showMessageDialog(null, "Votre sauvegarde a bien été prise en compte", "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                case "FERMER":
                    form.fermer();
                    break;
                default:
                    throw new GUISiteException("Erreur critique action non autorisée", new IllegalArgumentException());
            }
        }catch (GUISiteException e){
            Site.logger.fatal(e.getMessage(), e.getCause());
        }
    }

    /**
     * Récupérer l'affichage des prix d'une commande
     * @param commande La commande traitée
     * @return map<Commande, List<String>> Pour la liste 0 : récupère l'affichage en String | 1 : récupère le prix total de la commande
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
     */
    private int getIdentifiant(Formulaire form) {
        String commande = form.getValeurChamp("RESULTATS");
        String[] parties = commande.split(":");
        String[] partie = parties[1].split(" ");
        return Integer.parseInt(partie[1].trim());
    }

}
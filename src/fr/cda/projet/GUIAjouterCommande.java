package projet;

import ihm.Formulaire;
import ihm.FormulaireInt;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Classe pour l'affichage IHM du formulaire d'ajout de commande
 * @author Nguyen Nicolas
 * @version 1.00
 */
public class GUIAjouterCommande implements FormulaireInt {
    private GUISite formPP;

    /**
     * Constructeur
     * @param formPP le site en cours d'utilisation
     * @author Nguyen Nicolas
     */
    public GUIAjouterCommande(GUISite formPP) {
        this.formPP = formPP;

        Formulaire form = new Formulaire("Formulaire de bon de commande",this,700,500);

        // Titre
        form.setPosition(20, 20);
        form.addLabel("Bon de commande : ");

        // Client
        form.addLabel("");
        form.addText("CLIENT", "Client : ", true, "NOM prenom");
        form.addLabel("");
        form.addButton("AJOUT_CLIENT", "Ajouter client");

        // Liste des produits
        form.addLabel("");
        List<Produit> stock = formPP.site.getStock();
        String[] produits = new String[stock.size()];
        for (int i = 0; i < stock.size(); i++) {
            produits[i] = stock.get(i).getReference();
        }
        form.addListScroll("REF", "Référence Produit : ", true, produits, 200,100);

        // Définir une quantitée
        form.addLabel("");
        form.addText("QUANTITE", "Quantité : ", true, "");

        // Bouton ajouter
        form.addLabel("");
        form.addButton("AJOUT_PRODUIT", "ajouter référence");

        // Affichage bon de commande
        form.setPosition(300, 20);
        form.addLabel("");
        form.addZoneText("AFF_CLIENT","Client",
                true,
                "",
                300,50);
        form.addLabel("");
        form.addZoneText("AFF_BON","Référence",
                true,
                "",
                300,150);

        // Bouton valider
        form.setPosition(250, 375);
        form.addLabel("");
        form.addButton("SUBMIT", "Valider bon de commande");

        form.setPosition(300, 425);
        // Bouton fermer
        form.addLabel("");
        form.addButton("FERMER", "Quitter");

        form.afficher();
    }

    /**
     * Gestionnaire d'actions du formulaire
     * @param form Le formulaire dans lequel se trouve le bouton
     * @param nom Le nom du bouton qui a �t� utilis�.
     */
    @Override
    public void submit(Formulaire form, String nom) {
        switch (nom){
            case "AJOUT_CLIENT":
                String client = form.getValeurChamp("CLIENT");
                form.setValeurChamp("AFF_CLIENT", client);
                break;
            case "AJOUT_PRODUIT":
                try{
                    StringBuilder sb = new StringBuilder();
                    String bon = form.getValeurChamp("AFF_BON");
                    String ref = form.getValeurChamp("REF");
                    String quantite = form.getValeurChamp("QUANTITE");
                    String affichageRef = ref+" = ";

                    if (!bon.contains(affichageRef)){
                        // Si la référence n'existe pas, on la créée
                        sb.append(bon);
                        sb.append(miseEnFormeAffichage(ref, quantite));
                        sb.append("\n");
                        form.setValeurChamp("AFF_BON", sb.toString());
                    }else{
                        // Si la référence existe, on la remplace
                        String regex = ref+" = [^\\s]+";
                        bon = bon.replaceAll(regex, miseEnFormeAffichage(ref, quantite).toString());
                        sb.append(bon);
                        form.setValeurChamp("AFF_BON", sb.toString());
                    }
                }catch (InputMismatchException e){
                    throw new GUIAjouterCommandeException("Une mauvaise donnée a été entrée", e.getCause());
                }
                break;
            case "SUBMIT":
                if (!form.getValeurChamp("CLIENT").isEmpty() && form.getValeurChamp("AFF_BON").length() != 0){
                    // Récupération des données
                    String leClient = form.getValeurChamp("CLIENT");
                    String[] bonDeCommande = form.getValeurChamp("AFF_BON").split("\n");
                    String[] formatBonDeCommande = miseEnFormeReference(bonDeCommande);

                    // Vérification que les champs sont remplis
                    if (!leClient.isEmpty() && bonDeCommande.length != 0){
                        List<String> resultat = new ArrayList<>();

                        // On fait une liste des références et de leurs quantitées
                        for (int i = 0; i < formatBonDeCommande.length; i++) {
                            resultat.add(formatBonDeCommande[i]);
                        }

                        // On met en forme la date actuelle
                        LocalDate dateActuelle = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        String date = dateActuelle.format(formatter);

                        // Création de la commande
                        formPP.site.getCommandes().add(new Commande(Site.indexCommande, leClient, date, resultat));
                        JOptionPane.showMessageDialog(null, "Le bon de commande a été créé", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                        form.fermer();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Veuillez remplir les champs avant de valider le bon de commande", "Action impossible", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "FERMER":
                form.fermer();
                break;
            default:
                throw new IllegalArgumentException("Une erreur critique est arrivée dans la sélection de commande de création d'un bon de commande");
        }
    }

    /**
     * Mise en forme des données pour le programme
     * @param bonDeCommande tableau de donnée contenant référence et quantitée
     * @return String[] format de stockage de donnée REF=QUANTITE
     * @author Nguyen Nicolas
     */
    private String[] miseEnFormeReference(String[] bonDeCommande) {
        String[] rs = new String[bonDeCommande.length];
        for (int i = 0; i < bonDeCommande.length; i++) {
            String[] ligne = bonDeCommande[i].split("=");
            String nouvelleLigne = ligne[0].trim()+"="+ligne[1].trim();
            rs[i] = nouvelleLigne;
        }
        return rs;
    }

    /**
     * Mise en forme des données pour l'affichage dans l'IHM
     * @param ref référence d'un produit
     * @param quantite quantité d'un produit
     * @return StringBuilder référence et quantité mise en forme
     * @author Nguyen Nicolas
     */
    private StringBuilder miseEnFormeAffichage(String ref, String quantite){
        StringBuilder sb = new StringBuilder();
        sb.append(ref);
        sb.append(" = ");
        sb.append(quantite);
        return sb;
    }

}

package projet;

import ihm.Formulaire;
import ihm.FormulaireInt;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire de modification des stocks par le biais d'une commande
 * @author Nguyen Nicolas
 * @version 1.00
 */
public class GUIModifierCommande implements FormulaireInt {
    private GUISite formPP;
    private Commande commande;

        /**
         * Constructeur
         * @param site le site en cours d'utilisation
         * @param commande la commande en cours d'affichage
         */
    public GUIModifierCommande(GUISite site, Commande commande){
        this.formPP = site;
        this.commande = commande;

        Formulaire form = new Formulaire("Formulaire de modification des stocks",this,400,300);

        // Titre
        form.setPosition(20, 20);
        form.addLabel("Modifier les quantitées en stock pour la commande : "+commande.getNumero());

        // On récupère les raisons (Format : ref=quantitée)
        String[] raisons = commande.getRaison().split(";");

        // On boucle pour afficher les champs
        form.setPosition(20, 60);
        for (String raison : raisons) {
            String[]donnees = raison.split("=");
            String label = "Il manque "+donnees[1]+" "+donnees[0]+" : ";

            // Mise en forme du texte pour l'affichage en ajoutant des espaces
            for (int i = 0; i < 30; i++) {
                if (label.length() < 30){
                    label += " ";
                }
            }
            form.addText(donnees[0], label, true, donnees[1]);
        }

        // Affichage du bouton de validation
        form.setPosition(60, 175);
        form.addButton("SUBMIT", "Valider les changements de stock");

        // Affichage du bouton fermer
        form.setPosition(150,225);
        form.addButton("FERMER", "Fermer");

        // Générer l'affichage
        form.afficher();
    }

    /**
     * Ajout des valeurs inscrite par l'utilisateur dans les données en cours d'utilisation
     * @param listeValeurs input utilisateur sous forme de List<String>
     */
    private void validerStock(List<String> listeValeurs) {
        for (String valeur : listeValeurs) {
            String[] donnees = valeur.split("=");
            for (Produit produit : formPP.site.getStock()){
                if (produit.getReference().equals(donnees[0])){
                    produit.ajoutQuantite(Integer.parseInt(donnees[1]));
                }
            }
        }
    }

    /**
     * EventListener sur le bouton submit
     * @param form Le formulaire dans lequel se trouve le bouton
     * @param nom Le nom du bouton qui a �t� utilis�.
     */
    @Override
    public void submit(Formulaire form, String nom) {
        try{
            switch(nom){
                case "SUBMIT":
                    // on récupère les valeurs des champs au nombre indéfini
                    String[] raisons = commande.getRaison().split(";");
                    List<String> listeValeurs = new ArrayList<>();
                    for (String raison : raisons) {
                        String[] donnees = raison.split("=");
                        listeValeurs.add(donnees[0]+"="+form.getValeurChamp(donnees[0]));
                    }
                    // Validation de la modification des stock
                    this.validerStock(listeValeurs);

                    JOptionPane.showMessageDialog(null, "Les valeurs entrées ont bien été ajoutées au stock", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "FERMER":
                    form.fermer();
                    break;
                default:
                    throw new IllegalStateException("Valeur inattendue : " + nom);
            }
            formPP.form.setValeurChamp("RESULTATS", "");
            formPP.form.setValeurChamp("RESULTATS", formPP.site.listerCommande(commande.getNumero(), false));
            form.fermer();
        }catch (IllegalStateException e){
            Site.logger.error("Une erreur critique est survenu dans l'écoute des boutons du formulaire de modification");
            JOptionPane.showMessageDialog(null, "Une erreur est survenu votre action n'a pas été enregistrée", "Erreur critique", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

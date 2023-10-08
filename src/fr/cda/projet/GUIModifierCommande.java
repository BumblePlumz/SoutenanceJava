package projet;

import ihm.Formulaire;
import ihm.FormulaireInt;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GUIModifierCommande implements FormulaireInt {
    private GUISite formPP;
    private Commande commande;
    private List<Produit> stock;

    public GUIModifierCommande(GUISite site, Commande commande, List<Produit> stock){
        this.formPP = site;
        this.commande = commande;
        this.stock = stock;

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

            // Mise en forme du texte pour l'affichage
            for (int i = 0; i < 30; i++) {
                if (label.length() < 30){
                    label += " ";
                }
            }
            form.addText(donnees[0], label, true, donnees[1]);
        }

        // Affichage du bouton de validation
        form.setPosition(80, 150);
        form.addButton("SUBMIT", "Valider les changements de stock");
        form.setPosition(20, 175);
        form.addLabel("*La commande sera automatiquement livré");
        form.addLabel(" si les stocks sont suffisant");
        form.setPosition(150,225);
        form.addButton("FERMER", "Fermer");
        form.afficher();
    }

    @Override
    public void submit(Formulaire form, String nom) {
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
                break;
            case "FERMER":
                form.fermer();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Une erreur est survenu votre action n'a pas été enregistrée", "Erreur", JOptionPane.INFORMATION_MESSAGE);
                throw new IllegalStateException("Valeur inattendue : " + nom);
        }
        form.fermer();
    }

    private void validerStock(List<String> listeValeurs) {
        for (String valeur : listeValeurs) {
            String[] donnees = valeur.split("=");
            for (Produit produit : stock){
                if (produit.getReference().equals(donnees[0])){
                    produit.ajoutQuantite(Integer.parseInt(donnees[1]));
                }
            }
        }
        try{
            formPP.site.reCalculerStock(commande);
        }catch (CommandeException e){
            formPP.site.logger.error("Une erreur est survenu dans la recalculation des stocks ", e);
        }
        // Recalcul des stocks
    }
}

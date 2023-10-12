package projet;

import ihm.Formulaire;
import ihm.FormulaireInt;
import util.Configuration;

import javax.swing.*;

/**
 * Classe de gestion des configurations du logiciel
 * @author Nguyen Nicolas
 * @version 1.00
 */
public class GUIParametre implements FormulaireInt {
    private GUISite guiSite;
    private JCheckBox sauvegardeCheckBox;
    public GUIParametre(GUISite guiSite) {
        this.guiSite = guiSite;

        Formulaire form = new Formulaire("Paramètres",this,700,500);

        // Titre
        form.setPosition(20, 20);
        form.addLabel("Paramètres : ");

        // Système de sauvegarde local
        form.setPosition(20, 80);
        form.addLabel("Sauvegarde locale de sécuritée : ");

        // Gestion de l'état Checkbox
        sauvegardeCheckBox = new JCheckBox("Activer/Désactiver");
        sauvegardeCheckBox.setSelected(Configuration.paramSauvegardeLocale);
        JPanel panel = new JPanel();
        panel.add(sauvegardeCheckBox);

        // Ajoute du checkBox au formulaire
        form.setPosition(240, 73);
        form.addPanel(panel, 160, 40);

        // Sauvegarder Configuration
        form.setPosition(270, 380);
        form.addButton("SAUV_CONFIG", "Sauvegarder paramètres");

        // Fermer
        form.setPosition(320, 420);
        form.addButton("FERMER", "Quitter");

        form.afficher();
    }

    @Override
    public void submit(Formulaire form, String nom) {
        switch(nom){
            case "SAUV_CONFIG":
                // On récupère l'état du checkbox et on le sauvegarde dans l'attribut de la classe Configuration
                Configuration.paramSauvegardeLocale = sauvegardeCheckBox.isSelected();

                // On effectue une sauvegarde des configurations
                Configuration.sauvegarderConfiguration();

                // Affichage de la validation
                JOptionPane.showMessageDialog(null, "Paramètres sauvegardés", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                break;
            case"FERMER":
                form.fermer();
                break;
        }
    }
}

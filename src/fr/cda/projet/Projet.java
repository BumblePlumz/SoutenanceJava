// Projet 1 CDA
// 
// NOM,Prenom
// NGUYEN,Nicolas
//
package projet;

import util.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Classe d'exécution du programme
 *
 * @author Vendor & Nguyen Nicolas
 * @version 1.0
 */
public class Projet
{
    public static void main(String a_args[])
    {
        Terminal.ecrireStringln("Execution du projet ");

        // On charge les configurations
        Configuration.chargerConfiguration();

        // Initialisation du programme
        Site site = new Site();
        GUISite ihm = new GUISite(site);
        JFrame frame = ihm.form.getFrame();

        // On écoute les fermetures du programme qui ne sont pas validées par le bouton "FERMER" par l'utilisateur
        // Cela peut se produire en cas de crash du programme et sert de sécurité quand à la perte de données
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Si le paramètre est activé
                if (Configuration.paramSauvegardeLocale){
                    // On affiche une demande de sauvegarde (On pourrait juste faire une sauvegarde directement, la demande est pour un visuel)
                    Configuration.demandeDeSauvegarde(frame, site);
                }
            }
        });
    }


}

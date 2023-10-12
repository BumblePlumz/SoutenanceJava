package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import projet.Site;

import javax.swing.*;

/**
 * Les constantes et configuration du programmme
 * Contient les façades de sauvegarde
 */
 public class Configuration {
    public static final String stockFilePath = "data/Produits.txt";
    public static final String localStockFilePath = "src/fr/resources/Produits.txt";
    public static final String commandesFilePath= "data/Commandes.txt";
    public static final String localCommandesFilePath = "src/fr/resources/Commandes.txt";
    public static final String paramFilePath = "src/fr/resources/config.cfg";
    public static final Logger logger = LogManager.getLogger(Site.class); // Gestion des logs d'erreurs
    public static final Logger loggerInfo = LogManager.getLogger("info");
    public static Boolean paramSauvegardeLocale = false;
    public static Boolean sauvegardeLocale = false;

   /**
    * Sauvegarder les paramètres dans le fichier src/fr/resources/config.cfg
    */
   public static void sauvegarderConfiguration() {
      String[] lignes = Terminal.lireFichierTexte(paramFilePath);
      StringBuffer stringBuffer = new StringBuffer();

      for (String ligne: lignes){
         if (ligne.startsWith("paramSauvegardeLocale=")){
            String[] donnees = ligne.split("=");
            stringBuffer.append("paramSauvegardeLocale=").append(paramSauvegardeLocale).append("\n");
         }else if (ligne.startsWith("sauvegardeLocale=")){
            String[] donnees = ligne.split("=");
            stringBuffer.append("sauvegardeLocale=").append(sauvegardeLocale).append("\n");
         }else{
            stringBuffer.append(ligne).append("\n");
         }
      }
      Terminal.ecrireFichier(paramFilePath, stringBuffer);
   }

   /**
    * Charger les paramètres du fichier src/fr/resources/config.cfg
    */
   public static void chargerConfiguration(){
      String[] lignes = Terminal.lireFichierTexte(paramFilePath);
      for (String ligne : lignes) {
         if (ligne.startsWith("paramSauvegardeLocale=")){
            String[] donnees = ligne.split("=");
            paramSauvegardeLocale = Boolean.valueOf(donnees[1]);
         }
         if (ligne.startsWith("sauvegardeLocale=")){
            String[] donnees = ligne.split("=");
            sauvegardeLocale = Boolean.valueOf(donnees[1]);
         }
      }
   }

   /**
    * Fenêtre de rappel pour une sauvegarde en cas de fermeture du programme en dehors du bouton "quitter".
    * @param frame
    * @param site
    */
   public static void demandeDeSauvegarde(JFrame frame, Site site) {
      String[] options = {"Oui", "Non"};
      int option = JOptionPane.showOptionDialog(frame, "Voulez-vous sauvegarder avant de quitter?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

      if (option == 0) {
         // Si l'utilisateur
         site.sauvegarderCommandes(Configuration.localCommandesFilePath);
         site.sauvegarderStock(Configuration.localStockFilePath);
         Configuration.sauvegardeLocale = true;
         sauvegarderConfiguration();
      } else if (option == 1) {
         Configuration.sauvegardeLocale = false;
         sauvegarderConfiguration();
      } else {
         System.exit(0); // Quitter l'application
      }
   }
}

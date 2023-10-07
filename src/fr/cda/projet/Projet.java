// Projet 1 CDA
// 
// NOM,Prenom
// NGUYEN,Nicolas
//
package projet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.*;

/**
 * Classe d'exécution du programme
 *
 * @author Vendor & Nguyen Nicolas
 * @version 0.01
 */
public class Projet
{
    private static final Logger logger = LogManager.getLogger(Projet.class);
    public static void main(String a_args[])
    {
        Terminal.ecrireStringln("Execution du projet ");
        Site site = new Site();
        GUISite ihm = new GUISite(site);

        // Test des toString()
//        List<String> refs = new ArrayList<>();
//        refs.add("LIVRE-1=1");
//        refs.add("LIVRE-2=4");
//
//        Commande c = new Commande(1, "06-10-2023", "Nicolas");
//        c.setReferences(refs);
//        System.out.println(c.toString());
//        System.out.println(c.toString());


        // Test du fichier configuration log4J2
//        System.out.println("Print");
//        System.out.println("Root Logger Level: " + LogManager.getRootLogger().getLevel());
//
//        logger.info("Ceci est un message d'information.");
//        logger.error("Ceci est un message d'erreur.");



    }
}

// Projet 1 CDA
// 
// NOM,Prenom
// NGUYEN,Nicolas
//
package projet;

import util.*;

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
        Site site = new Site();
        GUISite ihm = new GUISite(site);
    }
}

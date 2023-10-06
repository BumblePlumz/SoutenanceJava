// Projet 1 CDA
// 
// NOM,Prenom
// NGUYEN,Nicolas
//
package projet;

import util.*;

// Classe principale d'execution du projet
//
public class Projet
{
    public static void main(String a_args[])
    {
        Terminal.ecrireStringln("Execution du projet ");
        Site site = new Site();
        GUISite ihm = new GUISite(site);
    }
}

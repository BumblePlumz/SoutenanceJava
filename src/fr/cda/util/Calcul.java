package util;

public class Calcul {
    /**
     * Soustraction de deux nombres
     * @param n1 premier nombre
     * @param n2 deuxième nombre
     * @param bool true renvoie la valeur absolue de la soustraction
     * @return le resultat de la soustraction
     */
    public static int soustraction (int n1, int n2, boolean bool){
        if (bool){
            return Math.abs(n1 - n2);
        }else{
            return n1-n2;
        }
    }

}

// Fichier: Tore.java
// Par: J. Laforgue
//
// Ce fichier doit appartenir au package Tore.
//
// Il contient des methodes statiques permettant de realiser des
// operations vectorielles dans un espace de Tore
//
package tore;

import java.awt.Point;
import java.util.Vector;

/**
  Classe d'impl�mentation d'un espace rectangulaire orthonorm� de Tore
*/
public class Tore
{
    private int nbX; // Taille en colonne (largeur) de l'espace
    private int nbY; // Taille en ligne (hauteur) de l'espace
    
    /** Cr�ation d'un espace de Tore d�fini par sa longueur et sa hauteur
        @param width longueur (pixel)
        @param height largeur (pixel)
    */
    public Tore(int width, int height)
    {
        this.nbX = width;
        this.nbY = height;
    }

    /**
        Cette methode retourne l'image de B par rapporrt a A, c'est a dire le point le plus proche de A dans un espace de tore
        @param A : coordonnees du point A
        @param B : coordonnees du point B
        @return coordonnees de l'image de B
    */
    public Point image(Point A,
                       Point B)
    {
        int xA;
        int yA;
        int xB;
        int yB;
        int x;
        int y;
        double min;
        double distance;
        
        xA = (int)(A.x);
        yA = (int)(A.y);
        xB = (int)(B.x);
        yB = (int)(B.y);
        
        x=0;
        y=0;
        
        int TX[] = new int[3];
        int TY[] = new int[3];
        TX[0] = -nbX+xB;
        TX[1] = xB;
        TX[2] = nbX+xB;
        TY[0] = -nbY+yB;
        TY[1] = yB;
        TY[2] = nbY+yB;
        
        min=9*(nbX*nbX+nbY*nbY);
        for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                    {
                        xB = TX[i] - xA;
                        yB = TY[j] - yA;
                        distance = Math.sqrt(xB*xB+yB*yB);
                        if (distance < min)
                            {
                                min = distance;
                                x = TX[i];
                                y = TY[j];
                            }
                    }
            }
        return ( new Point(x,y) );
    }

    /**
     * retourne la distance entre 2 points
     * @param p1 premier point
     * @param p2 deuxième point
     * @return retourne la distance (Double)
     */
    static private double distance(Point p1, Point p2)
    {
        return ( Math.sqrt( (p2.x - p1.x)*(p2.x - p1.x) +
                            (p2.y - p1.y)*(p2.y - p1.y) ));
    }


    /**
       Retourne le sens du vecteur (ou vecteur unitaire) de A vers B dans l'espace de Tore.<br>
       Cela correspond au vecteur de A vers l'image de B.<br>
       @param A le point A
       @param B le point B
       @return la direction ( (-1,O,+1)(ex: (-1,-1) (1,0), ...)
     */
    public Point sensTore(Point A,
                          Point B)
    {
        return sens(A,image(A,B));
    }

    /**
     Cette methode consiste � d�terminer le point le plus proche de A dans un espace de Tore parmi un ensemble de points ET dont la distance est superieur � une distance seuil.
     @param A coordonnees du point A
     @param points Ensemble des points
     @param distanceSeuil disnance seuil
     @return le point le plus proche<br>
     si il n'existe pas de tel point alors retourne null. Grace au seuil il ne sera pas pris en consideration. (Sauf si ce seuil est egal a 0).
     <br>
     Remarque: A peut appartenir � l'ensemble des points.
    */
    public Point lePlusProche(Point A,
                              Vector points,
                              int distanceSeuil)
    {
        double min;
        double dis;
        Point image;
        Point p = null;
        Point pcour;
        
        if (points == null) return(p);
        if (points.size() == 0) return(p);
        
        min = 999999;
        
        for(int i=0;i<points.size();i++)
            {
                pcour = (Point)(points.get(i));
                if (! pcour.equals(A))
                    {
                        image = image(A,pcour);
                        dis = distance(A,image);
                        if ( (dis<min) && (dis>(double)distanceSeuil) )
                            {
                                min=dis;
                                p = image;
                            }
                    }
            }
        return(p);
    }
    
    /**
     Cette methode retourne le vecteur unitaire du vecteur AB dans un espace normal euclidien orthonorme (ne tient pas compte du tore).
     @param A le point A
     @param B le point B
     @return La valeur de retour est de la forme (+1,+1) , (+1,-1), (+1,0), ....
    */
    public Point sens(Point A,Point B)
    {
        int sensX;
        int sensY;
        
        // Vecteur AB
        int x = (int)B.x - (int)A.x;
        int y = (int)B.y - (int)A.y;
        
        // Norme du vecteur
        double norme = Math.sqrt(x*x+y*y);
        
        // Vecteur unitaire vers ce point le plus proche
        double dx = (double)x/norme;
        double dy = (double)y/norme;
        
        // On definie le sens en X et Y afin de se diriger vers B
        sensX=0;
        if (dx>0.5){sensX=1;}
        if (dx<-0.5){sensX=-1;}
        sensY=0;
        if (dy>0.5){sensY=1;}
        if (dy<-0.5){sensY=-1;}
        
        return( new Point(sensX,sensY));
    }
    
    /**
       Calcule les nouvelles coordonn�es d'un point en fonction de son d�placment d'une case dans une des 8 directions.<br>
       Le calcul tient compte du Tore.
       @param x coordonn�e en x du point
       @param y coordonn�e en y du point
       @param sensX direction en X (-1, 0, +1)
       @param sensY direction en Y (-1, 0, +1)
       @return les nouvelles coordonn�es du point
    */
    public Point deplacer(int x,int y,int sensX,int sensY)
    {
        int xn = x+sensX;
        int yn = y+sensY;
        
        if (xn<0) { xn=nbX-1; }
        if (xn>=nbX) { xn=0; }
        if (yn<0) { yn=nbY-1; }
        if (yn>=nbY) { yn=0; }
        
        return (new Point(xn,yn));
    }
}
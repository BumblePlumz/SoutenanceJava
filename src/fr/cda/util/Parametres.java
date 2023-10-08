package util;

import java.util.*;
import util.*;

/**
   Classe de decodage des parametres en entree d'un programme java.<br>
   Les parametres doivent tous etre de la forme : param=value <br>
   Importe peu l'ordre des parametres
 */
public class Parametres
{
    private Hashtable<String,String> params;

    /**
       Creer le decodage des parametres
       @param args tableau de string ou String...
       @exception ParametresSyntaxErrorException erreur de syntaxe (un des parametre n'est pas de la forme param=value)
     */
    public Parametres(String... args) throws ParametresSyntaxErrorException
    {
        params = new Hashtable<String,String>();

        for(String param:args)
            {
                String[] targ = param.split("=");
                if (targ.length!=2)
                    throw new ParametresSyntaxErrorException(param);

                params.put(targ[0],targ[1]);
            }
    }
    
    /**
       Retourne un parametre de valeur chaine
       @param param nom du parametre
       @return String la valeur du parametre
       @exception ParametresNotFoundException erreur si param n'est pas trouve
     */
    public String getString(String param) throws ParametresNotFoundException
    {
        String value = params.get(param);
        if (value==null) throw new ParametresNotFoundException(param);
        return value;
    }
    
    /**
       Retourne un parametre de valeur chaine
       @param param nom du parametre
       @param defaut valeur par defaut si le parametre n'existe pas
       @return String la valeur du parametre
     */
    public String getString(String param,String defaut)
    {
        String value = params.get(param);
        if (value==null) return defaut;
        return value;
    }
    
    /**
       Retourne un parametre de valeur int
       @param param nom du parametre
       @return int la valeur du parametre 
       @exception ParametresNotFoundException erreur si param n'est pas trouve
     */
    public int getInt(String param) throws ParametresNotFoundException
    {
        String value = params.get(param);
        if (value==null) throw new ParametresNotFoundException(param);
        return Integer.parseInt(value);
    }
    
    /**
       Retourne un parametre de valeur int
       @param param nom du parametre
       @param defaut valeur par defaut si le parametre n'existe pas
       @return int la valeur du parametre 
     */
    public int getInt(String param,int defaut)
    {
        String value = params.get(param);
        if (value==null) return defaut;
        return Integer.parseInt(value);
    }
    
    /**
       Retourne un parametre de valeur double
       @param param nom du parametre
       @return double la valeur du parametre 
       @exception ParametresNotFoundException erreur si param n'est pas trouve
     */
    public double getDouble(String param,String... defaut) throws ParametresNotFoundException
    {
        String value = params.get(param);
        if (value==null) throw new ParametresNotFoundException(param);
        return Double.parseDouble(value);
    }

    /**
       Retourne un parametre de valeur double
       @param param nom du parametre
       @param defaut valeur par defaut si le parametre n'existe pas
       @return double la valeur du parametre 
     */
    public double getDouble(String param,double defaut)
    {
        String value = params.get(param);
        if (value==null) return defaut;
        return Double.parseDouble(value);
    }
}
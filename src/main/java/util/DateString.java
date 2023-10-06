package util;

import java.util.*;
import java.text.*;

public class DateString
{
    // Teste si date1 est inferieure strict a date2
    //
    public static boolean inferieurStrict(String date1,String date2)
    {
        int r = toCalendar(date1).compareTo(toCalendar(date2));
        return (r==-1);
    }
    
    // Teste si date1 est inferieure ou egale a date2
    //
    public static boolean inferieurOuEgal(String date1,String date2)
    {
        int r = toCalendar(date1).compareTo(toCalendar(date2));
        return ((r==-1)||(r==0));
    }
    
    // Teste si date1 est inferieure ou egale a date2
    //
    public static boolean superieurStrict(String date1,String date2)
    {
        int r = toCalendar(date1).compareTo(toCalendar(date2));
        return (r==1);
    }
    
    // Teste si date1 est superieure ou egale a date2
    //
    public static boolean superieurOuEgal(String date1,String date2)
    {
        int r = toCalendar(date1).compareTo(toCalendar(date2));
        return ((r==1)||(r==0));
    }
    
    // Retourne la date suivante
    //
    public static String dateSuivante(String date)
    {
        Calendar cal = DateString.toCalendar(date);
        cal.set(Calendar.SECOND,86400);
        return toString(cal);
    }

    // Retourne la date precedente
    //
    public static String datePrecedente(String date)
    {
        Calendar cal = DateString.toCalendar(date);
        cal.set(Calendar.SECOND,-86400);
        return toString(cal);
    }


    // Retourne la date Calendar d'une date au format "JJ/MM/AAAA"
    //
    public static Calendar toCalendar(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = sdf.parse(date,
                           new ParsePosition(0));
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(d);
        return dateCal;
    }

    // Retourne la date au format "JJ/MM/AAAA" d'une date Calendar
    //
    public static String toString(Calendar dateCal)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        StringBuffer sb = new StringBuffer();
        sdf.format(dateCal.getTime(),sb,new FieldPosition(0));
        
        return sb.toString();
    }
}
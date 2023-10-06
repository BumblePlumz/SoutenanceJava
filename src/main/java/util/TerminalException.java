package util;

/** Classe de définition de l'exception TerminalException qui peut être retournée dans l'usage des méthodes de la classe Terminal. */
public class TerminalException extends RuntimeException{
    Exception ex;
    TerminalException(Exception e){
        ex = e;
    }
}

package util;

public class ParametresSyntaxErrorException extends RuntimeException
{
    public ParametresSyntaxErrorException(String texte)
    {
        super(texte);
    }
}
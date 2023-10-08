package ihm;

/**
   Dans le cas de l'utilisation d'un formulaire en mode synchrone, si durant l'exécution du formulaire une exception ihm se produit, alors le formulaire sort du mode synhrone et retourne cette exception.
*/
public class FormulaireException extends RuntimeException
{
    public FormulaireException(Exception ex)
    {
        super(ex);
    }
}

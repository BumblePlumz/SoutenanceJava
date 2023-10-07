package ihm;

/** Interface d'utilisation d'un formulaire.<br>
    L'applicatif passer en param�tre du formulaire doit 
    impl�menter cette interface. */
public interface FormulaireInt
{
    /** Cette m�thode est appel�e lors de l'utilsation d'un bouton.<br>
        L'appel � form.getValeurChamp permet de r�cup�rer les valeurs des champs.<br>
        L'appel � form.setValeurChamp permet de changer les valeurs des champs.<br>
        @param form Le formulaire dans lequel se trouve le bouton
        @param nom Le nom du bouton qui a �t� utilis�.
     */
    public void     submit(Formulaire form,String nom);
}

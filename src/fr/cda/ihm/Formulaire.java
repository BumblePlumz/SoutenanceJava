package ihm;

import java.lang.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;


/**
   Classe de d?finition d'un formulaire JAVA permettant de faire ? 
   minima une IHM Java pour saisir des informations et faire des 
   actions via des boutons.<BR>
   Pour cela, il faut que l'applicatif impl?mente les m?thodes de 
   l'interface FormulaireInt.<BR><BR>
*/
public class Formulaire
{
    private FormulaireInt             app;
    private JFrame                    frame;
    private int                       widthFrame;
    private int                       heightFrame;
    private JPanel                    panelPP;
    private ArrayList<JButton>        buttons;
    private boolean                   synchrone;
    private String                    buttonFermer;
    private Exception                 exceptionForm;
    private int                       xCour;
    private int                       yCour;
    private int                       widthLabelCour;
    private int                       widthGapCour;
    private int                       widthTextCour;
    private int                       widthButtonCour;
    private boolean                   sensHorizontal;
    private boolean                   sensVertical;
    private String                    font;     
    private boolean                   autoWidth;

    private static int tailleFonte  = 11;
    private static int heightText   = 20;

    /*  Verrou de synchronisation pour ?tre synchrone
       sur l'affichage d'un formulaire */
    private Integer verrou;

    // Les ?l?ments IHM du formulaire
    Hashtable<String,JComponent>  elements;
    
    /**
       Constructeur d'un formulaire.<br>
       @param titre Titre affich? dans le bandeau de la fenetre
       @param app Un objet dont la classe impl?mente l'interface FormualaireInt
       @param width longueur du formulaire
       @param height hauteur du formulaire
    */
    public Formulaire(String titre, 
                      FormulaireInt app,
                      int width,
                      int height)
    {
        initFormulaire(titre,app,false,width,height,true);
    }

    /**
       Constructeur d'un formulaire.<br>
       @param titre Titre affich? dans le bandeau de la fenetre
       @param app Un objet dont la classe impl?mente l'interface FormualaireInt
       @param synchrone l'ex?cution est synchrone si true
       @param width longueur du formulaire
       @param height hauteur du formulaire
       @param avecFrame si false alors pas de frame cree : que le panel
    */
    public Formulaire(String titre, 
                      FormulaireInt app,
                      boolean synchrone,
                      int width,
                      int height,
                      boolean avecFrame)
    {
        initFormulaire(titre,app,synchrone,width,height,avecFrame);
    }

    public void initFormulaire(String titre, 
                               FormulaireInt app,
                               boolean synchrone,
                               int width,
                               int height,
                               boolean avecFrame)
    {
        this.app             = app;
        if (avecFrame)
            {
                this.frame           = new JFrame(titre);
                this.widthFrame      = width;
                this.heightFrame     = height;
            }
        else
            this.frame       = null;
        this.synchrone       = synchrone;
        this.buttonFermer    = "";
        this.font            = "Courier";
        
        this.panelPP     = new JPanel();
        this.panelPP.setLayout(null);

        if (this.frame != null)
            {
                this.frame.add(panelPP);
                this.frame.addWindowListener(new FormulaireWindowListener());
            }

        this.xCour               = 0;
        this.yCour               = 0;
        this.widthLabelCour      = 100;
        this.widthGapCour        = 0;
        this.widthTextCour       = 100;
        this.widthButtonCour     = 100;
        this.sensVertical        = true;
        this.sensHorizontal      = false;
        this.autoWidth           = true;       

        this.verrou = new Integer(0);

        this.elements = new Hashtable<String,JComponent>();
    }

    /** Initialise l'applicatif.<br>Cette m?thode est utilis?e dans le cas o? quand on cr?e le formulaire on ne connait pas encore l'applicatif qui g?re le formulaire. On appelle alors cette m?thode pour initialiser l'applicatif du formulaire.
        @param app Un objet qui impl?mente l'interface Formulaireint */
    public void setApp(FormulaireInt app)
    {
        this.app = app;
    }

    /**
       Retourne le panel principal du formulaire
       @return panel principal du formulaire
     */
    public JPanel getPanel()
    {
        return this.panelPP;
    }

    /** Retourne la position courante en X de la position des ?l?ments
        @return int la valeur X */
    public int getXCour(){return xCour;}

    /** Retourne la position courante en Y de la position des ?l?ments
        @return int la valeur Y */
    public int getYCour(){return yCour;}

    /** Change la position courante 
     @param x position en abscisse (horizontal) de la fenetre 
     @param y position en ordonn?e (vertical) de la fenetre 
    */
    public void setPosition(int x,int y){xCour=x;yCour=y;}

    /** Change la position courante par delta
     @param deltax position en abscisse (horizontal) de la fenetre 
     @param deltay position en ordonn?e (vertical) de la fenetre 
    */
    public void addPosition(int deltax,int deltay)
    {
        xCour=xCour+deltax;
        yCour=yCour+deltay;
    }

    /** Change la position courante en passant a la ligne suivante en position posx
        @param posx nouvelle position courante en x */
    public void dessous(int posx)
    {
        this.xCour = posx;
        this.yCour = this.yCour+this.heightText+3;
    }

    /** change le sens en HORIZONTAL */
    public void horizontal()
    {
        this.sensHorizontal=true;
        this.sensVertical=false;
    }

    /** change le sens en VERTICAL */
    public void vertical()
    {
        this.sensHorizontal=false;
        this.sensVertical=true;
    }

    /** Le contour des textes de Label Text et Button sont autoamtiquement ou pas ajuster a la taille 
        @param auto boolean
     */
    public void setAutoWidth(boolean auto){autoWidth=auto;}


    /** Change la largeur courante des label
        @param width nouvelle largeur courante
     */
    public void setWidthLabelCour(int width){widthLabelCour=width;}

    /** Change la largeur courante des textes de saisi
        @param width nouvelle largeur courante
     */
    public void setWidthTextCour(int width){widthTextCour=width;}


    /** Change la largeur courante de s?paration entre le label et le texte des champs de saisi
        @param width nouvelle largeur courante
     */
    public void setWidthGapCour(int width){widthGapCour=width;}

    /** Change la largeur courante des boutons
        @param width nouvelle largeur courante
     */
    public void setWidthButtonCour(int width){widthButtonCour=width;}



    /** Affichage du formulaire.<br>
     Cette m?thode peut ?tre synhrone (voir le parametre synhrone ? la cr?ation du formulaire.
    */
    public void afficher() throws FormulaireException
    {
        this.frame.setPreferredSize(new Dimension(this.widthFrame+15,this.heightFrame+40));
        this.frame.pack();
        this.frame.show();
        if (this.synchrone) 
            {
                attendre();
                if (exceptionForm!=null)
                    {
                        Exception ex = exceptionForm;
                        exceptionForm = null;
                        throw new FormulaireException(ex);
                    }
            }
    }

    /** Affichage du formulaire en x,y de l'ecran */
    public void afficher(int x,int y) throws FormulaireException
    {
        this.frame.setLocation(x,y);
        afficher();
    }

    /** Permet de fermer le formulaire
     */
    public void fermer()
    {
        frame.dispose();
        if (synchrone) debloquer();
    }


    /** Ajout dans le formulaire un label.<br>
        Valeur -1 implique prend la valeur pr?c?dente et comportement par d?faut de positionnement 
        @param label Chaine qui pr?c?de la zone de saisie
    */
    public void addLabel(String label)
    {
        int xChamp=this.xCour;
        int yChamp=this.yCour;
        int widthLabel=this.widthLabelCour;
        
        if (sensHorizontal) 
            this.xCour=this.xCour+this.widthLabelCour;
        if (sensVertical)
            this.yCour=yChamp+this.heightText+3;

        JLabel l = new JLabel(label);

        JPanel p = new JPanel();
        p.setLayout(null);
        Font f = new Font(font,Font.BOLD,tailleFonte);
        l.setFont(f);

        if (autoWidth) widthLabel = l.getFontMetrics(f).stringWidth(label); 

        l.setBounds(0,0,widthLabel,this.heightText);

        p.add(l);

        p.setBounds(xChamp,yChamp,widthLabel,this.heightText);

        this.panelPP.add(p);
        this.panelPP.repaint();
        if (this.frame!=null)
            this.frame.repaint();
    }
    


    /** Ajout dans le formulaire d'un texte de saisie compos? d'un label 
        et d'une zone de saisie.<br>
        @param nom Le nom du champ
        @param label Chaine qui pr?c?de la zone de saisie
        @param editable d?termine si la zone de saisie est ?ditable 
        @param value valeur initiale dans la zone de saisie
    */
    public void addText(String nom,
                        String label,
                        boolean editable,
                        String value)
    {
        int xChamp=this.xCour;
        int yChamp=this.yCour;
        int widthLabel=this.widthLabelCour;
        int widthGap=this.widthGapCour;
        int widthText=this.widthTextCour;
        
        if (this.sensHorizontal)
            this.xCour=xChamp+widthLabel+widthGap+widthText+3;
        if (this.sensVertical)
            this.yCour=yChamp+this.heightText+3;

        JLabel l = new JLabel(label);
        JTextField tf = new JTextField();
        tf.setEditable(editable);
        tf.setText(value);
        elements.put(nom,tf);
        

        JPanel p = new JPanel();
        p.setLayout(null);
        Font f = new Font(font,Font.BOLD,tailleFonte);
        l.setFont(f);
        tf.setFont(f);

        if (autoWidth) widthLabel = l.getFontMetrics(f).stringWidth(label)+3; 

        l.setBounds(0,0,widthLabel,this.heightText);
        tf.setBounds(widthLabel+widthGap,0,widthText,this.heightText);


        p.add(l);
        p.add(tf);

        p.setBounds(xChamp,yChamp,widthLabel+widthGap+widthText,this.heightText);

        this.panelPP.add(p);
        this.panelPP.repaint();
        if (this.frame!=null) this.frame.repaint();
    }

    /** Ajout dans le formulaire d'un texte de saisie compos? d'un label 
        et d'une zone de saisie.<br>
        @param nom Le nom du champ
        @param label Chaine qui pr?c?de la zone de saisie
        @param editable d?termine si la zone de saisie est ?ditable 
        @param value valeur initiale dans la zone de saisie 
        @param width longueur de la zone de texte
        @param height hauteur de la zone de texte
    */
    public void addZoneText(String nom,
                            String label,
                            boolean editable,
                            String value,
                            int width,
                            int height)
    {
        int xChamp=this.xCour;
        int yChamp=this.yCour;

        if (this.sensHorizontal)
            this.xCour=xChamp + width+3;
        if (this.sensVertical)
            this.yCour=yChamp+height+3;
        
        JLabel l = new JLabel(label);
        l.setFont(new Font(font,Font.BOLD,tailleFonte));
        l.setBounds(0,0,width,this.heightText);

        JTextArea tf = new JTextArea();
        tf.setEditable(editable);
        tf.setText(value);
        tf.setFont(new Font(font,Font.BOLD,tailleFonte));

        elements.put(nom,tf);

        JScrollPane scrollbar = new JScrollPane(tf);
        scrollbar.setBounds(0,0+this.heightText+3,width,height-this.heightText-3);
        scrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
        scrollbar.setPreferredSize(new Dimension(width,height-this.heightText-3));

        JPanel p = new JPanel();
        p.setLayout(null);

        p.add(l);
        p.add(scrollbar);
        
        p.setBounds(xChamp,yChamp,width,height);

        this.panelPP.add(p);
        this.panelPP.repaint();
        if (this.frame!=null) this.frame.repaint();
    }
    
    /** Ajout dans le formulaire d'une liste scrollable de valeurs.<br>
        @param nom Le nom de la liste scrollable
        @param titre Titre qui pr?c?de la liste
        @param editable d?termine si la zone de saisie est ?ditable 
        @param values valeur initiale dans la zone de saisie 
        @param width longueur de la zone (ou -1)
        @param height hauteur de la zone 
    */
    public void addListScroll(String nom,
                              String titre,
                              boolean editable,
                              String[] values,
                              int width,
                              int height)
    {
        int xChamp=this.xCour;
        int yChamp=this.yCour;

        if (this.sensHorizontal)
            this.xCour=xChamp + width+3;
        if (this.sensVertical)
            this.yCour=yChamp+height+3;
        
        JLabel titrel = new JLabel(titre);
        titrel.setFont(new Font(font,Font.BOLD,tailleFonte));
        titrel.setBounds(0,0,width,this.heightText);

        JList liste = new JList(new DefaultListModel());
        liste.setFont(new Font(font,Font.BOLD,tailleFonte));
        JScrollPane scroll = new JScrollPane(liste);
        scroll.setBounds(0,0+this.heightText+3,width,height-this.heightText-3);

        liste.setEnabled(editable);
        Vector<String> v = new Vector<String>();
        if (values!=null)
            Collections.addAll(v,values);

        elements.put(nom,liste);


        JPanel p = new JPanel(new BorderLayout());
        p.add(titrel,BorderLayout.NORTH);
        p.add(scroll,BorderLayout.CENTER);

        p.setBounds(xChamp,yChamp,width,height);

        liste.setListData(v);

        this.panelPP.add(p);
        this.panelPP.repaint();
        if (this.frame!=null)this.frame.repaint();
    }
    
    /** Designe le bouton qui ferme la fenetre et debloque si synchrone
        @param nomButton le nom du bouton
     */
    public void setButtonFermer(String nomButton)
    {
        this.buttonFermer = nomButton;
    }

    /** Ajout dans le formulaire d'un bouton.<br>
        @param nom Le nom du bouton
        @param button Texte du bouton 
    */
    public void addButton(String nom,
                          String button)
    {
        int xChamp=this.xCour;
        int yChamp=this.yCour;
        int width =this.widthButtonCour;

        if (this.sensHorizontal)
            this.xCour=xChamp+width+3;
        if (this.sensVertical)
            this.yCour=yChamp+this.heightText+3;

        
        JButton b = new JButton(button);
        Font f = new Font(font,Font.BOLD,tailleFonte);
        b.setFont(f);
        elements.put(nom,b);
        b.setMargin(new Insets(0, 0, 0, 0));
        if (autoWidth) width = b.getFontMetrics(f).stringWidth(button)+20;

        b.setBounds(xChamp,yChamp,width,this.heightText);

        b.setPreferredSize(new Dimension(width,this.heightText));
        b.addActionListener(new SubmitListener(this,nom));

        this.panelPP.add(b);
        this.panelPP.repaint();
        if (this.frame!=null)this.frame.repaint();
    }

    /** Ajout d'un Panel dans le formulaire
        @param panel le panel a ajouter
        @param width largeur du panel
        @param height hauteur du panel
    */
    public void addPanel(JPanel panel,int width, int height)
    {
        int posx = this.xCour;
        int posy = this.yCour;

        if (this.sensHorizontal)
            this.xCour=posx+width+3;
        if (this.sensVertical)
            this.yCour=posy+height+3;

        panel.setBounds(posx,posy,width,height);
        this.panelPP.add(panel);
        this.panelPP.repaint();
        if (this.frame!=null)this.frame.repaint();
    }

    /** Methode qui ajoute un canvas de grille
        @param nbLigne nombre de ligne de la grille
        @param nbColonne nombre de colonne de la grille
        @param tailleCase taille de la case (en pixel)
        @param controle objet dont la classe surcharge les m?thodes de l'interface ControlesCanvasIHM. Il permet de r?aliser les actions r?alis?es dans la grille.
     */
    public CanvasIHM addGrilleIHM(int nbLigne,
                                  int nbColonne,
                                  int tailleCase,
                                  AdaptaterControlesCanvasIHM controle)
    {
        CanvasIHM grille;
        
        grille = new CanvasIHM(nbLigne,nbColonne,tailleCase);
        grille.setActions(controle);
        addPanel(grille.getPanel(),
                 grille.getWidth()+30,grille.getHeight()+30);
        
        return(grille);
    }

    /** Methode qui ajoute un canvas de grille
        @param width largeur du canvas
        @param height hauteur du canvas
        @param controle objet dont la classe surcharge les m?thodes de l'interface ControlesCanvasIHM. Il permet de r?aliser les actions r?alis?es dans la grille.
     */
    public CanvasIHM addCanvasIHM(int width,
                                  int height,
                                  AdaptaterControlesCanvasIHM controle)
    {
        CanvasIHM grille;
        
        grille = new CanvasIHM(width,height);
        grille.getPanel().setBackground(Color.yellow);
        grille.setActions(controle);
        addPanel(grille.getPanel(),
                 grille.getWidth()+30,grille.getHeight()+30);

        return(grille);
    }

    /** M?thode qui retourne la valeur d'un champ.
        @param nom Le nom du champ
        @return valeur la nouvelle valeur du champ */
    public String getValeurChamp(String nom)
    {
        String ret="";
        JComponent comp = elements.get(nom);
            
        try{
            if ( comp instanceof JList )
                {
                    JList l = (JList)comp;
                    if (! l.isSelectionEmpty())
                        ret = (String)(l.getSelectedValue());
                }
            else
                {
                    JTextComponent ct = (JTextComponent)comp;
                    ret=ct.getText();
                }
        }catch(Exception ex){}
        return ret;
    }

    /** M?thode qui change la valeur d'un champ.
        @param nom Le nom du champ
        @param valeur la nouvelle valeur du champ */
    public void setValeurChamp(String nom,String valeur)
    {
        JComponent comp = elements.get(nom);
        try{
            JTextComponent ct = (JTextComponent)comp;
            ct.setText(valeur);
        }catch(Exception ex){}
    }


    /** M?thode qui change les valeurs d'une liste de scroll
        @param nom Le nom du champ
        @param values la nouvelle liste de valeur */
    public void setListData(String nom,String[] values)
    {
        try{
            JComponent comp = elements.get(nom);
            JList l = (JList)comp;
            Vector<String> v = new Vector<String>();
            if (values!=null)
                Collections.addAll(v,values);
            l.setListData(v);
        }catch(Exception ex){}
    }


    // Classe d'action des boutons du formulaire
    class SubmitListener implements ActionListener
    {
        private Formulaire form;
        private String     nomSubmit;

        public SubmitListener(Formulaire form, String nomSubmit)
        {
            this.form      = form;
            this.nomSubmit = nomSubmit;
        }

        public void actionPerformed(ActionEvent e)
        {
            try{
                if (app!=null)   // NE PAS UTILISER this.app : on est dans une inner class
                    app.submit(form,nomSubmit);
                
                if (this.nomSubmit.equals( buttonFermer))
                    {
                        frame.dispose();
                        if (synchrone) debloquer();
                    }
            }
            catch(Exception ex)
                {
                    ex.printStackTrace();
                    if (synchrone) 
                        {
                            frame.dispose();
                            debloquer();
                            exceptionForm = ex;
                        }
                }
        }
    }

    // Classe pour fermer le formulaire
    class FormulaireWindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            frame.dispose();
            if (synchrone) debloquer();
        }
    }

    /* Pour attendre l'execution du code si mmode synchrine */
    private void attendre() 
    {
        synchronized(this.verrou)
            {
                try{this.verrou.wait();}catch(Exception l_ex)
                    {System.out.println(l_ex);};
            }
    }
    private void debloquer()
    {
        synchronized(this.verrou)
            {
                try{this.verrou.notify();}catch(Exception l_ex){};
            }
    }

    // ==========================================
    /** M?thodes de saisie ?l?mentaire d'un texte.<BR>
        Ceci remplace la classe Terminal
        @param texteInvite Texte d'invite de saisi
    */
    public static String lireString(String texteInvite)
    {
        SaisieString saisie = new SaisieString();
        Formulaire form = new Formulaire("Saisi de String",
                                         saisie,
                                         true,150,2*Formulaire.heightText,
                                         true);

        form.addText("String",texteInvite,true,"");
        form.addButton("Valider","Valider");
        form.setButtonFermer("Valider");
        try{
            form.afficher();
        }catch(Exception ex){
            System.out.println("Erreur de saisi dans le formulaire"); }
        
        return saisie.value;
    }

    /**
       desactiver un des composants du formulaire
       @param nom Nom du composant
    */
    public void desactiver(String nom) throws RuntimeException
    {
        JComponent e = elements.get(nom);
        if (e!=null)
            {
                if (e.getClass().getName().equals("javax.swing.JButton"))
                    e.setEnabled(false);
                if (e.getClass().getName().equals("javax.swing.JTextField"))
                    {
                        JTextField tf = (JTextField)e;
                        tf.setEditable(false);
                    }
            }
        else
            throw new RuntimeException(nom+" n'existe pas");
    }

    /**
       activer un des composants du formulaire
       @param nom Nom du composant
    */
    public void activer(String nom) throws RuntimeException
    {
        JComponent e = elements.get(nom);
        if (e!=null)
            {
                if (e.getClass().getName().equals("javax.swing.JButton"))
                    e.setEnabled(true);
                if (e.getClass().getName().equals("javax.swing.JTextField"))
                    {
                        JTextField tf = (JTextField)e;
                        tf.setEditable(true);
                    }
            }
        else
            throw new RuntimeException(nom+" n'existe pas");
    }


    /**
      Programme principal de test de la classe Formulaire.
      Cette m?thode teste les m?thodes de classe.
    */
    public static void main (String... args) throws Exception
    {
        // Affichage d'un formulaire qui calcule l'addition de deux entiers
        //
        TesterFormulaire test1 = new TesterFormulaire();
        boolean synchrone = false;
        Formulaire form = new Formulaire("TESTER",test1,synchrone,700,600,true);

        form.setAutoWidth(true);
        form.addLabel("Faire l'addition de deux entiers :");
        form.addText("val1","Valeur 1 :",true,"123");
        form.addText("val2","Valeur 2",true,"456");
        form.setWidthButtonCour(150);
        form.addButton("add","Additionner");
        String[] values = { "la belle de nuit", "la fille de l'air","le gar?on manqu?","abcdefghijklmnopqrstuvwxyz","111","2222","3333"};
        form.addListScroll("LIST","Zone ",true,values,200,100);
        form.addButton("SELECTION","SELECTION");
        form.setPosition(300,10);
        form.addZoneText("zone","Historique",true,"",300,500);


        form.addButton("exit","Fermer la fenetre");
        form.setButtonFermer("exit");
        form.afficher();
        
        // Saisie SYNCHRONE d'une chaine
        //
        //String str = Formulaire.lireString("Saisir");
        //System.out.println(str);


        // Un autre exemple de formulaire pour montrer les possibilit?s de positionnement
        //
        form = new Formulaire("TESTER",test1,false,1200,600,true);
        
        form.setPosition(10,10);
        form.horizontal();
        form.addText("Exemple","Exemple",true,"ABCDE");
        form.addPosition(50,form.getYCour());
        form.addText("Champ1","Champ1",true,""); 
        form.dessous(10);
        form.vertical();
        form.setWidthButtonCour(50);
        form.addButton("B1","B1"); 
        form.addButton("B2","B2");
        form.horizontal();
        form.addButton("B3","B3");
        form.addButton("B4","B4"); //x=100 et en dessous par defaut
        form.addButton("B5","B5"); 
        form.dessous(30);
        form.vertical();
        form.addText("Champ2","Champ2",false,"222"); // non editable
        form.addText("Champ3","Champ3",true,"33333");
        form.setPosition(300,100);
        form.vertical();
        form.addZoneText("z1","Zone 1",true,"",300,100);
        form.addZoneText("z2","Zone 2",true,"",100,100);
        form.addText("z4","Champ4",true,"33333");
        form.setPosition(650,10);
        form.addGrilleIHM(10,10,20,new TestActions(test1));
        form.setWidthButtonCour(150);
        form.addButton("Couleur1","Couleur1");
        form.addButton("Couleur2","Couleur2");
        form.addButton("Couleur3","Couleur3");
        form.addButton("Couleur4","Couleur4");
        form.afficher();

    }
}

// ================================================================

// Classe non inner-class car utilis?e dans une m?thode statique.
// Cette classe est utilis? pour saisir une chaine dans une IHM
//  et de mani?re synchrone
//
class SaisieString implements FormulaireInt
{
    String value;
    
    public SaisieString()
    {
        value="";
    }
    
    public void submit(Formulaire form,String nomSubmit)
    {
        if (nomSubmit.equals("Valider"))
            {
                this.value   = form.getValeurChamp("String");
            }
        
    }

}

class TesterFormulaire implements FormulaireInt
{
    String historique;
    public int couleur;
    
    public TesterFormulaire()
    {
        historique = "";
        couleur=1;
    }

    public void submit(Formulaire form,String nomSubmit)
    {
        if (nomSubmit.equals("add"))
            {
                try{
                    String s1 = form.getValeurChamp("val1");
                    String s2 = form.getValeurChamp("val2");
                    int v1 = Integer.parseInt(s1);
                    int v2 = Integer.parseInt(s2);
                    
                    historique = historique+String.format("%5d + %5d = %7d\n",v1,v2,v1+v2);
                }catch(Exception ex)
                    {
                        historique = historique+ex.getMessage()+"\n";
                    }
            }
        if (nomSubmit.equals("SELECTION"))
            {
                historique = historique + ">> "+ form.getValeurChamp("LIST")+"\n";
            }
        form.setValeurChamp("zone",historique);

        if (nomSubmit.equals("B1"))
            {
                form.desactiver("B3");
            }
        if (nomSubmit.equals("B2"))
            {
                form.activer("B3");
            }
        if (nomSubmit.equals("B4"))
            {
                form.activer("Champ2");
            }
        if (nomSubmit.equals("B5"))
            {
                form.desactiver("Champ2");
            }
        if (nomSubmit.equals("Couleur1")) couleur=1;
        if (nomSubmit.equals("Couleur2")) couleur=2;
        if (nomSubmit.equals("Couleur3")) couleur=3;
        if (nomSubmit.equals("Couleur4")) couleur=4;
    }
}

class TestActions extends AdaptaterControlesCanvasIHM
{
    TesterFormulaire tf;
    public TestActions(TesterFormulaire tf)
    {
        this.tf=tf;
    }

    public void pointerCaseGrille(int xCase,int yCase,CanvasIHM ihm)
    {
        //point = new Point(xCase,yCase);
        ihm.setMarque(tf.couleur,xCase,yCase);
    }
}

    

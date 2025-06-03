import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ButtonBar.ButtonData ;

import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.util.ArrayList;


/**
 * Vue du jeu du pendu
 */
public class Pendu extends Application {
    /**
     * modèle du jeu
     **/
    private MotMystere modelePendu;
    /**
     * Liste qui contient les images du jeu
     */
    private ArrayList<Image> lesImages;
    /**
     * Liste qui contient les noms des niveaux
     */    
    public List<String> niveaux;

    // les différents contrôles qui seront mis à jour ou consultés pour l'affichage
    /**
     * le dessin du pendu
     */
    private ImageView dessin;
    /**
     * le mot à trouver avec les lettres déjà trouvé
     */
    private Text motCrypte;
    /**
     * la barre de progression qui indique le nombre de tentatives
     */
    private ProgressBar pg;
    /**
     * le clavier qui sera géré par une classe à implémenter
     */
    private Clavier clavier;
    /**
     * le text qui indique le niveau de difficulté
     */
    private Text leNiveau;
    /**
     * le chronomètre qui sera géré par une clasee à implémenter
     */
    private Chronometre chrono;
    /**
     * le panel Central qui pourra être modifié selon le mode (accueil ou jeu)
     */
    private BorderPane panelCentral;
    /**
     * le bouton Paramètre / Engrenage
     */
    private Button boutonParametres;
    /**
     * le bouton Accueil / Maison
     */    
    private Button boutonMaison;
    /**
     * le bouton qui permet de (lancer ou relancer une partie
     */ 
    private Button bJouer;
    /**
     * la scène de l'application
     */
    private Scene scene;

    /**
     * initialise les attributs (créer le modèle, charge les images, crée le chrono ...)
     */
    @Override
    public void init() {
        this.modelePendu = new MotMystere("/usr/share/dict/french", 3, 10, MotMystere.FACILE, 10);
        this.lesImages = new ArrayList<>();
        this.chargerImages("./img");
        this.chrono = new Chronometre();
    }

    /**
     * @return  le graphe de scène de la vue à partir de methodes précédentes
     */
    private Scene laScene(){
        BorderPane fenetre = new BorderPane();
        fenetre.setTop(this.titre());
        fenetre.setCenter(this.panelCentral);
        this.scene = new Scene(fenetre, 800, 1000);
        return this.scene;
    }

    /**
     * @return le panel contenant le titre du jeu
     */
    private BorderPane titre(){     
        BorderPane banniere = new BorderPane();
        Label title = new Label("Jeu du Pendu");
        title.setFont(Font.font("Arial", 32));

        ImageView home = new ImageView(new Image("file:./img/home.png"));
        home.setFitHeight(30.0);
        home.setFitWidth(30.0);
        this.boutonMaison = new Button("",home);
        this.boutonMaison.setOnAction(new RetourAccueil(this.modelePendu, this));
        
        ImageView param = new ImageView(new Image("file:./img/parametres.png"));
        param.setFitHeight(30.0);
        param.setFitWidth(30.0);
        this.boutonParametres = new Button("",param);
        this.boutonParametres.setOnAction(new ControleurParametres(this.modelePendu, this));

        ImageView info = new ImageView(new Image("file:./img/info.png"));
        info.setFitHeight(30.0);
        info.setFitWidth(30.0);
        Button infos = new Button("",info);
        infos.setOnAction(new ControleurInfos(this));

        banniere.setLeft(title);
        HBox boutons = new HBox(this.boutonMaison, this.boutonParametres, infos);
        banniere.setRight(boutons);
        banniere.setPadding(new Insets(15));
        banniere.setStyle("-fx-background-color: rgb(215, 211, 255);");
        BorderPane.setAlignment(title, Pos.CENTER);
        return banniere;
    }

    // /**
     // * @return le panel du chronomètre
     // */
    // private TitledPane leChrono(){
        // A implementer
        // TitledPane res = new TitledPane();
        // return res;
    // }

    // /**
     // * @return la fenêtre de jeu avec le mot crypté, l'image, la barre
     // *         de progression et le clavier
     // */
    private BorderPane fenetreJeu(){
        BorderPane res = new BorderPane();
        VBox center = new VBox();
        Text mot = this.motCrypte;
        ImageView pendu = this.dessin;
        ProgressBar bar = this.pg;
        Clavier touches = new Clavier("ABCDEFGHIJKLMNOPQRSTUVWXYZ-", new ControleurLettres(modelePendu, this));
        center.getChildren().addAll(mot,pendu,bar,touches);

        VBox right = new VBox();
        Text niv = new Text("Niveau "+this.leNiveau);
        Chronometre chronometre = this.chrono;
        this.bJouer.setText("Nouveau mot");
        right.getChildren().addAll(niv,chronometre,this.bJouer);

        center.setPadding(new Insets(10));
        right.setPadding(new Insets(20));
        res.setCenter(center);
        res.setRight(right);
        return res;
    }

    // /**
     // * @return la fenêtre d'accueil sur laquelle on peut choisir les paramètres de jeu
     // */
    private BorderPane fenetreAccueil(){
        BorderPane res = new BorderPane();
        this.bJouer.setText("Lancer une partie");
        ToggleGroup niveau = new ToggleGroup();
        RadioButton fac = new RadioButton("Facile");
        RadioButton med = new RadioButton("Médium");
        RadioButton dif = new RadioButton("Difficile");
        RadioButton exp = new RadioButton("Expert");
        fac.setToggleGroup(niveau);
        dif.setToggleGroup(niveau);
        med.setToggleGroup(niveau);
        exp.setToggleGroup(niveau);
        fac.setSelected(true);

        VBox grNiveau = new VBox();
        grNiveau.getChildren().addAll(fac, med, dif, exp);
        TitledPane difficulte = new TitledPane("Niveau de difficulté", grNiveau);
        BorderPane blocLevel = new BorderPane();
        blocLevel.setTop(difficulte);
        res.setTop(this.bJouer);
        res.setCenter(blocLevel);
        BorderPane.setMargin(start, new Insets(10));
        BorderPane.setMargin(blocLevel, new Insets(10));
        return res;
    }

    /**
     * charge les images à afficher en fonction des erreurs
     * @param repertoire répertoire où se trouvent les images
     */
    private void chargerImages(String repertoire){
        for (int i=0; i<this.modelePendu.getNbErreursMax()+1; i++){
            File file = new File(repertoire+"/pendu"+i+".png");
            System.out.println(file.toURI().toString());
            this.lesImages.add(new Image(file.toURI().toString()));
        }
    }

    public void modeAccueil(){
        BorderPane accueil = new BorderPane();
        accueil.setTop(this.titre());
        this.boutonMaison.setDisable(true);
        this.boutonParametres.setDisable(false);
        accueil.setCenter(this.fenetreAccueil());
        boutonMaison.disableProperty();
        boutonParametres.setDisable(false);
        this.scene.setRoot(accueil);
    }
    
    public void modeJeu(){
        BorderPane jeu = new BorderPane();
        jeu.setTop(this.titre());
        this.boutonMaison.setDisable(false);
        this.boutonParametres.setDisable(true);
        jeu.setCenter(this.fenetreJeu());
        boutonParametres.disableProperty();
        boutonMaison.setDisable(false);
        this.scene.setRoot(jeu);
    }
    
    public void modeParametres(){
        // A implémenter
    }

    /** lance une partie */
    public void lancePartie(){
        this.modelePendu.setMotATrouver();
        this.chrono.resetTime();
        this.dessin.setImage(lesImages.get(0));
        this.modeJeu();
    }

    /**
     * raffraichit l'affichage selon les données du modèle
     */
    public void majAffichage(){
        this.chrono = this.getChrono();
        this.dessin.setImage(this.lesImages.get(this.modelePendu.getNbErreursMax()-this.modelePendu.getNbErreursRestants()));
        double progression = this.modelePendu.getMotCrypte().length()-this.modelePendu.getNbLettresRestantes()/this.modelePendu.getMotCrypte().length();
        this.pg = new ProgressBar(progression);
        this.motCrypte = new Text(this.modelePendu.getMotCrypte());
        this.clavier.desactiveTouches(this.modelePendu.getLettresEssayees());
        if (this.modelePendu.gagne()){
            this.popUpMessageGagne();
        } else if (this.modelePendu.perdu()){
            this.popUpMessagePerdu();
        }
    }

    /**
     * accesseur du chronomètre (pour les controleur du jeu)
     * @return le chronomètre du jeu
     */
    public Chronometre getChrono(){
        // A implémenter
        return null; // A enlever
    }

    public Alert popUpPartieEnCours(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"La partie est en cours!\n Etes-vous sûr de l'interrompre ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Attention");
        return alert;
    }
        
    public Alert popUpReglesDuJeu(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pour gagner, il vous faut décrypter le mot caché.\n ATTENTION ! Vous ne disposez que de 9 essais pour y parvenir.\n Faites également attention au chronomètre.", ButtonType.OK);
        alert.setTitle("Jeu du Pendu");
        alert.setHeaderText("Règles du jeu");
        return alert;
    }
    
    public Alert popUpMessageGagne(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Bravo ! Vous avez gagné !",ButtonType.OK);     
        alert.setTitle("Jeu du Pendu"); 
        alert.setHeaderText("Vous avez gagné :)");  
        return alert;
    }
    
    public Alert popUpMessagePerdu(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vous avez perdu\n Le mot à trouver était "+this.modelePendu.getMotATrouve(), ButtonType.OK);     
        alert.setTitle("Jeu du Pendu"); 
        alert.setHeaderText("Vous avez perdu :(");  
        return alert;
    }

    /**
     * créer le graphe de scène et lance le jeu
     * @param stage la fenêtre principale
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("IUTEAM'S - La plateforme de jeux de l'IUTO");
        stage.setScene(this.laScene());
        this.modeAccueil();
        stage.show();
    }

    /**
     * Programme principal
     * @param args inutilisé
     */
    public static void main(String[] args) {
        launch(args);
    }    
}

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
import javax.imageio.plugins.tiff.TIFFTagSet;
import javax.swing.text.html.ImageView;


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
        this.bJouer = new Button();
        this.dessin = new ImageView(this.lesImages.get(0));
        this.pg = new ProgressBar(0);
        this.clavier = new Clavier("ABCDEFGHIJKLMNOPQRSTUVWXYZ-", new ControleurLettres(modelePendu, this));
        this.motCrypte = new Text(this.modelePendu.getMotCrypte());
        this.motCrypte.setFont(new Font(20));
        this.niveaux = Arrays.asList("Facile","Médium","Difficile","Expert");
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
        //this.boutonParametres.setOnAction(new ControleurParametres(this.modelePendu, this));

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
    private TitledPane leChrono(){
        this.chrono.start();
        TitledPane res = new TitledPane("Chronomètre", this.chrono);
        return res;
    }

    // /**
     // * @return la fenêtre de jeu avec le mot crypté, l'image, la barre
     // *         de progression et le clavier
     // */
    private BorderPane fenetreJeu(){

        this.modelePendu = new MotMystere("/usr/share/dict/french", 3, 10, this.modelePendu.getNiveau(), 10);
        BorderPane res = new BorderPane();
        VBox center = new VBox();
        center.getChildren().addAll(this.motCrypte,this.dessin,this.pg,this.clavier);

        VBox right = new VBox();
        this.leNiveau = new Text("Niveau "+this.niveaux.get(this.modelePendu.getNiveau()));
        this.bJouer.setText("Nouveau mot");
        right.getChildren().addAll(this.leNiveau,this.leChrono(),this.bJouer);

        center.setPadding(new Insets(60));
        right.setPadding(new Insets(20));
        BorderPane.setMargin(this.leChrono(), new Insets(10));
        BorderPane.setMargin(this.bJouer, new Insets(10));
        BorderPane.setMargin(this.dessin, new Insets(10));
        BorderPane.setMargin(this.pg, new Insets(10));
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
        this.bJouer.setOnAction(new ControleurLancerPartie(modelePendu, this));
        ToggleGroup niveau = new ToggleGroup();
        VBox grNiveau = new VBox();
        for (int i=0; i<this.niveaux.size(); i++){
            RadioButton niv = new RadioButton(this.niveaux.get(i)); 
            niv.setOnAction(new ControleurNiveau(modelePendu));
            niv.setToggleGroup(niveau);
            if (i==0){
                niv.setSelected(true);
            }
            grNiveau.getChildren().add(niv);
        }
        TitledPane difficulte = new TitledPane("Niveau de difficulté", grNiveau);
        BorderPane blocLevel = new BorderPane();
        blocLevel.setTop(difficulte);
        res.setTop(this.bJouer);
        res.setCenter(blocLevel);
        BorderPane.setMargin(this.bJouer, new Insets(10));
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
        this.panelCentral=this.fenetreAccueil();
        BorderPane fenetre = new BorderPane();
        fenetre.setCenter(this.panelCentral);
        fenetre.setTop(this.titre());
        this.boutonMaison.setDisable(true);
        this.boutonParametres.setDisable(false);
        this.scene.setRoot(fenetre);
    }
    
    public void modeJeu(){ 
        this.panelCentral=this.fenetreJeu();
        BorderPane fenetre = new BorderPane();
        fenetre.setCenter(this.panelCentral);
        fenetre.setTop(this.titre());
        this.boutonMaison.setDisable(false);
        this.boutonParametres.setDisable(true);
        this.scene.setRoot(fenetre);
    }
    
    public void modeParametres(){
        // A implémenter
    }

    /** lance une partie */
    public void lancePartie(){
        this.modelePendu.setMotATrouver();
        this.chrono.resetTime();
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
        return this.chrono;
    }

    public Alert popUpPartieEnCours(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"La partie est en cours!\n Etes-vous sûr de l'interrompre ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Attention");
        return alert;
    }
        
    public Alert popUpReglesDuJeu(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pour gagner, il vous faut décrypter le mot caché.\n ATTENTION ! Vous ne disposez que de 10 essais.", ButtonType.OK);
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

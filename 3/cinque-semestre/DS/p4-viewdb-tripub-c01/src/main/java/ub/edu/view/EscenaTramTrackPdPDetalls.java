package ub.edu.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EscenaTramTrackPdPDetalls extends Escena{
    private static final double ESPAI_ENTRE_BOTONS = 100;
    public AnchorPane tramTrack_pane;
    public Text tramTrack_text1;
    public Text tramTrack_text2;
    public Button tramTrack_btn;
    private String correu_persona;
    private Integer id_ruta;
    private Integer id_tram_track;

    public void start() throws Exception {
        this.correu_persona=this.controller.getSessionMemory().getCorreuPersona();
        this.id_ruta=this.controller.getSessionMemory().getIdRuta();
        this.id_tram_track=this.controller.getSessionMemory().getIdTram();
        popularPuntDePas();
    }

    public void popularPuntDePas() throws Exception {
        //coger el tramtrack por ese id concreto
        //coger la  propiedad de los 2 ids que son fk que son de los 2 punt de pas
        HashMap<Object,Object> hashMaptramTrack = controller.getTramTrackById(this.id_tram_track);

        //coger los 2 punt de pas y los 2 localitats asociadas
        Integer id_pdp_origen = (Integer) hashMaptramTrack.get("id_pdp_origen");
        Integer id_pdp_desti = (Integer) hashMaptramTrack.get("id_pdp_desti");
        HashMap<Object,Object> hashMaptramPuntDePas_Origen = controller.getLocalitat_y_PuntDePasById(id_pdp_origen);
        HashMap<Object,Object> hashMaptramPuntDePas_Desti = controller.getLocalitat_y_PuntDePasById(id_pdp_desti);
        //los hashmaps contienen: id,nom,highlight

        List<HashMap<Object,Object>> hashMapList = new ArrayList<>();
        hashMapList.add(hashMaptramPuntDePas_Origen);
        hashMapList.add(hashMaptramPuntDePas_Desti);

        List<Node> excursionsPaneChildren = tramTrack_pane.getChildren();

        double layoutX_text1 = tramTrack_text1.getLayoutX();
        double layoutY_text1 = tramTrack_text1.getLayoutY();

        double layoutX_text2 = tramTrack_text2.getLayoutX();
        double layoutY_text2 = tramTrack_text2.getLayoutY();

        double width = tramTrack_btn.getWidth();
        double height = tramTrack_btn.getHeight();
        double layoutX = tramTrack_btn.getLayoutX();
        double layoutY = tramTrack_btn.getLayoutY();

        String s;
        Button new_btn;
        Text new_text1, new_text2;
        String text_btn = "Valorar";
        for (int i = 0; i < hashMapList.size(); i++) {
            String hasMapText1 = (String) hashMapList.get(i).get("nom");
            String hasMapText2 = (String) hashMapList.get(i).get("highlight");
            String s1 = "Localitat: " +hasMapText1;
            String s2 = "Highlight: "+hasMapText2;
            Integer hasMapValue = (Integer) hashMapList.get(i).get("id");

            new_text1 = createEtapaOrTracksText(s1, width, height, layoutX_text1, layoutY_text1);
            new_text2 = createEtapaOrTracksText(s2, width, height, layoutX_text2, layoutY_text2);
            new_btn = createEtapaOrTracksButton(hasMapValue, text_btn, width, height, layoutX, layoutY);

            excursionsPaneChildren.add(new_text1);
            excursionsPaneChildren.add(new_text2);
            excursionsPaneChildren.add(new_btn);
            layoutY += ESPAI_ENTRE_BOTONS;
            layoutY_text1 += ESPAI_ENTRE_BOTONS;
            layoutY_text2 += ESPAI_ENTRE_BOTONS;
        }
        //Actualitzem la mida del pane que conté els botons perque es pugui fer scroll cap abaix si hi ha més botons dels que caben al pane
        tramTrack_pane.setPrefHeight(layoutY);
        //Esborrem excursio_btn, que l'utilitzavem únicament com a referència per la mida dels botons
        excursionsPaneChildren.remove(tramTrack_btn);
    }

    private Text createEtapaOrTracksText(String text, double width, double height, double layoutX, double layoutY){
        Text new_text = new Text();
        new_text.setLayoutX(layoutX);
        new_text.setLayoutY(layoutY);
        new_text.setText(text);
        new_text.setTextAlignment(TextAlignment.LEFT);

        return new_text;
    }

    private Button createEtapaOrTracksButton(Integer value, String text, double width, double height, double layoutX, double layoutY){
        Button new_btn = new Button();
        new_btn.setPrefWidth(width);
        new_btn.setPrefHeight(height);
        new_btn.setText(text);
        new_btn.setLayoutX(layoutX);
        new_btn.setLayoutY(layoutY);
        new_btn.setAlignment(Pos.BASELINE_CENTER);
        new_btn.setOnMouseClicked(event ->
        {
            if (event.getButton() == MouseButton.PRIMARY)
            {
                try {
                    mostrarFinestraValorarPuntDePas(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new_btn;
    }

    public void mostrarFinestraValorarPuntDePas(Integer id_PuntDePas) throws IOException {
        Escena register = EscenaFactory.INSTANCE.creaEscena("valorarPuntDePas-view", "Valorar Punt De Pas: "+String.valueOf(id_PuntDePas));
        EscenaValorarPuntDePas escenaValorarPuntDePas = ((EscenaValorarPuntDePas) register);
        register.setController(controller);
        this.controller.getSessionMemory().setIdPuntDePas(id_PuntDePas);
        escenaValorarPuntDePas.start();
    }
}



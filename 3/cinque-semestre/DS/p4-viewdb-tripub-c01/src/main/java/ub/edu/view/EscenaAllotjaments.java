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


public class EscenaAllotjaments extends Escena{
    private static final double ESPAI_ENTRE_BOTONS = 35;
    public AnchorPane allotjament_pane;
    public Text allotjament_text;
    public Button allotjament_btn;
    private String correu_persona;
    private Integer id_ruta;
    private Integer id_tram_etapa;
    private Integer id_localitzacio;

    public void start() throws Exception {
        this.correu_persona=this.controller.getSessionMemory().getCorreuPersona();
        this.id_ruta=this.controller.getSessionMemory().getIdRuta();
        this.id_tram_etapa=this.controller.getSessionMemory().getIdTram();
        this.id_localitzacio=this.controller.getSessionMemory().getIdLocalitzacio();
        popularAllotjaments();
    }

    public void popularAllotjaments() throws Exception {
        List<Node> excursionsPaneChildren = allotjament_pane.getChildren();

        double layoutX_text1 = allotjament_text.getLayoutX();
        double layoutY_text1 = allotjament_text.getLayoutY();

        double width = allotjament_btn.getWidth();
        double height = allotjament_btn.getHeight();
        double layoutX = allotjament_btn.getLayoutX();
        double layoutY = allotjament_btn.getLayoutY();


        //coger el tramtrack por ese id concreto
        //coger la  propiedad de los 2 ids que son fk que son de los 2 punt de pas
        List<HashMap<Object,Object>> listHashAllotjament = controller.getAllAllotjaments();
        String text_btn = "Reservar";
        Button new_btn;
        Text new_text1;
        for (int i = 0; i < listHashAllotjament.size(); i++) {
            Integer id = (Integer) listHashAllotjament.get(i).get("id");
            String nom = (String) listHashAllotjament.get(i).get("nom");

            new_text1 = createAllotjamentText(nom, width, height, layoutX_text1, layoutY_text1);
            new_btn = createAllotjamentButton(id, text_btn, width, height, layoutX, layoutY);

            excursionsPaneChildren.add(new_text1);
            excursionsPaneChildren.add(new_btn);
            layoutY += ESPAI_ENTRE_BOTONS;
            layoutY_text1 += ESPAI_ENTRE_BOTONS;
        }

        //Actualitzem la mida del pane que conté els botons perque es pugui fer scroll cap abaix si hi ha més botons dels que caben al pane
        allotjament_pane.setPrefHeight(layoutY);
        //Esborrem excursio_btn, que l'utilitzavem únicament com a referència per la mida dels botons
        excursionsPaneChildren.remove(allotjament_btn);
    }

    private Text createAllotjamentText(String text, double width, double height, double layoutX, double layoutY){
        Text new_text = new Text();
        new_text.setLayoutX(layoutX);
        new_text.setLayoutY(layoutY);
        new_text.setText(text);
        new_text.setTextAlignment(TextAlignment.LEFT);
        return new_text;
    }

    private Button createAllotjamentButton(Integer value, String text, double width, double height, double layoutX, double layoutY){
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
                    mostrarFinestraValorarAllotjament(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new_btn;
    }

    public void mostrarFinestraValorarAllotjament(Integer id_allotjament) throws Exception {
        Escena register = EscenaFactory.INSTANCE.creaEscena("reservarAllotjament-view", "Reservar Allotjament: "+String.valueOf(id_allotjament)+" per a la localitzacio: "+this.id_localitzacio);
        EscenaReservarAllotjament escenaReservarAllotjament = ((EscenaReservarAllotjament) register);
        register.setController(controller);
        this.controller.getSessionMemory().setIdAllotjament(id_allotjament);
        escenaReservarAllotjament.start();
    }
}



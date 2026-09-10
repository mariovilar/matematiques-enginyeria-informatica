package ub.edu.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.List;


public class EscenaTramDetalls extends Escena{
    private static final double ESPAI_ENTRE_BOTONS = 30;
    public AnchorPane tram_pane;
    public Button tram_btn;
    private String correu_persona;
    private Integer id_ruta;

    public void start() throws Exception {
        this.correu_persona=this.controller.getSessionMemory().getCorreuPersona();
        this.id_ruta=this.controller.getSessionMemory().getIdRuta();
        popularTrams(id_ruta);
    }

    public void popularTrams(Integer id) throws Exception {
        List<HashMap<Object,Object>> hashMapList = controller.getAllTramsEtapaTrackByRutaId(id);
        if(hashMapList.size()==0){
            tram_btn.setText("Empty Trams");
            tram_btn.setDisable(true);
            return;
        }
        List<Node> excursionsPaneChildren = tram_pane.getChildren();

        double width = tram_btn.getWidth();
        double height = tram_btn.getHeight();
        double layoutX = tram_btn.getLayoutX();
        double layoutY = tram_btn.getLayoutY();

        String s;
        Button new_btn;
        for (int i = 0; i < hashMapList.size(); i++) {
            Integer hasMapId = (Integer) hashMapList.get(i).get("id");
            Integer hasMapValue = (Integer) hashMapList.get(i).get("value");
            String hasMapText = (String) hashMapList.get(i).get("text");
            new_btn = createEtapaOrTracksButton(hasMapId, hasMapValue, hasMapText, width, height, layoutX, layoutY);
            excursionsPaneChildren.add(new_btn);
            layoutY += ESPAI_ENTRE_BOTONS;
        }
        //Actualitzem la mida del pane que conté els botons perque es pugui fer scroll cap abaix si hi ha més botons dels que caben al pane
        tram_pane.setPrefHeight(layoutY);
        //Esborrem excursio_btn, que l'utilitzavem únicament com a referència per la mida dels botons
        excursionsPaneChildren.remove(tram_btn);
    }

    private Button createEtapaOrTracksButton(Integer id, Integer flag_tipus, String text, double width, double height, double layoutX, double layoutY){
        Button new_btn = new Button();
        new_btn.setPrefWidth(width);
        new_btn.setPrefHeight(height);
        new_btn.setText(text);
        new_btn.setLayoutX(layoutX);
        new_btn.setLayoutY(layoutY);
        new_btn.setAlignment(Pos.BASELINE_LEFT);
        new_btn.setOnMouseClicked(event ->
        {
            if (event.getButton() == MouseButton.PRIMARY)
            {
                try {
                    if(flag_tipus==1){ //caso de tramEtapa
                        mostrarFinestraTramEtapaDetalls(id);
                    }else if(flag_tipus==2){ //caso de tramTrack
                        mostrarFinestraTramTrackPdPDetalls(id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new_btn;
    }

    private void mostrarFinestraTramEtapaDetalls(Integer id_tram) throws Exception {
        Escena register = EscenaFactory.INSTANCE.creaEscena("tramEtapaDetalls-view", "Etapes asociades al TramEtapa: "+String.valueOf(id_tram));
        EscenaTramEtapaDetalls escenaTramEtapaDetalls = ((EscenaTramEtapaDetalls) register);
        register.setController(controller);
        //en este caso, el Tram corresponde al TramEtapa
        this.controller.getSessionMemory().setIdTram(id_tram);
        escenaTramEtapaDetalls.start();
    }

    private void mostrarFinestraTramTrackPdPDetalls(Integer id_tram) throws Exception {
        Escena register = EscenaFactory.INSTANCE.creaEscena("tramTrackPdPDetalls-view", "Valorar Punt De Pas Del TramTrack: "+String.valueOf(id_tram));
        EscenaTramTrackPdPDetalls escenaTramTrackDetalls = ((EscenaTramTrackPdPDetalls) register);
        register.setController(controller);
        //en este caso, el Tram corresponde al Tramtrack
        this.controller.getSessionMemory().setIdTram(id_tram);
        escenaTramTrackDetalls.start();
    }
}



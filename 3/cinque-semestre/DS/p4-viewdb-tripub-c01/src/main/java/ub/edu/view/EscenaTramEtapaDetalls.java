package ub.edu.view;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

import java.util.HashMap;


public class EscenaTramEtapaDetalls extends Escena{
    private Integer id_ruta;
    private Integer id_tramEtapa;
    public Text data_text_principal;
    public Text data_text_origen;
    public Text data_text_desti;
    public Button origen_btn;
    public Button desti_btn;

    private String correu_persona;


    public void start() throws Exception {
        this.correu_persona=this.controller.getSessionMemory().getCorreuPersona();
        this.id_ruta=this.controller.getSessionMemory().getIdRuta();
        this.id_tramEtapa=this.controller.getSessionMemory().getIdTram();
        popularEtapes();
    }

    private void popularEtapes() throws Exception {
        //coger el tramtrack por ese id concreto
        //coger la  propiedad de los 2 ids que son fk que son de los 2 punt de pas
        HashMap<Object,Object> hashMaptramTrack = controller.getTramEtapaById(this.id_tramEtapa);

        //coger los 2 punt de pas y los 2 localitats asociadas
        Integer id_etapa_origen = (Integer) hashMaptramTrack.get("id_etapa_origen");
        Integer id_etapa_desti = (Integer) hashMaptramTrack.get("id_etapa_desti");
        HashMap<Object,Object> hashMap_TramEtapa_Origen = controller.getLocalitat_y_EtapaByID(id_etapa_origen);
        HashMap<Object,Object> hashMap_TramEtapa_Desti = controller.getLocalitat_y_EtapaByID(id_etapa_desti);
        //los hashmaps contienen: id,nom
        //asginar a los botones el text correspodniente
        String sOrigen = hashMap_TramEtapa_Origen.get("id").toString() + " | " + hashMap_TramEtapa_Origen.get("nom");
        String sDesti = hashMap_TramEtapa_Desti.get("id").toString() + " | " + hashMap_TramEtapa_Desti.get("nom");
        origen_btn.setText(sOrigen);
        desti_btn.setText(sDesti);
        data_text_principal.setText("Etapa: "+this.id_tramEtapa);

        origen_btn.setOnMouseClicked(event ->
        {
            if (event.getButton() == MouseButton.PRIMARY) {
                System.out.println("Enter Origen");
                try {
                    mostrarFinestraTramTrackPdPDetalls(id_etapa_origen);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        desti_btn.setOnMouseClicked(event ->
        {
            if (event.getButton() == MouseButton.PRIMARY) {
                System.out.println("Enter Destino");
                try {
                    mostrarFinestraTramTrackPdPDetalls(id_etapa_desti);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void mostrarFinestraTramTrackPdPDetalls(Integer id_localitzacio) throws Exception {
        Escena register = EscenaFactory.INSTANCE.creaEscena("tramAllotjaments-view", "Allotjaemnts de l'Etapa: "+this.id_tramEtapa);
        EscenaAllotjaments escenaAllotjaments = ((EscenaAllotjaments) register);
        register.setController(controller);
        this.controller.getSessionMemory().setIdLocalitzacio(id_localitzacio);
        escenaAllotjaments.start();
    }

}



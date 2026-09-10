package ub.edu.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;


public class EscenaRutaDetalls extends Escena{
    public Text nomRuta_text;
    public Text data_text_caracteristiques;
    public Text data_text_id;
    public Text data_text_nom;
    public Text data_text_dataCreacio;
    public Text data_text_durada;
    public Text data_text_descripcio;
    public Text data_text_cost;
    public Text data_text_distancia;
    public Text data_text_dificultat;
    public Text data_text_tipusRuta;


    public Text data_text_origen;
    public Text data_text_desti;

    public Button origen_btn;
    public Button desti_btn;
    public Button valorar_btn;
    public Button trams_btn;

    private String correu_persona;
    private Integer id_ruta;

    public void start() throws Exception {
        this.correu_persona=this.controller.getSessionMemory().getCorreuPersona();
        this.id_ruta=this.controller.getSessionMemory().getIdRuta();
        initData();
    }

    public void initData() throws Exception {
        HashMap<Object,Object> rutaHashMap = controller.getRutaById(this.id_ruta);
        String nom, dataCreacio="", descripcio, dificultat, tipusRuta;
        Integer id, durada, id_lloc_origen, id_lloc_desti;
        Double cost, distancia;
        if(rutaHashMap.get("id")!=null){
            id = (Integer) rutaHashMap.get("id");
            //this.id_ruta=id; //ya me lo dan por parametro en el constructor de la escena
            data_text_id.setText("Id: " + id.toString());
        }
        if(rutaHashMap.get("nom")!=null){
            nom = (String) rutaHashMap.get("nom");
            nomRuta_text.setText(nom);
            data_text_nom.setText("Nom: " + nom);
        }
        if(rutaHashMap.get("dataCreacio")!=null){
            Object OBJ_dataCreacio = (Object) rutaHashMap.get("dataCreacio");
            if(OBJ_dataCreacio!=null){
                dataCreacio=OBJ_dataCreacio.toString();
            }
            data_text_dataCreacio.setText("Data de Creacio: " + dataCreacio);
        }
        if(rutaHashMap.get("durada")!=null){
            durada = (Integer)  rutaHashMap.get("durada");
            data_text_durada.setText("Durada: " + durada);
        }
        if(rutaHashMap.get("descripcio")!=null){
            descripcio = (String) rutaHashMap.get("descripcio");
            data_text_descripcio.setText("Descripcio: " + descripcio);
        }
        if(rutaHashMap.get("cost")!=null){
            cost = (Double) rutaHashMap.get("cost");
            data_text_cost.setText("Cost: " + cost);
        }
        if(rutaHashMap.get("distancia")!=null){
            distancia = (Double) rutaHashMap.get("distancia");
            data_text_cost.setText("Distancia: " + distancia);
        }
        if(rutaHashMap.get("dificultat")!=null){
            dificultat = (String) rutaHashMap.get("Dificultat");
            data_text_cost.setText("Dificultat: " + dificultat);
        }
        if(rutaHashMap.get("tipusRuta")!=null){
            tipusRuta = (String) rutaHashMap.get("tipusRuta");
            data_text_cost.setText("Tipus de la Ruta: " + tipusRuta);
        }
        if(rutaHashMap.get("id_lloc_origen")!=null){
            id_lloc_origen = (Integer) rutaHashMap.get("id_lloc_origen");
            HashMap<Object,Object> hasmap_localitat_origen = controller.getLocalitatByID(id_lloc_origen);
            origen_btn.setText(""+hasmap_localitat_origen.get("id")+" | "+hasmap_localitat_origen.get("nom"));
            initObserverOrigen(id_lloc_origen);
        }
        if(rutaHashMap.get("id_lloc_desti")!=null){
            id_lloc_desti = (Integer) rutaHashMap.get("id_lloc_desti");
            HashMap<Object,Object> hasmap_localitat_desti = controller.getLocalitatByID(id_lloc_desti);
            desti_btn.setText(""+hasmap_localitat_desti.get("id")+" | "+hasmap_localitat_desti.get("nom"));
            initObserverDesti(id_lloc_desti);
        }

        //id_tripUB //no es necesario
    }

    public void initObserverOrigen(Integer id_lloc_origen) {
        origen_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    mostrarFinestraTramTrackPdPDetalls(id_lloc_origen);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void initObserverDesti(Integer id_lloc_desti) {
        desti_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    mostrarFinestraTramTrackPdPDetalls(id_lloc_desti);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onBtnValorarClick() throws IOException {
        //Nova finestra
        Escena escena = EscenaFactory.INSTANCE.creaEscena("valorarRuta-view", "Valorar Ruta: "+String.valueOf(this.id_ruta));
        EscenaValorarRuta escenaValorarRuta = ((EscenaValorarRuta)escena);
        escenaValorarRuta.setController(controller);
        escenaValorarRuta.start();
    }

    public void onBtntramsDetallsClick() throws Exception {
        //Nova finestra
        Escena escena = EscenaFactory.INSTANCE.creaEscena("tramDetalls-view", "Trams de la Ruta: "+String.valueOf(this.id_ruta));
        EscenaTramDetalls escenaTramDetalls = ((EscenaTramDetalls)escena);
        escenaTramDetalls.setController(controller);
        escenaTramDetalls.start();
    }

    private void mostrarFinestraTramTrackPdPDetalls(Integer id_localitzacio) throws Exception {
        //Nova finestra
        Escena register = EscenaFactory.INSTANCE.creaEscena("tramAllotjaments-view", "Allotjaemnts de l'Etapa: "+id_localitzacio);
        EscenaAllotjaments escenaAllotjaments = ((EscenaAllotjaments) register);
        register.setController(controller);
        this.controller.getSessionMemory().setIdLocalitzacio(id_localitzacio);
        escenaAllotjaments.start();
    }
}

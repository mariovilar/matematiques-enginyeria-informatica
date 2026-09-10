package ub.edu.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class EscenaValorarPuntDePas extends Escena {

    public RadioButton radioButton_Group1_Like;
    public RadioButton radioButton_Group1_Dislike;
    public RadioButton radioButton_Group2_Text1;
    public RadioButton radioButton_Group2_Text2;
    public RadioButton radioButton_Group2_Text3;
    public RadioButton radioButton_Group2_Text4;
    public RadioButton radioButton_Group2_Text5;
    public RadioButton radioButton_Group3_Text1;
    public RadioButton radioButton_Group3_Text2;
    public Button button_valorar;
    public Button button_cancel;

    private String correu_persona;
    private Integer id_ruta;
    private Integer id_tram_track;
    private Integer id_PuntDePas;



    public void start(){
        System.out.println("Entro a Valorar Punt de Pas");
        this.correu_persona=this.controller.getSessionMemory().getCorreuPersona();
        this.id_ruta=this.controller.getSessionMemory().getIdRuta();
        this.id_tram_track=this.controller.getSessionMemory().getIdTram();
        this.id_PuntDePas=this.controller.getSessionMemory().getIdPuntDePas();
        initialize_RB();
    }

    @FXML
    private void initialize_RB() {
        ToggleGroup group = new ToggleGroup();
        radioButton_Group1_Like.setToggleGroup(group);
        radioButton_Group1_Dislike.setToggleGroup(group);

        ToggleGroup group2= new ToggleGroup();
        radioButton_Group2_Text1.setToggleGroup(group2);
        radioButton_Group2_Text2.setToggleGroup(group2);
        radioButton_Group2_Text3.setToggleGroup(group2);
        radioButton_Group2_Text4.setToggleGroup(group2);
        radioButton_Group2_Text5.setToggleGroup(group2);

        ToggleGroup group3= new ToggleGroup();
        radioButton_Group3_Text1.setToggleGroup(group3);
        radioButton_Group3_Text2.setToggleGroup(group3);

        initDisabled_RB();
        initObservers_RB();

    }

    public void initDisabled_RB(){
        radioButton_Group1_Like.setDisable(true);
        radioButton_Group1_Dislike.setDisable(true);
        radioButton_Group2_Text1.setDisable(true);
        radioButton_Group2_Text2.setDisable(true);
        radioButton_Group2_Text3.setDisable(true);
        radioButton_Group2_Text4.setDisable(true);
        radioButton_Group2_Text5.setDisable(true);
        radioButton_Group3_Text1.setDisable(false);
        radioButton_Group3_Text2.setDisable(false);
    }

    public void initObservers_RB(){
        radioButton_Group3_Text1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                radioButton_Group1_Like.setDisable(false);
                radioButton_Group1_Dislike.setDisable(false);

                radioButton_Group2_Text1.setDisable(true);
                radioButton_Group2_Text2.setDisable(true);
                radioButton_Group2_Text3.setDisable(true);
                radioButton_Group2_Text4.setDisable(true);
                radioButton_Group2_Text5.setDisable(true);
            }
        });

        radioButton_Group3_Text2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                radioButton_Group1_Like.setDisable(true);
                radioButton_Group1_Dislike.setDisable(true);

                radioButton_Group2_Text1.setDisable(false);
                radioButton_Group2_Text2.setDisable(false);
                radioButton_Group2_Text3.setDisable(false);
                radioButton_Group2_Text4.setDisable(false);
                radioButton_Group2_Text5.setDisable(false);
            }
        });
    }

    public void onButtonValorarClick(){
        //enviar la valoracion punto de paso
        System.out.println("TODO: Entro a enviar una valoració de PdP");
        //TODO:
        // La idea es: guardar la valoracion en el modelo y actualizar la vista en caso necesario
        // Para ello:
        // 1-Recoger los datos seleccionados de la vista (cómo se recogen? ver código ejemplo de más abajo)
        // 2-Conectar con el controller pasandole los parametros necesarios para que
        // el controller el modelo (y opcionalmente se ejecute el metodo add de los respectivos DAO_X_DB)
        // 3- Refrescar la vista si es necesario

        // Aqui tienes un código de ejemplo para ver cómo recoger el valor de un radio button
        String typeValorar="";
        String valor="";
        if (radioButton_Group3_Text1.isSelected()) {
            typeValorar = "Like";
            if (radioButton_Group1_Like.isSelected()) valor = radioButton_Group1_Like.getText();
            else valor = radioButton_Group1_Dislike.getText();
        }
        System.out.println("Valoració de tipus: "+ typeValorar+ " és: "+ valor);
        /** Your Code Here **/


        stage.close();
    }

    public void onButtonCancelarClick(){
        //enviar la valoracion
        System.out.println("Entro en cancelar una valoracion PdP");
        stage.close();
    }


}
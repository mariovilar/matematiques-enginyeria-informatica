package ub.edu.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ub.edu.model.StatusType;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class EscenaRegister extends Escena {


    public Button registre_btn;
    public Button tornarLogin_reg_btn;
    public TextField registre_correu;
    public PasswordField registre_pwd;
    public PasswordField registre_pwd_repeat;
    public TextField registre_nom;
    public TextField registre_cognoms;
    public TextField registre_dni;
    public TextField registre_grup;
    public ComboBox comboBox_register_grup;
    public RadioButton radioButton_Group1_Text1;
    public RadioButton radioButton_Group1_Text2;

    public void start() {
        initialize_RB();
        popularGrups();
    }

    public void initialize_RB(){
        ToggleGroup group = new ToggleGroup();
        radioButton_Group1_Text1.setToggleGroup(group);
        radioButton_Group1_Text2.setToggleGroup(group);

        initDisabled();
        initObservers();
    }

    public void initDisabled(){
        radioButton_Group1_Text1.setDisable(false);
        radioButton_Group1_Text2.setDisable(false);

        comboBox_register_grup.setDisable(true);
        registre_grup.setDisable(true);
    }

    public void initObservers(){
        radioButton_Group1_Text1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                comboBox_register_grup.setDisable(false);
                registre_grup.setDisable(true);
            }
        });

        radioButton_Group1_Text2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                comboBox_register_grup.setDisable(true);
                registre_grup.setDisable(false);
            }
        });
    }


    public void popularGrups(){
        List<HashMap<Object,Object>> grups = controller.getAllGrupsPerNom();
        if(grups == null){
            return;
        }

        //añadir con los nombres de los grupos existentes recogido de la DB
        for (int i = 0; i < grups.size(); i++) {
            comboBox_register_grup.getItems().add(grups.get(i).get("nom"));
        }

    }

    @FXML
    public void onRegistreButtonClick() throws Exception {
        String correu = registre_correu.getText();
        String nom = registre_nom.getText();
        String cognoms = registre_cognoms.getText();
        String dni = registre_dni.getText();
        String pwd = registre_pwd.getText();
        String pwd_repeat = registre_pwd_repeat.getText();

        Integer finalgrup=null;
        boolean activeValidComboBox=false;


        //System.out.println(grupCB);

        if (correu==null || correu.equals("") || pwd==null || pwd.equals("") || !pwd.equals(pwd_repeat)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el registre");
            alert.setContentText("El correu y la contraseña son obligatories. Les contrasenyes deben coincidir");
            alert.showAndWait();
            return;
        }

        if(radioButton_Group1_Text1.isSelected()){
            Object grupCB;
            for (int i = 0; i < comboBox_register_grup.getItems().size(); i++) {
                grupCB = comboBox_register_grup.getValue();
                if(grupCB==null){
                    finalgrup=null;
                    activeValidComboBox=false;
                }else{
                    grupCB=grupCB.toString();
                    if(grupCB.equals(comboBox_register_grup.getItems().get(i))){
                        finalgrup=i;
                    }
                    activeValidComboBox=true;
                }
            }
        }else if (radioButton_Group1_Text2.isSelected()){
            // 3 pasos: PAS1- Crear un nuevo Grupo. PAS2- Crear una nueva Persona. PAS3- Crear Relacion entre Grup-Persona
            // PAS1- crear un nuevo grupo
            String grupNom = registre_grup.getText();

            //verificar el nombre no esté repetido
            List<HashMap<Object,Object>> grups = controller.getAllGrupsPerNom();
            if(grups == null){
                //pass
            }else{
                for (HashMap<Object,Object> g:grups) {
                    if(g.get("nom").equals(grupNom)){
                        //significa que está repetido el nombre -> lanzar mensage de error
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error en el registre");
                        alert.setContentText("El Grup introduit ya Existeix. Selecciona'l de la llista de grups disponibles o canvieu-li  el nom");
                        alert.showAndWait();
                        return;
                    }
                }
            }

            if(grupNom.equals("")){
                grupNom=null;
                activeValidComboBox=false;
            }
            if(grupNom==null || activeValidComboBox){
                System.out.println("Error al crear un nuevo grupo");
                activeValidComboBox=false;
            }else{
                finalgrup = controller.addNewGrup(grupNom);
                activeValidComboBox=true;
            }
        }else{
            finalgrup=null;
            activeValidComboBox=false;
        }

        if(!activeValidComboBox || finalgrup == null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el registre");
            alert.setContentText("El Grup Introduit es erroni. Está buit i es null o no s'ha especificat el nom");
            alert.showAndWait();
            return;
        }

        System.out.println("finalgrup:"+finalgrup);
        //PAS2- Crear una nueva Persona
        StatusType resultat = controller.addNewPersona(correu, nom, cognoms, dni, pwd, finalgrup);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (resultat == StatusType.PERSONA_DUPLICADA || resultat == StatusType.FORMAT_INCORRECTE || resultat == StatusType.FORMAT_INCORRECTE_CORREU_PWD) {
            //Problema en el registre:
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el registre");
            alert.setContentText(resultat.toString());
        } else {
            //Registre amb èxit:

            //PAS3- crear relacion grupoPersona
            controller.addRelacionGrupPersona(correu,finalgrup);

            alert.setTitle("Registre exitòs");
            alert.setHeaderText("Registre exitòs");
            alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                @Override
                public void handle(DialogEvent dialogEvent) {
                    //System.out.println(alert.getResult());
                    String resu_ButtonData = String.valueOf(alert.getResult().getButtonData());
                    if(resu_ButtonData.equals("OK_DONE")){
                        //boton aceptar
                        event_goLogin(correu);

                    }else{
                        //boton cancelar
                        //no hacer nada, nos quedamos donde ya estamos
                    }
                }
            });
        }
        alert.showAndWait();
    }

    private void event_goLogin (String correu){
        try {
            Escena login = EscenaFactory.INSTANCE.creaEscena("login-view", "TripUB Login View");
            EscenaLogin escenaLogin = ((EscenaLogin) login);
            login.setController(controller);
            this.controller.getSessionMemory().setCorreuPersona(correu);
            escenaLogin.start();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onTornarLoginButtonClick(){
        event_goLogin(null);
    }

}
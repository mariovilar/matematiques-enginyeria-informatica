package ub.edu.controller;

import ub.edu.model.*;
import ub.edu.resources.ResourcesFacade;

import java.util.*;


public class Controller {

    private ResourcesFacade inicialitzador;

    private ModelFacade modelFacade;

    private TripUB tripUB;

    private SessionMemory sessionMemory;

    public Controller() {
        tripUB = new TripUB();
        sessionMemory = new SessionMemory();
        modelFacade = new ModelFacade(tripUB);
        inicialitzador = new ResourcesFacade(tripUB, modelFacade);
        inicialitzador.populateTripUB();

    }

    public SessionMemory getSessionMemory() {
        return sessionMemory;
    }

    public List<HashMap<Object,Object>> getAllRutesPerNom() {
        return modelFacade.getAllRutesPerNom();
    }

    public List<HashMap<Object,Object>> getAllGrupsPerNom() {
        return modelFacade.getAllGrupsPerNom();
    }

    public String validateRegistrePersona(String username, String password) {
        switch (modelFacade.validateRegistrePersona(username, password)) {
            case REGISTRE_VALID:
                return "Registre vàlid";
            case PERSONA_DUPLICADA:
                return "Persona duplicada";
            case FORMAT_INCORRECTE_CORREU_PWD:
                return "Format incorrecte";
            default:
        }
        return"Cas no contemplat";
    }

    public String loguejarPersona(String username, String password){

        switch (modelFacade.loguejarPersona(username, password)) {
            case 0:
                return StatusType.REGISTRE_VALID.toString();
            case 1:
                return StatusType.CORREU_INEXISTENT.toString();
            case 2:
                 return StatusType.CONTRASENYA_INCORRECTA.toString();
             default:
        }
        return"Cas no contemplat";
    }

    public String recuperarContrasenya(String username){
        return modelFacade.recuperarContrasenya(username);
    }

    public StatusType addNewPersona(String correu, String nom, String cognoms, String dni, String password, Integer id_tripUB) throws Exception {
        return inicialitzador.addNewPersona(correu, nom, cognoms, dni, password, id_tripUB);
    }

    public StatusType loguejarSociStatus(String correu, String password){
        return (modelFacade.loguejarSociStatus(correu, password));
    }

    public Integer addNewGrup(String grupNom) throws Exception {
        return inicialitzador.addNewGrup(grupNom);
    }

    public boolean addRelacionGrupPersona(String correuPersona, Integer idGrup) throws Exception {
       return  inicialitzador.addRelacionGrupPersona(correuPersona, idGrup);

    }

    public ArrayList<HashMap<Object,Object>> getAllGrupsPerPersona(String correu) throws Exception {
        return modelFacade.getAllGrupsPerPersona(correu);
    }

    public List<HashMap<Object, Object>> getAllComarques() throws Exception {
        return modelFacade.getAllComarques();
    }

    public List<HashMap<Object,Object>> getAllLocalitats() throws Exception {
        return modelFacade.getAllLocalitats();
    }

    public List<HashMap<Object,Object>> getAllTramsEtapaTrackByRutaId(Integer id_ruta) throws Exception {
        return modelFacade.getAllTramsEtapaTrackByRutaId(id_ruta);

    }

    public HashMap<Object,Object> getTramTrackById(Integer id) throws Exception {
        return modelFacade.getTramTrackById(id);
    }

    public HashMap<Object,Object> getLocalitat_y_PuntDePasById(Integer id) throws Exception {
        return modelFacade.getLocalitat_y_PuntDePasById(id);
    }


    public HashMap<Object, Object> getTramEtapaById(Integer id) throws Exception {
        return modelFacade.getTramEtapaById(id);
    }

    public HashMap<Object, Object> getLocalitat_y_EtapaByID(Integer id) throws Exception {
        return modelFacade.getLocalitat_y_EtapaByID(id);
    }

    public List<HashMap<Object,Object>> getAllAllotjaments() throws Exception {
        return modelFacade.getAllAllotjaments();
    }


    public HashMap<Object, Object> getRutaById(Integer id_ruta) throws Exception {
        return modelFacade.getRutaById(id_ruta);
    }

    public HashMap<Object,Object> getLocalitatByID(Integer id) throws Exception {
        return modelFacade.getLocalitatByID(id);
    }

    public Integer getIdGrupByName(String newValue) {
        return tripUB.getIdGrupByName(newValue);
    }


    public String validatePassword(String b) {
        switch (Seguretat.validatePassword(b)) {
            case CONTRASENYA_NO_SEGURA:
                return "Contrasenya no prou segura";
            case CONTRASENYA_SEGURA:
                return "Contrasenya segura";
        }
        return "Cas no contemplat";
    }

    public String validateUsername(String a) {
        switch (Seguretat.validateUsername(a)) {
            case CORREU_INCORRECTE:
                return "Correu en format incorrecte";
            case CORREU_CORRECTE:
                return "Correu en format correcte";
        }
        return "Cas no contemplat";

    }
}
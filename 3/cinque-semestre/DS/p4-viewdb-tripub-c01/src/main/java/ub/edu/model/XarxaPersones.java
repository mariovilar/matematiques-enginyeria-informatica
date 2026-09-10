package ub.edu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XarxaPersones {
    // Esta clase puede ser la clase Grup?
    private HashMap<String, Persona> llista;

    public XarxaPersones(){
        llista = new HashMap<>();
    }

    public XarxaPersones(List<Persona> allSocis) {
        this();
        for (Persona p: allSocis) {
            llista.put(p.getCorreu(), p);
            p.setPerfil(new PerfilPersona());
        }
    }
    public Persona find(String correu) {
        return llista.get(correu);
    }

    public ArrayList<String> getAllGrupsPerPersona(String correu) {
        ArrayList<String> llista = new ArrayList<>();
        Persona p;
        p = find(correu);
        if (p!=null) {
            llista = p.getNomGrups();
        }
        return llista;
    }

}

package ub.edu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PerfilPersona {
    HashMap<Integer, Opinio> opinionsPuntPas;
    HashMap<Integer,Reserva> reserves;
    HashMap<Integer,Grup> grups;
    // El Vot conté el grup, la ruta i el valor del vot
    HashMap<Integer,Vot>  votsRutes;

    public PerfilPersona() {
        opinionsPuntPas = new HashMap<Integer, Opinio>();
        reserves = new HashMap<Integer, Reserva>();
        grups = new HashMap<Integer, Grup>();
        votsRutes = new HashMap<Integer, Vot>();
    }

    public void addOpinio(Opinio opinio) {
        opinionsPuntPas.put(opinio.getId(), opinio);
    }
    public void addReserva(Reserva reserva) {
        reserves.put(reserva.getId(), reserva);
    }
    public void addVot(Vot vot) {
        votsRutes.put(vot.getId(), vot);
    }
    public void addGrup(Grup grup) {
        grups.put(grup.getId(), grup);
    }

    public Opinio getOpinio(Integer id_Opinio) {
        return opinionsPuntPas.get(id_Opinio);
    }
    public Reserva getReserva(Integer id_Reserva) {
        return reserves.get(id_Reserva);
    }

    public HashMap<Integer, Grup> getAllGrups(){
        return this.grups;
    }
    public Grup getGrup(Integer id_grup) {
        return grups.get(id_grup);
    }
    public Vot getVot(Integer id_Vot) {
        return votsRutes.get(id_Vot);
    }

    public ArrayList<String> getNomGrups() {
        ArrayList<String> l = new ArrayList<>();
        for (Grup g: grups.values()) {
            l.add(g.getNom());
        }
        return l;
    }
}

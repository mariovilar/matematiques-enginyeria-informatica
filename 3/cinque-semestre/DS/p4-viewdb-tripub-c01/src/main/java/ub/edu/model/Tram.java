package ub.edu.model;

import java.util.HashMap;
import java.util.List;

public class Tram {
    private Integer id;
    private Integer id_ruta;
    private Integer id_transport_recomenat;

    private HashMap<Integer, Transport> transportsPossibles;
    public Tram() {
        this.transportsPossibles = new HashMap<Integer, Transport>();
    }

    public Tram(Integer id_ruta, Integer id_transport_recomenat) {
        this();
        this.id_ruta = id_ruta;
        this.id_transport_recomenat = id_transport_recomenat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(Integer id_ruta) {
        this.id_ruta = id_ruta;
    }

    public Integer getId_transport_recomenat() {
        return id_transport_recomenat;
    }

    public void setId_transport_recomenat(Integer id_transport_recomenat) {
        this.id_transport_recomenat = id_transport_recomenat;
    }

    @Override
    public String toString() {
        return "Tram{" +
                "id=" + id +
                ", id_ruta=" + id_ruta +
                ", id_transport_recomenat=" + id_transport_recomenat +
                '}';
    }

   public void addTransportPossible(Transport t) {
       transportsPossibles.put(t.getId(),t);
   }



}

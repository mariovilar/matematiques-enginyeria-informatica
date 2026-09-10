package ub.edu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Etapa extends Localitat{
    private Integer id;

    private HashMap<Integer,Allotjament> lAllotjaments;
    public Etapa() {
        super();
        lAllotjaments = new HashMap<Integer, Allotjament>();
    }

    public Etapa(String nom, Double altitud, Double latitud, Double longitud, Integer id_comarca) {
        super(nom, altitud, latitud, longitud, id_comarca);
        lAllotjaments = new HashMap<Integer, Allotjament>();
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Etapa{" +
                "id=" + id +
                '}';
    }


    public void addAllotjament(Allotjament a) {
        this.lAllotjaments.put(a.getId(), a);
    }

    public Allotjament getAllotjament(Integer id_allotjament) {
        return (this.lAllotjaments.get(id_allotjament));
    }

    public List<Allotjament> getAllAllotjaments() {
        //no se puede hacer cast directo entre map->hasmap a una collection->list. Se debe instanciar una nueva arraylist
        return new ArrayList<Allotjament>(this.lAllotjaments.values());
    }
}

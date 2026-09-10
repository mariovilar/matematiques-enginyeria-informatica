package ub.edu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Comarca {
    private Integer id;
    private String nom;

    private HashMap<Integer, Localitat> localitats;

    public Comarca(){
        localitats = new HashMap<>();
    }

    public Comarca(String nom){
        this();
        this.nom = nom;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom(){
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Comarca{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }

    public void addLocalitat(Localitat l) {
        localitats.put(l.getId(), l);
    }

    public Localitat getLocalitat(Integer id) {
        return localitats.get(id);
    }

    public List<Localitat> getAllLocalitats() {
        //no se puede hacer cast directo entre map->hasmap a una collection->list. Se debe instanciar una nueva arraylist
        return new ArrayList<Localitat>(this.localitats.values());
    }
}

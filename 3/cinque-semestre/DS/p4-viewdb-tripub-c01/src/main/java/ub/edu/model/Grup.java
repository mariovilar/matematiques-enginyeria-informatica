package ub.edu.model;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Grup {
    private Integer id;
    private String nom;

    private HashMap<String, Persona> personesGrup;

    public Grup() {
        personesGrup = new HashMap<String, Persona>();
    }

    public Grup(String nom) {
        this();
        this.nom = nom;
        personesGrup = new HashMap<String, Persona>();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Grup{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }


    public void addPersona(Persona persona) {
        personesGrup.put(persona.getCorreu(), persona);
    }

    public Persona findPersona(String correu) {
        return personesGrup.get(correu);
    }
}

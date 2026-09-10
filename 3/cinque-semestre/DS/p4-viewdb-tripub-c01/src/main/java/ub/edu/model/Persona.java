package ub.edu.model;

import java.util.ArrayList;
import java.util.List;

public class Persona {
    private String correu;
    private String nom;
    private String cognom;
    private String dni;
    private String pwd;
    private Integer id_tripUB;

    private PerfilPersona perfil;

    public Persona() {
    }

    public Persona(String correu) {
        this.correu = correu;
    }

    public Persona(String nom, String pwd) {
        this.pwd = pwd;
        this.nom = nom;
    }

    public Persona(String correu, String nom, String cognom, String dni, String pwd, Integer id_tripUB) {
        this.correu = correu;
        this.nom = nom;
        this.cognom = cognom;
        this.dni = dni;
        this.pwd = pwd;
        this.id_tripUB = id_tripUB;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return nom;
    }

    public void setName(String nom) {
        this.nom = nom;
    }

    public String getCorreu() {
        return correu;
    }

    public void setCorreu(String correu) {
        this.correu = correu;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognom() {
        return cognom;
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }


    public Integer getIdTripUB() {
        return id_tripUB;
    }

    public void setIdTripUB(Integer id_tripUB) {
        this.id_tripUB = id_tripUB;
    }
    @Override
    public String toString() {
        return "Persona{" +
                "correu='" + correu + '\'' +
                ", nom='" + nom + '\'' +
                ", cognom='" + cognom + '\'' +
                ", dni='" + dni + '\'' +
                ", pwd='" + pwd + '\'' +
                ", id_tripUB=" + id_tripUB +
                '}';
    }

    public PerfilPersona getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilPersona perfil) {
        this.perfil = perfil;
    }

    public ArrayList<String> getNomGrups() {
        return perfil.getNomGrups();
    }
}

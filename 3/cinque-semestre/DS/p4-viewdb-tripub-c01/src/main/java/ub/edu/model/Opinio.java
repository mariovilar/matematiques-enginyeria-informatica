package ub.edu.model;

import java.time.LocalDate;

// Opinio d'un Punt de Pas
public class Opinio {
    private Integer id;
    private Integer id_puntDePas;
    private PuntDePas puntDePas;
    private String correu_persona;
    private Persona persona;

    //TODO: Cal que canvieu les opinions i els seus tipus segons el que teniu de la practica 3

    private Integer idTipusOpinio; //NO SE USA, pero es necesario para marcar la relacion n-1 pero los datos se almacenan en tipusOpinio y valorOpinio
    private String tipusOpinio; // "Opinio" "Like"
    private String valorOpinio; //  "No ho faria".... "Like" "Unlike"

    public Opinio() {
    }

    public Opinio(Integer id_puntDePas, String tipusOpinio, String valorOpinio, String correu_persona) {
        this();
        this.id_puntDePas = id_puntDePas;
        this.tipusOpinio = tipusOpinio;
        this.valorOpinio = valorOpinio;
        this.correu_persona = correu_persona;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdPuntDePas() {
        return id_puntDePas;
    }

    public void setIdPuntDePas(Integer id_puntDePas) {
        this.id_puntDePas = id_puntDePas;
    }

    public Integer getIdTipusOpinio() {
        return idTipusOpinio;
    }

    public void setIdTipusOpinio(Integer idTipusOpinio) {
        this.idTipusOpinio = idTipusOpinio;
    }

    public String getTipusOpinio() {
        return tipusOpinio;
    }

    public void setTipusOpinio(String id_tipusOpinio) {
        this.tipusOpinio = id_tipusOpinio;
    }

    public String getValorOpinio() {
        return valorOpinio;
    }
    public void setValorOpinio(String valorOpinio) {
        this.valorOpinio = valorOpinio;
    }


    public String getCorreuPersona() {
        return correu_persona;
    }

    public void setCorreuPersona(String correu_persona) {
        this.correu_persona = correu_persona;
    }

    @Override
    public String toString() {
        return "Opinio{" +
                "id=" + id +
                ", id_puntDePas=" + id_puntDePas +
                ", tipusOpinio=" + tipusOpinio +
                ", valorOpinio=" + valorOpinio +
                ", correu_persona='" + correu_persona + '\'' +
                '}';
    }

    public PuntDePas getPuntDePas() {
        return puntDePas;
    }

    public void setPuntDePas(PuntDePas puntDePas) {
        this.puntDePas = puntDePas;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
}

package ub.edu.model;

import java.time.LocalDate;

// Vot d'una Ruta (valoracio d'una Ruta)
public class Vot {
    private Integer id;
    private LocalDate data;
    private Integer idRuta;

    private Ruta ruta;

    private String correuPersona;
    private Persona persona;

    private Integer idGrup;
    private Grup grup;

    //TODO: Cal que canvieu els vots segons el que teniu de la practica 3

    private Integer idTipusVot; //NO SE USA, pero es necesario para marcar la relacion n-1 pero los datos se almacenan en tipusVot y valorVot
    private String tipusVot; // "Estrelles", "Like"
    private String valorVot; //"1", "2", ..."Like" "Unlike"

    public Vot() {
    }

    public Vot(LocalDate data, Integer id_ruta, String correu_persona, String tipusVot, String valorVot) {
        this.data = data;
        this.idRuta = id_ruta;
        this.correuPersona = correu_persona;
        this.tipusVot = tipusVot;
        this.valorVot = valorVot;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Integer id_ruta) {
        this.idRuta = id_ruta;
    }

    public String getCorreuPersona() {
        return correuPersona;
    }

    public void setCorreuPersona(String correu_persona) {
        this.correuPersona = correu_persona;
    }

    public Integer getIdTipusVot() {
        return idTipusVot;
    }

    public void setIdTipusVot(Integer idTipusVot) {
        this.idTipusVot = idTipusVot;
    }

    public String getTipusVot() {
        return tipusVot;
    }

    public void setTipusVot(String tipusVot) {
        this.tipusVot = tipusVot;
    }

    public String getValorVot() {
        return valorVot;
    }

    public void setValorVot(String valorVot) {
        this.valorVot = valorVot;
    }

    @Override
    public String toString() {
        return "Vot{" +
                "id=" + id +
                ", data=" + data +
                ", id_ruta=" + idRuta +
                ", correu_persona='" + correuPersona + '\'' +
                ", id_tipusVot=" + tipusVot +
                '}';
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Grup getGrup() {
        return grup;
    }

    public void setGrup(Grup grup) {
        this.grup = grup;
    }

    public Integer getIdGrup() {
        return idGrup;
    }

    public void setIdGrup(Integer id_grup) {
        this.idGrup = id_grup;
    }
}

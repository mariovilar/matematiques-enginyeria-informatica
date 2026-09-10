package ub.edu.controller;

public class SessionMemory {
    String correuPersona;
    Integer idGrup;
    Integer idRuta;
    Integer idTram; //TramEtapa o TramTrack
    Integer idLocalitzacio;
    Integer idPuntDePas;
    Integer idAllotjament;

    public SessionMemory() {
    }

    public String getCorreuPersona() {
        return correuPersona;
    }

    public void setCorreuPersona(String correuPersona) {
        this.correuPersona = correuPersona;
    }

    public Integer getIdGrup() {
        return idGrup;
    }

    public void setIdGrup(Integer idGrup) {
        this.idGrup = idGrup;
    }

    public Integer getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Integer idRuta) {
        this.idRuta = idRuta;
    }

    public Integer getIdTram() {
        return idTram;
    }

    public void setIdTram(Integer idTram) {
        this.idTram = idTram;
    }

    public Integer getIdLocalitzacio() {
        return idLocalitzacio;
    }

    public void setIdLocalitzacio(Integer idLocalitzacio) {
        this.idLocalitzacio = idLocalitzacio;
    }

    public Integer getIdPuntDePas() {
        return idPuntDePas;
    }

    public void setIdPuntDePas(Integer idPuntDePas) {
        this.idPuntDePas = idPuntDePas;
    }

    public Integer getIdAllotjament() {
        return idAllotjament;
    }

    public void setIdAllotjament(Integer idAllotjament) {
        this.idAllotjament = idAllotjament;
    }
}

package ub.edu.model;

import java.time.LocalDate;

public class Reserva {
    private Integer id;
    private LocalDate dateCheckIn;
    private LocalDate dateCheckOut;
    // TODO: canviar pel que tingueu implementat a la pràctica 3 per a les reserves
    private String tipusPagament; // "Anticipat"....
    private String correuPersona;
    private Integer idAllotjament;

    private Allotjament allotjament;
    private Persona     persona;

    public Reserva() {
    }

    public Reserva(LocalDate dateCheckIn, LocalDate dateCheckOut,
                   String tipusPagament, String correu_persona, Integer id_allotjament) {
        this();
        this.dateCheckIn = dateCheckIn;
        this.dateCheckOut = dateCheckOut;
        this.tipusPagament = tipusPagament;
        this.correuPersona = correu_persona;
        this.idAllotjament = id_allotjament;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDateCheckIn() {
        return dateCheckIn;
    }

    public void setDateCheckIn(LocalDate dateCheckIn) {
        this.dateCheckIn = dateCheckIn;
    }

    public LocalDate getDateCheckOut() {
        return dateCheckOut;
    }

    public void setDateCheckOut(LocalDate dateCheckOut) {
        this.dateCheckOut = dateCheckOut;
    }

    public String getTipusPagament() {
        return tipusPagament;
    }

    public void setTipusPagament(String tipusPagament) {
        this.tipusPagament = tipusPagament;
    }

    public String getCorreuPersona() {
        return correuPersona;
    }

    public void setCorreuPersona(String correuPersona) {
        this.correuPersona = correuPersona;
    }

    public Integer getIdAllotjament() {
        return idAllotjament;
    }

    public void setIdAllotjament(Integer idAllotjament) {
        this.idAllotjament = idAllotjament;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", dateCheckIn=" + dateCheckIn +
                ", dateCheckOut=" + dateCheckOut +
                ", tipusPagament='" + tipusPagament + '\'' +
                ", correu_persona='" + correuPersona + '\'' +
                ", id_allotjament=" + idAllotjament +
                '}';
    }

    public Allotjament getAllotjament() {
        return allotjament;
    }

    public void setAllotjament(Allotjament allotjament) {
        this.allotjament = allotjament;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
}

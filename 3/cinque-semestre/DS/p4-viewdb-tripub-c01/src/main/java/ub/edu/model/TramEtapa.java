package ub.edu.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TramEtapa  extends Tram{
    private Integer id;
    private Double kms;
    private LocalTime tempsEstimat;
    private Double cost;
    private Integer idEtapaOrigen;
    private Integer idEtapaDesti;

    private Etapa etapaOrigen;

    private Etapa etapaDesti;

    public TramEtapa() {
        super();
    }

    public TramEtapa(Integer id_ruta, Integer id_transport_recomenat) {
        super(id_ruta, id_transport_recomenat);
    }

    public TramEtapa(Integer id_ruta, Integer id_transport_recomenat,
                     Double kms, LocalTime tempsEstimat, Double cost, Integer id_etapa_origen, Integer id_etapa_desti) {
        this(id_ruta, id_transport_recomenat);
        this.kms = kms;
        this.tempsEstimat = tempsEstimat;
        this.cost = cost;
        this.idEtapaOrigen = id_etapa_origen;
        this.idEtapaDesti = id_etapa_desti;
    }

    public TramEtapa(Double kms, LocalTime tempsEstimat, Double cost, Integer id_etapa_origen, Integer id_etapa_desti) {
        this();
        this.kms = kms;
        this.tempsEstimat = tempsEstimat;
        this.cost = cost;
        this.idEtapaOrigen = id_etapa_origen;
        this.idEtapaDesti = id_etapa_desti;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Double getKms() {
        return kms;
    }

    public void setKms(Double kms) {
        this.kms = kms;
    }

    public LocalTime getTempsEstimat() {
        return tempsEstimat;
    }

    public void setTempsEstimat(LocalTime tempsEstimat) {
        this.tempsEstimat = tempsEstimat;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getIdEtapaOrigen() {
        return idEtapaOrigen;
    }

    public void setIdEtapaOrigen(Integer id_etapa_origen) {
        this.idEtapaOrigen = id_etapa_origen;
    }

    public Integer getIdEtapaDesti() {
        return idEtapaDesti;
    }

    public void setIdEtapaDesti(Integer id_etapa_desti) {
        this.idEtapaDesti = id_etapa_desti;
    }

    public Etapa getEtapaOrigen() {
        return etapaOrigen;
    }

    public void setEtapaOrigen(Etapa etapaOrigen) {
        this.etapaOrigen = etapaOrigen;
    }

    public Etapa getEtapaDesti() {
        return etapaDesti;
    }

    public void setEtapaDesti(Etapa etapaDesti) {
        this.etapaDesti = etapaDesti;
    }

    @Override
    public String toString() {
        return "TramEtapa{" +
                "id=" + id +
                ", kms=" + kms +
                ", tempsEstimat=" + tempsEstimat +
                ", cost=" + cost +
                ", id_etapa_origen=" + idEtapaOrigen +
                ", id_etapa_desti=" + idEtapaDesti +
                '}';
    }


    public Etapa findEtapa(Integer id_etapa) {
        Etapa e = null;
        if (idEtapaDesti == id_etapa)
            e = etapaDesti;
        if (idEtapaOrigen == id_etapa)
            e = etapaOrigen;
        return e;
    }

    public Allotjament findAllotjament(Integer idAllotjament) {
        Allotjament a = null;
        a = etapaOrigen.getAllotjament(idAllotjament);
        if (a!=null) return a;
        else {
            a = etapaDesti.getAllotjament(idAllotjament);
        }
        return a;
    }


    public List<Allotjament> getAllAllotjaments() {
        List<Allotjament> listAllotjaments = new ArrayList<>();
        for (Allotjament a: etapaDesti.getAllAllotjaments()) {
            listAllotjaments.add(a);
        }
        for (Allotjament a: etapaOrigen.getAllAllotjaments()) {
            listAllotjaments.add(a);
        }
        return listAllotjaments;
    }
}

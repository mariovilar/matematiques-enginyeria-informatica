package ub.edu.model;

import java.time.LocalTime;
import java.util.List;

public class TramTrack extends Tram{
    private Integer id;
    private LocalTime duracioEstimada;
    private Double distancia;
    private Double desnivellPositiu;
    private Double desnivellNegatiu;

    private Integer idPuntDePasOrigen;
    private PuntDePas puntDePasOrigen;
    private Integer idPuntDePasDesti;
    private PuntDePas puntDePasDesti;
    public TramTrack() {
        super();
    }

    public TramTrack(Integer id_ruta, Integer id_transport_recomenat) {
        super(id_ruta, id_transport_recomenat);
    }

    public TramTrack(Integer id_ruta, Integer id_transport_recomenat,
                     LocalTime duracioEstimada, Double distancia, Double desnivellPositiu,
                     Double desnivellNegatiu, Integer id_puntDePas_origen, Integer id_puntDePas_desti) {
        this(id_ruta, id_transport_recomenat);
        this.duracioEstimada = duracioEstimada;
        this.distancia = distancia;
        this.desnivellPositiu = desnivellPositiu;
        this.desnivellNegatiu = desnivellNegatiu;
        this.idPuntDePasOrigen = id_puntDePas_origen;
        this.idPuntDePasDesti = id_puntDePas_desti;
    }

    public TramTrack(LocalTime duracioEstimada, Double distancia, Double desnivellPositiu,
                     Double desnivellNegatiu, Integer id_puntDePas_origen, Integer id_puntDePas_desti) {
        this();
        this.duracioEstimada = duracioEstimada;
        this.distancia = distancia;
        this.desnivellPositiu = desnivellPositiu;
        this.desnivellNegatiu = desnivellNegatiu;
        this.idPuntDePasOrigen = id_puntDePas_origen;
        this.idPuntDePasDesti = id_puntDePas_desti;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getDuracioEstimada() {
        return duracioEstimada;
    }

    public void setDuracioEstimada(LocalTime duracioEstimada) {
        this.duracioEstimada = duracioEstimada;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public Double getDesnivellPositiu() {
        return desnivellPositiu;
    }

    public void setDesnivellPositiu(Double desnivellPositiu) {
        this.desnivellPositiu = desnivellPositiu;
    }

    public Double getDesnivellNegatiu() {
        return desnivellNegatiu;
    }

    public void setDesnivellNegatiu(Double desnivellNegatiu) {
        this.desnivellNegatiu = desnivellNegatiu;
    }

    public Integer getIdPuntDePasOrigen() {
        return idPuntDePasOrigen;
    }

    public void setIdPuntDePasOrigen(Integer id_puntDePas_origen) {
        this.idPuntDePasOrigen = id_puntDePas_origen;
    }

    public Integer getIdPuntDePasDesti() {
        return idPuntDePasDesti;
    }

    public void setIdPuntDePasDesti(Integer id_puntDePas_desti) {
        this.idPuntDePasDesti = id_puntDePas_desti;
    }

    @Override
    public String toString() {
        return "TramTrack{" +
                "id=" + id +
                ", duracioEstimada=" + duracioEstimada +
                ", distancia=" + distancia +
                ", desnivellPositiu=" + desnivellPositiu +
                ", desnivellNegatiu=" + desnivellNegatiu +
                ", id_puntDePas_origen=" + idPuntDePasOrigen +
                ", id_puntDePas_desti=" + idPuntDePasDesti +
                '}';
    }

    public PuntDePas getPuntDePasOrigen() {
        return puntDePasOrigen;
    }

    public void setPuntDePasOrigen(PuntDePas puntDePasOrigen) {
        this.puntDePasOrigen = puntDePasOrigen;
    }

    public PuntDePas getPuntDePasDesti() {
        return puntDePasDesti;
    }

    public void setPuntDePasDesti(PuntDePas puntDePasDesti) {
        this.puntDePasDesti = puntDePasDesti;
    }

    public PuntDePas findPuntDePas(Integer idPuntDePas) {
        if (idPuntDePasOrigen == idPuntDePas) return puntDePasOrigen;
        else
            if (idPuntDePasDesti==idPuntDePas) return puntDePasDesti;
            else return null;
    }
}

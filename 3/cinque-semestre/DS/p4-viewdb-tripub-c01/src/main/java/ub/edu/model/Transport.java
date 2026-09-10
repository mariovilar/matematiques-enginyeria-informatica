package ub.edu.model;

public class Transport {
    private Integer id;
    private Double velocitatEstimada;
    private String nomTransport;

    public Transport() {
    }

    public Transport(Double velocitatEstimada, String nomTransport) {
        this();
        this.velocitatEstimada = velocitatEstimada;
        this.nomTransport =  nomTransport;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getVelocitatEstimada() {
        return velocitatEstimada;
    }

    public void setVelocitatEstimada(Double velocitatEstimada) {
        this.velocitatEstimada = velocitatEstimada;
    }

    public String getNomTransport() {
        return nomTransport;
    }

    public void setNomTransport(String nomTransport) {
        this.nomTransport = nomTransport;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "id=" + id +
                ", velocitatEstimada=" + velocitatEstimada +
                ", nomTransport=" + nomTransport +
                '}';
    }

}

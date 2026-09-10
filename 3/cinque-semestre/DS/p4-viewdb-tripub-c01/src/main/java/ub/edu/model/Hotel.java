package ub.edu.model;

public class Hotel extends Allotjament{
    private Integer id;
    private Double preuEsmorzar;
    private Double preuMP;
    private Double preuPC;

    public Hotel() {
        super();
    }

    public Hotel(String nom, Double preuPerNit, Integer id_etapa) {
        super(nom, preuPerNit);
    }

    public Hotel(String nom, Double preuPerNit, Integer id_etapa,
                 Double preuEsmorzar, Double preuMP, Double preuPC) {
        this(nom, preuPerNit, id_etapa);
        this.preuEsmorzar = preuEsmorzar;
        this.preuMP = preuMP;
        this.preuPC = preuPC;
    }

    public Hotel(Double preuEsmorzar, Double preuMP, Double preuPC) {
        this();
        this.preuEsmorzar = preuEsmorzar;
        this.preuMP = preuMP;
        this.preuPC = preuPC;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPreuEsmorzar() {
        return preuEsmorzar;
    }

    public void setPreuEsmorzar(Double preuEsmorzar) {
        this.preuEsmorzar = preuEsmorzar;
    }

    public Double getPreuMP() {
        return preuMP;
    }

    public void setPreuMP(Double preuMP) {
        this.preuMP = preuMP;
    }

    public Double getPreuPC() {
        return preuPC;
    }

    public void setPreuPC(Double preuPC) {
        this.preuPC = preuPC;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", preuEsmorzar=" + preuEsmorzar +
                ", preuMP=" + preuMP +
                ", preuPC=" + preuPC +
                '}';
    }

}

package ub.edu.model;

public class CasaRural extends Allotjament{
    private Integer id;
    private Double preuEsmorzar;

    public CasaRural() {
        super();
    }

    public CasaRural(String nom, Double preuPerNit, Integer id_etapa) {
        super(nom, preuPerNit);
    }

    public CasaRural(String nom, Double preuPerNit, Integer id_etapa,
                     Double preuEsmorzar) {
        this(nom, preuPerNit, id_etapa);
        this.preuEsmorzar = preuEsmorzar;
    }

    public CasaRural(Double preuEsmorzar) {
        this();
        this.preuEsmorzar = preuEsmorzar;
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

    @Override
    public String toString() {
        return "CasaRural{" +
                "id=" + id +
                ", preuEsmorzar=" + preuEsmorzar +
                '}';
    }

}

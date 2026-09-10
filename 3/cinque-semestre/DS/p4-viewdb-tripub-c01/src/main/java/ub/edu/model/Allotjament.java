package ub.edu.model;

public class Allotjament {
    private Integer id;
    private String nom;
    private Double preuPerNit;

    private Integer id_etapa; // Necessari per a poder lligar l'etapa amb l'allotjament
    private Etapa etapa;

    public Allotjament() {
    }

    public Allotjament(String nom, Double preuPerNit) {
        this();
        this.nom = nom;
        this.preuPerNit = preuPerNit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getPreuPerNit() {
        return preuPerNit;
    }

    public void setPreuPerNit(Double preuPerNit) {
        this.preuPerNit = preuPerNit;
    }

    public Integer getIdEtapa() {
        return id_etapa;
    }

    public void setIdEtapa(Integer id_etapa) {
        this.id_etapa = id_etapa;
    }

    @Override
    public String toString() {
        return "Allotjament{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", preuPerNit=" + preuPerNit +
                ", id_etapa=" + id_etapa +
                '}';
    }

    public void setEtapa(Etapa e) {
        this.etapa = e;
    }

}

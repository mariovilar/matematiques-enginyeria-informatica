package ub.edu.model;

public class Localitat {
    private Integer id;
    private String nom;
    private Double altitud;
    private Double latitud;
    private Double longitud;
    private Integer idComarca;
    private Comarca comarca;

    public Localitat() {
    }

    public Localitat(String nom, Double altitud, Double latitud, Double longitud, Integer id_comarca) {
        this();
        this.nom = nom;
        this.altitud = altitud;
        this.latitud = latitud;
        this.longitud = longitud;
        this.idComarca = id_comarca;
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

    public Double getAltitud() {
        return altitud;
    }

    public void setAltitud(Double altitud) {
        this.altitud = altitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Integer getIdComarca() {
        return idComarca;
    }

    public void setIdComarca(Integer idComarca) {
        this.idComarca = idComarca;
    }

    public Comarca getComarca() {
        return comarca;
    }

    public void setComarca(Comarca comarca) {
        this.comarca = comarca;
    }


    @Override
    public String toString() {
        return "Localitat{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", altitud=" + altitud +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", id_comarca=" + idComarca +
                '}';
    }



}

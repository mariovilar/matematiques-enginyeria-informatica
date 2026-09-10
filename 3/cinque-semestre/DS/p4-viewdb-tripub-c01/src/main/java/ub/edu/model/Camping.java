package ub.edu.model;

public class Camping extends Allotjament{
    private Integer id;

    public Camping() {
        super();
    }

    public Camping(String nom, Double preuPerNit, Integer id_etapa) {
        super(nom, preuPerNit);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Camping{" +
                "id=" + id +
                '}';
    }


}

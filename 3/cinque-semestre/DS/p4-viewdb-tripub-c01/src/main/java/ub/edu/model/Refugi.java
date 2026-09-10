package ub.edu.model;

public class Refugi  extends Allotjament{
    private Integer id;
    private Double preuMP;

    public Refugi() {
        super();
    }

    public Refugi(String nom, Double preuPerNit, Integer id_etapa) {
        super(nom, preuPerNit);
    }

    public Refugi(String nom, Double preuPerNit, Integer id_etapa,
                  Double preuMP) {
        this(nom, preuPerNit, id_etapa);
        this.preuMP = preuMP;
    }

    public Refugi(Double preuMP) {
        this();
        this.preuMP = preuMP;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPreuMP() {
        return preuMP;
    }

    public void setPreuMP(Double preuMP) {
        this.preuMP = preuMP;
    }

    @Override
    public String toString() {
        return "Refugi{" +
                "id=" + id +
                ", preuMP=" + preuMP +
                '}';
    }

    private void llamar_toString_super(){
        System.out.println(super.toString());
    }
}

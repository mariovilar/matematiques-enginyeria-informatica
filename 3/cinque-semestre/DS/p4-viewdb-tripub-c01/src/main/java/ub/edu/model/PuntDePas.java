package ub.edu.model;

public class PuntDePas extends Localitat{
    private Integer id;
    private String highlight;

    public PuntDePas() {
        super();
    }

    public PuntDePas(String nom, Double altitud, Double latitud, Double longitud, Integer id_comarca) {
        super(nom, altitud, latitud, longitud, id_comarca);
    }

    public PuntDePas(String nom, Double altitud, Double latitud, Double longitud, Integer id_comarca,
                     String highlight) {
        this(nom, altitud, latitud, longitud, id_comarca);
        this.highlight = highlight;
    }

    public PuntDePas(String highlight) {
        this();
        this.highlight = highlight;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    @Override
    public String toString() {
        return "PuntDePas{" +
                "id=" + id +
                ", highlight='" + highlight + '\'' +
                '}';
    }


    private void llamar_toString_super(){
        System.out.println(super.toString());
    }
}

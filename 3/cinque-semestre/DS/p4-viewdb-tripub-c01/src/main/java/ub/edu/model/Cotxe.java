package ub.edu.model;

public class Cotxe extends Transport{
    private Integer id;
    private String model;

    // la DB aceptaría cualquiera de las 2, tanto si es boolean como int. Coger la que mejor os vaya.
    private Boolean tipusCombustible;
    //private Integer tipusCombustible;


    public Cotxe() {
        super();
    }

    public Cotxe(Double velocitatEstimada, String nomTransport) {
        super(velocitatEstimada, nomTransport);
    }

    public Cotxe(Double velocitatEstimada,  String nomTransport,
                 String model, Boolean tipusCombustible) {
        this(velocitatEstimada, nomTransport);
        this.model = model;
        this.tipusCombustible = tipusCombustible;
    }

    public Cotxe(String model, Boolean tipusCombustible) {
        this();
        this.model = model;
        this.tipusCombustible = tipusCombustible;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Boolean isTipusCombustible() {
        return tipusCombustible;
    }

    public void setTipusCombustible(Boolean tipusCombustible) {
        this.tipusCombustible = tipusCombustible;
    }

    @Override
    public String toString() {
        return "Cotxe{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", tipusCombustible=" + tipusCombustible +
                '}';
    }

}

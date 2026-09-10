package ub.edu.model;

public class Bicicleta extends Transport{
    private Integer id;
    private String model;

    public Bicicleta() {
        super();
    }

    public Bicicleta(Double velocitatEstimada, String nomTransport) {
        super(velocitatEstimada, nomTransport);
    }

    public Bicicleta(Double velocitatEstimada, String nomTransport,
                     String model) {
        this(velocitatEstimada, nomTransport);
        this.model = model;
    }

    public Bicicleta(String model) {
        this();
        this.model = model;
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

    @Override
    public String toString() {
        return "Bicicleta{" +
                "id=" + id +
                ", model='" + model + '\'' +
                '}';
    }

}

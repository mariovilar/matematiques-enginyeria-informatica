package ub.edu.model;

public class aPeu extends Transport{
    private Integer id;

    public aPeu() {
        super();
    }

    public aPeu(Double velocitatEstimada, String nomTransport) {
        super(velocitatEstimada, nomTransport);
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
        return "aPeu{" +
                "id=" + id +
                '}';
    }

}

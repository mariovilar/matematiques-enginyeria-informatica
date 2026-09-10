package ub.edu.resources.dao.MOCK;

import ub.edu.model.Comarca;
import ub.edu.resources.dao.DAO_Interfaces.DAOComarca;

import java.util.*;

public class DAOComarcaMOCK implements DAOComarca {

    private Map<String, Comarca> llistaComarques = new HashMap<>();

    public DAOComarcaMOCK() {
        addComarca("Alt Empordà");
        addComarca("Cerdanya");
        addComarca("Ripollès");
        addComarca("Garrotxa");
        addComarca("Terra Alta");
        addComarca("Ribera d'Ebre");
        addComarca("Montsià");
        addComarca("Baix Ebre");
        addComarca("La Selva");
        addComarca("Baix Empordà");
        addComarca("Osona");
        addComarca("Gironès");

    }

    private void addComarca(String nom){
        llistaComarques.put(nom, new Comarca(nom));
    }



    @Override
    public List<Comarca> getAll() {
        return new ArrayList<>(llistaComarques.values());
    }

    @Override
    public Optional<Comarca> getById(String id) {
        return Optional.ofNullable(llistaComarques.get(id));
    }

    @Override
    public boolean add(final Comarca Comarca) {

        if (getById(Comarca.getNom()).isPresent()) {
            return false;
        }
        llistaComarques.put(Comarca.getNom(), Comarca);
        return true;
    }

    @Override
    public boolean update(final Comarca comarca, String[] params) {
        comarca.setNom(Objects.requireNonNull(
                params[0], "Title cannot be null"));
        return llistaComarques.replace(comarca.getNom(), comarca) != null;
    }

    @Override
    public boolean delete(final Comarca comarca) {
        return llistaComarques.remove(comarca.getNom()) != null;
    }
}

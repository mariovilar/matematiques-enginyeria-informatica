package ub.edu.resources.dao.MOCK;

import ub.edu.model.Ruta;
import ub.edu.resources.dao.DAO_Interfaces.DAORuta;

import java.util.*;

public class DAORutaMOCK implements DAORuta {

    private Map<String, Ruta> idRuta = new HashMap<>();

    public DAORutaMOCK() {
        addRuta("De la Cerdanya fins al Mar", "29/09/2021", 5);
        addRuta( "Terres de l'Ebre", "04/10/2021", 2);
        addRuta( "La Costa Brava", "10/10/2021", 10);
        addRuta("Ribera del Ter", "11/10/2021", 4);
    }

    private void addRuta(String nom, String dataText, Integer numDies){
        idRuta.put(nom, new Ruta(nom, dataText, numDies));
    }



    @Override
    public List<Ruta> getAll() {
        return new ArrayList<>(idRuta.values());
    }

    @Override
    public Optional<Ruta> getById(String id) {
        return Optional.ofNullable(idRuta.get(id));
    }

    @Override
    public boolean add(final Ruta ruta) {

        if (getById(ruta.getNom()).isPresent()) {
            return false;
        }
        idRuta.put(ruta.getNom(), ruta);
        return true;
    }

    @Override
    public boolean update(final Ruta ruta, String[] params) {
        ruta.setNom(Objects.requireNonNull(
                params[0], "Title cannot be null"));
        return idRuta.replace(ruta.getNom(), ruta) != null;
    }

    @Override
    public boolean delete(final Ruta ruta) {
        return idRuta.remove(ruta.getNom()) != null;
    }
}

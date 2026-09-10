package ub.edu.resources.dao.MOCK;

import ub.edu.resources.dao.DAO_Interfaces.DAORelacioComarcaRuta;
import ub.edu.resources.dao.Parell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAORelacioComarcaRutaMOCK implements DAORelacioComarcaRuta {

    // el primer element del parell és la comarca i el segon, la ruta

    List<Parell<String, String> > relacions = new ArrayList<>();

    public DAORelacioComarcaRutaMOCK() {
        afegirComarcaRuta("Cerdanya", "De la Cerdanya fins al Mar");
        afegirComarcaRuta("Ripollès", "De la Cerdanya fins al Mar");
        afegirComarcaRuta("Garrotxa", "De la Cerdanya fins al Mar");
        afegirComarcaRuta("Alt Empordà", "De la Cerdanya fins al Mar");
        //Terres de l'Ebre
        afegirComarcaRuta("Terra Alta", "Terres de l'Ebre");
        afegirComarcaRuta("Ribera d'Ebre", "Terres de l'Ebre");
        afegirComarcaRuta("Montsià", "Terres de l'Ebre");
        afegirComarcaRuta("Baix Ebre", "Terres de l'Ebre");
        //La Costa Brava
        afegirComarcaRuta("La Selva", "La Costa Brava");
        afegirComarcaRuta("Baix Empordà", "La Costa Brava");
        afegirComarcaRuta("Alt Empordà", "La Costa Brava");
        //Ribera del Ter
        afegirComarcaRuta("Ripollès", "Ribera del Ter");
        afegirComarcaRuta("Osona", "Ribera del Ter");
        afegirComarcaRuta("La Selva", "Ribera del Ter");
        afegirComarcaRuta("Gironès", "Ribera del Ter");
        afegirComarcaRuta("Baix Empordà", "Ribera del Ter");
    }

    private boolean afegirComarcaRuta(String nomComarca, String nomRuta){
        return relacions.add(new Parell<>(nomComarca, nomRuta));
    }

    @Override
    public Optional<Parell<String, String>> getById(String id) throws Exception {
        // Mètode que no té gaire sentit en aquesta classe
        return Optional.empty();
    }

    @Override
    public List<Parell<String, String>> getAll() throws Exception {
        return relacions;
    }

    @Override
    public boolean add(Parell<String, String> stringStringParell)  {
        if (relacions.contains(stringStringParell)) {
            return false;
        } else {
            relacions.add(stringStringParell);
            return true;
        }
    }

    @Override
    public boolean update(Parell<String, String> stringStringParell, String[] params) {
        // Mètode que no té gaire sentit en aquesta classe
        return false;
    }

    @Override
    public boolean delete(Parell<String, String> parell) throws Exception {
        if (relacions.contains(parell)) {
            relacions.remove(parell);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getComarquesByRuta(String idRuta) throws Exception {
        List<String> comarques = new ArrayList<>();
        for (Parell<String, String> parell : relacions) {
            if (parell.getElement2().equals(idRuta)) {
                comarques.add(parell.getElement1());
            }
        }
        return comarques;
    }

    @Override
    public List<String> getRutesByComarca(String idComarca) throws Exception {
        List<String> rutes = new ArrayList<>();
        for (Parell<String, String> parell : relacions) {
            if (parell.getElement1().equals(idComarca)) {
                rutes.add(parell.getElement2());
            }
        }
        return rutes;
    }
}


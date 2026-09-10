package ub.edu.resources.dao.MOCK;

import ub.edu.model.Persona;
import ub.edu.resources.dao.DAO_Interfaces.DAOPersona;

import java.util.*;

public class DAOPersonaMOCK implements DAOPersona {

    private Map<String, Persona> xarxaPersones = new HashMap<>();

    public DAOPersonaMOCK() {
        xarxaPersones.put("ajaleo@gmail.com", new Persona("ajaleo@gmail.com", "ajaleoPassw7"));
        xarxaPersones.put("dtomacal@yahoo.cat", new Persona("dtomacal@yahoo.cat", "Qwertyft5"));
        xarxaPersones.put("heisenberg@gmail.com", new Persona("heisenberg@gmail.com", "the1whoknocks"));
        xarxaPersones.put("rick@gmail.com",  new Persona("rick@gmail.com", "wabalabadapdap22"));
        xarxaPersones.put("nietolopez10@gmail.com",new Persona("nietolopez10@gmail.com", "pekFD91m2a"));
        xarxaPersones.put("nancyarg10@yahoo.com", new Persona("nancyarg10@yahoo.com", "contra10LOadc"));
        xarxaPersones.put( "CapitaCC@gmail.com",  new Persona("CapitaCC@gmail.com", "Alistar10"));
        xarxaPersones.put( "nauin2@gmail.com", new Persona("nauin2@gmail.com", "kaynJGL20"));
        xarxaPersones.put( "juancarlos999@gmail.com", new Persona("juancarlos999@gmail.com", "staIamsA12"));
        xarxaPersones.put( "judit121@gmail.com", new Persona("judit121@gmail.com", "Ordinador1"));
    }

    @Override
    public List<Persona> getAll() {
        return new ArrayList<>(xarxaPersones.values());
    }

    @Override
    public Optional<Persona> getById(String id) {
        return Optional.ofNullable(xarxaPersones.get(id));
    }

    @Override
    public boolean add(final Persona persona) {
        if (xarxaPersones.containsKey(persona.getName())) {
            return false;
        }
        xarxaPersones.put(persona.getName(), persona);
        return true;
    }

    @Override
    public boolean update(final Persona persona, String[] params) {
        persona.setName(Objects.requireNonNull(
                params[0], "Name cannot be null"));
        persona.setPwd(Objects.requireNonNull(
                params[1], "Password cannot be null"));
        return xarxaPersones.replace(persona.getName(), persona) != null;
    }

    @Override
    public boolean delete(final Persona persona) {
        return xarxaPersones.remove(persona.getName()) != null;
    }


    @Override
    public Persona findPersonaByCorreu(String correu) throws Exception {
        return null;
    }
    @Override
    public Persona findPersonaByUserNameAndPassword(String usuari, String pwd) throws Exception {
        if (getById(usuari).isPresent()) {
            Persona c = xarxaPersones.get(usuari);
            if (c.getPwd().equals(pwd)) {
                return c;
            } else throw new Exception();
        } else throw new Exception();
    }



}

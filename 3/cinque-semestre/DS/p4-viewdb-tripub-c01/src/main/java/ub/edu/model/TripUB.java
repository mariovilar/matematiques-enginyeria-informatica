package ub.edu.model;

import ub.edu.resources.dao.Parell;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TripUB {
    private Integer id;
    private XarxaPersones xarxaPersones;   // tripub
    private Map<Integer, Ruta> rutaMap; // tripub
    private Map<Integer, Comarca> comarcaMap; // tripub
    private Map<Integer, Grup> grups;

    public TripUB() {
        grups = new HashMap<>();
        comarcaMap = new HashMap<>();
        rutaMap = new HashMap<>();
    }

    public boolean setGrups(List<Grup> lg) {
        this.grups =  (lg.stream()
                .collect(Collectors.toMap(Grup::getId, Function.identity())));
        return (grups!= null);
    }

    public void setXarxaPersones(XarxaPersones x) {
        this.xarxaPersones = x;
    }

    public boolean setRutes(List<Ruta> listRutas) {
        this.rutaMap =  (listRutas.stream()
                .collect(Collectors.toMap(Ruta::getId, Function.identity())));
        return (this.rutaMap != null);
    }

    public boolean  setComarques(List<Comarca> listComarques) {
        comarcaMap =  (listComarques.stream()
                .collect(Collectors.toMap(Comarca::getId, Function.identity())));
        return (comarcaMap != null) ;
    }
    public Integer getId() {
        return id;
    }

    public void setComarquesToRutes(List<Parell<Integer, Integer>> relacionsRC) {
        for (Parell p : relacionsRC) {
            Ruta r = rutaMap.get(p.getElement2());
            Comarca c = comarcaMap.get(p.getElement1());
            r.addComarca(c);
        }
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Comarca findComarca(Integer idComarca) {
        Comarca c = null;
        c = comarcaMap.get(idComarca);
        return c;
    }

    public Grup findGrup(Integer idGrup) {
        return grups.get(idGrup);
    }

    public Ruta findRuta(Integer idRuta) {
        return rutaMap.get(idRuta);
    }

    public List<Comarca> getAllComarques() {
        //no se puede hacer cast directo entre map->hasmap a una collection->list. Se debe instanciar una nueva arraylist
        return new ArrayList<Comarca>(this.comarcaMap.values());
    }

    public void addGrup(Grup grup) {
        grups.put(grup.getId(), grup);
    }


    public List<Ruta> getRutes() {
        //no se puede hacer cast directo entre map->hasmap a una collection->list. Se debe instanciar una nueva arraylist
        return new ArrayList<Ruta>(this.rutaMap.values());
    }

    public XarxaPersones getXarxaPersones() {
        return xarxaPersones;
    }

    public List<Grup> getAllGrups() {
        //no se puede hacer cast directo entre map->hasmap a una collection->list. Se debe instanciar una nueva arraylist
        return new ArrayList<Grup>(this.grups.values());
    }

    public Integer getIdGrupByName(String newValue) {
        List<Grup> list = this.getAllGrups();
        for (Grup g: list) {
            if(g.getNom().equals(newValue))
                return g.getId();
        }
        return null;
    }

    public List<Ruta> findRutaByOrigen(Integer id) {
        List<Ruta> rutes = new ArrayList<>();
        for (Ruta r: rutaMap.values()) {
            if (r.getIdLlocOrigen()==id) rutes.add(r);
        }
        return rutes;
    }

    public List<Ruta> findRutaByDesti(Integer id) {
        List<Ruta> rutes = new ArrayList<>();
        for (Ruta r: rutaMap.values()) {
            if (r.getIdLlocDesti()==id) rutes.add(r);
        }
        return rutes;
    }
}


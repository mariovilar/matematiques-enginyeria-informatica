package ub.edu.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Ruta {
    private Integer id;
    private String nom;
    private LocalDate dataCreacio;
    private Integer durada;

    private String descripcio;
    private Double cost;
    private Double distancia;
    private String dificultat;
    private String tipusRuta;

    private Integer idLlocDesti;
    private Integer idLlocOrigen;
    private Localitat localitatOrigen;
    private Localitat localitatDesti;

    private HashMap<Integer,Tram> trams;
    private Map<Integer,Comarca> comarques;
    public Ruta() {
        comarques = new HashMap<>();
        trams = new HashMap<Integer,Tram>();
    }

    public Ruta(String titol, String dataText, Integer numDies) {
        this();
        this.nom = titol;
        setDurada(numDies);
        setDataCreacio(dataText);
    }

    public Ruta(String titol, Integer numDies, String dataText, List<Comarca> comarcaList) {
        this();
        this.nom = titol;
        setDurada(numDies);
        setDataCreacio(dataText);
        comarques = (comarcaList.stream()
                .collect(Collectors.toMap(Comarca::getId, Function.identity())));
        trams = new HashMap<Integer,Tram>();
        }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getDurada(){
        return this.durada;
    }
    public void setDurada(Integer numdies){
        this.durada = numdies;
    }

    public LocalDate getDataCreacio() { return dataCreacio;}


    public void setDataCreacio(String dataText){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataCreacio = LocalDate.parse(dataText, formatter);
    }

    public void setDataCreacio(LocalDate dataCreacio){
        this.dataCreacio = dataCreacio;
    }
    public void addComarca(Comarca comarca){
        comarques.put(comarca.getId(), comarca);
    }

    public boolean containsComarca(Comarca comarca){
        return comarques.get(comarca.getId())!=null;
    }

    public List<Comarca> getComarques() { return new ArrayList<Comarca>(comarques.values());}

    public Etapa findEtapa(Integer id_etapa) {
        Etapa e = null;

        for (Tram t: trams.values()) {
            if (t instanceof TramEtapa) e = ((TramEtapa) t).findEtapa(id_etapa) ;
            if (e!= null) break;
        }
        return e;
    }


    // DB
    public Ruta(String nom, LocalDate dataCreacio, Integer durada, String descripcio, Double cost, Double distancia, String dificultat, String tipusRuta,
                Integer id_lloc_origen, Integer id_lloc_desti) {
        this();
        this.nom = nom;
        this.dataCreacio = dataCreacio;
        this.durada = durada;
        this.descripcio = descripcio;
        this.cost = cost;
        this.distancia = distancia;
        this.dificultat = dificultat;
        this.tipusRuta = tipusRuta;
        this.idLlocOrigen = id_lloc_origen;
        this.idLlocDesti = id_lloc_desti;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setComarques(Map<Integer, Comarca> comarques) {
        this.comarques = comarques;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public String getDificultat() {
        return dificultat;
    }

    public void setDificultat(String dificultat) {
        this.dificultat = dificultat;
    }

    public String getTipusRuta() {
        return tipusRuta;
    }

    public void setTipusRuta(String tipusRuta) {
        this.tipusRuta = tipusRuta;
    }

    public Integer getIdLlocOrigen() {
        return idLlocOrigen;
    }

    public void setIdLlocOrigen(Integer id_lloc_origen) {
        this.idLlocOrigen = id_lloc_origen;
    }

    public Integer getIdLlocDesti() {
        return idLlocDesti;
    }

    public void setIdLlocDesti(Integer id_lloc_desti) {
        this.idLlocDesti = id_lloc_desti;
    }


    public Localitat getLocalitatOrigen ()  {
        return localitatOrigen;
    }

    public Localitat getLocalitatDesti() {
        return localitatDesti;
    }

    public void setLocalitatOrigen (Localitat l){
        localitatOrigen = l;
    }

    public void setLocalitatDesti (Localitat l) {
        localitatDesti = l;
    }
    @Override
    public String toString() {
        return "Ruta{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", dataCreacio=" + dataCreacio +
                ", durada=" + durada +
                ", comarques=" + comarques +
                ", descripcio='" + descripcio + '\'' +
                ", cost=" + cost +
                ", distancia=" + distancia +
                ", dificultat='" + dificultat + '\'' +
                ", tipusRuta='" + tipusRuta + '\'' +
                ", id_lloc_origen=" + idLlocOrigen +
                ", id_lloc_desti=" + idLlocDesti +
                '}';
    }


    public void addTram(Tram te) {
        trams.put(te.getId(), te);
    }

    public boolean findTram(Integer id_tram) {
        return trams.get(id_tram) != null;
    }

    public void addTransportTram(Integer id_tram, Transport t) {
        trams.get(id_tram).addTransportPossible(t);
    }

    public PuntDePas findPuntDePas(Integer idPuntDePas) {
        PuntDePas pdp = null;
        for (Tram t: trams.values()) {
            if (t instanceof TramTrack) {
                pdp = ((TramTrack)(t)).findPuntDePas(idPuntDePas);
                if (pdp != null) {
                    return pdp;
                }
            }
        }
        return pdp;
    }

    public Allotjament findAllotjament(Integer idAllotjament) {
        Allotjament allotjament = null;
        for (Tram t: trams.values()) {
            if (t instanceof TramEtapa) {
                allotjament = ((TramEtapa)(t)).findAllotjament(idAllotjament);
                if (allotjament != null) {
                    return allotjament;
                }
            }
        }
        return allotjament;
    }

    public List<TramEtapa> getAllTramEtapas() {
        List<TramEtapa> llista = new ArrayList<>();
        for (Tram t: trams.values()) {
            if (t instanceof TramEtapa) {
                llista.add((TramEtapa)t);
            }
        }
        return llista;
    }
    public List<TramTrack> getAllTramTracks() {
        List<TramTrack> llista = new ArrayList<>();
        for (Tram t: trams.values()) {
            if (t instanceof TramTrack) {
                llista.add((TramTrack)t);
            }
        }
        return llista;
    }

    public TramTrack findTramTrackById(Integer id) {
        return (TramTrack) trams.get(id);
    }
    public TramEtapa findTramEtapaById(Integer id) {
        return (TramEtapa) trams.get(id);
    }

    public Etapa getEtapaById(Integer id) {
        Etapa etapa;
        for (Tram t : trams.values()) {
            if (t instanceof TramEtapa) {
                etapa = ((TramEtapa) t).findEtapa(id);
                if (etapa != null) return etapa;
            }
        }
        return null;
    }


    public PuntDePas getPuntDePasById(Integer id) {
        PuntDePas pdp;
        for (Tram t : trams.values()) {
            if (t instanceof TramTrack) {
                pdp = ((TramTrack) t).findPuntDePas(id);
                if (pdp != null) return pdp;
            }
        }
        return null;
    }

    public List<Allotjament> getAllAllotjaments() {
        List<Allotjament> listAllotjaments = new ArrayList<>();
        for (Tram t: trams.values()) {
            if (t instanceof TramEtapa) {
                List<Allotjament> le = ((TramEtapa)t).getAllAllotjaments();
                for (Allotjament te : le) {
                    listAllotjaments.add(te);
                }
            }
        }
        return listAllotjaments;
    }
}
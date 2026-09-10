package ub.edu.resources.dao.DB.DAO_Implementations;

import ub.edu.model.*;
import ub.edu.resources.dao.DAO_Interfaces.DAOTramTrack;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class DAOTramTrackDB implements DAOTramTrack {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private ArrayList<Object> columns_parametrosTabla;

    private String nameTable;

    DAOTramDB daoTramDB;

    public DAOTramTrackDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        this.columns_parametrosTabla=new ArrayList<>();

        if(this.array_AvailableTablesNames.contains("TramTrack")){
            this.nameTable = "TramTrack";
            this.daoTramDB = new DAOTramDB(this.connection);
            this.columns_parametrosTabla = this.crud_tables.obtener_nombre_de_todas_columnas(this.nameTable);
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOTramTrackDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<TramTrack> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr) throws ParseException {
        List<TramTrack> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> hashMap = (HashMap<Object,Object>) obj;
            TramTrack tt = new TramTrack();

            if(hashMap.containsKey("id_tramTrack")){tt.setId((Integer) hashMap.get("id_tramTrack"));}
            else{tt.setId(null);}

            if(hashMap.containsKey("distancia")){tt.setDistancia((Double) hashMap.get("distancia"));}
            else{tt.setDistancia(null);}

            // LocalTime
            if(hashMap.containsKey("duracioEstimada")){
                if(hashMap.get("duracioEstimada") == null || hashMap.get("duracioEstimada") == "null"){
                    tt.setDuracioEstimada(null);
                }else{
                    String str_Time = (String) hashMap.get("duracioEstimada");
                    //DateFormat df=new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat formateador = new SimpleDateFormat("hh:mm:ss");
                    Date t=formateador.parse(str_Time);
                    //System.out.println("t:"+t);
                    //System.out.println("t.getHours():"+t.getHours());
                    //System.out.println("t.getMinutes():"+t.getMinutes());
                    //System.out.println("t.getSeconds():"+t.getSeconds());
                    LocalTime localTime= LocalTime.of(t.getHours(),t.getMinutes(),t.getSeconds());
                    //System.out.println("localTime->{"+localTime+"}");
                    tt.setDuracioEstimada(localTime);
                }
            }
            else{tt.setDuracioEstimada(null);}

            if(hashMap.containsKey("desnivellPositiu")){tt.setDesnivellPositiu(((Double) hashMap.get("desnivellPositiu")));}
            else{tt.setDesnivellPositiu(null);}

            if(hashMap.containsKey("desnivellNegatiu")){tt.setDesnivellNegatiu(((Double) hashMap.get("desnivellNegatiu")));}
            else{tt.setDesnivellNegatiu(null);}

            if(hashMap.containsKey("id_puntDePas_origen")){tt.setIdPuntDePasOrigen((Integer) hashMap.get("id_puntDePas_origen"));}
            else{tt.setIdPuntDePasOrigen(null);}

            if(hashMap.containsKey("id_puntDePas_desti")){tt.setIdPuntDePasDesti((Integer) hashMap.get("id_puntDePas_desti"));}
            else{tt.setIdPuntDePasDesti(null);}

            list.add(tt);
            //System.out.println(p);
        }
        return list;
    }
    private void rellenarMadreEnLaHija(Tram madre, TramTrack hija){
        //el id no hace falta pues ya lo tiene puesto que es suyo y ya se lo asigna el propio crud
        //el id de la madre en la hija, al ser override nos da igual pues cogemos el de la hija
        hija.setId_ruta(madre.getId_ruta());
        hija.setId_transport_recomenat(madre.getId_transport_recomenat());
    }


    @Override
    public Optional<TramTrack> getById(String id) throws Exception {
        Optional<Tram> resuMadre = this.daoTramDB.getById(id);

        Tram tram = resuMadre.get();
        //System.out.println("allotjament:"+allotjament);


        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_tramTrack",Integer.valueOf(id));
        //System.out.println("arr:"+arr);
        List<TramTrack> list = this.convertir_array_de_Hasmaps_a_List(arr);
        //System.out.println("list:"+list);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 tramTrack con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            this.rellenarMadreEnLaHija(tram,list.get(0));
            return Optional.ofNullable(list.get(0));
        }

        return Optional.empty();
    }

    @Override
    public List<TramTrack> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<TramTrack> list = this.convertir_array_de_Hasmaps_a_List(arr);
        List<TramTrack> listFinal = new ArrayList<>();
        for (TramTrack tramTrack: list) {
            //cada Cotxe de la lista tiene la madre vacia
            //para poner los datos de la madre dentro de la madre de la hija
            //utilizar el getById que ya lo hace
            TramTrack tramTrackFinal = this.getById(tramTrack.getId().toString()).get();
            listFinal.add(tramTrackFinal);
        }
        System.out.println(listFinal);
        return listFinal;
    }

    @Override
    public boolean add(TramTrack tramTrack) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("distancia",tramTrack.getDistancia());
        hashmap.put("DuracioEstimada",tramTrack.getDuracioEstimada());
        hashmap.put("desnivellPositiu",tramTrack.getDesnivellPositiu());
        hashmap.put("desnivellNegatiu",tramTrack.getDesnivellNegatiu());
        hashmap.put("id_puntDePas_origen",tramTrack.getIdPuntDePasOrigen());
        hashmap.put("id_puntDePas_desti",tramTrack.getIdPuntDePasDesti());

        //como es una herencia, primero hay que añadir la clase madre
        boolean resuMadre = this.daoTramDB.add((Tram) tramTrack);
        Tram tram = this.daoTramDB.getTram((Tram) tramTrack).get();
        System.out.println("tram:"+tram);
        System.out.println("tramTrack:"+tramTrack);
        if(resuMadre){
            //si es true->no existia antes la madre, por lo que lo ha añdido
            // recuperar el id del nuevo elemento y ponerselo al hijo
            hashmap.put("id_tramTrack",tram.getId());
        } else {
            //si es false->ya existiade antes la madre,por lo que no se ha añadido
            //preguntar por el id haciendo un select

            System.out.println("ERROR: exepcion extraña en el add TramTrack");
            //nunca va a ser false porque la madre, la pk es autoincremental
        }
        //hotel.setId(allotjament.getId());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu) {
            //si lo ha insertado correctamete, entonces actualizar el id con el de la madre que es autoInc
            tramTrack.setId(tram.getId());
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(TramTrack tramTrack, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_tramTrack",tramTrack.getId());

        HashMap<Object, Object> hashmap_NEW_hija = new HashMap<Object, Object>();
        HashMap<Object, Object> hashmap_NEW_madre = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_tramTrack")){
                flag_pk = true;
            }
            if(this.columns_parametrosTabla.contains(params[i])){
                //si la contiene, significa que es de la hija
                hashmap_NEW_hija.put(params[i],params[i+1]);
            }else{
                //sino la contiene
                //siginica que queremos hacer el update de algo de la madre
                hashmap_NEW_madre.put(params[i],params[i+1]);
            }        }
        if(!flag_pk){
            hashmap_NEW_hija.put("id_tramTrack",tramTrack.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_tramTrack");

        boolean resu = false;
        if(hashmap_NEW_hija.size()==1 && !flag_pk){
            //no hay nada que hacer el update
            //solo ha entrado por el id. pero al ser pk_fk de la madre, no hay que hacer update
        }else {
            resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW_hija,array_AutoInc_PKs, true);
        }
        System.out.println(resu);

        ArrayList<Object> arrayKeysMadre_finalHasMap = new ArrayList<>(hashmap_NEW_madre.keySet());
        if(arrayKeysMadre_finalHasMap.size()==0){
            return resu; //no hace falta valorar la madre porque no la mandamos a hacer el update
        }

        int n = arrayKeysMadre_finalHasMap.size();
        String[] newArray = new String[n*2];

        // el array de keys se incrementa de 1 en 1.
        int count = 0;
        // el hasmap se incrementa de 2 en 2 pues cogemos key, value
        for (int i = 0; i < newArray.length; i=i+2) {
            //key
            newArray[i] = (String) arrayKeysMadre_finalHasMap.get(count);
            //value
            newArray[i+1] = (String) hashmap_NEW_madre.get(arrayKeysMadre_finalHasMap.get(count));
            count++;
        }

        boolean resuMadre = this.daoTramDB.update((Tram) tramTrack, newArray);
        System.out.println(resuMadre);
        return (resu && resuMadre);
    }

    @Override
    public boolean delete(TramTrack tramTrack) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_tramTrack",tramTrack.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        //una vez borrada la hija, tambien borramos la madre
        boolean resuMadre = this.daoTramDB.delete((Tram) tramTrack);

        return (resu && resuMadre);
    }
}

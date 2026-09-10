package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.resources.dao.DAO_Interfaces.DAOTransportPossiblePerAlTram;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;
import ub.edu.resources.dao.Parell;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOTransport_PossiblePerAl_TramDB implements DAOTransportPossiblePerAlTram {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOTransport_PossiblePerAl_TramDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Transport_PossiblePerAl_Tram")){
            this.nameTable = "Transport_PossiblePerAl_Tram";
        }else{
            System.out.println("\n********************************************************************************");
            System.out.println("ERROR en DAOTransport_PossiblePerAl_TramDB al intentar abrir el nombre de la tabla");
            System.out.println("**********************************************************************************");
        }
    }

    private List<Parell<Integer,Integer>> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<Parell<Integer,Integer>> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Parell<Integer,Integer> tpt = new Parell<>();

            if(h.containsKey("id_transport")){tpt.setElement1((Integer) h.get("id_transport"));}
            else{tpt.setElement1(null);}

            if(h.containsKey("id_tram")){tpt.setElement2((Integer) h.get("id_tram"));}
            else{tpt.setElement2(null);}

            list.add(tpt);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Parell<Integer,Integer>> getById(String id) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Parell<Integer,Integer>> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Parell<Integer,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Parell<Integer,Integer> tpt) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_transport",tpt.getElement1());
        hashmap.put("id_tram",tpt.getElement2());

        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,"id_transport","id_tram");
        //al no ser pk autoincremental, no hace falta recoger el id de la BD pues ya lo tienes
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Parell<Integer,Integer> tpt, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_transport",tpt.getElement1());
        hashmap_OLD.put("id_tram",tpt.getElement2());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk_1 = false;
        boolean flag_pk_2 = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_transport")){
                flag_pk_1 = true;
            }
            if(params[i].equals("id_tram")){
                flag_pk_2 = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk_1){
            hashmap_NEW.put("id_transport",tpt.getElement1());
        }
        if(!flag_pk_2){
            hashmap_NEW.put("id_tram",tpt.getElement2());
        }
        // debe ser null y false para poderse evaluar.
        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,null, false);
        System.out.println(resu);

        if(resu){
            //en caso de que el update haya sido satisfactorio
            //se ha updateado en la BD, pero como es una M-N
            //y sus pks no soy auto incrementales y son fks
            //lo que hayq ue hacer es actualizar ambos ids
            tpt.setElement1((Integer) Integer.valueOf((String) hashmap_NEW.get("id_transport")));
            tpt.setElement2((Integer) Integer.valueOf((String) hashmap_NEW.get("id_tram")));
        }
        return resu;
    }

    @Override
    public boolean delete(Parell<Integer,Integer> tpt) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_transport",tpt.getElement1());
        hashmap.put("id_tram",tpt.getElement2());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }


    /* Param String AND_OR must be: "AND" or "OR" */
    @Override
    public List<Parell<Integer,Integer>> getByIds_AND_OR (Integer id_transport, String AND_OR,  Integer id_tram){
        ArrayList<Object> arr = this.crud_tables.select_TwoParam_AND_OR_Table(this.nameTable,"id_transport",id_transport,AND_OR,"id_tram",id_tram);
        List<Parell<Integer,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }

    @Override
    public List<Parell<Integer,Integer>> getById_Transport (Integer id){
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_transport",id);
        List<Parell<Integer,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }

    @Override
    public List<Parell<Integer,Integer>> getById_Tram (Integer id){
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_tram",id);
        List<Parell<Integer,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }
}

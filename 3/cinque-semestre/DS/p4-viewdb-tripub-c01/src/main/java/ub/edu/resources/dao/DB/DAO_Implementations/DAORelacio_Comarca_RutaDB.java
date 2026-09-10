package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.resources.dao.DAO_Interfaces.DAORelacioComarcaRuta_2;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;
import ub.edu.resources.dao.Parell;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAORelacio_Comarca_RutaDB implements DAORelacioComarcaRuta_2 {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAORelacio_Comarca_RutaDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Relacio_Comarca_Ruta")){
            this.nameTable = "Relacio_Comarca_Ruta";
        }else{
            System.out.println("\n********************************************************************************");
            System.out.println("ERROR en DAORelacio_Comarca_RutaDB al intentar abrir el nombre de la tabla");
            System.out.println("**********************************************************************************");
        }
    }

   private List<Parell<Integer,Integer>> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<Parell<Integer,Integer>> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Parell<Integer,Integer> rcr = new Parell<>();

            if(h.containsKey("id_comarca")){rcr.setElement1((Integer) h.get("id_comarca"));}
            else{rcr.setElement1(null);}

            if(h.containsKey("id_ruta")){rcr.setElement2((Integer) h.get("id_ruta"));}
            else{rcr.setElement2(null);}

            list.add(rcr);
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
    public boolean add(Parell<Integer,Integer> rcr) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_comarca",rcr.getElement1());
        hashmap.put("id_ruta",rcr.getElement2());

        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,"id_comarca","id_ruta");
        //al no ser pk autoincremental, no hace falta recoger el id de la BD pues ya lo tienes
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Parell<Integer,Integer> rcr, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_comarca",rcr.getElement1());
        hashmap_OLD.put("id_ruta",rcr.getElement2());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk_1 = false;
        boolean flag_pk_2 = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_comarca")){
                flag_pk_1 = true;
            }
            if(params[i].equals("id_ruta")){
                flag_pk_2 = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk_1){
            hashmap_NEW.put("id_comarca",rcr.getElement1());
        }
        if(!flag_pk_2){
            hashmap_NEW.put("id_ruta",rcr.getElement2());
        }
        // debe ser null y false para poderse evaluar.
        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,null, false);
        System.out.println(resu);

        if(resu){
            //en caso de que el update haya sido satisfactorio
            //se ha updateado en la BD, pero como es una M-N
            //y sus pks no soy auto incrementales y son fks
            //lo que hayq ue hacer es actualizar ambos ids
            rcr.setElement1((Integer) Integer.valueOf((String) hashmap_NEW.get("id_comarca")));
            rcr.setElement2((Integer) Integer.valueOf((String) hashmap_NEW.get("id_ruta")));
        }
        return resu;
    }

    @Override
    public boolean delete(Parell<Integer,Integer> rcr) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_comarca",rcr.getElement1());
        hashmap.put("id_ruta",rcr.getElement2());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    /* Param String AND_OR must be: "AND" or "OR" */
    @Override
    public List<Parell<Integer,Integer>> getByIds_AND_OR (Integer id_comarca, String AND_OR,  Integer id_ruta){
        ArrayList<Object> arr = this.crud_tables.select_TwoParam_AND_OR_Table(this.nameTable,"id_comarca",id_comarca,AND_OR,"id_ruta",id_ruta);
        List<Parell<Integer,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }

    @Override
    public List<Parell<Integer,Integer>> getById_Comarca (Integer id){
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_comarca",id);
        List<Parell<Integer,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }

    @Override
    public List<Parell<Integer,Integer>> getById_Ruta (Integer id){
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_ruta",id);
        List<Parell<Integer,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }
}

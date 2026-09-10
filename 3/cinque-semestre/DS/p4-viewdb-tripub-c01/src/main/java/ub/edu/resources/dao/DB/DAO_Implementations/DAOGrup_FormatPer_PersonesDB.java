package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.resources.dao.DAO_Interfaces.DAOGrupFormatPerPersones;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;
import ub.edu.resources.dao.Parell;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOGrup_FormatPer_PersonesDB implements DAOGrupFormatPerPersones {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOGrup_FormatPer_PersonesDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Grup_FormatPer_Persones")){
            this.nameTable = "Grup_FormatPer_Persones";
        }else{
            System.out.println("\n********************************************************************************");
            System.out.println("ERROR en DAOGrup_FormatPer_PersonesDB al intentar abrir el nombre de la tabla");
            System.out.println("**********************************************************************************");
        }
    }

   private List<Parell<String,Integer>> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<Parell<String,Integer>> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Parell<String,Integer> gfp = new Parell<>();

            if(h.containsKey("correu_persona")){gfp.setElement1((String) h.get("correu_persona"));}
            else{gfp.setElement1(null);}

            if(h.containsKey("id_grup")){gfp.setElement2((Integer) h.get("id_grup"));}
            else{gfp.setElement2(null);}

            list.add(gfp);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Parell<String,Integer>> getById(String id) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Parell<String,Integer>> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Parell<String,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Parell<String,Integer> gfp) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("correu_persona",gfp.getElement1());
        hashmap.put("id_grup",gfp.getElement2());

        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,"correu_persona","id_grup");
        //al no ser pk autoincremental, no hace falta recoger el id de la BD pues ya lo tienes
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Parell<String,Integer> gfp, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("correu_persona",gfp.getElement1());
        hashmap_OLD.put("id_grup",gfp.getElement2());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk_1 = false;
        boolean flag_pk_2 = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("correu_persona")){
                flag_pk_1 = true;
            }
            if(params[i].equals("id_grup")){
                flag_pk_2 = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk_1){
            hashmap_NEW.put("correu_persona",gfp.getElement1());
        }
        if(!flag_pk_2){
            hashmap_NEW.put("id_grup",gfp.getElement2());
        }
        // debe ser null y false para poderse evaluar.
        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,null, false);
        System.out.println(resu);

        if(resu){
            //en caso de que el update haya sido satisfactorio
            //se ha updateado en la BD, pero como es una M-N
            //y sus pks no soy auto incrementales y son fks
            //lo que hayq ue hacer es actualizar ambos ids
            gfp.setElement1((String) hashmap_NEW.get("correu_persona"));
            gfp.setElement2((Integer) Integer.valueOf((String) hashmap_NEW.get("id_grup")));
        }
        return resu;
    }

    @Override
    public boolean delete(Parell<String,Integer> gfp) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("correu_persona",gfp.getElement1());
        hashmap.put("id_grup",gfp.getElement2());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    /* Param String AND_OR must be: "AND" or "OR" */
    @Override
    public List<Parell<String,Integer>> getByIds_AND_OR (String correu_persona, String AND_OR,  Integer id_grup){
        ArrayList<Object> arr = this.crud_tables.select_TwoParam_AND_OR_Table(this.nameTable,"correu_persona",correu_persona,AND_OR,"id_grup",id_grup);
        List<Parell<String,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }

    @Override
    public List<Parell<String,Integer>> getByCorreu_Persona (String correu_persona) {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"correu_persona",correu_persona);
        List<Parell<String,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }

    @Override
    public List<Parell<String,Integer>> getById_Grup (Integer id){
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_grup",id);
        List<Parell<String,Integer>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println("list:"+list);
        return list;
    }
}

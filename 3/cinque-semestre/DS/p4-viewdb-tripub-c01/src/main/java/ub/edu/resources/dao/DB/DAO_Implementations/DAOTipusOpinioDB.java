package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.resources.dao.DAO_Interfaces.DAOTipusOpinio;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;
import ub.edu.resources.dao.Quartet;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOTipusOpinioDB implements DAOTipusOpinio {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOTipusOpinioDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("TipusOpinio")){
            this.nameTable = "TipusOpinio";
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOTipusOpinioDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<Quartet<Integer,String,String,Boolean>> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<Quartet<Integer,String,String,Boolean>> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Quartet<Integer,String,String,Boolean> to = new Quartet<>();

            if(h.containsKey("id_tipusOpinio")){to.setElement1((Integer) h.get("id_tipusOpinio"));}
            else{to.setElement1(null);}

            if(h.containsKey("nom")){to.setElement2((String) h.get("nom"));}
            else{to.setElement2(null);}

            if(h.containsKey("comentari")){to.setElement3((String) h.get("comentari"));}
            else{to.setElement3(null);}

            if(h.containsKey("haveLike")){to.setElement4((Boolean) h.get("haveLike"));}
            else{to.setElement4(null);}

            list.add(to);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Quartet<Integer,String,String,Boolean>> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_tipusOpinio",Integer.valueOf(id));
        List<Quartet<Integer,String,String,Boolean>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 tipusOpinio con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Quartet<Integer,String,String,Boolean>> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Quartet<Integer,String,String,Boolean>> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Quartet<Integer,String,String,Boolean> tipusOpinio) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("nom",tipusOpinio.getElement2());
        hashmap.put("comentari",tipusOpinio.getElement3());
        hashmap.put("haveLike",tipusOpinio.getElement4());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            tipusOpinio.setElement1(id);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Quartet<Integer,String,String,Boolean> tipusOpinio, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_tipusOpinio",tipusOpinio.getElement1());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_tipusOpinio")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("id_tipusOpinio",tipusOpinio.getElement1());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_tipusOpinio");

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,array_AutoInc_PKs, true);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Quartet<Integer,String,String,Boolean> tipusOpinio) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_tipusOpinio",tipusOpinio.getElement1());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

}

package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.model.Localitat;
import ub.edu.resources.dao.DAO_Interfaces.DAOLocalitat;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOLocalitatDB implements DAOLocalitat {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOLocalitatDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Localitat")){
            this.nameTable = "Localitat";
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOLocalitatDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<Localitat> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<Localitat> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Localitat l = new Localitat();

            if(h.containsKey("id_lloc")){l.setId((Integer) h.get("id_lloc"));}
            else{l.setId(null);}

            if(h.containsKey("nom")){l.setNom((String) h.get("nom"));}
            else{l.setNom(null);}

            if(h.containsKey("altitud")){l.setAltitud((Double) h.get("altitud"));}
            else{l.setAltitud(null);}

            if(h.containsKey("latitud")){l.setLatitud((Double) h.get("latitud"));}
            else{l.setLatitud(null);}

            if(h.containsKey("longitud")){l.setLongitud((Double) h.get("longitud"));}
            else{l.setLongitud(null);}

            if(h.containsKey("id_comarca")){l.setIdComarca((Integer) h.get("id_comarca"));}
            else{l.setIdComarca(null);}

            list.add(l);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Localitat> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_lloc",Integer.valueOf(id));
        List<Localitat> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 localitat con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Localitat> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Localitat> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Localitat localitat) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("nom",localitat.getNom());
        hashmap.put("altitud",localitat.getAltitud());
        hashmap.put("latitud",localitat.getLatitud());
        hashmap.put("longitud",localitat.getLongitud());
        hashmap.put("id_comarca",localitat.getIdComarca());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            localitat.setId(id);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Localitat localitat, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_lloc",localitat.getId());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_lloc")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("id_lloc",localitat.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_lloc");

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,array_AutoInc_PKs, true);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Localitat localitat) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_lloc",localitat.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    public Optional<Localitat> getLocalitat (Localitat localitat) throws Exception{
        return this.getById(String.valueOf(localitat.getId()));
    }
}

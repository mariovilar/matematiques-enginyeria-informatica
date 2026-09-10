package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.model.Allotjament;
import ub.edu.resources.dao.DAO_Interfaces.DAOAllotjament;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOAllotjamentDB implements DAOAllotjament {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOAllotjamentDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Allotjament")){
            this.nameTable = "Allotjament";
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOAllotjamentDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<Allotjament> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<Allotjament> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Allotjament a = new Allotjament();

            if(h.containsKey("id_allotjament")){a.setId((Integer) h.get("id_allotjament"));}
            else{a.setId(null);}

            if(h.containsKey("nom")){a.setNom((String) h.get("nom"));}
            else{a.setNom(null);}

            if(h.containsKey("preuPerNit")){a.setPreuPerNit((Double) h.get("preuPerNit"));}
            else{a.setPreuPerNit(null);}

            if(h.containsKey("id_etapa")){a.setIdEtapa((Integer) h.get("id_etapa"));}
            else{a.setIdEtapa(null);}

            list.add(a);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Allotjament> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_allotjament",Integer.valueOf(id));
        List<Allotjament> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 allotjament con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Allotjament> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Allotjament> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Allotjament allotjament) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("nom",allotjament.getNom());
        hashmap.put("preuPerNit",allotjament.getPreuPerNit());
        hashmap.put("id_etapa",allotjament.getIdEtapa());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            allotjament.setId(id);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Allotjament allotjament, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_allotjament",allotjament.getId());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_allotjament")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("id_allotjament",allotjament.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_allotjament");

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,array_AutoInc_PKs, true);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Allotjament allotjament) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_allotjament",allotjament.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    public Optional<Allotjament> getAllotjament (Allotjament allotjament) throws Exception{
        return this.getById(String.valueOf(allotjament.getId()));
    }
}

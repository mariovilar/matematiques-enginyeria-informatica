package ub.edu.resources.dao.DB.DAO_Implementations;

import ub.edu.model.Persona;
import ub.edu.resources.dao.DAO_Interfaces.DAOPersona;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.util.*;

public class DAOPersonaDB implements DAOPersona {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOPersonaDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Persona")){
            this.nameTable = "Persona";
        }else{
            System.out.println("ERROR en DAOPersonaDB al intentar abrir el nombre de la tabla");
        }

    }

    private List<Persona> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<Persona> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Persona p = new Persona();

            if(h.containsKey("correu")){p.setCorreu((String) h.get("correu"));}
            else{p.setCorreu(null);}

            if(h.containsKey("nom")){p.setNom((String) h.get("nom"));}
            else{p.setNom(null);}

            if(h.containsKey("cognom")){p.setCognom((String) h.get("cognom"));}
            else{p.setCognom(null);}

            if(h.containsKey("dni")){p.setDni((String) h.get("dni"));}
            else{p.setDni(null);}

            if(h.containsKey("password")){p.setPwd((String) h.get("password"));}
            else{p.setPwd(null);}

            if(h.containsKey("id_tripUB")){p.setIdTripUB((Integer) h.get("id_tripUB"));}
            else{p.setIdTripUB(null);}

            list.add(p);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Persona> getById(String id) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Persona> getAll() throws Exception {
        //crud_persona.select_all_persona();
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Persona> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Persona persona) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("correu",persona.getCorreu());
        hashmap.put("nom",persona.getName());
        hashmap.put("cognom",persona.getCognom());
        hashmap.put("dni",persona.getDni());
        hashmap.put("password",persona.getPwd());
        hashmap.put("id_tripUB",persona.getIdTripUB());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,"correu",null);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Persona persona, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("correu",persona.getCorreu());
        hashmap_OLD.put("nom",persona.getName());
        hashmap_OLD.put("cognom",persona.getCognom());
        hashmap_OLD.put("dni",persona.getDni());
        hashmap_OLD.put("password",persona.getPwd());
        hashmap_OLD.put("id_tripUB",persona.getIdTripUB());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("entra");
            if(params[i].equals("correu")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("correu",persona.getCorreu());
        }

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW, null, false);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Persona persona) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("correu",persona.getCorreu());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    @Override
    public Persona findPersonaByCorreu(String correu) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(
                this.nameTable,"correu",correu);

        List<Persona> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 persona con el mismo correo");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return list.get(0);
        }
        return null;
    }
    @Override
    public Persona findPersonaByUserNameAndPassword(String usuari, String pwd) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_TwoParam_AND_OR_Table(
                this.nameTable,"nom",usuari,"AND","password",pwd);

        List<Persona> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 persona con el mismo nombre y contraseña");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return list.get(0);
        }
        return null;
    }
}

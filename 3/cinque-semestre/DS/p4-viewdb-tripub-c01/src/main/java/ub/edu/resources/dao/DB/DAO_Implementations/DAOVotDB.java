package ub.edu.resources.dao.DB.DAO_Implementations;

import ub.edu.model.Vot;
import ub.edu.resources.dao.DAO_Interfaces.DAOVot;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;
import ub.edu.resources.dao.Quartet;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOVotDB implements DAOVot {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    DAOTipusVotDB daoTipusVotDB;

    public DAOVotDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Vot")){
            this.nameTable = "Vot";
            this.daoTipusVotDB = new DAOTipusVotDB(this.connection);
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOVotDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<Vot> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr) throws Exception {
        List<Vot> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Vot v = new Vot();

            if(h.containsKey("id_vot")){v.setId((Integer) h.get("id_vot"));}
            else{v.setId(null);}

            //LocalDate
            if(h.containsKey("data")){
                if(h.get("data") == null || h.get("data") == "null"){
                    v.setData((LocalDate) null);
                }else{
                    String str_Time = (String) h.get("data");
                    LocalDate localDate = LocalDate.parse(str_Time, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    v.setData((LocalDate) localDate);
                }
            }
            else{v.setData((LocalDate) null);}

            if(h.containsKey("id_ruta")){v.setIdRuta((Integer) h.get("id_ruta"));}
            else{v.setIdRuta(null);}

            if(h.containsKey("correu_persona")){v.setCorreuPersona((String) h.get("correu_persona"));}
            else{v.setCorreuPersona(null);}

            if(h.containsKey("id_grup")){v.setIdGrup((Integer) h.get("id_grup"));}
            else{v.setIdGrup(null);}

            if(h.containsKey("id_tipusVot")){v.setIdTipusVot((Integer) h.get("id_tipusVot"));}
            else{v.setIdTipusVot(null);}

            actualizarVotConDataTipusVot(v);

            list.add(v);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Vot> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_vot",Integer.valueOf(id));
        List<Vot> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 vot con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Vot> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Vot> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Vot vot) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("data",vot.getData());
        hashmap.put("id_ruta",vot.getIdRuta());
        hashmap.put("correu_persona",vot.getCorreuPersona());
        hashmap.put("id_grup",vot.getIdGrup());
        hashmap.put("id_tipusVot",vot.getIdTipusVot());


        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            vot.setId(id);
            actualizarVotConDataTipusVot(vot);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Vot vot, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_vot",vot.getId());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_vot")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("id_vot",vot.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_vot");

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,array_AutoInc_PKs, true);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Vot vot) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_vot",vot.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    public Optional<Vot> getVot (Vot vot) throws Exception{
        return this.getById(String.valueOf(vot.getId()));
    }

    public void actualizarVotConDataTipusVot(Vot vot) throws Exception {
        //coger el id_tipusVot. Para ello, coger el elemnto tipusVot por el id -> llamar a tipusVot getById
        Quartet<Integer,String,Integer,Boolean> tipusVot = daoTipusVotDB.getById(vot.getIdTipusVot().toString()).get();
        //añadir parametros dle TipusVot en Vot
        String nom = tipusVot.getElement2();
        Integer valueEstrelles = tipusVot.getElement3();
        Boolean haveLike = tipusVot.getElement4();
        String STR_tipusVot = null;
        String STR_valorVot = null;
        if(haveLike!=null){
            System.out.println("DAOVotDB add haveLike != null -> true");
            STR_tipusVot = "Like";
            if(haveLike){
                STR_valorVot = "Like";
            }else{
                STR_valorVot = "Unlike";
            }
        }
        if(valueEstrelles!=null){
            System.out.println("DAOVotDB add valueEstrelles != null -> true");
            STR_tipusVot = "Estrelles";
            STR_valorVot = String.valueOf(valueEstrelles);
        }
        if(haveLike==null && valueEstrelles==null){
            System.out.println("DAOVotDB add haveLike == null && valueEstrelles == null --> true");
            STR_tipusVot = null;
            STR_valorVot = null;
        }
        vot.setTipusVot(STR_tipusVot);
        vot.setValorVot(STR_valorVot);
    }
}

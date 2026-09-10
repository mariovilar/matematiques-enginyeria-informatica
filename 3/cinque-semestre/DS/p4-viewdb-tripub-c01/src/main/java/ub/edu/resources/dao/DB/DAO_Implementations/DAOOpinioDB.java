package ub.edu.resources.dao.DB.DAO_Implementations;

import ub.edu.model.Opinio;
import ub.edu.resources.dao.DAO_Interfaces.DAOOpinio;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;
import ub.edu.resources.dao.Quartet;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOOpinioDB implements DAOOpinio {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    DAOTipusOpinioDB daoTipusOpinioDB;

    public DAOOpinioDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Opinio")){
            this.nameTable = "Opinio";
            this.daoTipusOpinioDB = new DAOTipusOpinioDB(this.connection);
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOOpinioDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<Opinio> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr) throws Exception {
        List<Opinio> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Opinio o = new Opinio();

            if(h.containsKey("id_opinio")){o.setId((Integer) h.get("id_opinio"));}
            else{o.setId(null);}

            if(h.containsKey("id_puntDePas")){o.setIdPuntDePas((Integer) h.get("id_puntDePas"));}
            else{o.setIdPuntDePas(null);}

            if(h.containsKey("id_tipusOpinio")){o.setIdTipusOpinio((Integer) h.get("id_tipusOpinio"));}
            else{o.setIdTipusOpinio(null);}

            if(h.containsKey("correu_persona")){o.setCorreuPersona((String) h.get("correu_persona"));}
            else{o.setCorreuPersona(null);}

            actualizarOpinioConDataTipusOpinio(o);

            list.add(o);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Opinio> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_opinio",Integer.valueOf(id));
        List<Opinio> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 opinio con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Opinio> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Opinio> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Opinio opinio) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_puntDePas",opinio.getIdPuntDePas());
        hashmap.put("id_tipusOpinio",opinio.getIdTipusOpinio());
        hashmap.put("correu_persona",opinio.getCorreuPersona());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            opinio.setId(id);
            actualizarOpinioConDataTipusOpinio(opinio);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Opinio opinio, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_opinio",opinio.getId());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_opinio")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("id_opinio",opinio.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_opinio");

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,array_AutoInc_PKs, true);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Opinio opinio) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_opinio",opinio.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    public Optional<Opinio> getOpinio (Opinio opinio) throws Exception{
        return this.getById(String.valueOf(opinio.getId()));
    }

    public void actualizarOpinioConDataTipusOpinio(Opinio opinio) throws Exception {
        //coger el id_tipusVot. Para ello, coger el elemnto tipusVot por el id -> llamar a tipusVot getById
        Quartet<Integer,String,String,Boolean> tipusVot = daoTipusOpinioDB.getById(opinio.getIdTipusOpinio().toString()).get();
        //añadir parametros dle TipusVot en Vot
        String nom = tipusVot.getElement2();
        String comentari = tipusVot.getElement3();
        Boolean haveLike = tipusVot.getElement4();
        String STR_tipusOpinio = null;
        String STR_valorOpinio = null;
        if(haveLike!=null){
            System.out.println("DAOOpinioDB add haveLike != null -> true");
            STR_tipusOpinio = "Like";
            if(haveLike){
                STR_valorOpinio = "Like";
            }else{
                STR_valorOpinio = "Unlike";
            }
        }
        if(comentari!=null){
            System.out.println("DAOOpinioDB add comentari != null -> true");
            STR_tipusOpinio = "Opinio";
            STR_valorOpinio = comentari;
        }
        if(haveLike==null && comentari==null){
            System.out.println("DAOOpinioDB add haveLike == null && comentari == null --> true");
            STR_tipusOpinio = null;
            STR_valorOpinio = null;
        }
        opinio.setTipusOpinio(STR_tipusOpinio);
        opinio.setValorOpinio(STR_valorOpinio);
    }
}

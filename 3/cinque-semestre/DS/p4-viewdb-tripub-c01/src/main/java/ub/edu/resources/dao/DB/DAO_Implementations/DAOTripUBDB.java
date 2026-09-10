package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.model.TripUB;
import ub.edu.resources.dao.DAO_Interfaces.DAOTripUB;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOTripUBDB implements DAOTripUB {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOTripUBDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("TripUB")){
            this.nameTable = "TripUB";
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOTripUBDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<TripUB> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<TripUB> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            TripUB t = new TripUB();

            if(h.containsKey("id_tripUB")){t.setId((Integer) h.get("id_tripUB"));}
            else{t.setId(null);}

            list.add(t);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<TripUB> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_tripUB",Integer.valueOf(id));
        List<TripUB> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 grup con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<TripUB> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<TripUB> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(TripUB tripUB) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        // TripUB es un caso especial pues solo tiene 1 columna y es PK autoincremental
        // por lo que la sentencia es:
        // sql: "INSERT INTO TripUB VALUES ();"
        // sqlite: "INSERT INTO TripUB DEFAULT VALUES;"

        boolean resu= this.crud_tables.insert(this.nameTable,null,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            tripUB.setId(id);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(TripUB tripUB, String[] params) throws Exception {
        System.out.println("Metodo update NO está permitido para "+this.nameTable+". " +
                "Debido a que unicamente tiene 1 columa y es PK autoincremental");
        return false;
    }

    @Override
    public boolean delete(TripUB tripUB) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_tripUB",tripUB.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    public Optional<TripUB> getTripUB (TripUB tripUB) throws Exception{
        return this.getById(String.valueOf(tripUB.getId()));
    }
}

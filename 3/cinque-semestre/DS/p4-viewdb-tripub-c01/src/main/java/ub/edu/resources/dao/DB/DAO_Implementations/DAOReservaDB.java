package ub.edu.resources.dao.DB.DAO_Implementations;

import ub.edu.model.Reserva;
import ub.edu.resources.dao.DAO_Interfaces.DAOReserva;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOReservaDB implements DAOReserva {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAOReservaDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Reserva")){
            this.nameTable = "Reserva";
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOReservaDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<Reserva> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr) throws ParseException {
        List<Reserva> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Reserva r = new Reserva();

            if(h.containsKey("id_reserva")){r.setId((Integer) h.get("id_reserva"));}
            else{r.setId(null);}

            //LocalDate
            if(h.containsKey("dateCheckIn")){
                if(h.get("dateCheckIn") == null || h.get("dateCheckIn") == "null"){
                    r.setDateCheckIn((LocalDate) null);
                }else{
                    String str_Time = (String) h.get("dateCheckIn");
                    LocalDate localDate = LocalDate.parse(str_Time, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    r.setDateCheckIn((LocalDate) localDate);
                }
            }
            else{r.setDateCheckIn((LocalDate) null);}

            //LocalDate
            if(h.containsKey("dateCheckOut")){
                if(h.get("dateCheckOut") == null || h.get("dateCheckOut") == "null"){
                    r.setDateCheckOut((LocalDate) null);
                }else{
                    String str_Time = (String) h.get("dateCheckOut");
                    LocalDate localDate = LocalDate.parse(str_Time, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    r.setDateCheckOut((LocalDate) localDate);
                }
            }
            else{r.setDateCheckOut((LocalDate) null);}

            if(h.containsKey("tipusPagament")){r.setTipusPagament((String) h.get("tipusPagament"));}
            else{r.setTipusPagament(null);}

            if(h.containsKey("correu_persona")){r.setCorreuPersona((String) h.get("correu_persona"));}
            else{r.setCorreuPersona(null);}

            if(h.containsKey("id_allotjament")){r.setIdAllotjament((Integer) h.get("id_allotjament"));}
            else{r.setIdAllotjament(null);}

            list.add(r);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Reserva> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_reserva",Integer.valueOf(id));
        List<Reserva> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 reserva con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Reserva> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Reserva> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Reserva reserva) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("dateCheckIn",reserva.getDateCheckIn());
        hashmap.put("dateCheckOut",reserva.getDateCheckOut());
        hashmap.put("tipusPagament",reserva.getTipusPagament());
        hashmap.put("correu_persona",reserva.getCorreuPersona());
        hashmap.put("id_allotjament",reserva.getIdAllotjament());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            reserva.setId(id);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Reserva reserva, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_reserva",reserva.getId());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_reserva")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("id_reserva",reserva.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_reserva");

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,array_AutoInc_PKs, true);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Reserva reserva) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_reserva",reserva.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    public Optional<Reserva> getRuta (Reserva reserva) throws Exception{
        return this.getById(String.valueOf(reserva.getId()));
    }
}

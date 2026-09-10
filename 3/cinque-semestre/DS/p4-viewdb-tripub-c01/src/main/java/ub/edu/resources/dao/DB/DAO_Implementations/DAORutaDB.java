package ub.edu.resources.dao.DB.DAO_Implementations;
import ub.edu.model.Ruta;
import ub.edu.resources.dao.DAO_Interfaces.DAORuta;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DAORutaDB implements DAORuta {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;
    private String nameTable;

    public DAORutaDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        if(this.array_AvailableTablesNames.contains("Ruta")){
            this.nameTable = "Ruta";
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAORutaDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<Ruta> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr) throws ParseException {
        List<Ruta> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> h = (HashMap<Object,Object>) obj;
            Ruta r = new Ruta();

            if(h.containsKey("id_ruta")){r.setId((Integer) h.get("id_ruta"));}
            else{r.setId(null);}

            if(h.containsKey("nom")){r.setNom((String) h.get("nom"));}
            else{r.setNom(null);}

            if(h.containsKey("dataCreacio")){
                if(h.get("dataCreacio") == null || h.get("dataCreacio") == "null"){
                    r.setDataCreacio((LocalDate) null);
                }else{
                    String str_Time = (String) h.get("dataCreacio");
                    LocalDate localDate = LocalDate.parse(str_Time, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    r.setDataCreacio((LocalDate) localDate);
                }
            }
            else{r.setDataCreacio((String) null);}

            if(h.containsKey("durada")){r.setDurada((Integer) h.get("durada"));}
            else{r.setDurada(null);}

            if(h.containsKey("descripcio")){r.setDescripcio((String) h.get("descripcio"));}
            else{r.setDescripcio(null);}

            if(h.containsKey("cost")){r.setCost((Double) h.get("cost"));}
            else{r.setCost(null);}

            if(h.containsKey("distancia")){r.setDistancia((Double) h.get("distancia"));}
            else{r.setCost(null);}

            if(h.containsKey("dificultat")){r.setDificultat((String) h.get("dificultat"));}
            else{r.setDificultat(null);}

            if(h.containsKey("tipusRuta")){r.setTipusRuta((String) h.get("tipusRuta"));}
            else{r.setTipusRuta(null);}

            if(h.containsKey("id_lloc_origen")){r.setIdLlocOrigen((Integer) h.get("id_lloc_origen"));}
            else{r.setIdLlocOrigen(null);}

            if(h.containsKey("id_lloc_desti")){r.setIdLlocDesti((Integer) h.get("id_lloc_desti"));}
            else{r.setIdLlocDesti(null);}


            list.add(r);
            //System.out.println(p);
        }
        return list;
    }

    @Override
    public Optional<Ruta> getById(String id) throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_ruta",Integer.valueOf(id));
        List<Ruta> list = this.convertir_array_de_Hasmaps_a_List(arr);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 ruta con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Ruta> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<Ruta> list = this.convertir_array_de_Hasmaps_a_List(arr);
        System.out.println(list);
        return list;
    }

    @Override
    public boolean add(Ruta ruta) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("nom",ruta.getNom());
        hashmap.put("dataCreacio",ruta.getDataCreacio());
        hashmap.put("durada",ruta.getDurada());
        hashmap.put("descripcio",ruta.getDescripcio());
        hashmap.put("cost",ruta.getCost());
        hashmap.put("distancia",ruta.getDistancia());
        hashmap.put("dificultat",ruta.getDificultat());
        hashmap.put("tipusRuta",ruta.getTipusRuta());
        hashmap.put("id_lloc_origen",ruta.getIdLlocOrigen());
        hashmap.put("id_lloc_desti",ruta.getIdLlocDesti());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu){
            //si lo ha insertado correctamete, entonces
            Integer id = this.crud_tables.id_value_of_last_PKs_autoincrementales_on_tabla(this.nameTable);
            ruta.setId(id);
        }
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean update(Ruta ruta, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_ruta",ruta.getId());

        HashMap<Object, Object> hashmap_NEW = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_ruta")){
                flag_pk = true;
            }
            hashmap_NEW.put(params[i],params[i+1]);
        }
        if(!flag_pk){
            hashmap_NEW.put("id_ruta",ruta.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_ruta");

        boolean resu = this.crud_tables.update(this.nameTable,hashmap_OLD,hashmap_NEW,array_AutoInc_PKs, true);
        System.out.println(resu);
        return resu;
    }

    @Override
    public boolean delete(Ruta ruta) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_ruta",ruta.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        return resu;
    }

    public Optional<Ruta> getRuta (Ruta ruta) throws Exception{
        return this.getById(String.valueOf(ruta.getId()));
    }
}

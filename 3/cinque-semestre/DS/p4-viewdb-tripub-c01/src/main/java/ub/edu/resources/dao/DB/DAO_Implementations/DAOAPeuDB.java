package ub.edu.resources.dao.DB.DAO_Implementations;

import ub.edu.model.Transport;
import ub.edu.model.aPeu;
import ub.edu.resources.dao.DAO_Interfaces.DAOAPeu;
import ub.edu.resources.dao.DB.Data.CRUD_Tables;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DAOAPeuDB implements DAOAPeu {

    private final CRUD_Tables crud_tables;
    private final Connection connection;
    private final ArrayList<Object> array_AvailableTablesNames;

    private ArrayList<Object> columns_parametrosTabla;
    private String nameTable;
    DAOTransportDB daoTransportDB;

    public DAOAPeuDB(Connection c) {
        this.crud_tables = new CRUD_Tables(c);
        this.connection = c;
        this.array_AvailableTablesNames = this.crud_tables.obtener_nombre_de_todas_tablas();
        //System.out.println("array_AvailableTablesNames:"+array_AvailableTablesNames);

        this.columns_parametrosTabla=new ArrayList<>();

        if(this.array_AvailableTablesNames.contains("APeu")){
            this.nameTable = "APeu";
            this.daoTransportDB = new DAOTransportDB(this.connection);
            this.columns_parametrosTabla = this.crud_tables.obtener_nombre_de_todas_columnas(this.nameTable);
        }else{
            System.out.println("\n*****************************************************************");
            System.out.println("ERROR en DAOaPeuDB al intentar abrir el nombre de la tabla");
            System.out.println("*****************************************************************");
        }
    }

    private List<aPeu> convertir_array_de_Hasmaps_a_List(ArrayList<Object> arr){
        List<aPeu> list = new ArrayList<>();
        for(Object obj: arr){
            HashMap<Object,Object> hashMap = (HashMap<Object,Object>) obj;
            aPeu p = new aPeu();

            if(hashMap.containsKey("id_peu")){p.setId((Integer) hashMap.get("id_peu"));}
            else{p.setId(null);}

            list.add(p);
            //System.out.println(p);
        }
        return list;
    }

    private void rellenarMadreEnLaHija(Transport madre, aPeu hija){
        //el id no hace falta pues ya lo tiene puesto que es suyo y ya se lo asigna el propio crud
        //el id de la madre en la hija, al ser override nos da igual pues cogemos el de la hija
        hija.setVelocitatEstimada(madre.getVelocitatEstimada());
        hija.setNomTransport(madre.getNomTransport());
    }

    @Override
    public Optional<aPeu> getById(String id) throws Exception {

        Optional<Transport> resuMadre = this.daoTransportDB.getById(id);

        Transport transport = resuMadre.get();
        //System.out.println("allotjament:"+allotjament);


        ArrayList<Object> arr = this.crud_tables.select_OneParam(this.nameTable,"id_peu",Integer.valueOf(id));
        //System.out.println("arr:"+arr);
        List<aPeu> list = this.convertir_array_de_Hasmaps_a_List(arr);
        //System.out.println("list:"+list);
        if(list.size()>1){
            System.out.println("ERROR: Existe mas de 1 aPeu con el mismo id");
        }
        if(!list.isEmpty()){
            System.out.println(list);
            this.rellenarMadreEnLaHija(transport,list.get(0));
            return Optional.ofNullable(list.get(0));
        }

        return Optional.empty();
    }

    @Override
    public List<aPeu> getAll() throws Exception {
        ArrayList<Object> arr = this.crud_tables.select_all(this.nameTable);
        List<aPeu> list = this.convertir_array_de_Hasmaps_a_List(arr);
        List<aPeu> listFinal = new ArrayList<>();
        for (aPeu apeu: list) {
            //cada aPeu de la lista tiene la madre vacia
            //para poner los datos de la madre dentro de la madre de la hija
            //utilizar el getById que ya lo hace
            aPeu apeuFinal = this.getById(apeu.getId().toString()).get();
            listFinal.add(apeuFinal);
        }
        System.out.println(listFinal);
        return listFinal;
    }

    @Override
    public boolean add(aPeu peu) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();

        //como es una herencia, primero hay que añadir la clase madre
        boolean resuMadre = this.daoTransportDB.add((Transport) peu);
        Transport transport = this.daoTransportDB.getTransport((Transport) peu).get();
        System.out.println("transport:"+transport);
        System.out.println("peu:"+peu);
        if(resuMadre){
            //si es true->no existia antes la madre, por lo que lo ha añdido
            // recuperar el id del nuevo elemento y ponerselo al hijo
            hashmap.put("id_peu",transport.getId());
        } else {
            //si es false->ya existiade antes la madre,por lo que no se ha añadido
            //preguntar por el id haciendo un select

            System.out.println("ERROR: exepcion extraña en el add CasaRural");
            //nunca va a ser false porque la madre, la pk es autoincremental
        }
        //hotel.setId(allotjament.getId());
        boolean resu= this.crud_tables.insert(this.nameTable,hashmap,null,null);
        // si la pk es un id autoincremental, entonces una vez añadido en la DB
        // hay que recogerlo de allí y saber su id y actualizar el obj.
        if(resu) {
            //si lo ha insertado correctamete, entonces actualizar el id con el de la madre que es autoInc
            peu.setId(transport.getId());
        }
        System.out.println(resu);
        return resu;
    }


    @Override
    public boolean update(aPeu peu, String[] params) throws Exception {
        HashMap<Object, Object> hashmap_OLD = new HashMap<Object, Object>();
        hashmap_OLD.put("id_peu",peu.getId());

        HashMap<Object, Object> hashmap_NEW_hija = new HashMap<Object, Object>();
        HashMap<Object, Object> hashmap_NEW_madre = new HashMap<Object, Object>();
        boolean flag_pk = false;
        //System.out.println(params.length);
        for (int i = 0; i < params.length; i=i+2) {
            //System.out.println("params[i]"+params[i]);
            //System.out.println("params[i+1]"+params[i+1]);
            if(params[i].equals("id_peu")){
                flag_pk = true;
            }
            if(this.columns_parametrosTabla.contains(params[i])){
                //si la contiene, significa que es de la hija
                hashmap_NEW_hija.put(params[i],params[i+1]);
            }else{
                //sino la contiene
                //siginica que queremos hacer el update de algo de la madre
                hashmap_NEW_madre.put(params[i],params[i+1]);
            }
        }
        if(!flag_pk){
            hashmap_NEW_hija.put("id_peu",peu.getId());
        }

        ArrayList<Object> array_AutoInc_PKs = new ArrayList<>();
        array_AutoInc_PKs.add("id_peu");

        boolean resu = false;
        if(hashmap_NEW_hija.size()==1 && !flag_pk){
            //no hay nada que hacer el update
            //solo ha entrado por el id. pero al ser pk_fk de la madre, no hay que hacer update
        }else {
             resu = this.crud_tables.update(this.nameTable, hashmap_OLD, hashmap_NEW_hija, array_AutoInc_PKs, true);
        }
        System.out.println(resu);

        ArrayList<Object> arrayKeysMadre_finalHasMap = new ArrayList<>(hashmap_NEW_madre.keySet());
        if(arrayKeysMadre_finalHasMap.size()==0){
            return resu; //no hace falta valorar la madre porque no la mandamos a hacer el update
        }

        int n = arrayKeysMadre_finalHasMap.size();
        String[] newArray = new String[n*2];

        // el array de keys se incrementa de 1 en 1.
        int count = 0;
        // el hasmap se incrementa de 2 en 2 pues cogemos key, value
        for (int i = 0; i < newArray.length; i=i+2) {
            //key
            newArray[i] = (String) arrayKeysMadre_finalHasMap.get(count);
            //value
            newArray[i+1] = (String) hashmap_NEW_madre.get(arrayKeysMadre_finalHasMap.get(count));
            count++;
        }

        boolean resuMadre = this.daoTransportDB.update((Transport) peu, newArray);
        System.out.println(resuMadre);
        return (resu && resuMadre);
    }

    @Override
    public boolean delete(aPeu peu) throws Exception {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        hashmap.put("id_peu",peu.getId());
        boolean resu = this.crud_tables.delete(this.nameTable,hashmap);
        System.out.println(resu);
        //una vez borrada la hija, tambien borramos la madre
        boolean resuMadre = this.daoTransportDB.delete((Transport) peu);

        return (resu && resuMadre);
    }
}

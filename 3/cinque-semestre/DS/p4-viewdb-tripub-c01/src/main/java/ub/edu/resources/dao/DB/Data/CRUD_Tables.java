package ub.edu.resources.dao.DB.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import static org.apache.commons.lang3.math.NumberUtils.isParsable;

public class CRUD_Tables {

    Connection c;
    Statement stmt = null;
    public CRUD_Tables(Connection c) {
        this.c = c;
    }

    public ArrayList<Object> iteration_rsNext(ResultSet rs, String nameTable){
        ArrayList<Object> array = new ArrayList<>();
        HashMap<Object, Object> hashmap = this.select_tablename(nameTable);
        ArrayList<Object> arrayKeys = new ArrayList<>(hashmap.keySet());
        HashMap<Object, Object> resu = new HashMap<>();
        boolean flag = false;
        try{
            while ( rs.next() ) {
                // correu,nom,cognom,dni,password,TripUB_idTripUB
                resu = new HashMap<>();
                int i = 0;
                for (Object key : arrayKeys) {
                    //System.out.println("key: "+key);
                    //System.out.println("hashmap.get(key): {"+hashmap.get(key)+"}");
                    if(hashmap.get(key).equals("TEXT")){
                        resu.put(key,rs.getString((String) key));
                    }else if(hashmap.get(key).equals("INTEGER")){
                        //System.out.println("entro2");
                        resu.put(key,rs.getInt((String) key));
                    }else if(hashmap.get(key).equals("REAL")){
                        //System.out.println("entro2");
                        resu.put(key,rs.getDouble((String) key));
                        resu.put(key,rs.getDouble((String) key));
                    }else if(hashmap.get(key).equals("NUMERIC")){
                        //numeric representa un conjunto muy grande de valores
                        // ya sea boleanos, enums, fechas...
                        Object obj = rs.getString((String) key);
                        //System.out.println(obj);
                        if(obj instanceof Boolean || (obj instanceof String && (obj.equals("1") || obj.equals("0")))){
                            //true-false-0-1
                            //0 y 1 los coge como boleanos y no integers. Pero tambien puede cogerlos como string
                            //porque estamos en el caso numerico
                            resu.put(key,rs.getBoolean((String) key));
                        }else if(obj instanceof LocalDate){
                            resu.put(key,rs.getDate((String) key));
                        }else if(obj instanceof Date){
                            resu.put(key,rs.getDate((String) key));
                        }else if(obj instanceof LocalTime){
                            resu.put(key,rs.getDate((String) key));
                        }else if(obj instanceof String){
                            //nunca entra en LocalDate, Date o LocalTime porque lo coge como cadena string
                            resu.put(key,rs.getString((String) key));
                        }else if(obj==null){
                            resu.put(key,null);
                        }
                        else{
                            System.out.println("**********************************************************");
                            System.out.println("**Tipo de dato no añadido en NUMERIC iteration_rsNext*****");
                            System.out.println("**********************************************************");
                        }
                    }else{
                        System.out.println("*************************************************");
                        System.out.println("**Tipo de dato no añadido en iteration_rsNext*****");
                        System.out.println("**************************************************");
                    }
                    i++;
                    //System.out.println("resu: "+resu);
                }
                array.add(resu);
            }
        } catch ( Exception e ) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
        System.out.println("nameMethod:" + nameMethod);

        //System.exit(0);
        }
        return array;
    }

    private ArrayList<Object> lista_PKs_tabla(String nameTable) {
        ArrayList<Object> lista_PKs = new ArrayList<>();
        try{
            stmt = this.c.createStatement();
            String sql = "SELECT name,pk  FROM pragma_table_info(\""+nameTable+"\");";
            //System.out.println(stmt.executeQuery(sql));
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                String resuKey = rs.getString("name");
                String resuPk = rs.getString("pk");
                //System.out.println(resuPk.getClass()); -> siempre será String
                if(resuPk.equals("1")){
                    lista_PKs.add(resuKey);
                }
            }
            //System.out.println("lista_PKs:"+lista_PKs);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return lista_PKs;
    }


    private boolean check_PKs_autoincrementales_on_tabla(String nameTable) {
        boolean flag=false;
        try{
            stmt = this.c.createStatement();
            String sql = "SELECT * FROM sqlite_sequence where name=\""+nameTable+"\";";
            //System.out.println(stmt.executeQuery(sql));
            ResultSet rs = stmt.executeQuery(sql);
            if ( rs.next() ) {
                flag=true;
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return flag;
    }


    public Integer id_value_of_last_PKs_autoincrementales_on_tabla(String nameTable) {
        int id;
        String resuValue = "";
        try{
            stmt = this.c.createStatement();
            String sql = "SELECT * FROM sqlite_sequence where name=\""+nameTable+"\";";
            //System.out.println(stmt.executeQuery(sql));
            ResultSet rs = stmt.executeQuery(sql);
            if ( rs.next() ) {
                resuValue = rs.getString("seq");
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        if(Objects.equals(resuValue, "")){
            return null;
        }
        id=Integer.parseInt(resuValue);
        //el resultado, el valor del id es el del ultimo elemento. Por tanto, devolverlo tal cual
        return id;
    }

    private boolean check_if_all_pks_are_on_hashmap (String nameTable, HashMap<Object,Object> hashMap){
        ArrayList<Object> lista_PKs = this.lista_PKs_tabla(nameTable);
        for (Object pk_list: lista_PKs) {
            if(!hashMap.containsKey(pk_list)){
                return false;
            }
        }
        return true;
    }

    private ArrayList<Object> array_of_all_pks_are_on_hashmap (String nameTable, HashMap<Object,Object> hashMap){
        ArrayList<Object> lista_PKs = this.lista_PKs_tabla(nameTable);
        ArrayList<Object> resu = new ArrayList<Object>();
        for (Object pk_list: lista_PKs) {
            if(hashMap.containsKey(pk_list)){
                resu.add(pk_list);
            }
        }
        return resu;
    }


    public HashMap<Object, Object> select_tablename(String nameTable) {
        HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
        try{
            stmt = this.c.createStatement();
            String sql = "SELECT name,type  FROM pragma_table_info(\""+nameTable+"\");";
            //System.out.println(stmt.executeQuery(sql));
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                String resuKey = rs.getString("name");
                String resuValue = rs.getString("type");
                hashmap.put(resuKey,resuValue);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return hashmap;
    }

    public ArrayList<Object> select_all(String nameTable) {
        ArrayList<Object> array = new ArrayList<>();
        try{
            stmt = this.c.createStatement();
            String sql = "SELECT * FROM "+nameTable+";";
            ResultSet rs = stmt.executeQuery(sql);
            array=this.iteration_rsNext(rs,nameTable);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return array;
    }

    public boolean insert(String nameTable, HashMap<Object,Object> hashMap, String pk_name1, String pk_name2) {
        // caso especial de TripUB. Solo hay un 1 columna, y es PK autoincremental.
        // la sentencia no puede ser: "INSERT INTO TripUB () VALUES ();"
        // sino que en SQL deberia ser: "INSERT INTO TripUB VALUES ();"
        // pero para SQLite tiene que ser: "INSERT INTO TripUB DEFAULT VALUES;"

        if(hashMap==null){
            //System.out.println("Entramos en caso especial");
            //entramos en caso especial de TripUB
            String sql = "INSERT INTO "+nameTable+" DEFAULT VALUES;";
            boolean flag=false;
            try{
                System.out.println("sql: "+sql);
                stmt.executeUpdate(sql);
                stmt.close();
                c.commit();
                flag=true;
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
                System.out.println("nameMethod:" + nameMethod);
                //System.exit(0);
            }
            return flag;
        }
        // correu,nom,cognom,dni,password,TripUB_idTripUB
        boolean flag = false;
        ArrayList<Object> array = null;
        boolean tabla_contains_pks_autoincrement = check_PKs_autoincrementales_on_tabla(nameTable);

        if(!tabla_contains_pks_autoincrement) {
            if(check_if_all_pks_are_on_hashmap(nameTable,hashMap)){
                // primero mirar si hay alguna otra fila con la misma pk (correu)
                if (pk_name2 != null) {
                    array = this.select_TwoParam_AND_OR_Table(nameTable, pk_name1, hashMap.get(pk_name1), "AND", pk_name2, hashMap.get(pk_name2));
                } else {
                    //caso especial de herencia, pk_name1 and pk_name2 son nulas
                    if(pk_name1 == null && pk_name2 ==null){
                        array = null;
                    }else {
                        array = this.select_OneParam(nameTable, pk_name1, hashMap.get(pk_name1));
                    }
                }
                if (array != null && array.size() > 0) {
                    return flag;
                }
            }else{
                return false;
            }
        }
        // en caso de que no haya alguna otra fila con la misma pk (correu), entonces añadir la nueva fila
        try{
            stmt = this.c.createStatement();
            // los strings/text/varchar deben ir entre comillas, ejemplo:
            /*
            INSERT INTO direccio (carrer, numero, poblacio, codi_postal, fk_correuPersona)
            VALUES ("Plaza Joan D'espi",33,"Barcelona",08003,"ajaleo@gmail.com");
            */
            String sql = "INSERT INTO "+nameTable+" (";
            boolean flag_first = true;
            ArrayList<Object> hashKeys = new ArrayList<Object> (hashMap.keySet());
            for (Object key : hashKeys) {
                if (flag_first){
                    flag_first=false;
                    sql=sql+key;
                }else {
                    sql=sql+","+key;
                }
            }
            sql=sql+") VALUES (";
            flag_first = true;
            for (Object key : hashKeys) {
                String sql1;
                Object obj = hashMap.get(key);
                if(obj instanceof String){
                    sql1= "\""+obj+"\"";
                    //System.out.println("sql1 String: " + sql1);
                }else if (obj instanceof LocalTime){
                    sql1= "\""+obj+"\"";
                    //System.out.println("sql1 NO String: " + sql1);
                }else if (obj instanceof LocalDate){
                    sql1= "\""+obj+"\"";
                    //System.out.println("sql1 NO String: " + sql1);
                }else{
                    sql1= ""+obj;
                    //System.out.println("sql1 NO String: " + sql1);
                }
                if (flag_first){
                    flag_first=false;
                    sql=sql+sql1;
                }else {
                    sql=sql+","+sql1;
                }
                //System.out.println("sql: "+sql);
            }
            sql=sql+");";
            System.out.println("sql: "+sql);
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            flag = true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return flag;
    }

    public boolean update(String nameTable, HashMap<Object,Object> hashMap_OLD_ONLY_PK_Data, HashMap<Object,Object> hashMap_NEW_Data, ArrayList<Object> arrayAutoIncPks_or_pk_is_fk_of_pkAutoInc, boolean pk_isAutoInc_or_pk_is_fk_of_pkAutoInc_Herencia) {
        // correu,nom,cognom,dni,password,TripUB_idTripUB
        boolean flag = false;
        ArrayList<Object> hashKeys_old = new ArrayList<Object> (hashMap_OLD_ONLY_PK_Data.keySet());
        ArrayList<Object> hashKeys_new = new ArrayList<Object> (hashMap_NEW_Data.keySet());
        ArrayList<Object> array = null;
        boolean tabla_contains_pks_autoincrement = check_PKs_autoincrementales_on_tabla(nameTable);

        //System.out.println("hashKeys_old:"+hashKeys_old);
        //System.out.println("hashKeys_old.size():"+hashKeys_old.size());

        if(!tabla_contains_pks_autoincrement) {
            //en el update no se tiene que hacer por si se quiere actulizar aplicando otros filtros
            //if(check_if_all_pks_are_on_hashmap(nameTable,hashMap_OLD_ONLY_PK_Data)){
                //Paso 1: mirar si existe el elemento viejo que queremos actualizar
                if(hashKeys_old.size()==0){
                    //System.out.println("hashKeys_old.size()==0");
                    return flag;
                } else if (hashKeys_old.size() == 1) {
                    //System.out.println("Entra1 hashKeys_old.size() == 1");
                    array = this.select_OneParam(nameTable, (String) hashKeys_old.get(0),hashMap_OLD_ONLY_PK_Data.get(hashKeys_old.get(0)));
                } else if (hashKeys_old.size() == 2) {
                    //System.out.println("Entra1 hashKeys_old.size() == 2");
                    array = this.select_TwoParam_AND_OR_Table(nameTable, (String) hashKeys_old.get(0),hashMap_OLD_ONLY_PK_Data.get(hashKeys_old.get(0)),
                                                        "AND", (String) hashKeys_old.get(1),hashMap_OLD_ONLY_PK_Data.get(hashKeys_old.get(1)));
                } /*else if (hashKeys_old.size() == 3) {
                    //System.out.println("Entra1 hashKeys_old.size() == 3");
                    array = this.select_ThreeParam_AND_OR_1_Table(nameTable, (String) hashKeys_old.get(0), hashMap_OLD_ONLY_PK_Data.get(hashKeys_old.get(0)),
                                                            "AND", (String) hashKeys_old.get(1),hashMap_OLD_ONLY_PK_Data.get(hashKeys_old.get(1)),
                                                            "AND", (String) hashKeys_old.get(2),hashMap_OLD_ONLY_PK_Data.get(hashKeys_old.get(2)));
                }*/else if (hashKeys_old.size() > 2 ){
                    if(this.exists_select_AllAND_AllParams_of_HasMap(nameTable,hashMap_OLD_ONLY_PK_Data)){
                        array = new ArrayList<>();
                        array.add("pass");
                    }
                }
                if(array==null || array.size() == 0){
                    //System.out.println("Entra1 array==null || array.size() == 0");
                    return flag;
                }
            /*}else{
                return false;
            }*/
        }

        //verificar que actualiza la pk y ya exista
        //ArrayList<Object> lista_pks = this.lista_PKs_tabla(nameTable);
        HashMap<Object,Object> finalHasMap = this.fusion_Hasmaps_NuevoSobreescribeViejo(nameTable,
                                                                hashMap_OLD_ONLY_PK_Data,hashMap_NEW_Data,arrayAutoIncPks_or_pk_is_fk_of_pkAutoInc);

        ArrayList<Object> arrayKeys_finalHasMap = new ArrayList<>(finalHasMap.keySet());

        if(pk_isAutoInc_or_pk_is_fk_of_pkAutoInc_Herencia) {
            //al ser o depender de una pk autoincremenetal
            //si lo evaluamos estariamos evaluando campos que si pueden repetirse
            //pues el unico que debe ser unico, es la pk
            //en caso de que algun otro campo deba ser unico, habría que ver-> TODO? no me acuerdo si ya está fix, pero apostaría que no
            // do nothing
            array=null;
        }else{
            if (arrayKeys_finalHasMap.size() == 1) {
                //System.out.println("Entra2 hashKeys_old.size() == 1");
                array = this.select_OneParam(nameTable, (String) arrayKeys_finalHasMap.get(0),finalHasMap.get(arrayKeys_finalHasMap.get(0)));
            } else if (arrayKeys_finalHasMap.size() == 2) {
                //System.out.println("Entra2 hashKeys_old.size() == 2");
                array = this.select_TwoParam_AND_OR_Table(nameTable, (String) arrayKeys_finalHasMap.get(0),finalHasMap.get(arrayKeys_finalHasMap.get(0)),
                        "AND", (String) arrayKeys_finalHasMap.get(1),finalHasMap.get(arrayKeys_finalHasMap.get(1)));
            } /*else if (arrayKeys_finalHasMap.size() == 3) {
                //System.out.println("Entra2 hashKeys_old.size() == 3");
                array = this.select_ThreeParam_AND_OR_1_Table(nameTable, (String) arrayKeys_finalHasMap.get(0), finalHasMap.get(arrayKeys_finalHasMap.get(0)),
                        "AND", (String) arrayKeys_finalHasMap.get(1),finalHasMap.get(arrayKeys_finalHasMap.get(1)),
                        "AND", (String) arrayKeys_finalHasMap.get(2),finalHasMap.get(arrayKeys_finalHasMap.get(2)));
            } */else if (arrayKeys_finalHasMap.size() >2){
                if(!this.exists_select_AllAND_AllParams_of_HasMap(nameTable,finalHasMap)){
                    array = null;
                }
            }
        }

        //System.out.println("array.size():"+array.size());
        if(array!=null &&  array.size() > 0){
            //System.out.println("Entra2 array==null || array.size() == 0");
            return flag;
        }


        //Paso3: en caso de que exista una fila con los viejos datos, y no exista ninguna otra con
        // los nuevos datos, coger la vieja fila y actualizar con los nuevos datos
        try{
            stmt = this.c.createStatement();
            // los strings/text/varchar deben ir entre comillas
            // EL PRIMER ESPACIO ES FUNDAMENTAL PARA QUE NO SE PEGUEN y van separados por comas


            String sql = "UPDATE "+nameTable+" set";
            boolean flag_first = true;
            for (Object key : arrayKeys_finalHasMap) {
                String sql1;
                Object obj = null;
                if (hashMap_NEW_Data.get(key) != null) {
                    obj = hashMap_NEW_Data.get(key);
                } else {
                    //caso especial para persona pues su id no es un integer PK sino un String
                    obj = hashMap_OLD_ONLY_PK_Data.get(key);
                }
                sql1 = " " + key + " =";
                // a diferencia del isNumeric, el isParsable prueba con todos: integer, double...
                if(obj instanceof Integer){
                    //caso epecial para persona, si le pasas un integer peta al cast String del isParsable
                    sql1= sql1+""+obj;
                }else if(isParsable((String) obj)){
                    // si no se añade, lo que pasa es que los que son numericos
                    // integer, double... los coge siempre como string
                    sql1= sql1+""+obj;
                }else if(obj instanceof String){
                    sql1= sql1+"\""+obj+"\"";
                    //System.out.println("sql1 String: " + sql1);
                }else{
                    sql1= sql1+""+obj;
                    //System.out.println("sql1 NO String: " + sql1);
                }
                if (flag_first){
                    flag_first=false;
                    sql=sql+" "+sql1;
                }else {
                    sql=sql+","+sql1;
                }
                //System.out.println("sql: "+sql);
            }
            sql=sql+" WHERE ";
            flag_first = true;
            for (Object key : hashKeys_old) {
                String sql1;
                Object obj = hashMap_OLD_ONLY_PK_Data.get(key);
                sql1=" "+key+" =";
                if(obj instanceof String){
                    sql1= sql1+"\""+obj+"\"";
                    //System.out.println("sql1 String: " + sql1);
                }else{
                    sql1= sql1+""+obj;
                    //System.out.println("sql1 NO String: " + sql1);
                }
                if (flag_first){
                    flag_first=false;
                    sql=sql+""+sql1;
                }else {
                    sql=sql+" AND "+sql1;
                }
                //System.out.println("sql: "+sql);
            }
            sql=sql+";";
            System.out.println("sql: "+sql);
            stmt.executeUpdate(sql);
            c.commit();
            flag = true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return flag;
    }

    public boolean delete(String nameTable, HashMap<Object,Object> hashMap) {
        String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
        // correu,nom,cognom,dni,password,TripUB_idTripUB
        boolean flag = false;
        // primero mirar si hay alguna otra fila con la misma pk (correu)
        ArrayList<Object> array = null;
        ArrayList<Object> hashKeys = new ArrayList<Object> (hashMap.keySet());

        boolean exists = this.exists_select_AllAND_AllParams_of_HasMap(nameTable,hashMap);
        if(!exists){
            System.out.println(nameMethod+": No existe el elemento a eliminar");
            return false;
        }

        // en caso de que si haya alguna otra fila con la misma pk (correu), entonces eliminar la fila ya existente
        try{
            stmt = c.createStatement();
            String sql = "DELETE from "+nameTable+" where ";//correu=\""+correu+"\";";

            //System.out.println("sql: "+sql);
            boolean flag_first = true;
            for (Object key : hashKeys) {
                String sql1;
                Object obj = hashMap.get(key);
                sql1=" "+key+" =";
                if(obj instanceof String){
                    sql1= sql1+"\""+obj+"\"";
                    //System.out.println("sql1 String: " + sql1);
                }else{
                    sql1= sql1+""+obj;
                    //System.out.println("sql1 NO String: " + sql1);
                }
                if (flag_first){
                    flag_first=false;
                    sql=sql+""+sql1;
                }else {
                    sql=sql+" AND "+sql1;
                }
                //System.out.println("sql: "+sql);
            }
            sql=sql+";";
            System.out.println("sql: "+sql);
            stmt.executeUpdate(sql);
            c.commit();
            flag=true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return flag;
    }


    //find_OneParam -> campos variables
    public ArrayList<Object> select_OneParam(String nameTable, String param_key, Object param_value) {
        // correu,nom,cognom,dni,password,TripUB_idTripUB
        ArrayList<Object> array = new ArrayList<>();
        try{
            stmt = c.createStatement();
            String sql = "SELECT * FROM "+nameTable+" " +
                    "where ";

            String sql1;
            if(param_value instanceof String){
                sql1= ""+param_key+"=\""+param_value+"\"";
                //System.out.println("sql1 String: " + sql1);
            }else{
                sql1= ""+param_key+"="+param_value;
                //System.out.println("sql1 NO String: " + sql1);
            }
            sql=sql+sql1+";";
            System.out.println("sql: "+sql);
            ResultSet rs = stmt.executeQuery(sql);
            array=this.iteration_rsNext(rs,nameTable);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return array;
    }

    public ArrayList<Object> select_TwoParam_AND_OR_Table(String nameTable,
                                                          String param_key1, Object param_value1,
                                                          String AND_OR,
                                                          String param_key2, Object param_value2) {

        // correu,nom,cognom,dni,password,TripUB_idTripUB
        ArrayList<Object> array = new ArrayList<>();
        try{
            stmt = c.createStatement();
            String sql = "SELECT * FROM "+nameTable+" " +
                    "where ";

            String sql1,sql2;
            if(param_value1 instanceof Integer){
                // si no se añade, lo que pasa es que los que son numericos
                // integer, double... los coge siempre como string
                sql1= ""+param_key1+"="+param_value1;
            }else if(param_value1 instanceof String){
                if(isNumeric((String) param_value1)){
                    //es un numero  transformado en string
                    sql1= ""+param_key1+"="+param_value1;
                }else{
                    // es un string de verdad
                    sql1= ""+param_key1+"=\""+param_value1+"\"";
                    //System.out.println("sql1 String: " + sql1);
                }
            }else{
                sql1= ""+param_key1+"="+param_value1;
                //System.out.println("sql1 NO String: " + sql1);
            }


            if(param_value2 instanceof Integer){
                // si no se añade, lo que pasa es que los que son numericos
                // integer, double... los coge siempre como string
                sql2= ""+param_key2+"="+param_value2;
            }else if(param_value2 instanceof String){
                if(isNumeric((String) param_value2)){
                    //es un numero  transformado en string
                    sql2= ""+param_key2+"="+param_value2;
                }else{
                    // es un string de verdad
                    sql2= ""+param_key2+"=\""+param_value2+"\"";
                    //System.out.println("sql2 String: " + sql2);
                }
            }else{
                sql2= ""+param_key2+"="+param_value2;
                //System.out.println("sql2 NO String: " + sql2);
            }

            sql=sql+sql1+" "+AND_OR+" "+sql2+";";
            System.out.println("sql: "+sql);

            ResultSet rs = stmt.executeQuery(sql);
            array=this.iteration_rsNext(rs,nameTable);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return array;
    }

    // TODO: Se ha comentado entero porque en la primera llamada salta una excepción pero debe saltar -> mirarlo
    public ArrayList<Object> select_ThreeParam_AND_OR_1_Table(String nameTable,
                                                              String param_key1, Object param_value1,
                                                              String AND_OR_1,
                                                              String param_key2, Object param_value2,
                                                              String AND_OR_2,
                                                              String param_key3, Object param_value3) {
        // correu,nom,cognom,dni,password,TripUB_idTripUB
        ArrayList<Object> array = new ArrayList<>();
        try{
            stmt = c.createStatement();
            String sql = "SELECT * FROM "+nameTable+" " +
                    "where ";

            String sql1,sql2,sql3;
            if(param_value1 instanceof String){
                sql1= ""+param_key1+"=\""+param_value1+"\"";
                //System.out.println("sql1 String: " + sql1);
            }else{
                sql1= ""+param_key1+"="+param_value1;
                //System.out.println("sql1 NO String: " + sql1);
            }

            if(param_value2 instanceof String){
                sql2= ""+param_key2+"=\""+param_value2+"\"";
                //System.out.println("sql2 String: " + sql2);
            }else{
                sql2= ""+param_key2+"="+param_value2;
                //System.out.println("sql2 NO String: " + sql2);
            }

            if(param_value3 instanceof String){
                sql3= ""+param_key3+"=\""+param_value3+"\"";
                //System.out.println("sql3 String: " + sql3);
            }else{
                sql3= ""+param_key3+"="+param_value2;
                //System.out.println("sql3 NO String: " + sql3);
            }

            sql=sql+"("+sql1+" "+AND_OR_1+" "+sql2+") "+AND_OR_2+" "+sql3+";";
            //System.out.println("sql: "+sql);

            ResultSet rs = stmt.executeQuery(sql);
            array=this.iteration_rsNext(rs,nameTable);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return array;
    }

    /*
    // TODO: esperar a solucionar el de arriba
    public ArrayList<Object> select_ThreeParam_AND_OR_2_Table(String nameTable,
                                                              String param_key1, Object param_value1,
                                                              String AND_OR_1,
                                                              String param_key2, Object param_value2,
                                                              String AND_OR_2,
                                                              String param_key3, Object param_value3) {
        // correu,nom,cognom,dni,password,TripUB_idTripUB
        ArrayList<Object> array = new ArrayList<>();
        try{
            stmt = c.createStatement();
            String sql = "SELECT * FROM "+nameTable+" " +
                    "where ";

            String sql1,sql2,sql3;
            if(param_value1 instanceof String){
                sql1= ""+param_key1+"=\""+param_value1+"\"";
                //System.out.println("sql1 String: " + sql1);
            }else{
                sql1= ""+param_key1+"="+param_value1;
                //System.out.println("sql1 NO String: " + sql1);
            }

            if(param_value2 instanceof String){
                sql2= ""+param_key2+"=\""+param_value2+"\"";
                //System.out.println("sql2 String: " + sql2);
            }else{
                sql2= ""+param_key2+"="+param_value2;
                //System.out.println("sql2 NO String: " + sql2);
            }

            if(param_value3 instanceof String){
                sql3= ""+param_key3+"=\""+param_value3+"\"";
                //System.out.println("sql3 String: " + sql3);
            }else{
                sql3= ""+param_key3+"="+param_value3;
                //System.out.println("sql3 NO String: " + sql3);
            }

            sql=sql+""+sql1+" "+AND_OR_1+" ("+sql2+" "+AND_OR_2+" "+sql3+");";
            //System.out.println("sql: "+sql);
            ResultSet rs = stmt.executeQuery(sql);
            array=this.iteration_rsNext(rs,nameTable);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return array;
    }
*/
    private boolean exists_select_AllAND_AllParams_of_HasMap (String nameTable, HashMap<Object,Object> hashMap){
        // correu,nom,cognom,dni,password,TripUB_idTripUB
        ArrayList<Object> array = new ArrayList<>();
        ArrayList<Object> arrayKeys = new ArrayList<>(hashMap.keySet());
        try{
            stmt = c.createStatement();
            String sql = "SELECT * FROM "+nameTable+" " +
                    "where ";
            boolean isFirst = true;
            for (Object hasmap_key: arrayKeys) {
                String sql1="";
                if(isFirst){
                    isFirst=false;
                    if(hashMap.get(hasmap_key) instanceof String){
                        sql1= ""+hasmap_key+"=\""+hashMap.get(hasmap_key)+"\"";
                        //System.out.println("sql1 String: " + sql1);
                    }else{
                        sql1= ""+hasmap_key+"="+hashMap.get(hasmap_key);
                        //System.out.println("sql1 NO String: " + sql1);
                    }
                }else{
                    if(hashMap.get(hasmap_key) instanceof String){
                        sql1= " AND "+hasmap_key+"=\""+hashMap.get(hasmap_key)+"\"";
                        //System.out.println("sql1 String: " + sql1);
                    }else{
                        sql1= " AND "+hasmap_key+"="+hashMap.get(hasmap_key);
                        //System.out.println("sql1 NO String: " + sql1);
                    }
                }
                sql=sql+sql1;
            }
            //System.out.println("sql:"+sql);
            ResultSet rs = stmt.executeQuery(sql+";");
            array=this.iteration_rsNext(rs,nameTable);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            return false;
            //System.exit(0);
        }
        if(array==null){
            return false;
        }
        if(array.size()==0){
            return false;
        }
        return true;
    }


    private HashMap<Object,Object> fusion_Hasmaps_NuevoSobreescribeViejo (String nameTable,
                                                      HashMap<Object,Object> hashMap_OLD,
                                                      HashMap<Object,Object> hashMap_NEW,
                                                      ArrayList<Object> arrayNameAutoIncrementalPKsTale){
        //recorrer el viejo
        //en caso de no exitir la key, añadir la del nuevo con su value
        //en caso de si existir, cambiar el value para esa key
        ArrayList<Object> arrayKeys_hashMap_OLD = new ArrayList<>(hashMap_OLD.keySet());
        ArrayList<Object> arrayKeys_hashMap_NEW = new ArrayList<>(hashMap_NEW.keySet());
        ArrayList<Object> fusion = new ArrayList<>();

        for (Object key : arrayKeys_hashMap_OLD) {
            if(!fusion.contains(key)){
                fusion.add(key);
            }
        }
        for (Object key : arrayKeys_hashMap_NEW) {
            if(!fusion.contains(key)){
                fusion.add(key);
            }
        }

        //System.out.println(fusion);
        HashMap<Object, Object> finalHashMap = new HashMap<Object,Object>();
        ArrayList<Object> arrayPks = this.lista_PKs_tabla(nameTable);

        for (Object key : fusion) {
            //queremos guardar solo las keys que no sean pks autoincrementales
            //verificar que si la key está como pk, que no sea autoincremental
            if(arrayNameAutoIncrementalPKsTale==null){
                //si el array es nulo significa que no hay inguna pk autoincrmenetal,
                //por tanto todas se pueden evaluar
                if(arrayKeys_hashMap_NEW.contains(key)){
                    //si el nuevo hashmap continee la key, singinifica que la sobreescibr -> coger del nuevo
                    finalHashMap.put(key,(Object) hashMap_NEW.get(key));
                }else{
                    //si el nuevo hashmap continee la key, singinifica que se queda igual -> coger del viejo
                    finalHashMap.put(key,(Object) hashMap_OLD.get(key));
                }
            }else{
                if(arrayNameAutoIncrementalPKsTale.contains(key)){
                    //si es una pk autocincremental, no queremos añadirla para ser updated
                }else{
                    if(arrayKeys_hashMap_NEW.contains(key)){
                        //si el nuevo hashmap continee la key, singinifica que la sobreescibr -> coger del nuevo
                        finalHashMap.put(key,(Object) hashMap_NEW.get(key));
                    }else{
                        //si el nuevo hashmap continee la key, singinifica que se queda igual -> coger del viejo
                        finalHashMap.put(key,(Object) hashMap_OLD.get(key));
                    }
                }
            }
        }

        return finalHashMap;
    }

    public ArrayList<Object> obtener_nombre_de_todas_tablas(){
        ArrayList<Object> array = new ArrayList<Object>();
        try{
            stmt = c.createStatement();
            String sql = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;";
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                array.add(rs.getString("name"));
            }
            //System.out.println("array:"+array);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
            return null;
        }
        return array;
    }

    public ArrayList<Object> obtener_nombre_de_todas_columnas (String nameTable){
        ArrayList<Object> lista_columns = new ArrayList<>();
        try{
            stmt = this.c.createStatement();
            String sql = "SELECT name FROM pragma_table_info(\""+nameTable+"\");";
            //System.out.println(stmt.executeQuery(sql));
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                String resuKey = rs.getString("name");
                //System.out.println(resuPk.getClass()); -> siempre será String
                lista_columns.add(resuKey);
            }
            //System.out.println("lista_PKs:"+lista_PKs);
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            String nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            System.out.println("nameMethod:" + nameMethod);
            //System.exit(0);
        }
        return lista_columns;
    }

    public boolean isNumeric(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }

}

package com.delarosa.portal.db;

import com.delarosa.portal.db.entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author odelarosa
 */
public class DB {

    public static final String TABLE_USER = "usuarios.json";

    public static void insert(Object obj, Type type, String table) {

        try {
            String content = getTable(table);
            List list = new Gson().fromJson(content, type);

            if (list == null) {
                list = new ArrayList();
            }
            list.add(obj);

            FileUtils.writeStringToFile(getFile(table), new GsonBuilder().setPrettyPrinting().create().toJson(list));
        } catch (IOException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static File getFile(String table) {
        // Carpeta padre
        File parent = new File("tablas");

        if (!parent.exists()) {
            parent.mkdir();
        }

        // Archivo de datos
        File file = new File(parent, table);

        return file;
    }

    public static String getTable(String table) {
        String tableContent = StringUtils.EMPTY;
        try {

            File file = getFile(table);

            if (file.exists()) {
                // Leemos el contenido del archivo
                tableContent = FileUtils.readFileToString(file);
            }
        } catch (IOException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tableContent;
    }

    public static List<User> getUsersLis() {
        String content = getTable(TABLE_USER);
        List<User> list = new Gson().fromJson(content, User.LIST_TYPE);
        return list == null ? new ArrayList<>() : list;
    }

}

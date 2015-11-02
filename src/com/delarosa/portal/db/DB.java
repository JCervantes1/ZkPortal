package com.delarosa.portal.db;

import com.delarosa.portal.db.entity.Cita;
import com.delarosa.portal.db.entity.Patient;
import com.delarosa.portal.db.entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.MutableDateTime;

/**
 *
 * @author odelarosa
 */
public class DB {

    public static final String TABLE_USER = "usuarios.json";
    public static final String TABLE_PATIENT = "pacientes.json";
    public static final String TABLE_CITA = "citas.json";

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

    public static Patient getPatient(String id) {
        String content = getTable(TABLE_PATIENT);
        List<Patient> list = new Gson().fromJson(content, Patient.LIST_TYPE);

        if (list == null) {
            list = new ArrayList<>();
        }

        Patient patient = null;

        for (Patient p : list) {
            if (id.equals(p.getId())) {
                patient = p;
                break;
            }
        }

        return patient;
    }

    public static List<Patient> getPatientLis(String text) {
        String content = getTable(TABLE_PATIENT);
        List<Patient> list = new Gson().fromJson(content, Patient.LIST_TYPE);

        if (list == null) {
            list = new ArrayList<>();
        }

        List<Patient> tmp = new ArrayList<>();

        if (StringUtils.isBlank(text)) {
            tmp.addAll(list);
        } else {
            for (Patient patient : list) {
                if (StringUtils.containsIgnoreCase(patient.getNombre(), text) //
                        || StringUtils.containsIgnoreCase(patient.getApellido1(), text)//
                        || StringUtils.containsIgnoreCase(patient.getApellido2(), text)) {
                    tmp.add(patient);
                }
            }
        }

        return tmp;
    }

    public static List<Cita> getCitasLis(Date date1, Date date2) {
        String content = getTable(TABLE_CITA);
        List<Cita> list = new Gson().fromJson(content, Cita.LIST_TYPE);

        List<Cita> tmp = new ArrayList<>();

        if (list != null && date1 != null && date2 != null) {
            for (Cita cita : list) {
                MutableDateTime f1 = new MutableDateTime(date1);
                f1.setSecondOfDay(0);
                
                MutableDateTime f2 = new MutableDateTime(date2);
                f2.setHourOfDay(23);
                f2.setMinuteOfHour(59);
                
                if (cita.getFecha().after(f1.toDate()) && cita.getFecha().before(f2.toDate())) {
                    tmp.add(cita);
                }
            }
        }

        return tmp;
    }

}

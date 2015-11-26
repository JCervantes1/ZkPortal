package com.delarosa.portal.ui;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.Appointment;
import com.delarosa.portal.db.entity.Patient;
import com.delarosa.portal.zk.GridLayout;
import com.delarosa.portal.zk.Listhead;
import com.delarosa.portal.zk.SearchWindow;
import com.delarosa.portal.zk.Window;
import com.delarosa.portal.zk.ZKUtils;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

/**
 *
 * @author jona
 */
public class BusquedaCitas extends SearchWindow implements ListitemRenderer<Appointment> {

    private Datebox date1;
    private Datebox date2;

    public BusquedaCitas() {
        super(true);

        Toolbarbutton nuevo = new Toolbarbutton();
        nuevo.setIconSclass("z-icon-file-o fa-2x");

        nuevo.addEventListener(Events.ON_CLICK, (Event t) -> {
            NuevaCita cita = new NuevaCita();
            cita.setPage(getPage());
            cita.doModal();
        });

        getToolbar().appendChild(nuevo);
    }

    @Override
    public Component getSearchPanel() {
        GridLayout gridLayout = new GridLayout();

        date1 = new Datebox(new Date());

        date1.addEventListener(Events.ON_CHANGE, (Event t) -> {
            refresh();
        });

        date2 = new Datebox(new Date());

        date2.addEventListener(Events.ON_CHANGE, (Event t) -> {
            refresh();
        });

        gridLayout.addRow("Fecha Inicial", date1, "Fecha Final", date2, null, null);

        return gridLayout;
    }

    @Override
    public ListitemRenderer<Appointment> getItemRenderer() {
        return this;
    }

    @Override
    public Listhead getListHeader() {
        Listhead listhead = new Listhead();
        listhead.newHeader("Nombre del Paciente").setHflex("1");
        listhead.newHeader("Fecha").setHflex("min");
        return listhead;
    }

    @Override
    public Collection<Appointment> getResults() {
        return DB.getAppointmentList(date1.getValue(), date2.getValue());
    }

    @Override
    public void render(Listitem lstm, Appointment appointment, int i) throws Exception {
        Patient patient = DB.getPatient(appointment.getPaciente());
        lstm.appendChild(new Listcell(patient.toString()));
        lstm.appendChild(new Listcell(new SimpleDateFormat("dd/MM//yyyy hh:mm aa").format(appointment.getFecha())));
        
        lstm.addEventListener(Events.ON_CLICK, (Event t) -> {
            NuevaCita nuevaCita = new NuevaCita(appointment);
            nuevaCita.setPage(getPage());
            nuevaCita.doModal();
        });
    }

    public class NuevaCita extends Window {

        private final Datebox fecha;
        private final Textbox motivo;
        private final Combobox list;
        private String id;

        public NuevaCita() {
            this(null);
        }

        public NuevaCita(Appointment appointment) {
            super(true);
            setClosable(true);
            setSizable(true);
            setPosition("center");
            setTitle("Nueva Cita");
            setWidth("90%");
            setHeight("300px");

            Toolbarbutton save = new Toolbarbutton();
            save.setIconSclass("z-icon-save fa-2x");

            getToolbar().appendChild(save);

            GridLayout gridLayout = new GridLayout();

            fecha = new Datebox(new Date());
            fecha.setFormat("dd/MM/yyyy hh:mm aa");
            motivo = new Textbox();
            motivo.setRows(3);

            list = new Combobox();
            list.addEventListener(Events.ON_CHANGING, (InputEvent t) -> {
                String text = t.getValue();

                list.getChildren().clear();

                List<Patient> lst = DB.getPatientList(text);

                Collections.sort(lst, (Patient o1, Patient o2) -> o1.toString().compareTo(o2.toString()));

                for (Patient patient : lst) {
                    Comboitem c = new Comboitem(patient.toString());
                    c.setValue(patient.getId());

                    int years = Years.yearsBetween(new DateTime(patient.getFechaNac()), new DateTime()).getYears();

                    c.setDescription("Edad: " + years + " Sexo: " + patient.getSexo());

                    list.appendChild(c);
                }

                if (lst.isEmpty()) {
                    list.close();
                } else {
                    list.open();
                }
            });

            save.addEventListener(Events.ON_CLICK, (Event t) -> {
                ZKUtils.notNull(fecha);
                ZKUtils.notNull(list);
                ZKUtils.notNull(motivo);
                
                Appointment cita = new Appointment();

                cita.setPaciente(list.getSelectedItem().getValue());
                cita.setFecha(new Timestamp(fecha.getValue().getTime()));
                cita.setMotivo(motivo.getText());

                if (StringUtils.isBlank(id)) {
                    cita.setId(UUID.randomUUID().toString());
                    DB.insert(cita, Appointment.LIST_TYPE, DB.TABLE_APPOINTMENT);
                } else {
                    cita.setId(id);
                    DB.updateAppointment(cita);
                }

                ZKUtils.showInfoMessage("Cita guardada!");

                NuevaCita.this.detach();

                refresh();
            });

            gridLayout.addRow("Fecha", fecha, "Paciente", list, "Motivo", motivo);

            getPanelLayout().newPanelChildren("Datos", true, gridLayout);

            if (appointment != null) {
                load(appointment);
            }
        }

        private void load(Appointment appointment) {
            fecha.setValue(appointment.getFecha());
            motivo.setText(appointment.getMotivo());

            Patient patient = DB.getPatient(appointment.getPaciente());

            Comboitem comboitem = new Comboitem(patient.toString());
            int years = Years.yearsBetween(new DateTime(patient.getFechaNac()), new DateTime()).getYears();

            comboitem.setDescription("Edad: " + years + " Sexo: " + patient.getSexo());
            comboitem.setValue(patient.getId());

            list.appendChild(comboitem);
            list.setSelectedIndex(0);

            id = appointment.getId();
        }

    }

}

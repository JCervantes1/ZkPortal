package com.delarosa.portal.ui;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.Cita;
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

/**
 *
 * @author jona
 */
public class VerCitas extends SearchWindow implements ListitemRenderer<Cita> {

    private Datebox date1;
    private Datebox date2;

    public VerCitas() {
        super(true);

        Toolbarbutton nuevo = new Toolbarbutton();
        nuevo.setIconSclass("z-icon-file-o fa-2x");

        nuevo.addEventListener(Events.ON_CLICK, (Event t) -> {
            NewCita cita = new NewCita();
            VerCitas.this.appendChild(cita);
            cita.doOverlapped();
//            //ZKUtils.notNull(date);
//
//            Cita citas = new Cita();
//            //citas.setFecha(date.get);
//
//            DB.insert(citas, Cita.LIST_TYPE, DB.TABLE_CITA);
//
//            ZKUtils.showInfoMessage("Se guardó la cita con exito");
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
    public ListitemRenderer<Cita> getItemRenderer() {
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
    public Collection<Cita> getResults() {
        return DB.getCitasLis(date1.getValue(), date2.getValue());
    }

    @Override
    public void render(Listitem lstm, Cita t, int i) throws Exception {
        Patient patient = DB.getPatient(t.getPaciente());
        lstm.appendChild(new Listcell(patient.getNombre() + " " + patient.getApellido1() + " " + patient.getApellido2()));
        lstm.appendChild(new Listcell(new SimpleDateFormat("dd/MM//yyyy").format(t.getFecha())));
    }

    public class NewCita extends Window {

        private final Datebox fecha;
        private final Textbox motivo;
        private final Listbox list;

        public NewCita() {
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

            save.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

                @Override
                public void onEvent(Event t) throws Exception {
                    Cita cita = new Cita();
                    cita.setPaciente(list.getSelectedItem().getValue());
                    cita.setFecha(new Timestamp(fecha.getValue().getTime()));
                    cita.setMotivo(motivo.getText());

                    DB.insert(cita, Cita.LIST_TYPE, DB.TABLE_CITA);

                    ZKUtils.showInfoMessage("Cita guardada!");

                    NewCita.this.detach();
                    
                    refresh();
                }
            });

            GridLayout gridLayout = new GridLayout();

            fecha = new Datebox(new Date());
            fecha.setFormat("dd/MM/yyyy HH:mm");
            motivo = new Textbox();
            motivo.setRows(3);

            list = new Listbox();
            list.setMold("select");
            list.setRows(1);

            List<Patient> patientList = DB.getPatientLis(null);

            Collections.sort(patientList, (Patient o1, Patient o2) -> o1.toString().compareTo(o2.toString()));

            patientList.stream().forEach((patient) -> {
                list.appendItem(patient.toString(), patient.getId());
            });

            gridLayout.addRow("Fecha", fecha, "Paciente", list, "Motivo", motivo);

            getPanelLayout().newPanelChildren("Datos", true, gridLayout);
        }

    }

}

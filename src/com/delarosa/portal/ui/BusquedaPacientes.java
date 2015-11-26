package com.delarosa.portal.ui;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.Patient;
import com.delarosa.portal.zk.GridLayout;
import com.delarosa.portal.zk.Listhead;
import com.delarosa.portal.zk.SearchWindow;
import java.text.SimpleDateFormat;
import java.util.Collection;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

/**
 *
 * @author jonathan
 */
public class BusquedaPacientes extends SearchWindow implements ListitemRenderer<Patient> {

    private Textbox text;

    public BusquedaPacientes() {
        super(true);
    }

    @Override
    public Component getSearchPanel() {
        GridLayout gridLayout = new GridLayout();

        text = new Textbox();
        text.addEventListener(Events.ON_OK, (Event t) -> {
            refresh();
        });

        gridLayout.addRow("Búsqueda", text, null, null, null, null);

        return gridLayout;
    }

    @Override
    public ListitemRenderer<Patient> getItemRenderer() {
        return this;
    }

    @Override
    public Listhead getListHeader() {
        Listhead listhead = new Listhead();
        listhead.newHeader("Nombre").setHflex("1");
        listhead.newHeader("Apellido Paterno").setHflex("min");
        listhead.newHeader("Apellido Materno").setHflex("min");
        listhead.newHeader("Fecha Nacimiento").setHflex("min");
        listhead.newHeader("Sexo").setHflex("min");
        return listhead;
    }

    @Override
    public Collection<?> getResults() {
        return DB.getPatientList(text.getText());
    }

    @Override
    public void render(Listitem lstm, final Patient patient, int i) throws Exception {
        lstm.appendChild(new Listcell(patient.getNombre()));
        lstm.appendChild(new Listcell(patient.getApellido1()));
        lstm.appendChild(new Listcell(patient.getApellido2()));
        lstm.appendChild(new Listcell(new SimpleDateFormat("dd/MM/yyyy").format(patient.getFechaNac())));
        lstm.appendChild(new Listcell(patient.getSexo()));
        
        lstm.addEventListener(Events.ON_CLICK, (Event t) -> {
            AltaPacientes altaPacientes = new AltaPacientes(patient);
            altaPacientes.setClosable(true);
            altaPacientes.setTitle("Edición de Paciente");
            altaPacientes.setWidth("800px");
            altaPacientes.setHeight("500px");
            altaPacientes.setPage(getPage());
            altaPacientes.doModal();
        });
    }

}

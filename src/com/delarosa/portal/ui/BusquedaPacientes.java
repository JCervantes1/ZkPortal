package com.delarosa.portal.ui;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.Patient;
import com.delarosa.portal.zk.GridLayout;
import com.delarosa.portal.zk.Listhead;
import com.delarosa.portal.zk.SearchWindow;
import java.util.ArrayList;
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
        return listhead;
    }

    @Override
    public Collection<?> getResults() {
        return DB.getPatientLis(text.getText());
    }

    @Override
    public void render(Listitem lstm, Patient t, int i) throws Exception {
        lstm.appendChild(new Listcell(t.getNombre()));
        lstm.appendChild(new Listcell(t.getApellido1()));
        lstm.appendChild(new Listcell(t.getApellido2()));
    }

}

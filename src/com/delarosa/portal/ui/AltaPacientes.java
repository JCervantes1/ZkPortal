package com.delarosa.portal.ui;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.Patient;
import com.delarosa.portal.zk.GridLayout;
import com.delarosa.portal.zk.Window;
import com.delarosa.portal.zk.ZKUtils;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

/**
 *
 * @author jonathan
 */
public class AltaPacientes extends Window {

    private final Textbox nombre;
    private final Textbox apellido1;
    private final Textbox apellido2;
    private final Listbox sexo;
    private final Datebox fechaNac;
    private final Textbox direccion;
    private String id;

    public AltaPacientes() {
        this(null);
    }

    public AltaPacientes(Patient patient) {
        super(true);

        nombre = new Textbox();
        apellido1 = new Textbox();
        apellido2 = new Textbox();
        sexo = new Listbox();
        fechaNac = new Datebox();
        direccion = new Textbox();

        sexo.setMold("select");
        sexo.appendItem("Femenino", "Femenino");
        sexo.appendItem("Masculino", "Masculino");

        if (patient != null) {
            load(patient);
        }

        Toolbarbutton save = new Toolbarbutton();
        save.setIconSclass("z-icon-save fa-2x");

        save.addEventListener(Events.ON_CLICK, (Event t) -> {
            ZKUtils.notNull(nombre);
            ZKUtils.notNull(apellido1);
            ZKUtils.notNull(apellido2);
            ZKUtils.notNull(fechaNac);
            ZKUtils.notNull(sexo);
            ZKUtils.notNull(direccion);

            Patient p = new Patient();
            p.setNombre(nombre.getText());
            p.setApellido1(apellido1.getText());
            p.setApellido2(apellido2.getText());
            p.setFechaNac(fechaNac.getValue());
            p.setSexo(sexo.getSelectedItem().getValue());
            p.setDireccion(direccion.getText());

            if (StringUtils.isNoneBlank(id)) {
                p.setId(id);
                DB.updatePatient(p);
            } else {
                p.setId(UUID.randomUUID().toString());
                DB.insert(p, Patient.LIST_TYPE, DB.TABLE_PATIENT);
            }

            ZKUtils.showInfoMessage("Se guardó el paciente con exito");
        });

        getToolbar().appendChild(save);

        GridLayout gridLayout = new GridLayout();

        getPanelLayout().newPanelChildren("Datos Generales", true, gridLayout);

        gridLayout.addRow("Nombre", nombre, "Apellido Paterno", apellido1, "Apellido Materno", apellido2);
        gridLayout.addRow("Fecha Nacimiento", fechaNac, "Sexo", sexo, null, null);
        gridLayout.addRow("Dirección", direccion, null, null, null, null);
    }

    private void load(Patient patient) {
        nombre.setText(patient.getNombre());
        apellido1.setText(patient.getApellido1());
        apellido2.setText(patient.getApellido2());
        fechaNac.setValue(patient.getFechaNac());
        ZKUtils.setSelectedValue(sexo, patient.getSexo());
        direccion.setText(patient.getDireccion());

        id = patient.getId();
    }

}

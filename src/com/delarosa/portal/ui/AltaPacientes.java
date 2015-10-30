package com.delarosa.portal.ui;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.Patient;
import com.delarosa.portal.zk.GridLayout;
import com.delarosa.portal.zk.Window;
import com.delarosa.portal.zk.ZKUtils;
import java.util.UUID;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
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

    public AltaPacientes() {
        super(true);
        
        nombre = new Textbox();
        apellido1 = new Textbox();
        apellido2 = new Textbox();
        
        Toolbarbutton save = new Toolbarbutton();
        save.setIconSclass("z-icon-save fa-2x");
        
        save.addEventListener(Events.ON_CLICK, (Event t) -> {
            ZKUtils.notNull(nombre);
            ZKUtils.notNull(apellido1);
            ZKUtils.notNull(apellido2);
            
            Patient patient = new Patient();
            patient.setId(UUID.randomUUID().toString());
            patient.setNombre(nombre.getText());
            patient.setApellido1(apellido1.getText());
            patient.setApellido2(apellido2.getText());
            
            DB.insert(patient, Patient.LIST_TYPE, DB.TABLE_PATIENT);
            
            ZKUtils.showInfoMessage("Se guardó el paciente con exito");
        });
        
        getToolbar().appendChild(save);
        
        GridLayout gridLayout = new GridLayout();
        
        getPanelLayout().newPanelChildren("Datos Generales", true, gridLayout);
        
        gridLayout.addRow("Nombre", nombre, "Apellido Paterno", apellido1, "Apellido Materno", apellido2);
    }
    
}

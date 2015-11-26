package com.delarosa.portal.ui;

import com.delarosa.portal.db.DB;
import com.delarosa.portal.db.entity.User;
import com.delarosa.portal.zk.GridLayout;
import com.delarosa.portal.zk.Notification;
import com.delarosa.portal.zk.Window;
import com.delarosa.portal.zk.ZKUtils;
import java.sql.Timestamp;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

/**
 *
 * @author jcervantes
 */
public class NuevoUsuario extends Window {

    private final Textbox alias;
    private final Textbox name;
    private final Textbox pass;
    private final Textbox conf;

    public NuevoUsuario() {
        super(true);

        setTitle("Nuevo Usuario");
        setClosable(true);
        setWidth("800px");
        setHeight("400px");

        alias = new Textbox();
        name = new Textbox();
        pass = new Textbox();
        conf = new Textbox();

        pass.setType("password");
        conf.setType("password");

        GridLayout gridLayout = new GridLayout();
        gridLayout.addRow("Alias", alias, "Nombre", name, null, null);
        gridLayout.addRow("Password", pass, "Confirma", conf, null, null);

        getPanelLayout().newPanelChildren(StringUtils.EMPTY, true, gridLayout);

        Toolbarbutton save = new Toolbarbutton();
        save.setIconSclass("z-icon-save fa-2x");

        getToolbar().appendChild(save);

        save.addEventListener(Events.ON_CLICK, (Event t) -> {
            save();
        });

    }

    private void save() {
        ZKUtils.notNull(alias);
        ZKUtils.notNull(name);
        ZKUtils.notNull(pass);
        ZKUtils.notNull(conf);

        if (!conf.getText().equals(pass.getText())) {
            new SimpleConstraint(SimpleConstraint.NO_EMPTY, "El password no coincide").validate(conf, null);
        }

        User user = new User();
        user.setAlias(alias.getText());
        user.setName(name.getText());
        user.setPassword(pass.getText());
        user.setCreated(new Timestamp(System.currentTimeMillis()));
        user.setUpdated(new Timestamp(System.currentTimeMillis()));

        DB.insert(user, User.LIST_TYPE, DB.TABLE_USER);

        Notification.showInfo("Usuario creado con exito");

        detach();
    }
}

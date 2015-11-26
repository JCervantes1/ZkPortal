package com.delarosa.portal.ui;

import com.delarosa.portal.authentication.MyAuthenticationService;
import com.delarosa.portal.zk.ZKUtils;
import org.zkoss.essentials.services.AuthenticationService;
import org.zkoss.essentials.services.UserCredential;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.event.SwipeEvent;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.North;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;
import org.zkoss.zul.theme.Themes;

/**
 *
 * @author odelarosa
 */
public class Main extends Window {

    private final Borderlayout borderlayout = new Borderlayout();
    private final Center center = new Center();
    private final West west = new West();
    private final UserPanel userPanel = new UserPanel();
    private final North north = new North();
    private final AuthenticationService authService = new MyAuthenticationService();

    public Main() {
        UserCredential userCredential = authService.getUserCredential();

        if (userCredential.isAnonymous()) {
            Executions.getCurrent().sendRedirect("/index.zul");
        } else {

            north.appendChild(userPanel);
            north.setStyle("background:#5687A8;text-align:right;");

            west.setWidth("200px");
            west.setTitle(" ");
            west.setCollapsible(true);

            createMenu(west);

            EventListener<SwipeEvent> swipe = (SwipeEvent t) -> {
                if (null != t.getSwipeDirection()) {
                    switch (t.getSwipeDirection()) {
                        case "right":
                            west.setOpen(true);
                            break;
                        case "left":
                            west.setOpen(false);
                            break;
                    }
                }
            };

            west.addEventListener(Events.ON_SWIPE, swipe);
            center.addEventListener(Events.ON_SWIPE, swipe);

            if ("Y".equals(Sessions.getCurrent().getAttribute("external"))) {
                Themes.setTheme(Executions.getCurrent(), "silvertail");
            } else {
                borderlayout.appendChild(north);
            }

            borderlayout.appendChild(west);
            borderlayout.appendChild(center);
            borderlayout.setWidth("100%");
            borderlayout.setHeight("100%");

            appendChild(borderlayout);

            addEventListener("onLoad", (Event t) -> {
                open(new Home());
            });

            Events.echoEvent("onLoad", this, null);

            setBorder(false);
            setWidth("100%");
            setHeight("100%");

            addEventListener("onPrinted", (Event t) -> {
                borderlayout.resize();
            });

            if (ZKUtils.isMobile()) {
                west.setOpen(false);
            }
        }
    }

    public void open(Component component) {
        center.getChildren().clear();
        center.appendChild(component);
    }

    public void createMenu(West west) {
        Navbar navbar = new Navbar("vertical");

        Navitem home = new Navitem();
        home.setLabel("Inicio");
        home.setId("k");
        home.setIconSclass("z-icon-home");
        home.setWidth("100%");
        home.setSelected(true);

        Navitem altaPacientes = new Navitem();
        altaPacientes.setLabel("Alta de Pacientes");
        altaPacientes.setId("a");
        altaPacientes.setIconSclass("z-icon-user-plus");
        altaPacientes.setWidth("100%");

        Navitem busquedaPacientes = new Navitem();
        busquedaPacientes.setLabel("Búsqueda de Pacientes");
        busquedaPacientes.setId("b");
        busquedaPacientes.setIconSclass("z-icon-refresh");
        busquedaPacientes.setWidth("100%");

        Navitem verCitas = new Navitem();
        verCitas.setLabel("Ver Citas");
        verCitas.setId("c");
        verCitas.setIconSclass("z-icon-calendar-o");
        verCitas.setWidth("100%");

        navbar.appendChild(home);
        navbar.appendChild(altaPacientes);
        navbar.appendChild(busquedaPacientes);
        navbar.appendChild(verCitas);

        EventListener<SelectEvent> eventListener = (SelectEvent t) -> {
            Navitem navitem = (Navitem) t.getSelectedItems().iterator().next();
            String id = navitem.getId();
            switch (id) {
                case "k":
                    open(new Home());
                    break;
                case "a":
                    open(new AltaPacientes());
                    break;
                case "b":
                    open(new BusquedaPacientes());
                    break;
                case "c":
                    open(new BusquedaCitas());
                    break;
            }
        };

        navbar.addEventListener(Events.ON_SELECT, eventListener);

        west.appendChild(navbar);

        navbar.setWidth("100%");
        navbar.setHeight("100%");
    }

    public void onCreate() {
        getPage().setTitle("Portal Web");

        Events.echoEvent("onPrinted", this, null);
    }

}

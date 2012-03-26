package org.netomi.tracker.ui;

import org.netomi.tracker.util.VaadinApplicationObjectSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@Configurable(preConstruction = true)
public class MainLayout
    extends VerticalLayout {

    /** The serializable UID. */
    private static final long serialVersionUID = 1165491903823037565L;

    @Autowired
    private transient VaadinApplicationObjectSupport app;

    private TabSheet tabSheet;
    
    public MainLayout() {
        setMargin(true);
        setSizeFull();

        Label title = new Label(app.getMessage("application.name"));
        title.addStyleName(Runo.LABEL_H1);
        title.setSizeUndefined();
        addComponent(title);
        setComponentAlignment(title, Alignment.TOP_CENTER);

        Label slogan = new Label(app.getMessage("application.slogan"));
        slogan.addStyleName(Runo.LABEL_SMALL);
        slogan.setSizeUndefined();
        addComponent(slogan);
        setComponentAlignment(slogan, Alignment.TOP_CENTER);

        Label spacer = new Label("");
        spacer.setHeight("20px");
        addComponent(spacer);

        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        //tabSheet.addStyleName(Runo.TABSHEET_SMALL);

        addComponent(tabSheet);
        setExpandRatio(tabSheet, 1);

        tabSheet.addTab(new WelcomeScreen(), "Welcome");
        tabSheet.addTab(new SatelliteScreen(), "Satellites");
        tabSheet.addTab(new VisibilityScreen(), "Visibility");
        tabSheet.addTab(new TrackingScreen(), "Tracking");
    }
}

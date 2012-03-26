package org.netomi.tracker.ui;

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.VerticalLayout;

@Configurable(preConstruction = true)
public class SatelliteScreen extends VerticalLayout {

    private static final long serialVersionUID = 4716936214457141838L;

    public SatelliteScreen() {
        setMargin(true);
        setSpacing(true);
        
        SatelliteTable table = new SatelliteTable();
        table.setSizeUndefined();

        addComponent(table);
    }
}

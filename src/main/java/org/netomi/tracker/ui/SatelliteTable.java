package org.netomi.tracker.ui;

import java.util.Date;

import org.netomi.tracker.model.Satellite;
import org.netomi.tracker.service.SatelliteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;

import com.vaadin.ui.Table;

@Configurable(preConstruction = true)
public class SatelliteTable
    extends Table {

    private static final long serialVersionUID = -3668743563885962873L;

    private final static Logger logger = LoggerFactory.getLogger(SatelliteTable.class);

    @Autowired
    private transient SatelliteService satService;

    private EntityContainer<Satellite> entityContainer;

    private static String[] visibleColumnIds = {
        "catalogNumber", "commonName", "type", "designator", "country", "launchDate", "launchSite", "decayDate",
        "period", "inclination", "apogee", "perigee", "rcs"
    };

    private static String[] visibleColumnLabels = {
        "Catalog Number", "Common Name", "Sat Type", "Int. Designator", "Country", "Launch Date",
        "Launch Site", "Decay Date", "Period", "Incl.", "Apogee", "Perigee", "Rcs"
    };

    public SatelliteTable() {
        this(visibleColumnIds, visibleColumnLabels, 10);
    }
    
    public SatelliteTable(final String[] columnIds, final String[] columnLabels, int pageLength) {
        
        entityContainer = new EntityContainer<Satellite>(satService.getEntityManager(), true, false, false,
                                                         Satellite.class, 100, new Object[] {
                                                             "catalogNumber", "commonName"
                                                         }, new boolean[] {
                                                             true, true
                                                         });

        entityContainer.addContainerProperty("catalogNumber", Long.class, 0, true, true);
        entityContainer.addContainerProperty("commonName", String.class, "", true, true);
        entityContainer.addContainerProperty("type", String.class, "", true, true);
        entityContainer.addContainerProperty("designator", String.class, "", true, true);
        entityContainer.addContainerProperty("country", String.class, "", true, true);
        entityContainer.addContainerProperty("launchDate", Date.class, null, true, true);
        entityContainer.addContainerProperty("launchSite", String.class, "", true, true);
        entityContainer.addContainerProperty("decayDate", Date.class, null, true, true);
        entityContainer.addContainerProperty("period", Double.class, 0.0d, true, true);
        entityContainer.addContainerProperty("inclination", Double.class, 0.0d, true, true);
        entityContainer.addContainerProperty("apogee", Double.class, 0.0d, true, true);
        entityContainer.addContainerProperty("perigee", Double.class, 0.0d, true, true);
        entityContainer.addContainerProperty("rcs", Double.class, 0.0d, true, true);

        // add a custom date formatter for the launch date and decay date
        addGeneratedColumn("launchDate", new DateColumnGenerator());
        addGeneratedColumn("decayDate", new DateColumnGenerator());

        setPageLength(pageLength);
        setContainerDataSource(entityContainer);

        setVisibleColumns(columnIds);
        setColumnHeaders(columnLabels);

        String[] colAlignments = new String[columnIds.length];
        for (int i = 0; i < colAlignments.length; i++) {
            colAlignments[i] = Table.ALIGN_LEFT;
        }
        setColumnAlignments(colAlignments);

        setEditable(false);
        setMultiSelect(false);
        setSelectable(true);
        setWriteThrough(false);
    }

    public void refresh() {
        if (logger.isDebugEnabled()) {
            logger.debug("refreshing table");
        }
        entityContainer.refresh();
    }

}

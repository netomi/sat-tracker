package org.netomi.tracker.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.FastMath;
import org.netomi.tracker.model.Satellite;
import org.netomi.tracker.model.TleEntry;
import org.netomi.tracker.orekit.SatellitePropagation;
import org.netomi.tracker.orekit.VisibilityEntry;
import org.netomi.tracker.service.SatelliteService;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.vol.Marker;
import org.vaadin.vol.MarkerLayer;
import org.vaadin.vol.OpenLayersMap;
import org.vaadin.vol.OpenStreetMapLayer;
import org.vaadin.vol.Point;
import org.vaadin.vol.PolyLine;
import org.vaadin.vol.Style;
import org.vaadin.vol.StyleMap;
import org.vaadin.vol.Vector;
import org.vaadin.vol.VectorLayer;
import org.vaadin.vol.VectorLayer.DrawingMode;
import org.vaadin.vol.VectorLayer.SelectionMode;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

@Configurable(preConstruction = true)
public class VisibilityScreen
    extends HorizontalLayout {

    private static final long serialVersionUID = -6709613234549226348L;

    @Autowired
    private transient SatelliteService satService;

    private TextField latitudeField;

    private TextField longitudeField;
    
    private Table visTable;
    
    private BeanItemContainer<VisibilityEntry> ic;

    private VectorLayer vectorLayer;

    private MarkerLayer markerLayer;

    private Map<Long, Component> visiblePasses = new HashMap<Long, Component>();

    private Map<Long, Set<Component>> visibleMarkers = new HashMap<Long, Set<Component>>();
    
    private TLE latestTLE;
    private double latestLat;
    private double latestLon;

    public VisibilityScreen() {
        setMargin(true);
        setSpacing(true);
        setSizeFull();

        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setSizeUndefined();

        final SatelliteTable table = new SatelliteTable(new String[] { "catalogNumber", "commonName" }, 
                                                        new String[] { "Catalog Number", "Common Name" }, 5);
        table.setSizeUndefined();

        vl.addComponent(table);

        // create the lat/lon fields and set them to a default location
        latitudeField = new TextField();
        latitudeField.setCaption("Latitude");
        latitudeField.setValue("50.84");
        vl.addComponent(latitudeField);

        longitudeField = new TextField();
        longitudeField.setCaption("Longitude");
        longitudeField.setValue("4.35");
        vl.addComponent(longitudeField);

        Button compute = new Button("Show", new Button.ClickListener() {

            private static final long serialVersionUID = -2433152978338766513L;

            public void buttonClick(ClickEvent event) {
                if (table.getValue() == null) {
                    return;
                }

                Item item = table.getItem(table.getValue());
                Long id = (Long) item.getItemProperty("catalogNumber").getValue();
                Satellite sat = satService.getSatellite(id);
                TleEntry entry = satService.getLatestTleEntry(sat);

                vectorLayer.removeAllComponents();
                markerLayer.removeAllComponents();

                try {
                    latestTLE = new TLE(entry.getLine1(), entry.getLine2());

                    AbsoluteDate startDate = new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

                    ic.removeAllItems();

                    latestLat = Double.valueOf((String) latitudeField.getValue());
                    latestLon = Double.valueOf((String) longitudeField.getValue());
                    
                    // get visibilities for the next 5 days
                    List<VisibilityEntry> entries = SatellitePropagation.getVisibilities(latestLat, latestLon,
                                                                                         0, FastMath.toRadians(10),
                                                                                         latestTLE,
                                                                                         startDate, 60L*60*24*5);
                    ic.addAll(entries);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        compute.setWidth("100%");
        compute.addStyleName(Runo.BUTTON_DEFAULT);
        compute.addStyleName(Runo.BUTTON_BIG);

        vl.addComponent(compute);

        visTable = new Table();
        visTable.setSizeUndefined();

        ic = new BeanItemContainer<VisibilityEntry>(VisibilityEntry.class);
        
        visTable.setContainerDataSource(ic);
        visTable.addGeneratedColumn("start", new ShortDateTimeColumnGenerator());
        visTable.addGeneratedColumn("end", new ShortDateTimeColumnGenerator());
        visTable.addGeneratedColumn("visible", new ColumnGenerator() {

            private static final long serialVersionUID = 9130845892092636484L;

            public Component generateCell(final Table source, final Object itemId, final Object columnId) {
                final CheckBox checkbox = new CheckBox();
                checkbox.setValue(false);
                checkbox.setImmediate(true);
                checkbox.addListener(new CheckBox.ValueChangeListener() {

                    private static final long serialVersionUID = -6410213284048446509L;

                    public void valueChange(Property.ValueChangeEvent event) {
                        @SuppressWarnings("unchecked")
                        BeanItem<VisibilityEntry> item = (BeanItem<VisibilityEntry>) visTable.getItem(itemId);
                        VisibilityEntry entry = item.getBean();
                        boolean show = (Boolean) event.getProperty().getValue();
                        checkbox.setValue(show);

                        if (show) {
                            addPass(entry);
                        } else {
                            Component comp = visiblePasses.get(entry.getStart().getTime());
                            if (comp != null) {
                                vectorLayer.removeComponent(comp);
                                for (Component c : visibleMarkers.get(entry.getStart().getTime())) {
                                    markerLayer.removeComponent(c);
                                }
                                markerLayer.requestRepaint();
                            }
                        }
                    }
                });
                return checkbox;
            }
        });

        visTable.setVisibleColumns(new String[] { "start", "end", "visible" });
        visTable.setColumnHeaders(new String[] { "Start", "End", "Show" });
        visTable.setColumnIcon("start", new ExternalResource("images/up.png"));
        visTable.setColumnIcon("end", new ExternalResource("images/dwn.png"));
        visTable.setColumnAlignment("visible", Table.ALIGN_CENTER);

        visTable.setPageLength(5);
        vl.addComponent(visTable);

        addComponent(vl);

        OpenLayersMap map = new OpenLayersMap();
        map.setSizeFull();
        map.setImmediate(true); // update extent and zoom to server as they change

        OpenStreetMapLayer osm = new OpenStreetMapLayer();
        map.addLayer(osm);

        vectorLayer = new VectorLayer();
        vectorLayer.setSelectionMode(SelectionMode.NONE);

        Style defaultStyle = new Style();
        /* Set stroke color to green, otherwise like default style */
        defaultStyle.extendCoreStyle("default");
        defaultStyle.setStrokeColor("#00b963");
        defaultStyle.setStrokeOpacity(0.5);
        defaultStyle.setStrokeWidth(4);

        Style selectStyle = new Style();
        /* Set select color to blue, otherwise like default style */
        selectStyle.extendCoreStyle("default");
        selectStyle.setStrokeColor("#0063b9");
        selectStyle.setStrokeOpacity(0.5);
        selectStyle.setStrokeWidth(4);

        StyleMap styleMap = new StyleMap(defaultStyle, selectStyle, null);
        // make selectStyle inherit attributes not explicitly set
        styleMap.setExtendDefault(true);

        vectorLayer.setStyleMap(styleMap);
        vectorLayer.setDrawindMode(DrawingMode.NONE);

        map.addLayer(vectorLayer);

        // Define a Marker Layer
        markerLayer = new MarkerLayer();

        map.addLayer(markerLayer);

        addComponent(map);
        setExpandRatio(map, 1);
    }
    
    private void addPass(final VisibilityEntry entry) {
        try {
            SatellitePropagation.fillVisibilityEntry(latestLat, latestLon, 0, latestTLE, entry);
            
            Set<Component> markers = new HashSet<Component>();

            List<Point> npoints = new LinkedList<Point>();
            for (org.netomi.tracker.orekit.VisibilityEntry.Point pe : entry.getPoints()) {
                final double lat = pe.getLat();
                final double lon = pe.getLon();
                npoints.add(new Point(lon, lat));
            }

            Vector line = new PolyLine();
            line.setPoints(npoints.toArray(new Point[npoints.size()]));

            vectorLayer.addVector(line);
            visiblePasses.put(entry.getStart().getTime(), line);

            // add markers at start and end
            Point[] points = line.getPoints();
            Marker startMarker = new Marker(points[0].getLon(), points[0].getLat());
            startMarker.setIcon(new ExternalResource("images/up.png"), 14, 14);
            markerLayer.addMarker(startMarker);
            markers.add(startMarker);

            int lastIdx = points.length - 1;
            Marker endMarker = new Marker(points[lastIdx].getLon(), points[lastIdx].getLat());
            endMarker.setIcon(new ExternalResource("images/dwn.png"), 14, 14);
            markerLayer.addMarker(endMarker);
            markers.add(endMarker);

            visibleMarkers.put(entry.getStart().getTime(), markers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

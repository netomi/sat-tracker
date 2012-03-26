package org.netomi.tracker.ui;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.netomi.tracker.model.Satellite;
import org.netomi.tracker.model.TleEntry;
import org.netomi.tracker.orekit.SatellitePropagation;
import org.netomi.tracker.service.SatelliteService;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.BoundedPropagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
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

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

@Configurable(preConstruction = true)
public class TrackingScreen
    extends HorizontalLayout {

    private static final long serialVersionUID = -2661285059951989348L;

    @Autowired
    private transient SatelliteService satService;

    private VectorLayer vectorLayer;
    private MarkerLayer markerLayer;
    private Marker marker;

    private Tracker currentTracker = null;

    public TrackingScreen() {
        setMargin(true);
        setSpacing(true);
        setSizeFull();

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeUndefined();

        final SatelliteTable table = new SatelliteTable(new String[] { "catalogNumber", "commonName" }, 
                                                        new String[] { "Catalog Number", "Common Name" }, 10);
        table.setSizeUndefined();

        vl.addComponent(table);

        Button compute = new Button("Track", new Button.ClickListener() {

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
                    TLE tle = new TLE(entry.getLine1(), entry.getLine2());

                    AbsoluteDate startDate = new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

                    double period = sat.getPeriod();
                    double iterations = 2;
                    long step = period > 100 ? 180 : 90;
                    BoundedPropagator boundedProp = SatellitePropagation.getEphemeris(tle,
                                                                                      startDate.shiftedBy(-60 * 30),
                                                                                      60L * period * iterations, step);

                    currentTracker = new Tracker(boundedProp);
                    AbsoluteDate date = currentTracker.getStartDate();

                    List<Point> points = new LinkedList<Point>();
                    double lastLon = 0d;

                    while (date.compareTo(currentTracker.getEndDate()) < 0) {
                        GeodeticPoint gp = currentTracker.getPoint(date);

                        double lat = Math.toDegrees(gp.getLatitude());
                        double lon = Math.toDegrees(gp.getLongitude());

                        if (lastLon > 0 && lon < 0) {
                            Vector line = new PolyLine();
                            line.setPoints(points.toArray(new Point[points.size()]));
                            vectorLayer.addVector(line);
                            points.clear();
                        }

                        points.add(new Point(lon, lat));
                        lastLon = lon;
                        date = date.shiftedBy(step);
                    }

                    Vector line = new PolyLine();
                    line.setPoints(points.toArray(new Point[points.size()]));
                    vectorLayer.addVector(line);
                    
                    GeodeticPoint gp = currentTracker.getPoint(startDate);
                    double lat = Math.toDegrees(gp.getLatitude());
                    double lon = Math.toDegrees(gp.getLongitude());

                    marker = new Marker(lon, lat);
                    // URL of marker Icon
                    marker.setIcon("images/satellite_48_hot.png", 24, 24);
                    markerLayer.addMarker(marker);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        compute.setWidth("100%");
        compute.addStyleName(Runo.BUTTON_DEFAULT);
        compute.addStyleName(Runo.BUTTON_BIG);

        vl.addComponent(compute);

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
        defaultStyle.setStrokeWidth(1);

        Style selectStyle = new Style();
        /* Set select color to blue, otherwise like default style */
        selectStyle.extendCoreStyle("default");
        selectStyle.setStrokeColor("#0063b9");
        selectStyle.setStrokeOpacity(0.5);
        selectStyle.setStrokeWidth(1);

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

        Refresher r = new Refresher();
        r.setRefreshInterval(1000);
        r.addListener(new RefreshListener() {

            private static final long serialVersionUID = 2875834508560395285L;

            @Override
            public void refresh(Refresher source) {
                if (marker != null && currentTracker != null) {
                    try {
                        AbsoluteDate currentDate = new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());
                        GeodeticPoint gp = currentTracker.getPoint(currentDate);
                        double lat = Math.toDegrees(gp.getLatitude());
                        double lon = Math.toDegrees(gp.getLongitude());
                        
                        marker.setLon(lon);
                        marker.setLat(lat);
                    } catch (OrekitException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        addComponent(r);
    }

    private static class Tracker {

        private BoundedPropagator prop;

        private BodyShape earth;

        private TopocentricFrame stationFrame;

        public Tracker(final BoundedPropagator prop)
            throws OrekitException {
            this.prop = prop;

            earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, Constants.WGS84_EARTH_FLATTENING,
                                         FramesFactory.getITRF2005());

            // setup an arbitrary station on the surface of the earth
            double longitude = Math.toRadians(45.);
            double latitude = Math.toRadians(25.);
            double altitude = 0.;
            final GeodeticPoint station = new GeodeticPoint(latitude, longitude, altitude);

            stationFrame = new TopocentricFrame(earth, station, "ground location");
        }

        public AbsoluteDate getStartDate() {
            return prop.getMinDate();
        }

        public AbsoluteDate getEndDate() {
            return prop.getMaxDate();
        }

        public GeodeticPoint getPoint(final AbsoluteDate date)
            throws OrekitException {
            final SpacecraftState state = prop.propagate(date);
            return SatellitePropagation.convertToGeodeticPoint(state, stationFrame, earth);
        }
    }
}

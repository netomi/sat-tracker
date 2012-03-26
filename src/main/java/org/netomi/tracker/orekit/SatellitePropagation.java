package org.netomi.tracker.orekit;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.netomi.tracker.orekit.VisibilityEntry.Point;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.BoundedPropagator;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides satellite propagation calculation.
 */
public class SatellitePropagation {

    private final static Logger logger = LoggerFactory.getLogger(SatellitePropagation.class);

    // Earth constants and reference frame

    // equatorial radius in meter
    private static final double ae = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

    // flattening
    private static final double f = Constants.WGS84_EARTH_FLATTENING;

    /**
     * Propagates a satellite in the given time interval and returns a {@link List} of {@link GeodeticPoint}
     * entries along the path.
     * 
     * @param satTLE the two-line element of the satellite
     * @param startDate the starting date for the propagation
     * @param duration the duration of the propagation
     * @param step the step size of the propagation (in seconds)
     * @return
     * @throws OrekitException
     */
    public static List<GeodeticPoint> propagate(final TLE satTLE, final AbsoluteDate startDate,
                                                final double duration, final long step)
        throws OrekitException {
        // Propagator based on TLE propagation
        Propagator prop = TLEPropagator.selectExtrapolator(satTLE);

        // terrestrial frame at an arbitrary date
        Frame ITRF2005 = FramesFactory.getITRF2005();

        final BodyShape earth = new OneAxisEllipsoid(ae, f, ITRF2005);

        // setup an arbitrary station on the surface of the earth
        double longitude = Math.toRadians(45.);
        double latitude = Math.toRadians(25.);
        double altitude = 0.;
        final GeodeticPoint station = new GeodeticPoint(latitude, longitude, altitude);

        final TopocentricFrame stationFrame = new TopocentricFrame(earth, station, "ground location");

        prop.propagate(startDate);

        final List<GeodeticPoint> points = new LinkedList<GeodeticPoint>();
        prop.setMasterMode(step, new OrekitFixedStepHandler() {

            private static final long serialVersionUID = 1L;

            @Override
            public void init(SpacecraftState state, AbsoluteDate date) {
            }

            public void handleStep(SpacecraftState currentState, boolean isLast)
                throws PropagationException {

                try {
                    points.add(convertToGeodeticPoint(currentState, stationFrame, earth));
                } catch (OrekitException e) {
                    e.printStackTrace();
                }
            }
        });

        // Propagate from the start date for the fixed duration
        prop.propagate(startDate.shiftedBy(duration));
        return points;
    }

    /**
     * Calculates and returns an ephemeris for the given satellite in the specified time interval.
     * The returned {@link BoundedPropagator} can be used to propagate the {@link SpacecraftState}
     * of the satellite at any time within the time interval.
     * 
     * @param satTLE the two-line element of the satellite
     * @param startDate the starting date for the propagation
     * @param duration the duration of the propagation
     * @param step the step size of the propagation (in seconds)
     * @return a {@link BoundedPropagator} used to access the ephemeris in the time interval
     * @throws OrekitException
     */
    public static BoundedPropagator getEphemeris(final TLE satTLE, final AbsoluteDate startDate,
                                                 final double duration, final long step)
        throws OrekitException {

        // Propagator based on TLE propagation
        TLEPropagator prop = TLEPropagator.selectExtrapolator(satTLE);

        // propagate to the starting date in slave mode
        prop.propagate(startDate);

        // set ephemeris mode
        prop.setEphemerisMode();

        prop.setInitialState(startDate);

        // propagate from the start date for the fixed duration
        prop.propagate(startDate.shiftedBy(duration));

        return prop.getGeneratedEphemeris();

    }

    /**
     * Calculate visibility entries for a given ground location within a defined time-span. A visibility is defined to
     * be an event when the satellite is a specified elevation degree above the horizon from the viewpoint of the ground
     * station.
     * 
     * @param lat the latitude of the ground location (in degrees)
     * @param lon the longitude of the ground location (in degrees)
     * @param altitude the altitude of the ground location (in m)
     * @param elevation the minimum elevation to start a visibility
     * @param satTLE the two-line element of the satellite
     * @param startDate the starting date for the calculation
     * @param duration the duration of the calculation (in s)
     * @return a {@link List} of {@link VisibilityEntry} objects
     */
    public static List<VisibilityEntry> getVisibilities(double lat, double lon, double altitude, double elevation,
                                                        final TLE satTLE, final AbsoluteDate startDate, double duration)
        throws OrekitException {

        // Propagator based on TLE propagation
        Propagator prop = TLEPropagator.selectExtrapolator(satTLE);

        final BodyShape earth = new OneAxisEllipsoid(ae, f, FramesFactory.getITRF2005());

        // Get station data
        final double latitude = Math.toRadians(lat);
        final double longitude = Math.toRadians(lon);

        final GeodeticPoint station = new GeodeticPoint(latitude, longitude, altitude);
        final TopocentricFrame stationFrame = new TopocentricFrame(earth, station, "ground location");

        prop.propagate(startDate);

        // Create an event collector
        final EventCollector collector = new EventCollector();

        // Add now the visibility detectors

        // TODO: check if 3 min is ok
        final double maxcheck = 60. * 3;

        // the detector for the effective elevation in place
        final VisibilityDetector stationVisibility = new VisibilityDetector(maxcheck, elevation, stationFrame,
                                                                            collector);
        prop.addEventDetector(stationVisibility);

        // Propagate from the start date for the fixed duration
        prop.propagate(startDate.shiftedBy(duration));

        return collector.getVisibilityEntries();
    }

    public static void fillVisibilityEntry(double lat, double lon, double altitude,
                                           final TLE satTLE, final VisibilityEntry entry)
        throws OrekitException {

        // Propagator based on TLE propagation
        Propagator prop = TLEPropagator.selectExtrapolator(satTLE);

        final BodyShape earth = new OneAxisEllipsoid(ae, f, FramesFactory.getITRF2005());

        // Get station data
        final double latitude = Math.toRadians(lat);
        final double longitude = Math.toRadians(lon);

        final GeodeticPoint station = new GeodeticPoint(latitude, longitude, altitude);
        final TopocentricFrame stationFrame = new TopocentricFrame(earth, station, "ground location");

        prop.propagate(new AbsoluteDate(entry.getStart(), TimeScalesFactory.getUTC()));

        final List<Point> points = new LinkedList<Point>();

        // calculate an entry every 20s
        prop.setMasterMode(20, new OrekitFixedStepHandler() {

            private static final long serialVersionUID = 1L;

            @Override
            public void init(SpacecraftState state, AbsoluteDate date) {
            }

            public void handleStep(SpacecraftState currentState, boolean isLast)
                throws PropagationException {

                try {
                    points.add(convertToPoint(currentState, stationFrame, earth));
                } catch (OrekitException e) {
                    logger.error("failed to convert a satellite state to a geodetic point", e);
                }
            }
        });

        // Propagate from the start date for the fixed duration
        prop.propagate(new AbsoluteDate(entry.getEnd(), TimeScalesFactory.getUTC()));
        // points.add(convertToPoint(finalState, stationFrame, earth));

        entry.setPoints(points);
    }

    /**
     * Transforms a {@link SpacecraftState} into a {@link GeodeticPoint} on the surface of the
     * given body.
     * 
     * @param state the state to be transformed
     * @param frame the frame associated with the surface of the body
     * @param body the body
     * @return the transformed {@link GeodeticPoint} 
     * @throws OrekitException
     */
    public static GeodeticPoint convertToGeodeticPoint(final SpacecraftState state,
                                                       final TopocentricFrame frame,
                                                       final BodyShape body)
        throws OrekitException {

        Vector3D pos = state.getPVCoordinates(frame).getPosition();
        GeodeticPoint gp = body.transform(pos, frame, state.getDate());
        return gp;

    }

    private static Point convertToPoint(final SpacecraftState state, final TopocentricFrame stationFrame,
                                        final BodyShape body)
        throws OrekitException {

        Vector3D pos = state.getPVCoordinates(stationFrame).getPosition();
        GeodeticPoint gp = body.transform(pos, stationFrame, state.getDate());
        double lat = Math.toDegrees(gp.getLatitude());
        double lon = Math.toDegrees(gp.getLongitude());

        double elevation = Math.toDegrees(stationFrame.getElevation(state.getPVCoordinates().getPosition(),
                                                                    state.getFrame(), state.getDate()));

        double azimuth = Math.toDegrees(stationFrame.getAzimuth(state.getPVCoordinates().getPosition(),
                                                                state.getFrame(), state.getDate()));

        return new Point(state.getDate().toDate(TimeScalesFactory.getUTC()), elevation, azimuth, lat, lon);

    }

    /**
     * Collects elevation events and creates corresponding visibility entries, taking into account zero degree and
     * effective elevation.
     */
    private static class EventCollector {

        private List<VisibilityEntry> entries;

        private TimeScale utcScale;

        public EventCollector()
            throws OrekitException {
            entries = new ArrayList<VisibilityEntry>();
            utcScale = TimeScalesFactory.getUTC();
        }

        /**
         * Returns the captures visibility entries.
         * 
         * @return a {@link List} of {@link VisibilityEntry} object
         */
        public List<VisibilityEntry> getVisibilityEntries() {
            return entries;
        }

        public void foundVisibility(double elevation, final AbsoluteDate startTime, final AbsoluteDate endTime) {
            VisibilityEntry entry = new VisibilityEntry();
            entry.setStart(startTime.toDate(utcScale));
            entry.setEnd(endTime.toDate(utcScale));
            entry.setElev(elevation);
            entries.add(entry);
        }
    }

    /**
     * Finder for a visibility event.
     */
    private static class VisibilityDetector
        extends ElevationDetector {

        private static final long serialVersionUID = 1L;

        private transient EventCollector collector;

        private AbsoluteDate startTime;

        private AbsoluteDate endTime;

        public VisibilityDetector(double maxCheck, double elevation, TopocentricFrame topo,
                                  final EventCollector collector)
            throws OrekitException {
            super(maxCheck, elevation, topo);

            this.collector = collector;
            startTime = endTime = null;
        }

        /**
         * For each finished event, create a visibility entry.
         */
        public Action eventOccurred(final SpacecraftState s, final boolean increasing)
            throws OrekitException {
            if (increasing) {
                startTime = s.getDate();
            } else {
                endTime = s.getDate();
                if (startTime != null && endTime != null) {
                    if (collector != null) {
                        collector.foundVisibility(getElevation(), startTime, endTime);
                    }
                    startTime = endTime = null;
                }
            }

            return Action.CONTINUE;
        }
    }

    public static void main(String[] args)
        throws Exception {
        OrekitConfiguration.configureOrekit();

        String line1 = "1 29601U 06052A   12082.22855714 -.00000038  00000-0  10000-3 0  6711";
        String line2 = "2 29601  56.0932  58.5006 0039010 354.4184   5.5265  2.00577835 39160";

        TLE tle = new TLE(line1, line2);

        AbsoluteDate startDate = new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

        System.out.println(startDate);
        System.out.println(startDate.shiftedBy(60L * 717.93));

        BoundedPropagator boundedProp = SatellitePropagation.getEphemeris(tle, startDate, 60L * 717.93, 180);

        System.out.println(boundedProp.getMinDate());
        System.out.println(boundedProp.getMaxDate());

        AbsoluteDate endDate = boundedProp.getMaxDate();
        AbsoluteDate curr = boundedProp.getMinDate();

        // terrestrial frame at an arbitrary date
        Frame ITRF2005 = FramesFactory.getITRF2005();

        final BodyShape earth = new OneAxisEllipsoid(ae, f, ITRF2005);

        // setup an arbitrary station on the surface of the earth
        double longitude = Math.toRadians(45.);
        double latitude = Math.toRadians(25.);
        double altitude = 0.;
        final GeodeticPoint station = new GeodeticPoint(latitude, longitude, altitude);

        final TopocentricFrame stationFrame = new TopocentricFrame(earth, station, "ground location");

        while (curr.compareTo(endDate) < 0) {
            final SpacecraftState state = boundedProp.propagate(curr);
            final GeodeticPoint gp = convertToGeodeticPoint(state, stationFrame, earth);

            double lat = Math.toDegrees(gp.getLatitude());
            double lon = Math.toDegrees(gp.getLongitude());
            System.out.println("lon=" + lon + ", lat=" + lat + ", alt=" + gp.getAltitude());

            curr = curr.shiftedBy(90);
        }
    }
}

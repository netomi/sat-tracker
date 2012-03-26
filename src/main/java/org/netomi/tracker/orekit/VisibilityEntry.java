package org.netomi.tracker.orekit;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a single visibility entry for a ground location.
 */
public class VisibilityEntry {

    private Date start;

    private Date end;

    private double elev;

    private List<Point> points;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public double getElev() {
        return elev;
    }

    public void setElev(double elevation) {
        this.elev = elevation;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class Point {

        private Date time;

        private double elev;

        private double azi;

        private double lat;

        private double lon;

        public Point(Date time, double elev, double azi, double lat, double lon) {
            super();
            this.time = time;
            this.elev = elev;
            this.azi = azi;
            this.lat = lat;
            this.lon = lon;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public double getElev() {
            return elev;
        }

        public void setElev(double elev) {
            this.elev = elev;
        }

        public double getAzi() {
            return azi;
        }

        public void setAzi(double azi) {
            this.azi = azi;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }
}

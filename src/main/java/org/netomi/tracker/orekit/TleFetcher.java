package org.netomi.tracker.orekit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.netomi.tracker.model.Satellite;
import org.netomi.tracker.model.TleEntry;
import org.netomi.tracker.service.SatelliteService;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.TimeScalesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Fetches latest TLE data from celestrak.com for configured satellites.
 */
@Configurable(preConstruction = true)
public class TleFetcher {

    private final static Logger logger = LoggerFactory.getLogger(TleFetcher.class);

    @Autowired
    private transient SatelliteService satService;

    public void fetchData()
        throws IOException, URISyntaxException, OrekitException {

        String[] urls = {
            "http://www.celestrak.com/NORAD/elements/stations.txt",
            "http://www.celestrak.com/NORAD/elements/gps-ops.txt"
        };

        for (String url : urls) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(new URL(url).toURI());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStreamReader reader = new InputStreamReader(entity.getContent());
                BufferedReader is = new BufferedReader(reader);
                readTleStream(is);
            }
        }
    }

    private void readTleStream(final BufferedReader is)
        throws IOException, OrekitException {
        int lineCount = 0;
        String line;
        String identifier = null;
        String line1 = null, line2 = null;
        while ((line = is.readLine()) != null) {
            switch (lineCount % 3) {
                case 0:
                    identifier = line;
                    break;

                case 1:
                    line1 = line;
                    break;

                case 2:
                    line2 = line;
                    break;
            }

            if (lineCount % 3 == 2) {
                TLE tle = new TLE(line1, line2);

                Satellite sat = satService.getSatellite(tle.getSatelliteNumber());
                if (sat != null) {
                    logger.info("found entry for identifier " + identifier);
                    TleEntry entry = new TleEntry();
                    entry.setEpoch(tle.getDate().toDate(TimeScalesFactory.getUTC()));
                    entry.setLine1(tle.getLine1());
                    entry.setLine2(tle.getLine2());
                    entry.setSatellite(sat);
                    satService.updateTleEntry(entry);
                }
            }

            lineCount++;
        }

        logger.info("added " + (lineCount / 3) + " TLE entries");
    }
}

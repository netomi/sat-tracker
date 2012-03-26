package org.netomi.tracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.netomi.tracker.model.Satellite;
import org.netomi.tracker.model.Satellite.SatelliteType;
import org.netomi.tracker.orekit.OrekitConfiguration;
import org.netomi.tracker.orekit.TleFetcher;
import org.netomi.tracker.service.SatelliteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Initializes the environment.
 */
public class Initializer {

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    private transient SatelliteService satService;

    public Initializer()
        throws Exception {
        logger.info("Initializing application environment.");

        // bridge jul to slf4j
        // SLF4JBridgeHandler.install();

        // check orekit configuration
        OrekitConfiguration.configureOrekit();
    }

    @PostConstruct
    void init() throws Exception {
        logger.info("Creating sample data...");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        InputStream in = this.getClass().getResourceAsStream("/sample_data.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        try {
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    StringTokenizer st = new StringTokenizer(line, ",");
                    long catalogNumber = Long.valueOf(st.nextToken());
                    String commonName = st.nextToken().trim();
                    String strType = st.nextToken().trim();
                    SatelliteType type = SatelliteType.valueOf(strType);
                    String designator = st.nextToken().trim();
                    String country = st.nextToken().trim();
                    String launchDate = st.nextToken().trim();
                    String launchSite = st.nextToken().trim();
                    String decayDate = st.nextToken().trim();
                    double period = Double.valueOf(st.nextToken().trim());
                    double inclination = Double.valueOf(st.nextToken().trim());
                    double apogee = Double.valueOf(st.nextToken().trim());
                    double perigee = Double.valueOf(st.nextToken().trim());
                    double rcs = Double.valueOf(st.nextToken().trim());
                    
                    Satellite s = new Satellite();
                    s.setCatalogNumber(catalogNumber);
                    s.setCommonName(commonName);
                    s.setType(type);
                    s.setDesignator(designator);
                    s.setCountry(country);
                    s.setLaunchDate(df.parse(launchDate));
                    s.setLaunchSite(launchSite);
                    
                    if (decayDate != null && !decayDate.equals("null")) {
                        s.setDecayDate(df.parse(decayDate));
                    }
                    
                    s.setPeriod(period);
                    s.setInclination(inclination);
                    s.setApogee(apogee);
                    s.setPerigee(perigee);
                    s.setRcs(rcs);
                    
                    satService.addSatellite(s);
                }
            } while (line != null);
        } finally {
            reader.close();
        }
        
        TleFetcher fetcher = new TleFetcher();
        fetcher.fetchData();
    }

    @PreDestroy
    void cleanup() {
        logger.info("Shutting down the database.");
        satService.shutdown();
    }
}

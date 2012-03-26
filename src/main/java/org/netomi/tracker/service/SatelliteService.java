package org.netomi.tracker.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.netomi.tracker.model.Satellite;
import org.netomi.tracker.model.TleEntry;

/**
 * Simple service interface to the {@link Satellite} entity.
 */
public interface SatelliteService {

    EntityManager getEntityManager();
    
    void addSatellite(Satellite sat);
    
    Satellite updateSatellite(Satellite sat);
    
    Satellite getSatellite(long catalogNumber);
    
    List<Satellite> getSatellites();
    
    TleEntry getLatestTleEntry(Satellite sat);
    
    TleEntry updateTleEntry(TleEntry tle);
    
    void shutdown();
}

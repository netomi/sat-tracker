package org.netomi.tracker.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.netomi.tracker.model.Satellite;
import org.netomi.tracker.model.TleEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SatelliteServiceImpl
    implements SatelliteService {

    private final static Logger logger = LoggerFactory.getLogger(SatelliteServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addSatellite(final Satellite sat) {
        if (logger.isDebugEnabled()) {
            logger.debug("addSatellite(" + sat + ")");
        }
        em.persist(sat);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Satellite updateSatellite(final Satellite sat) {
        if (logger.isDebugEnabled()) {
            logger.debug("updateSatellite(" + sat + ")");
        }
        return em.merge(sat);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Satellite getSatellite(final long catalogNumber) {
        if (logger.isDebugEnabled()) {
            logger.debug("getSatellite(int)");
        }
        Query q = em.createNamedQuery(Satellite.QUERY_BY_CATALOGID).setParameter("catalogNumber", catalogNumber);
        @SuppressWarnings("rawtypes")
        List result = q.getResultList();
        if (result == null || result.size() == 0) {
            return null;
        } else {
            return (Satellite) q.getSingleResult();
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Satellite> getSatellites() {
        if (logger.isDebugEnabled()) {
            logger.debug("getSatellites()");
        }
        return (List<Satellite>) em.createNamedQuery(Satellite.QUERY_ALL).getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TleEntry getLatestTleEntry(final Satellite sat) {
        if (logger.isDebugEnabled()) {
            logger.debug("getTleEntry(int)");
        }
        Query q = em.createNamedQuery(TleEntry.QUERY_BY_SATELLITE).setParameter("satellite", sat);
        @SuppressWarnings("rawtypes")
        List result = q.getResultList();
        if (result == null || result.size() == 0) {
            return null;
        } else {
            return (TleEntry) q.getSingleResult();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TleEntry updateTleEntry(final TleEntry tle) {
        if (logger.isDebugEnabled()) {
            logger.debug("updateTleEntry(" + tle + ")");
        }
        return em.merge(tle);
    }

    @Transactional
    public void shutdown() {
        em.createNativeQuery("SHUTDOWN").executeUpdate();
    }
}

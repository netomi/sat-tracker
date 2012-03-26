package org.netomi.tracker.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@NamedQueries({
    @NamedQuery(name = Satellite.QUERY_ALL, query = "SELECT s FROM Satellite s"),
    @NamedQuery(name = Satellite.QUERY_BY_CATALOGID, query = "SELECT s FROM Satellite s WHERE s.catalogNumber = :catalogNumber")
})
public class Satellite
    implements Serializable {

    /** The serializable UID. */
    private static final long serialVersionUID = 20120321L;

    public static final String QUERY_ALL = "Satellite.queryAll";
    public static final String QUERY_BY_CATALOGID = "Satellite.queryByCatalogId";

    public enum SatelliteType {
        SpaceStation,
        Weather,
        GPS,
    }
    
    @Id
    @Column(name = "catalogNumber")
    private Long catalogNumber;

    @Column(name = "name", nullable = false)
    private String commonName;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SatelliteType type;
    
    @Column(name = "designator", nullable = false)
    private String designator;

    @Column(name = "country", nullable = false)
    private String country;
    
    @Column(name = "launchDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date launchDate;

    @Column(name = "launchSite", nullable = false)
    private String launchSite;

    @Column(name = "decayDate", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date decayDate;
    
    @Column(name = "period", nullable = false)
    private double period;
    
    @Column(name = "inclination", nullable = false)
    private double inclination;

    @Column(name = "apogee", nullable = false)
    private double apogee;
    
    @Column(name = "perigee", nullable = false)
    private double perigee;
    
    @Column(name = "rcs", nullable = false)
    private double rcs;
    
    @Version
    private Timestamp version;

    // --------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------

    // --------------------------------------------------------------------
    // Getter/Setter methods
    // --------------------------------------------------------------------

    public Long getCatalogNumber() {
        return catalogNumber;
    }
    
    public void setCatalogNumber(Long catalogNumber) {
        this.catalogNumber = catalogNumber;
    }
    
    public String getCommonName() {
        return commonName;
    }
    
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    public SatelliteType getType() {
        return type;
    }
    
    public void setType(SatelliteType type) {
        this.type = type;
    }

    public String getDesignator() {
        return designator;
    }
    
    public void setDesignator(String designator) {
        this.designator = designator;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Date getLaunchDate() {
        return launchDate;
    }
    
    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }
    
    public String getLaunchSite() {
        return launchSite;
    }
    
    public void setLaunchSite(String launchSite) {
        this.launchSite = launchSite;
    }
    
    public Date getDecayDate() {
        return decayDate;
    }
    
    public void setDecayDate(Date decayDate) {
        this.decayDate = decayDate;
    }
    
    public double getPeriod() {
        return period;
    }
    
    public void setPeriod(double period) {
        this.period = period;
    }
    
    public double getInclination() {
        return inclination;
    }
    
    public void setInclination(double inclination) {
        this.inclination = inclination;
    }
    
    public double getApogee() {
        return apogee;
    }
    
    public void setApogee(double apogee) {
        this.apogee = apogee;
    }
    
    public double getPerigee() {
        return perigee;
    }
    
    public void setPerigee(double perigee) {
        this.perigee = perigee;
    }
    
    public double getRcs() {
        return rcs;
    }
    
    public void setRcs(double rcs) {
        this.rcs = rcs;
    }

    public Timestamp getVersion() {
        return version;
    }

    // --------------------------------------------------------------------
    // Serialization methods
    // --------------------------------------------------------------------    

    // exclude the version field from equals and hashCode
    private static final String[] excludedFields = { "version" };

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other, excludedFields );
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.catalogNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

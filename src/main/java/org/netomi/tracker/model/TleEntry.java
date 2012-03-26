package org.netomi.tracker.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@NamedQueries({
    @NamedQuery(name = TleEntry.QUERY_BY_SATELLITE, query = "SELECT t FROM TleEntry t WHERE t.satellite = :satellite")
})
public class TleEntry
    implements Serializable {

    /** The serializable UID. */
    private static final long serialVersionUID = 20120321L;

    public static final String QUERY_BY_SATELLITE = "TleEntry.queryBySatellite";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    
    @OneToOne(cascade = CascadeType.ALL)
    private Satellite satellite;
    
    @Column(name = "epoch", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date epoch;
    
    @Column(name = "line1", nullable = false)
    private String line1;

    @Column(name = "line2", nullable = false)
    private String line2;
    
    @Version
    private Timestamp version;

    // --------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------

    // --------------------------------------------------------------------
    // Getter/Setter methods
    // --------------------------------------------------------------------
    
    public int getId() {
        return id;
    }
    
    public Satellite getSatellite() {
        return satellite;
    }
    
    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
    }
    
    public Date getEpoch() {
        return epoch;
    }

    public void setEpoch(Date epoch) {
        this.epoch = epoch;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
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
        return new HashCodeBuilder().append(this.id).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

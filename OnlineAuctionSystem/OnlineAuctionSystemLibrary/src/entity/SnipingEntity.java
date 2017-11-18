/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author User
 */
@Entity
public class SnipingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snipingId;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar snipingDate;
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal maxAmount;
    @Column(nullable = false)
    private Boolean enabled;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CustomerEntity customerEntity;
    @ManyToOne
    @JoinColumn(nullable = false)
    private AuctionListingEntity auctionListingEntity;

    public SnipingEntity() {
    }
    
    public SnipingEntity(Calendar snipingDate, BigDecimal amount, Boolean enabled) {
        this.snipingDate = snipingDate;
        this.maxAmount = amount;
        this.enabled = enabled;
    }

    public Long getSnipingId() {
        return snipingId;
    }

    public void setSnipingId(Long snipingId) {
        this.snipingId = snipingId;
    }

    public Calendar getSnipingDate() {
        return snipingDate;
    }

    public void setSnipingDate(Calendar snipingDate) {
        this.snipingDate = snipingDate;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public AuctionListingEntity getAuctionListingEntity() {
        return auctionListingEntity;
    }

    public void setAuctionListingEntity(AuctionListingEntity auctionListingEntity) {
        this.auctionListingEntity = auctionListingEntity;
    }
    

    public int hashCode() {
        int hash = 0;
        hash += (snipingId != null ? snipingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SnipingEntity)) {
            return false;
        }
        SnipingEntity other = (SnipingEntity) object;
        if ((this.snipingId == null && other.snipingId != null) || (this.snipingId != null && !this.snipingId.equals(other.snipingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SnipingEntity[ id=" + snipingId + " ]";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author lowru
 */
@Entity
public class BidEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 255, nullable = false)
    private Long auctionTransactionId;
    @Column(nullable = false)
    private BigDecimal bidAmount;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateTime;
    @Column(nullable = true)
    private Boolean winningBid;
    @OneToOne
    private AuctionListingEntity winningAuctionListingEntity;
    @ManyToOne
    private AuctionListingEntity auctionListingEntity;
    @ManyToOne
    private CustomerEntity customerEntity;


    public BidEntity() {
    }

    public BidEntity(BigDecimal bidAmount, Date dateTime) {
        this.bidAmount = bidAmount;
        this.dateTime = dateTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (auctionTransactionId != null ? auctionTransactionId.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BidEntity)) {
            return false;
        }
        BidEntity other = (BidEntity) object;
        if ((this.auctionTransactionId == null && other.auctionTransactionId != null) || (this.auctionTransactionId != null && !this.auctionTransactionId.equals(other.auctionTransactionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AuctionTransactionEntity[ id=" + auctionTransactionId + " ]";
    }

    /**
     * @return the auctionTransactionId
     */
    public Long getAuctionTransactionId() {
        return auctionTransactionId;
    }

    /**
     * @param auctionTransactionId the auctionTransactionId to set
     */
    public void setAuctionTransactionId(Long auctionTransactionId) {
        this.auctionTransactionId = auctionTransactionId;
    }

    /**
     * @return the bidAmount
     */
    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    /**
     * @param bidAmount the bidAmount to set
     */
    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    /**
     * @return the dateTime
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public AuctionListingEntity getAuctionListingEntity() {
        return auctionListingEntity;
    }

    public void setAuctionListingEntity(AuctionListingEntity auctionListingEntity) {
        this.auctionListingEntity = auctionListingEntity;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }
    
    public AuctionListingEntity getWinningAuctionListingEntity() {
        return winningAuctionListingEntity;
    }

    public void setWinningAuctionListingEntity(AuctionListingEntity winningAuctionListingEntity) {
        this.winningAuctionListingEntity = winningAuctionListingEntity;
    }

    public Boolean getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(Boolean winningBid) {
        this.winningBid = winningBid;
    }
    
    
}

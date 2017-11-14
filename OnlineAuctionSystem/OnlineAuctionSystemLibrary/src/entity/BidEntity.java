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

@Entity
public class BidEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 255, nullable = false)
    private Long bidId;
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal bidAmount;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar dateTime;
    @Column(nullable = true)
    private Boolean winningBid;
//    @OneToOne(optional = true)
//    @JoinColumn(nullable = true)
//    private AuctionListingEntity winningAuctionListingEntity;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AuctionListingEntity auctionListingEntity;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CustomerEntity customerEntity;
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true)
    private ProxyBiddingEntity proxyBiddingEntity;

    public BidEntity() {
    }

    public BidEntity(BigDecimal bidAmount, Calendar dateTime) {
        this.bidAmount = bidAmount;
        this.dateTime = dateTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bidId != null ? bidId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BidEntity)) {
            return false;
        }
        BidEntity other = (BidEntity) object;
        if ((this.bidId == null && other.bidId != null) || (this.bidId != null && !this.bidId.equals(other.bidId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AuctionTransactionEntity[ id=" + bidId + " ]";
    }

    public Long getBidId() {
        return bidId;
    }

    public void setBidId(Long bidId) {
        this.bidId = bidId;
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
    public Calendar getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setDateTime(Calendar dateTime) {
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

//    public AuctionListingEntity getWinningAuctionListingEntity() {
//        return winningAuctionListingEntity;
//    }
//
//    public void setWinningAuctionListingEntity(AuctionListingEntity winningAuctionListingEntity) {
//        this.winningAuctionListingEntity = winningAuctionListingEntity;
//    }
    public Boolean getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(Boolean winningBid) {
        this.winningBid = winningBid;
    }

    public ProxyBiddingEntity getProxyBiddingEntity() {
        return proxyBiddingEntity;
    }

    public void setProxyBiddingEntity(ProxyBiddingEntity proxyBiddingEntity) {
        this.proxyBiddingEntity = proxyBiddingEntity;
    }
}

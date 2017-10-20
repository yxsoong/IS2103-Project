/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

@Entity
public class AuctionListingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionListingId;
    @Column(length = 255, nullable = false)
    private String itemName;
    @Column(nullable = true)
    private BigDecimal startingBidAmount;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar startDateTime;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar endDateTime;
    @Column(nullable = false)
    private BigDecimal reservePrice;
    @Column(nullable = false)
    private Boolean openListing;
    @Column(nullable = false)
    private Boolean enabled;
    @ManyToOne
    private AddressEntity deliveryAddress;
    @OneToOne(mappedBy = "winningAuctionListingEntity")
    private BidEntity bidEntity;
    @OneToMany(mappedBy = "auctionListingEntity")
    private List<BidEntity> bidEntities;
    @ManyToOne
    private EmployeeEntity employeeEntity;

    public AuctionListingEntity() {
    }

    public AuctionListingEntity(String itemName, BigDecimal startingBidAmount, Calendar startDateTime, Calendar endDateTime, BigDecimal reservePrice, Boolean openListing, Boolean enabled, AddressEntity deliveryAddress) {
        this.itemName = itemName;
        this.startingBidAmount = startingBidAmount;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reservePrice = reservePrice;
        this.openListing = openListing;
        this.enabled = enabled;
        this.deliveryAddress = deliveryAddress;
    }



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (auctionListingId != null ? auctionListingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuctionListingEntity)) {
            return false;
        }
        AuctionListingEntity other = (AuctionListingEntity) object;
        if ((this.auctionListingId == null && other.auctionListingId != null) || (this.auctionListingId != null && !this.auctionListingId.equals(other.auctionListingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AuctionListingEntity[ id=" + auctionListingId + " ]";
    }

    public Long getAuctionListingId() {
        return auctionListingId;
    }

    public void setAuctionListingId(Long auctionListingId) {
        this.auctionListingId = auctionListingId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getStartingBidAmount() {
        return startingBidAmount;
    }

    public void setStartingBidAmount(BigDecimal startingBidAmount) {
        this.startingBidAmount = startingBidAmount;
    }

    public Calendar getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Calendar startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Calendar getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Calendar endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BigDecimal getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(BigDecimal reservePrice) {
        this.reservePrice = reservePrice;
    }

    public Boolean getOpenListing() {
        return openListing;
    }

    public void setOpenListing(Boolean openListing) {
        this.openListing = openListing;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public AddressEntity getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddressEntity deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<BidEntity> getBidEntities() {
        return bidEntities;
    }

    public void setBidEntities(List<BidEntity> bidEntities) {
        this.bidEntities = bidEntities;
    }

    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public void setEmployeeEntity(EmployeeEntity employeeEntity) {
        this.employeeEntity = employeeEntity;
    }

    public BidEntity getBidEntity() {
        return bidEntity;
    }

    public void setBidEntity(BidEntity bidEntity) {
        this.bidEntity = bidEntity;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    @Column(nullable = true, precision = 18, scale = 4)
    private BigDecimal startingBidAmount;
    @Column(nullable = true, precision = 18, scale = 4)
    private BigDecimal currentBidAmount;
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
    @Column(nullable = false)
    private Boolean manualAssignment;
    @ManyToOne
    @JoinColumn(nullable = true)
    private AddressEntity deliveryAddress;
    @OneToOne(optional = true)
    @JoinColumn(nullable = true)
    private BidEntity winningBidEntity;
    @OneToMany(mappedBy = "auctionListingEntity")
    private List<BidEntity> bidEntities;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private EmployeeEntity employeeEntity;
    @OneToMany(mappedBy = "auctionListingEntity")
    private List<ProxyBiddingEntity> proxyBiddingEntities;
    @OneToMany(mappedBy = "auctionListingEntity")
    private List<SnipingEntity> snipingEntities;

    public AuctionListingEntity() {
        bidEntities = new ArrayList<>();
        proxyBiddingEntities = new ArrayList<>();
        snipingEntities = new ArrayList<>();
    }

    public AuctionListingEntity(String itemName, BigDecimal startingBidAmount, BigDecimal currentBidAmount, Calendar startDateTime, Calendar endDateTime, BigDecimal reservePrice, Boolean openListing, Boolean enabled, Boolean manualAssignment) {
        this();
        this.itemName = itemName;
        this.startingBidAmount = startingBidAmount;
        this.currentBidAmount = currentBidAmount;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reservePrice = reservePrice;
        this.openListing = openListing;
        this.enabled = enabled;
        this.manualAssignment = manualAssignment;
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

    public BigDecimal getCurrentBidAmount() {
        return currentBidAmount;
    }

    public void setCurrentBidAmount(BigDecimal currentBidAmount) {
        this.currentBidAmount = currentBidAmount;
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

    public Boolean getManualAssignment() {
        return manualAssignment;
    }

    public void setManualAssignment(Boolean manualAssignment) {
        this.manualAssignment = manualAssignment;
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

    public BidEntity getWinningBidEntity() {
        return winningBidEntity;
    }

    public void setWinningBidEntity(BidEntity winningBidEntity) {
        this.winningBidEntity = winningBidEntity;
    }

    public List<ProxyBiddingEntity> getProxyBiddingEntities() {
        return proxyBiddingEntities;
    }

    public void setProxyBiddingEntities(List<ProxyBiddingEntity> proxyBiddingEntities) {
        this.proxyBiddingEntities = proxyBiddingEntities;
    }

    public List<SnipingEntity> getSnipingEntities() {
        return snipingEntities;
    }

    public void setSnipingEntities(List<SnipingEntity> snipingEntities) {
        this.snipingEntities = snipingEntities;
    }
    
}

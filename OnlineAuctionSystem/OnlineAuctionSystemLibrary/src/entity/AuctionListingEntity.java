/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author lowru
 */
@Entity
public class AuctionListingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionListingId;
    private String itemName;
    private BigDecimal startingBidAmount;
    private Date startDateTime;
    private Date endDateTime;
    private BigDecimal reservePrice;
    @OneToOne
    private BidEntity winningBid;
    private Boolean open;
    private Boolean enable;
    @ManyToOne
    private AddressEntity deliveryAddress;
    @OneToMany(mappedBy = "auctionListingEntity")
    private List<BidEntity> bidEntities;
    @ManyToOne
    private EmployeeEntity employeeEntity;

    public AuctionListingEntity() {
    }

    public AuctionListingEntity(String itemName, BigDecimal startingBidAmount, Date startDateTime, Date endDateTime, BigDecimal reservePrice, BidEntity winningBid, Boolean open, Boolean enable, AddressEntity deliveryAddress) {
        this.itemName = itemName;
        this.startingBidAmount = startingBidAmount;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reservePrice = reservePrice;
        this.winningBid = winningBid;
        this.open = open;
        this.enable = enable;
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

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BigDecimal getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(BigDecimal reservePrice) {
        this.reservePrice = reservePrice;
    }

    public BidEntity getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(BidEntity winningBid) {
        this.winningBid = winningBid;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
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
    
    
}

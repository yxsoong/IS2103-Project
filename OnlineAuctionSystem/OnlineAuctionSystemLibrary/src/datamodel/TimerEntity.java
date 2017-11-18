/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.io.Serializable;
import java.math.BigDecimal;


public class TimerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
   
    private Long timerId;
    private Long auctionListingId;
    private String type;
    private BigDecimal maxAmount;
    private Long customerId;

    public TimerEntity() {
    }

    public TimerEntity(Long auctionListingId, String type, BigDecimal maxAmount, Long customerId) {
        this.auctionListingId = auctionListingId;
        this.type = type;
        this.maxAmount = maxAmount;
        this.customerId = customerId;
    }

    public Long getTimerId() {
        return timerId;
    }

    public void setTimerId(Long timerId) {
        this.timerId = timerId;
    }

    public Long getAuctionListingId() {
        return auctionListingId;
    }

    public void setAuctionListingId(Long auctionListingId) {
        this.auctionListingId = auctionListingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (timerId != null ? timerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TimerEntity)) {
            return false;
        }
        TimerEntity other = (TimerEntity) object;
        if ((this.timerId == null && other.timerId != null) || (this.timerId != null && !this.timerId.equals(other.timerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TimerEntity[ id=" + timerId + " ]";
    }
    
}

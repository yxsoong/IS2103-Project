/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import util.enumeration.TimerTypeEnum;


public class TimerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
   
    private Long timerId;
    private Long auctionListingId;
    private TimerTypeEnum type;
    private BigDecimal maxAmount;
    private Long customerId;
    private Calendar runDate;

    public TimerEntity() {
    }

    public TimerEntity(Long auctionListingId, TimerTypeEnum type, BigDecimal maxAmount, Long customerId, Calendar runDate) {
        this.auctionListingId = auctionListingId;
        this.type = type;
        this.maxAmount = maxAmount;
        this.customerId = customerId;
        this.runDate = runDate;
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

    public TimerTypeEnum getType() {
        return type;
    }

    public void setType(TimerTypeEnum type) {
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
    
    public Calendar getRunDate() {
        return runDate;
    }

    public void setRunDate(Calendar runDate) {
        this.runDate = runDate;
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

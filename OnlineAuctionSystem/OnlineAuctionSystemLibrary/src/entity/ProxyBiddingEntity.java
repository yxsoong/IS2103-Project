/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ProxyBiddingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proxyBiddingId;
    private BigDecimal maximumAmount;
    @OneToMany(mappedBy = "proxyBiddingEntity")
    private List<BidEntity> bidEntities;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CustomerEntity customerEntity;

    public ProxyBiddingEntity() {
    }

    public ProxyBiddingEntity(BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public Long getProxyBiddingId() {
        return proxyBiddingId;
    }

    public void setProxyBiddingId(Long proxyBiddingId) {
        this.proxyBiddingId = proxyBiddingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (proxyBiddingId != null ? proxyBiddingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the proxyBiddingId fields are not set
        if (!(object instanceof ProxyBiddingEntity)) {
            return false;
        }
        ProxyBiddingEntity other = (ProxyBiddingEntity) object;
        if ((this.proxyBiddingId == null && other.proxyBiddingId != null) || (this.proxyBiddingId != null && !this.proxyBiddingId.equals(other.proxyBiddingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProxyBiddingEntity[ id=" + proxyBiddingId + " ]";
    }

    public BigDecimal getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public List<BidEntity> getBidEntities() {
        return bidEntities;
    }

    public void setBidEntities(List<BidEntity> bidEntities) {
        this.bidEntities = bidEntities;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author User
 */
@Entity
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressID;
    @Column(nullable = false, length = 255)
    private String streetAddress;
    @Column(nullable = false, length = 10)
    private String unitNumber;
    @Column(nullable = false, length = 10)
    private String postalCode;
    private Boolean enabled;
//    @ManyToOne
//    @JoinColumn(nullable = false)
//    private CustomerEntity customerEntity;
    @OneToMany(mappedBy = "deliveryAddress")
    @JoinColumn(nullable = true)
    private List<AuctionListingEntity> auctionListingEntities;
    
    public AddressEntity() {
        auctionListingEntities = new ArrayList<>();
    }

    public AddressEntity(String streetAddress, String unitNumber, String postalCode, Boolean enabled) {
        this.streetAddress = streetAddress;
        this.unitNumber = unitNumber;
        this.postalCode = postalCode;
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (addressID != null ? addressID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AddressEntity)) {
            return false;
        }
        AddressEntity other = (AddressEntity) object;
        if ((this.addressID == null && other.addressID != null) || (this.addressID != null && !this.addressID.equals(other.addressID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AddressEntity[ id=" + addressID + " ]";
    }

    public Long getAddressID() {
        return addressID;
    }

    public void setAddressID(Long addressID) {
        this.addressID = addressID;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

//    public CustomerEntity getCustomerEntity() {
//        return customerEntity;
//    }
//
//    public void setCustomerEntity(CustomerEntity customerEntity) {
//        this.customerEntity = customerEntity;
//    }

    public List<AuctionListingEntity> getAuctionListingEntities() {
        return auctionListingEntities;
    }

    public void setAuctionListingEntities(List<AuctionListingEntity> auctionListingEntities) {
        this.auctionListingEntities = auctionListingEntities;
    }
    
    
}

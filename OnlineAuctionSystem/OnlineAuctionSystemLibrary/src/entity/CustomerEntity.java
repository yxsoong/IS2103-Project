/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author User
 */
@Entity
public class CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    @Column(nullable = false, length = 255)
    private String firstName;
    @Column(nullable = false, length = 255)
    private String lastName;
    @Column(unique = true, nullable = false, length = 20)
    private String identificationNo;
    @Column(unique = true, nullable = false, length = 15)
    private String phoneNumber;
    @Column(precision = 18, scale = 4)
    private BigDecimal creditBalance;
    @Column(nullable = false)
    private Boolean premium;
    @Column(unique = true, nullable = false, length = 255)
    private String username;
    @Column(nullable = false, length = 255)
    private String password;
    @OneToMany
    private List<AddressEntity> addressEntities;
    @OneToMany(mappedBy = "customerEntity")
    private List<CreditTransactionEntity> creditTransactions;
    @OneToMany(mappedBy = "customerEntity")
    private List<BidEntity> bidEntities;

    public CustomerEntity() {
        addressEntities = new ArrayList<>();
        creditTransactions = new ArrayList<>();
        bidEntities = new ArrayList<>();
    }

    public CustomerEntity(String firstName, String lastName, String identificationNo, String phoneNumber, BigDecimal creditBalance, Boolean premium, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identificationNo = identificationNo;
        this.phoneNumber = phoneNumber;
        this.creditBalance = creditBalance;
        this.premium = premium;
        this.username = username;
        this.password = password;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (customerId != null ? customerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CustomerEntity)) {
            return false;
        }
        CustomerEntity other = (CustomerEntity) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CustomerEntity[ id=" + customerId + " ]";
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentificationNo() {
        return identificationNo;
    }

    public void setIdentificationNo(String identificationNo) {
        this.identificationNo = identificationNo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AddressEntity> getAddressEntities() {
        return addressEntities;
    }

    public void setAddressEntities(List<AddressEntity> addressEntities) {
        this.addressEntities = addressEntities;
    }

    public List<CreditTransactionEntity> getCreditTransactions() {
        return creditTransactions;
    }

    public void setCreditTransactions(List<CreditTransactionEntity> creditTransactions) {
        this.creditTransactions = creditTransactions;
    }

    public List<BidEntity> getBidEntities() {
        return bidEntities;
    }

    public void setBidEntities(List<BidEntity> bidEntities) {
        this.bidEntities = bidEntities;
    }

    
}

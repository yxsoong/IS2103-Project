/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import util.enumeration.CreditTransactionTypeEnum;

/**
 *
 * @author User
 */
@Entity
public class CreditTransactionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditPackageTransactionId;
    @Column(precision = 18, scale = 4, nullable = false)
    private BigDecimal numberOfCredits;
    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private CreditTransactionTypeEnum transactionType;
    @ManyToOne
    @JoinColumn(nullable = false)
    private CustomerEntity customerEntity;
    @ManyToOne
    @JoinColumn(nullable = true)
    private CreditPackageEntity creditPackageEntity;

    public CreditTransactionEntity() {
    }

    public CreditTransactionEntity(BigDecimal numberOfCredits, CreditTransactionTypeEnum transactionType) {
        this.numberOfCredits = numberOfCredits;
        this.transactionType = transactionType;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (creditPackageTransactionId != null ? creditPackageTransactionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CreditTransactionEntity)) {
            return false;
        }
        CreditTransactionEntity other = (CreditTransactionEntity) object;
        if ((this.creditPackageTransactionId == null && other.creditPackageTransactionId != null) || (this.creditPackageTransactionId != null && !this.creditPackageTransactionId.equals(other.creditPackageTransactionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CreditPackageTransaction[ id=" + creditPackageTransactionId + " ]";
    }

    public Long getCreditPackageTransactionId() {
        return creditPackageTransactionId;
    }

    public void setCreditPackageTransactionId(Long creditPackageTransactionId) {
        this.creditPackageTransactionId = creditPackageTransactionId;
    }

    public BigDecimal getNumberOfCredits() {
        return numberOfCredits;
    }

    public void setNumberOfCredits(BigDecimal numberOfCredits) {
        this.numberOfCredits = numberOfCredits;
    }

    public CreditTransactionTypeEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CreditTransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public CreditPackageEntity getCreditPackageEntity() {
        return creditPackageEntity;
    }

    public void setCreditPackageEntity(CreditPackageEntity creditPackageEntity) {
        this.creditPackageEntity = creditPackageEntity;
    }
    
    
    
}

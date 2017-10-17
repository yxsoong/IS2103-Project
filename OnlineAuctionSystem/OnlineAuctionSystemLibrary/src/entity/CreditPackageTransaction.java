/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author User
 */
@Entity
public class CreditPackageTransaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditPackageTransactionId;
    private BigDecimal numberOfCredits;
    @Enumerated(EnumType.STRING)
    private CreditPackageTransaction transactionType;

    public CreditPackageTransaction() {
    }

    public CreditPackageTransaction(Long creditPackageTransactionId, BigDecimal numberOfCredits, CreditPackageTransaction transactionType) {
        this.creditPackageTransactionId = creditPackageTransactionId;
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
        if (!(object instanceof CreditPackageTransaction)) {
            return false;
        }
        CreditPackageTransaction other = (CreditPackageTransaction) object;
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

    public CreditPackageTransaction getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CreditPackageTransaction transactionType) {
        this.transactionType = transactionType;
    }
    
    
    
}

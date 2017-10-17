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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author User
 */
@Entity
public class CreditPackageEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditPackageId;
    private String creditPackageName;
    private BigDecimal price;
    private BigDecimal numberOfCredits;
    private Boolean enable;
    @ManyToOne
    private EmployeeEntity employeeEntity;
    @OneToMany(mappedBy = "creditPackageEntity")
    private List<CreditPackageTransactionEntity> creditPackageTransactionEntities;

    public CreditPackageEntity() {
    }

    public CreditPackageEntity(String creditPackageName, BigDecimal price, BigDecimal numberOfCredits, Boolean enable) {
        this.creditPackageName = creditPackageName;
        this.price = price;
        this.numberOfCredits = numberOfCredits;
        this.enable = enable;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (creditPackageId != null ? creditPackageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CreditPackageEntity)) {
            return false;
        }
        CreditPackageEntity other = (CreditPackageEntity) object;
        if ((this.creditPackageId == null && other.creditPackageId != null) || (this.creditPackageId != null && !this.creditPackageId.equals(other.creditPackageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CreditPackage[ id=" + creditPackageId + " ]";
    }

    public Long getCreditPackageId() {
        return creditPackageId;
    }

    public void setCreditPackageId(Long creditPackageId) {
        this.creditPackageId = creditPackageId;
    }

    public String getCreditPackageName() {
        return creditPackageName;
    }

    public void setCreditPackageName(String creditPackageName) {
        this.creditPackageName = creditPackageName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getNumberOfCredits() {
        return numberOfCredits;
    }

    public void setNumberOfCredits(BigDecimal numberOfCredits) {
        this.numberOfCredits = numberOfCredits;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public List<CreditPackageTransactionEntity> getCreditPackageTransaction() {
        return creditPackageTransactionEntities;
    }

    public void setCreditPackageTransaction(List<CreditPackageTransactionEntity> creditPackageTransactionEntities) {
        this.creditPackageTransactionEntities = creditPackageTransactionEntities;
    }

    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public void setEmployeeEntity(EmployeeEntity employeeEntity) {
        this.employeeEntity = employeeEntity;
    }
    
}

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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import util.enumeration.EmployeeAccessRightEnum;

/**
 *
 * @author User
 */
@Entity
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeID;
    @Column(nullable = false, length = 255)
    private String firstName;
    @Column(nullable = false, length = 255)
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeAccessRightEnum accessRight;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @OneToMany(mappedBy = "employeeEntity")
    @JoinColumn(nullable = true)
    private List<CreditPackageEntity> creditPackagesEntities;
    @OneToMany(mappedBy = "employeeEntity")
    @JoinColumn(nullable = true)
    private List<AuctionListingEntity> auctionListingEntities;
    
    public EmployeeEntity() {
        creditPackagesEntities = new ArrayList<>();
        auctionListingEntities = new ArrayList<>();
    }

    public EmployeeEntity(String firstName, String lastName, EmployeeAccessRightEnum accessRight, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accessRight = accessRight;
        this.username = username;
        this.password = password;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeID != null ? employeeID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmployeeEntity)) {
            return false;
        }
        EmployeeEntity other = (EmployeeEntity) object;
        if ((this.employeeID == null && other.employeeID != null) || (this.employeeID != null && !this.employeeID.equals(other.employeeID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.EmployeeEntity[ id=" + employeeID + " ]";
    }

    public Long getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Long employeeID) {
        this.employeeID = employeeID;
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

    public EmployeeAccessRightEnum getAccessRight() {
        return accessRight;
    }

    public void setAccessRight(EmployeeAccessRightEnum accessRight) {
        this.accessRight = accessRight;
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

    public List<CreditPackageEntity> getCreditPackages() {
        return creditPackagesEntities;
    }

    public void setCreditPackages(List<CreditPackageEntity> creditPackagesEntities) {
        this.creditPackagesEntities = creditPackagesEntities;
    }

    public List<CreditPackageEntity> getCreditPackagesEntities() {
        return creditPackagesEntities;
    }

    public void setCreditPackagesEntities(List<CreditPackageEntity> creditPackagesEntities) {
        this.creditPackagesEntities = creditPackagesEntities;
    }

    public List<AuctionListingEntity> getAuctionListingEntities() {
        return auctionListingEntities;
    }

    public void setAuctionListingEntities(List<AuctionListingEntity> auctionListingEntities) {
        this.auctionListingEntities = auctionListingEntities;
    }
    
    
}

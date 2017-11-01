/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AddressEntityControllerLocal;
import ejb.session.stateless.CreditPackageEntityControllerLocal;
import ejb.session.stateless.CreditTransactionEntityControllerLocal;
import ejb.session.stateless.CustomerEntityControllerLocal;
import ejb.session.stateless.EmployeeEntityControllerLocal;
import entity.AddressEntity;
import entity.CreditPackageEntity;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import entity.EmployeeEntity;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.CreditTransactionTypeEnum;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author User
 */
@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean {

    @EJB
    private CreditPackageEntityControllerLocal creditPackageEntityControllerLocal;
    
    @EJB
    private CreditTransactionEntityControllerLocal creditTransactionEntityControllerLocal;

    @EJB
    private AddressEntityControllerLocal addressEntityControllerLocal;

    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;
    
    @EJB
    private EmployeeEntityControllerLocal employeeEntityControllerLocal;
    
    
    
    

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    public DataInitializationSessionBean() {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        try
        {
            employeeEntityControllerLocal.retrieveEmployeeByUsername("admin");
        }
        catch(EmployeeNotFoundException ex)
        {
            initializeData();
        }
    }
    
    private void initializeData(){
        employeeEntityControllerLocal.createEmployee(new EmployeeEntity("A", "A", EmployeeAccessRightEnum.SYSADMIN, "admin", "password"));
        EmployeeEntity financeEmployee = employeeEntityControllerLocal.createEmployee(new EmployeeEntity("B", "B", EmployeeAccessRightEnum.FINANCE, "finance", "password"));
        employeeEntityControllerLocal.createEmployee(new EmployeeEntity("C", "C", EmployeeAccessRightEnum.SALES, "sales", "password"));
        
        CustomerEntity customerEntity = customerEntityControllerLocal.createCustomer(new CustomerEntity("A", "A", "123", "123", BigDecimal.ZERO, BigDecimal.ZERO, Boolean.FALSE, "customer", "password"));
        
        AddressEntity addressEntity1 = addressEntityControllerLocal.createAddress(new AddressEntity("Heng Mui Keng", "10-10", "111111", Boolean.TRUE));
        AddressEntity addressEntity2 = addressEntityControllerLocal.createAddress(new AddressEntity("Heng Mui Keng", "10-11", "222222", Boolean.TRUE));
        
        customerEntity.getAddressEntities().add(addressEntity1);
        customerEntity.getAddressEntities().add(addressEntity2);
        
        CreditPackageEntity creditPackageEntity = new CreditPackageEntity("Basic Package", BigDecimal.valueOf(100), BigDecimal.valueOf(100), Boolean.TRUE);
        creditPackageEntity.setEmployeeEntity(financeEmployee);
        CreditPackageEntity creditPackageEntity2 = new CreditPackageEntity("Premium Package", BigDecimal.valueOf(300), BigDecimal.valueOf(320), Boolean.TRUE);
        creditPackageEntity2.setEmployeeEntity(financeEmployee);
        creditPackageEntityControllerLocal.createNewCreditPackage(creditPackageEntity);
        creditPackageEntityControllerLocal.createNewCreditPackage(creditPackageEntity2);
        
        CreditTransactionEntity creditTransactionEntity1 = new CreditTransactionEntity(BigDecimal.valueOf(100), CreditTransactionTypeEnum.TOPUP);
        creditTransactionEntity1.setCreditPackageEntity(creditPackageEntity);
        creditTransactionEntity1.setCustomerEntity(customerEntity);
        creditTransactionEntityControllerLocal.createCreditTransactionEntity(creditTransactionEntity1);
        
        customerEntity.setCreditBalance(customerEntity.getCreditBalance().add(creditTransactionEntity1.getNumberOfCredits()));
        customerEntityControllerLocal.updateCustomer(customerEntity);
        
        CreditTransactionEntity creditTransactionEntity2 = new CreditTransactionEntity(BigDecimal.valueOf(320), CreditTransactionTypeEnum.TOPUP);
        creditTransactionEntity2.setCreditPackageEntity(creditPackageEntity2);
        creditTransactionEntity2.setCustomerEntity(customerEntity);
        creditTransactionEntityControllerLocal.createCreditTransactionEntity(creditTransactionEntity2);
        
        customerEntity.setCreditBalance(customerEntity.getCreditBalance().add(creditTransactionEntity2.getNumberOfCredits()));
        customerEntityControllerLocal.updateCustomer(customerEntity);
    }
}

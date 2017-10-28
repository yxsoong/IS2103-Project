/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AddressEntityControllerLocal;
import ejb.session.stateless.CustomerEntityControllerLocal;
import ejb.session.stateless.EmployeeEntityControllerLocal;
import entity.AddressEntity;
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
        employeeEntityControllerLocal.createEmployee(new EmployeeEntity("B", "B", EmployeeAccessRightEnum.FINANCE, "finance", "password"));
        employeeEntityControllerLocal.createEmployee(new EmployeeEntity("C", "C", EmployeeAccessRightEnum.SALES, "sales", "password"));
        
        CustomerEntity customerEntity = customerEntityControllerLocal.createCustomer(new CustomerEntity("A", "A", "123", "123", BigDecimal.ZERO, BigDecimal.ZERO, Boolean.FALSE, "customer", "password"));
        
        AddressEntity addressEntity1 = addressEntityControllerLocal.createAddress(new AddressEntity("Heng Mui Keng", "10-10", "111111", Boolean.TRUE));
        AddressEntity addressEntity2 = addressEntityControllerLocal.createAddress(new AddressEntity("Heng Mui Keng", "10-11", "222222", Boolean.TRUE));
        
        customerEntity.getAddressEntities().add(addressEntity1);
        customerEntity.getAddressEntities().add(addressEntity2);
    }
}

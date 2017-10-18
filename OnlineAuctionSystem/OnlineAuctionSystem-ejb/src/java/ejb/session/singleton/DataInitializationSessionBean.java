/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeEntityControllerLocal;
import entity.EmployeeEntity;
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
        employeeEntityControllerLocal.createEmployee(new EmployeeEntity("A", "A", EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR, "admin", "password"));
        employeeEntityControllerLocal.createEmployee(new EmployeeEntity("B", "B", EmployeeAccessRightEnum.FINANCE, "finance", "password"));
        employeeEntityControllerLocal.createEmployee(new EmployeeEntity("C", "C", EmployeeAccessRightEnum.SALES, "sales", "password"));
    }
}

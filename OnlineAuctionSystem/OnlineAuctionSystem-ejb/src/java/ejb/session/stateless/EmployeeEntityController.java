/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author User
 */
@Local(EmployeeEntityControllerLocal.class)
@Remote(EmployeeEntityControllerRemote.class)
@Stateless
public class EmployeeEntityController implements EmployeeEntityControllerRemote, EmployeeEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public void helloWorld(){
        System.out.println("Hello world");
    }
   
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;

@Local(CustomerEntityControllerLocal.class)
@Remote(CustomerEntityControllerRemote.class)
@Stateless
public class CustomerEntityController implements CustomerEntityControllerRemote, CustomerEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public CustomerEntity createCustomer(CustomerEntity customerEntity){
        em.persist(customerEntity);
        em.flush();
        em.refresh(customerEntity);
        
        return customerEntity;
    }

    @Override
    public Boolean checkUsername(String username) {
        Query query = em.createQuery("SELECT COUNT(c) FROM CustomerEntity c WHERE c.username = :inUsername");
        query.setParameter("inUsername", username);

        Long count = (Long) query.getSingleResult();

        return count > 0;
    }

    @Override
    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.username = :inUsername AND c.password = :inPassword");
        query.setParameter("inUsername", username);
        query.setParameter("inPassword", password);
       
        try{
            return (CustomerEntity)query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex){
            throw new InvalidLoginCredentialException("Invalid login credential!");
        }
    }
    
    @Override
    public void updateCustomer(CustomerEntity customerEntity){
        em.merge(customerEntity);
    }
}

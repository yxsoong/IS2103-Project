/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import datamodel.CreditBalance;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InsufficientCreditsException;
import util.exception.InvalidLoginCredentialException;

@Local(CustomerEntityControllerLocal.class)
@Remote(CustomerEntityControllerRemote.class)
@Stateless
public class CustomerEntityController implements CustomerEntityControllerRemote, CustomerEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        em.persist(customerEntity);
        em.flush();
        em.refresh(customerEntity);

        return customerEntity;

        // note: need to do catch constaintViolationException
    }

    @Override
    public Boolean checkUsername(String username) {
        Query query = em.createQuery("SELECT COUNT(c) FROM CustomerEntity c WHERE c.username = :inUsername");
        query.setParameter("inUsername", username);

        Long count = (Long) query.getSingleResult();

        return count > 0;
    }

    @Override
    public CustomerEntity retrieveCustomerById(Long id) {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, id);

        return customerEntity;
    }

    @Override
    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.username = :inUsername AND c.password = :inPassword");
        query.setParameter("inUsername", username);
        query.setParameter("inPassword", password);

        try {
            CustomerEntity customerEntity = (CustomerEntity) query.getSingleResult();
            //customerEntity.getAddressEntities().size();
            return customerEntity;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new InvalidLoginCredentialException("Invalid login credential!");
        }
    }

    @Override
    public void updateCustomer(CustomerEntity customerEntity) {
        em.merge(customerEntity);
    }

    @Override
    public void topUpCredits(Long customerId, BigDecimal amount, CreditTransactionEntity creditTransactionEntity) {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        customerEntity.setCreditBalance(customerEntity.getCreditBalance().add(amount));
        customerEntity.setAvailableBalance(customerEntity.getAvailableBalance().add(amount));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void useCredits(Long customerId, BigDecimal amount) throws InsufficientCreditsException {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        BigDecimal holdingBalance = customerEntity.getHoldingBalance();
        holdingBalance = holdingBalance.add(amount);
        
        if (holdingBalance.compareTo(customerEntity.getCreditBalance()) <= 0) {
            customerEntity.setHoldingBalance(holdingBalance);
        } else {
            throw new InsufficientCreditsException("Insufficient credits!");
        }

    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void refundCredits(Long customerId, BigDecimal amount){
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        
        customerEntity.setHoldingBalance(customerEntity.getHoldingBalance().subtract(amount));
    }
    
    @Override
    public void deductCreditBalance(Long customerId, BigDecimal amount){
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        
        customerEntity.setHoldingBalance(customerEntity.getHoldingBalance().subtract(amount));
        customerEntity.setCreditBalance(customerEntity.getCreditBalance().subtract(amount));
    }
    
    @Override
    public CreditBalance retrieveCreditBalance(Long customerId){
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        
        CreditBalance creditBalance = new CreditBalance(customerEntity.getCreditBalance(),customerEntity.getHoldingBalance(), customerEntity.getAvailableBalance());
        return creditBalance;
    }
}

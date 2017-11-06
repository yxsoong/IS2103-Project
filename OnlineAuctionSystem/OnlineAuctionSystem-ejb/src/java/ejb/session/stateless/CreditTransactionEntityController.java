/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BidEntity;
import entity.CreditPackageEntity;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CreditTransactionTypeEnum;
import util.exception.InsufficientCreditsException;
import util.exception.InvalidBidException;
import util.exception.PlaceBidException;

@Local(CreditTransactionEntityControllerLocal.class)
@Remote(CreditTransactionEntityControllerRemote.class)
@Stateless
public class CreditTransactionEntityController implements CreditTransactionEntityControllerRemote, CreditTransactionEntityControllerLocal {

    @EJB
    private BidEntityControllerLocal bidEntityController;

    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;
    
    
    @Resource
    private EJBContext eJBContext;
    
    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CreditTransactionEntity createCreditTransactionEntity(CreditTransactionEntity creditTransactionEntity) {
        em.persist(creditTransactionEntity);
        em.flush();
        em.refresh(creditTransactionEntity);
        return creditTransactionEntity;
    }

    @Override
    public List<CreditTransactionEntity> retrieveCreditTransactions(Long customerId) {
        Query query = em.createQuery("SELECT t FROM CreditTransactionEntity t WHERE t.customerEntity.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);

        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void purchaseCreditPackage(CreditPackageEntity creditPackageEntity, int quantity, Long customerId) {
        creditPackageEntity = em.merge(creditPackageEntity);
        CustomerEntity customerEntity = customerEntityControllerLocal.retrieveCustomerById(customerId);
        CreditTransactionEntity creditTransactionEntity = new CreditTransactionEntity(creditPackageEntity.getNumberOfCredits().abs().multiply(BigDecimal.valueOf(quantity)), CreditTransactionTypeEnum.TOPUP);
        creditTransactionEntity.setCustomerEntity(customerEntity);
        creditTransactionEntity.setCreditPackageEntity(creditPackageEntity);
        try {
            creditTransactionEntity = createCreditTransactionEntity(creditTransactionEntity);

            customerEntityControllerLocal.topUpCredits(customerId, creditPackageEntity.getNumberOfCredits().multiply(BigDecimal.valueOf(quantity)), creditTransactionEntity);

            creditPackageEntity.getCreditTransactionEntities().add(creditTransactionEntity);
            //customerEntity.getCreditTransactions().add(creditTransactionEntity);
            //customerEntityControllerLocal.updateCustomer(customerEntity);

        } catch (Exception ex) {
            eJBContext.setRollbackOnly();
        }
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void placeBid(BidEntity bidEntity) throws PlaceBidException{
        CustomerEntity customerEntity = em.find(CustomerEntity.class, bidEntity.getCustomerEntity().getCustomerId());
        CreditTransactionEntity creditTransactionEntity = new CreditTransactionEntity(bidEntity.getBidAmount(), CreditTransactionTypeEnum.USAGE);
        creditTransactionEntity.setCustomerEntity(customerEntity);
        
        try{
            customerEntityControllerLocal.useCredits(bidEntity.getCustomerEntity().getCustomerId(), bidEntity.getBidAmount());
            createCreditTransactionEntity(creditTransactionEntity);
            bidEntityController.createNewBid(bidEntity);
        } catch (InsufficientCreditsException | InvalidBidException ex){
            eJBContext.setRollbackOnly();
            throw new PlaceBidException(ex.getMessage());
        }
    }
}

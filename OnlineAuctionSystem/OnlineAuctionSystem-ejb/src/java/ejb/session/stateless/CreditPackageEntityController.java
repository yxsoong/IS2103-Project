package ejb.session.stateless;

import entity.CreditPackageEntity;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.List;
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
import util.exception.CreditPackageNotFoundException;

@Local(CreditPackageEntityControllerLocal.class)
@Remote(CreditPackageEntityControllerRemote.class)
@Stateless
public class CreditPackageEntityController implements CreditPackageEntityControllerRemote, CreditPackageEntityControllerLocal {

    @EJB
    private CreditTransactionEntityControllerLocal creditTransactionEntityControllerLocal;
    
    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;

    private EJBContext eJBContext;
    
    
    
    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public CreditPackageEntity createNewCreditPackage(CreditPackageEntity creditPackageEntity) {
        em.persist(creditPackageEntity);
        em.flush();
        em.refresh(creditPackageEntity);
        
        return creditPackageEntity;
    }
    
    @Override
    public CreditPackageEntity retrieveCreditPackageById(Long creditPackageId) throws CreditPackageNotFoundException {
        CreditPackageEntity creditPackageEntity = em.find(CreditPackageEntity.class, creditPackageId);
        
        if(creditPackageEntity != null){
            return creditPackageEntity;
        }
        else{
            throw new CreditPackageNotFoundException("Credit Package ID: " + creditPackageId  + " does not exist");
        }
    }
    
    @Override
    public List<CreditPackageEntity> retrieveAllCreditPackages(){
        Query query = em.createQuery("SELECT e FROM CreditPackageEntity e");
        
        return query.getResultList();
    }
    
    @Override
    public void updateCreditPackage(CreditPackageEntity creditPackageEntity){
        em.merge(creditPackageEntity);
    }
    
    @Override
    public void deleteCreditPackage(Long creditPackageId){
        try{
        CreditPackageEntity creditPackageEntity = retrieveCreditPackageById(creditPackageId);
        em.remove(creditPackageEntity);
        } catch (CreditPackageNotFoundException ex){
            
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void purchaseCreditPackage(CreditPackageEntity creditPackageEntity, int quantity, Long customerId){
        creditPackageEntity = em.merge(creditPackageEntity);
        CustomerEntity customerEntity = customerEntityControllerLocal.retrieveCustomerById(customerId);
        
        try{
           customerEntityControllerLocal.topUpCredits(customerId, creditPackageEntity.getNumberOfCredits().multiply(BigDecimal.valueOf(quantity)));
           
           CreditTransactionEntity creditTransactionEntity = new CreditTransactionEntity(creditPackageEntity.getNumberOfCredits().abs().multiply(BigDecimal.valueOf(quantity)), CreditTransactionTypeEnum.TOPUP);
           creditTransactionEntity.setCustomerEntity(customerEntity);
           creditTransactionEntity.setCreditPackageEntity(creditPackageEntity);
           creditTransactionEntity = creditTransactionEntityControllerLocal.createCreditTransactionEntity(creditTransactionEntity);
           
           customerEntity.getCreditTransactions().add(creditTransactionEntity);
           customerEntityControllerLocal.updateCustomer(customerEntity);
           
        } catch(Exception ex){
            eJBContext.setRollbackOnly();
        }
    }
}

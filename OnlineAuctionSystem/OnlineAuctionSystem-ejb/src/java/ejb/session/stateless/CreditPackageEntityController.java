package ejb.session.stateless;

import entity.CreditPackageEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CreditPackageNotFoundException;

@Local(CreditPackageEntityControllerLocal.class)
@Remote(CreditPackageEntityControllerRemote.class)
@Stateless
public class CreditPackageEntityController implements CreditPackageEntityControllerRemote, CreditPackageEntityControllerLocal {

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

        if (creditPackageEntity != null) {
            return creditPackageEntity;
        } else {
            throw new CreditPackageNotFoundException("Credit Package ID: " + creditPackageId + " does not exist");
        }
    }

    @Override
    public List<CreditPackageEntity> retrieveAllCreditPackages() throws CreditPackageNotFoundException{
        Query query = em.createQuery("SELECT e FROM CreditPackageEntity e");
        List<CreditPackageEntity> creditPackageEntities = query.getResultList();
        
        if(creditPackageEntities.isEmpty()){
            throw new CreditPackageNotFoundException("No credit packages available.");
        } else{
            return creditPackageEntities;
        }        
    }
    
    @Override
    public List<CreditPackageEntity> retrieveEnabledCreditPackages() throws CreditPackageNotFoundException{
        Query query = em.createQuery("SELECT c FROM CreditPackageEntity c WHERE c.enabled = true");
        List<CreditPackageEntity> creditPackageEntities = query.getResultList();
        
        if(creditPackageEntities.isEmpty()){
            throw new CreditPackageNotFoundException("No credit packages available.");
        } else{
            return creditPackageEntities;
        }        
    }

    @Override
    public void updateCreditPackage(CreditPackageEntity creditPackageEntity) {
        em.merge(creditPackageEntity);
    }

    @Override
    public void deleteCreditPackage(Long creditPackageId) {
        try {
            CreditPackageEntity creditPackageEntity = retrieveCreditPackageById(creditPackageId);
            if (creditPackageEntity.getCreditTransactionEntities().isEmpty()) {
                em.remove(creditPackageEntity);
            } else {
                creditPackageEntity.setEnabled(Boolean.FALSE);
            }
        } catch (CreditPackageNotFoundException ex) {

        }
    }
}

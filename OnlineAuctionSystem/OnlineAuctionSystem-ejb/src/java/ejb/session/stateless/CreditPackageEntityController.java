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
}

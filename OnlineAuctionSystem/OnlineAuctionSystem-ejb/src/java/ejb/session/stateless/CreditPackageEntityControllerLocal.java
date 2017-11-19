package ejb.session.stateless;

import entity.CreditPackageEntity;
import java.util.List;
import util.exception.CreditPackageNotFoundException;

public interface CreditPackageEntityControllerLocal {

    public CreditPackageEntity createNewCreditPackage(CreditPackageEntity creditPackageEntity);

    public CreditPackageEntity retrieveCreditPackageById(Long creditPackageId) throws CreditPackageNotFoundException;

    public List<CreditPackageEntity> retrieveAllCreditPackages()  throws CreditPackageNotFoundException;

    public void updateCreditPackage(CreditPackageEntity creditPackageEntity);

    public void deleteCreditPackage(Long creditPackageId);
    
    public List<CreditPackageEntity> retrieveEnabledCreditPackages() throws CreditPackageNotFoundException;
}

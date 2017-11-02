package ejb.session.stateless;

import entity.CreditPackageEntity;
import java.util.List;
import util.exception.CreditPackageNotFoundException;

public interface CreditPackageEntityControllerLocal {

    public CreditPackageEntity createNewCreditPackage(CreditPackageEntity creditPackageEntity);

    public CreditPackageEntity retrieveCreditPackageById(Long creditPackageId) throws CreditPackageNotFoundException;

    public List<CreditPackageEntity> retrieveAllCreditPackages();

    public void updateCreditPackage(CreditPackageEntity creditPackageEntity);

    public void deleteCreditPackage(Long creditPackageId);

     public void purchaseCreditPackage(CreditPackageEntity creditPackageEntity, int quantity, Long customerId);
}

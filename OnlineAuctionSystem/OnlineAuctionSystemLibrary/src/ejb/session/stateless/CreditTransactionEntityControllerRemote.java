/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BidEntity;
import entity.CreditPackageEntity;
import entity.CreditTransactionEntity;
import java.util.List;
import util.exception.PlaceBidException;

public interface CreditTransactionEntityControllerRemote {

    public CreditTransactionEntity createCreditTransactionEntity(CreditTransactionEntity creditTransactionEntity);

    public List<CreditTransactionEntity> retrieveCreditTransactions(Long customerId);

    public void purchaseCreditPackage(CreditPackageEntity creditPackageEntity, int quantity, Long customerId);

    public void placeBid(BidEntity bidEntity) throws PlaceBidException;

}

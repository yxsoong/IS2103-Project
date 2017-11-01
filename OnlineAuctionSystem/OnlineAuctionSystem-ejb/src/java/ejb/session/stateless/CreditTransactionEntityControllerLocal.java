/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditTransactionEntity;
import java.util.List;


public interface CreditTransactionEntityControllerLocal {
    public CreditTransactionEntity createCreditTransactionEntity(CreditTransactionEntity creditTransactionEntity);
    
    public List<CreditTransactionEntity> retrieveCreditTransactions(Long customerId);
}

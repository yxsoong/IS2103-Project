/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditTransactionEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Local(CreditTransactionEntityControllerLocal.class)
@Remote(CreditTransactionEntityControllerRemote.class)
@Stateless
public class CreditTransactionEntityController implements CreditTransactionEntityControllerRemote, CreditTransactionEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public List<CreditTransactionEntity> retrieveCreditTransactions(Long customerId){
        Query query = em.createQuery("SELECT t FROM CreditTransactionEntity t WHERE t.customerEntity.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        
        return query.getResultList();
    } 
}

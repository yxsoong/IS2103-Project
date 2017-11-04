/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BidEntity;
import java.math.BigDecimal;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author User
 */
@Local(BidEntityControllerLocal.class)
@Remote(BidEntityControllerRemote.class)
@Stateless
public class BidEntityController implements BidEntityControllerRemote, BidEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public BidEntity createNewBid(BidEntity bidEntity) {
        em.persist(bidEntity);
        em.flush();
        em.refresh(bidEntity);
        return bidEntity;
    }

    @Override
    public BigDecimal getBidIncrement(BigDecimal currentPrice) {
        if (currentPrice.compareTo(BigDecimal.valueOf(5000)) == 1) {
            return BigDecimal.valueOf(100);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(2500)) == 1) {
            return BigDecimal.valueOf(50);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(1000)) == 1) {
            return BigDecimal.valueOf(25);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(500)) == 1) {
            return BigDecimal.valueOf(10);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(250)) == 1) {
            return BigDecimal.valueOf(5);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(100)) == 1) {
            return BigDecimal.valueOf(2.50);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(25)) == 1) {
            return BigDecimal.valueOf(1.00);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(5)) == 1) {
            return BigDecimal.valueOf(0.50);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(1)) == 1) {
            return BigDecimal.valueOf(0.25);
        } else if (currentPrice.compareTo(BigDecimal.valueOf(0.01)) == 1) {
            return BigDecimal.valueOf(0.05);
        } else {
            return BigDecimal.ZERO;
        }

    }
}

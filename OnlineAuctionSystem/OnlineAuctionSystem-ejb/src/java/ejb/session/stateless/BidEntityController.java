/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InvalidBidException;

/**
 *
 * @author User
 */
@Local(BidEntityControllerLocal.class)
@Remote(BidEntityControllerRemote.class)
@Stateless
public class BidEntityController implements BidEntityControllerRemote, BidEntityControllerLocal {

    @EJB
    private AuctionListingEntityControllerLocal auctionListingEntityControllerLocal;

    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BidEntity createNewBid(BidEntity bidEntity) throws InvalidBidException{
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, bidEntity.getAuctionListingEntity().getAuctionListingId());
        //List<BidEntity> bidEntities = auctionListingEntity.getBidEntities();
        
        if((auctionListingEntity.getCurrentBidAmount() == null && bidEntity.getBidAmount().compareTo(auctionListingEntity.getStartingBidAmount()) > 0)
                || bidEntity.getBidAmount().compareTo(auctionListingEntity.getCurrentBidAmount()) > 0) {
            em.persist(bidEntity);
            auctionListingEntity.getBidEntities().add(bidEntity);
            auctionListingEntity.setCurrentBidAmount(bidEntity.getBidAmount());
            em.flush();
            em.refresh(bidEntity);
            return bidEntity;
        } else {
            throw new InvalidBidException("Bid is lower than current bid!");
        }

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

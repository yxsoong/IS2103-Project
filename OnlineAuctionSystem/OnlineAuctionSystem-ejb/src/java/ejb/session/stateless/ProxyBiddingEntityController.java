/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import datamodel.CreditBalance;
import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CustomerEntity;
import entity.ProxyBiddingEntity;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InsufficientCreditsException;
import util.exception.InvalidBidException;
import util.exception.ProxyBiddingNotFoundException;

/**
 *
 * @author User
 */
@Local(ProxyBiddingEntityControllerLocal.class)
@Remote(ProxyBiddingEntityControllerRemote.class)
@Stateless
public class ProxyBiddingEntityController implements ProxyBiddingEntityControllerRemote, ProxyBiddingEntityControllerLocal {

    @EJB
    private BidEntityControllerLocal bidEntityControllerLocal;

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public ProxyBiddingEntity createProxyBidding(ProxyBiddingEntity proxyBiddingEntity) throws InsufficientCreditsException {
        BigDecimal availableBal = proxyBiddingEntity.getCustomerEntity().getAvailableBalance();

        if (proxyBiddingEntity.getMaximumAmount().compareTo(availableBal) > 0) {
            throw new InsufficientCreditsException("Insufficient funds!");
        }

        em.persist(proxyBiddingEntity);
        em.flush();
        em.refresh(proxyBiddingEntity);

        AuctionListingEntity auctionListingEntity = proxyBiddingEntity.getAuctionListingEntity();
        auctionListingEntity.getProxyBiddingEntities().add(proxyBiddingEntity);
        CustomerEntity customerEntity = proxyBiddingEntity.getCustomerEntity();
        customerEntity.getProxyBiddingEntities().add(proxyBiddingEntity);

        BigDecimal currentBidAmt = auctionListingEntity.getCurrentBidAmount();

        if (currentBidAmt == null) {
            BigDecimal startingBidAmt = auctionListingEntity.getStartingBidAmount();
            BigDecimal increment = bidEntityControllerLocal.getBidIncrement(startingBidAmt);
            try {
                BidEntity bidEntity = new BidEntity(startingBidAmt.add(increment), Calendar.getInstance());
                bidEntity.setCustomerEntity(customerEntity);
                bidEntity.setAuctionListingEntity(auctionListingEntity);
                bidEntity = bidEntityControllerLocal.createNewBid(bidEntity);

                proxyBiddingEntity.getBidEntities().add(bidEntity);
                bidEntity.setProxyBiddingEntity(proxyBiddingEntity);
            } catch (InvalidBidException ex) {

            }
        } else {
            List<BidEntity> bidEntities = auctionListingEntity.getBidEntities();
            BidEntity lastBid = bidEntities.get(bidEntities.size() - 1);
            if (!lastBid.getCustomerEntity().equals(customerEntity)) {
                Calendar currentTimestamp = Calendar.getInstance();
                BigDecimal increment = bidEntityControllerLocal.getBidIncrement(currentBidAmt);

                try {
                    BidEntity bidEntity = new BidEntity(currentBidAmt.add(increment), currentTimestamp);
                    bidEntity.setCustomerEntity(customerEntity);
                    bidEntity.setAuctionListingEntity(auctionListingEntity);
                    bidEntity = bidEntityControllerLocal.createNewBid(bidEntity);

                    proxyBiddingEntity.getBidEntities().add(bidEntity);
                    bidEntity.setProxyBiddingEntity(proxyBiddingEntity);
                } catch (InvalidBidException ex) {

                }
            }
        }

        return proxyBiddingEntity;
    }

    @Override
    public ProxyBiddingEntity retrieveHighestProxyBid(Long auctionListingId) throws ProxyBiddingNotFoundException{
        Query query = em.createQuery("SELECT p FROM ProxyBiddingEntity p WHERE p.auctionListingEntity.auctionListingId = :inAuctionListingId AND p.enabled = true ORDER BY p.maximumAmount");
        query.setParameter("inAuctionListingId", auctionListingId);
        
        List<ProxyBiddingEntity> proxyBiddingEntities = query.getResultList();
        
        if(!proxyBiddingEntities.isEmpty()){
            return proxyBiddingEntities.get(0);
        } else {
            throw new ProxyBiddingNotFoundException();
        }
    }
}

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
import util.exception.InvalidProxyBidException;
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
    public ProxyBiddingEntity createProxyBidding(ProxyBiddingEntity proxyBiddingEntity) throws InsufficientCreditsException, InvalidProxyBidException {
        BigDecimal availableBal = proxyBiddingEntity.getCustomerEntity().getAvailableBalance();

        if (proxyBiddingEntity.getMaximumAmount().compareTo(availableBal) > 0) {
            throw new InsufficientCreditsException("Insufficient funds!");
        }

        AuctionListingEntity auctionListingEntity = proxyBiddingEntity.getAuctionListingEntity();
        CustomerEntity customerEntity = proxyBiddingEntity.getCustomerEntity();

        Query query = em.createQuery("SELECT p FROM ProxyBiddingEntity p WHERE p.enabled = true AND p.customerEntity.customerId = :inCustomerId AND p.auctionListingEntity.auctionListingId = :inAuctionListingId");
        query.setParameter("inCustomerId", customerEntity.getCustomerId());
        query.setParameter("inAuctionListingId", auctionListingEntity.getAuctionListingId());

        List<ProxyBiddingEntity> proxyBiddingEntities = query.getResultList();

        for (ProxyBiddingEntity oldProxyBiddingEntity : proxyBiddingEntities) {
            oldProxyBiddingEntity.setEnabled(Boolean.FALSE);
        }

        em.persist(proxyBiddingEntity);

        auctionListingEntity.getProxyBiddingEntities().add(proxyBiddingEntity);

        customerEntity.getProxyBiddingEntities().add(proxyBiddingEntity);

        BigDecimal currentBidAmt = auctionListingEntity.getCurrentBidAmount();

        if (currentBidAmt == null) {
            BigDecimal startingBidAmt = auctionListingEntity.getStartingBidAmount();
            BigDecimal increment = bidEntityControllerLocal.getBidIncrement(startingBidAmt);
            BigDecimal nextBid = startingBidAmt.add(increment);
            if (nextBid.compareTo(proxyBiddingEntity.getMaximumAmount()) > 0) {
                throw new InvalidProxyBidException("Proxy bid has to be greater than or equal to current bid.");
            }
            try {
                BidEntity bidEntity = new BidEntity(nextBid, Calendar.getInstance());
                bidEntity.setCustomerEntity(customerEntity);
                bidEntity.setAuctionListingEntity(auctionListingEntity);
                bidEntity = bidEntityControllerLocal.createNewBid(bidEntity);

                proxyBiddingEntity.getBidEntities().add(bidEntity);
                bidEntity.setProxyBiddingEntity(proxyBiddingEntity);
            } catch (InvalidBidException ex) {

            }
        } else {
            query = em.createQuery("SELECT b FROM BidEntity b WHERE b.auctionListingEntity.auctionListingId = :inAuctionListingId ORDER BY b.bidId DESC");
            query.setParameter("inAuctionListingId", proxyBiddingEntity.getAuctionListingEntity().getAuctionListingId());
            List<BidEntity> bidEntities = query.getResultList();
            BidEntity lastBid = bidEntities.get(0);
            if (!lastBid.getCustomerEntity().equals(customerEntity)) {
                Calendar currentTimestamp = Calendar.getInstance();
                BigDecimal increment = bidEntityControllerLocal.getBidIncrement(currentBidAmt);
                
                BigDecimal nextBid = currentBidAmt.add(increment);
                if (nextBid.compareTo(proxyBiddingEntity.getMaximumAmount()) > 0) {
                    throw new InvalidProxyBidException("Proxy bid has to be greater than or equal to current bid.");
                }

                try {
                    BidEntity bidEntity = new BidEntity(currentBidAmt.add(increment), currentTimestamp);
                    bidEntity.setCustomerEntity(customerEntity);
                    bidEntity.setAuctionListingEntity(auctionListingEntity);
                    bidEntity = bidEntityControllerLocal.createNewBid(bidEntity);

                    proxyBiddingEntity.getBidEntities().add(bidEntity);
                    bidEntity.setProxyBiddingEntity(proxyBiddingEntity);
                } catch (InvalidBidException ex) {

                }
            } else {
                if (lastBid.getProxyBiddingEntity() != null) {
                    lastBid.setProxyBiddingEntity(proxyBiddingEntity);
                }
            }
        }

        em.flush();
        em.refresh(proxyBiddingEntity);
        return proxyBiddingEntity;
    }

    @Override
    public ProxyBiddingEntity retrieveHighestProxyBid(Long auctionListingId) throws ProxyBiddingNotFoundException {
        Query query = em.createQuery("SELECT p FROM ProxyBiddingEntity p WHERE p.auctionListingEntity.auctionListingId = :inAuctionListingId AND p.enabled = true ORDER BY p.maximumAmount");
        query.setParameter("inAuctionListingId", auctionListingId);

        List<ProxyBiddingEntity> proxyBiddingEntities = query.getResultList();

        if (!proxyBiddingEntities.isEmpty()) {
            return proxyBiddingEntities.get(0);
        } else {
            throw new ProxyBiddingNotFoundException();
        }
    }

    @Override
    public void disableProxyBids(Long auctionListingId) {
        Query query = em.createQuery("SELECT p FROM ProxyBiddingEntity p WHERE p.auctionListingEntity.auctionListingId = :inAuctionListId");
        query.setParameter("inAuctionListId", auctionListingId);

        List<ProxyBiddingEntity> proxyBiddingEntities = query.getResultList();

        for (ProxyBiddingEntity proxyBiddingEntity : proxyBiddingEntities) {
            proxyBiddingEntity.setEnabled(Boolean.FALSE);
        }
    }
}

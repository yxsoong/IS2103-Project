/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CustomerEntity;
import entity.ProxyBiddingEntity;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InsufficientCreditsException;
import util.exception.InvalidBidException;

/**
 *
 * @author User
 */
@Local(BidEntityControllerLocal.class)
@Remote(BidEntityControllerRemote.class)
@Stateless
public class BidEntityController implements BidEntityControllerRemote, BidEntityControllerLocal {

    @Resource
    private EJBContext eJBContext;

    @EJB
    private AuctionListingEntityControllerLocal auctionListingEntityControllerLocal;

    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BidEntity createNewBid(BidEntity bidEntity) throws InvalidBidException {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, bidEntity.getCustomerEntity().getCustomerId());
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, bidEntity.getAuctionListingEntity().getAuctionListingId());
        
        System.out.println("checking " + bidEntity.getBidAmount() + " " + auctionListingEntity.getStartingBidAmount());
        try {
            if ((auctionListingEntity.getCurrentBidAmount() == null && bidEntity.getBidAmount().compareTo(auctionListingEntity.getStartingBidAmount()) > 0)
                    || bidEntity.getBidAmount().compareTo(auctionListingEntity.getCurrentBidAmount()) > 0) {
                em.persist(bidEntity);
                auctionListingEntity.getBidEntities().add(bidEntity);
                auctionListingEntity.setCurrentBidAmount(bidEntity.getBidAmount());
                
                
                em.flush();
                em.refresh(bidEntity);

                System.out.println("current " + auctionListingEntity.getCurrentBidAmount());
            } else {
                throw new InvalidBidException("Bid is lower than current bid!");
            }
            customerEntityControllerLocal.useCredits(customerEntity.getCustomerId(), bidEntity.getBidAmount());

            //refund
            Query query = em.createQuery("SELECT b FROM BidEntity b WHERE b.auctionListingEntity.auctionListingId = :inAuctionListingId ORDER BY b.bidId DESC" );
            query.setParameter("inAuctionListingId", auctionListingEntity.getAuctionListingId());
            
            List<BidEntity> bidEntities = query.getResultList();

            if (bidEntities.size() > 1) {
                BidEntity refundBid = bidEntities.get(1);
                //System.out.println(refundBid.getCustomerEntity().getCustomerId() + " " + refundBid.getBidAmount());
                customerEntityControllerLocal.refundCredits(refundBid.getCustomerEntity().getCustomerId(), refundBid.getBidAmount());
            }

            //check proxyBids
            checkProxyBids(auctionListingEntity, bidEntity, customerEntity);

            return bidEntity;
        } catch (InsufficientCreditsException ex) {
            eJBContext.setRollbackOnly();
            Query query = em.createQuery("SELECT p FROM ProxyBiddingEntity p WHERE p.auctionListingEntity.auctionListingId = :inAuctionListingId AND p.customerEntity.customerId = :inCustomerId AND p.enabled = true");
            query.setParameter("inAuctionListingId", auctionListingEntity.getAuctionListingId());
            query.setParameter("inCustomerId", customerEntity.getCustomerId());

            try {
                ProxyBiddingEntity proxyBiddingEntity = (ProxyBiddingEntity) query.getSingleResult();
                proxyBiddingEntity.setEnabled(Boolean.FALSE);
            } catch (NonUniqueResultException | NoResultException exception) {
                System.out.println("Not proxy bid");
            }

            throw new InvalidBidException(ex.getMessage());
        }

    }

    private void checkProxyBids(AuctionListingEntity auctionListingEntity, BidEntity bidEntity, CustomerEntity customerEntity) {
        em.refresh(auctionListingEntity);
        
        if(!auctionListingEntity.getOpenListing()){
            return;
        }
        Query query = em.createQuery("SELECT p FROM ProxyBiddingEntity p WHERE p.auctionListingEntity.auctionListingId = :inAuctionListingId AND p.enabled = true ORDER BY p.proxyBiddingId");
        query.setParameter("inAuctionListingId", auctionListingEntity.getAuctionListingId());
        List<ProxyBiddingEntity> proxyBiddingEntities = query.getResultList();

        if (!proxyBiddingEntities.isEmpty()) {
            for (ProxyBiddingEntity proxyBiddingEntity : proxyBiddingEntities) {
                em.refresh(proxyBiddingEntity);

                BigDecimal nextBidAmount = bidEntity.getBidAmount().add(getBidIncrement(bidEntity.getBidAmount()));
                if (proxyBiddingEntity.getCustomerEntity().equals(customerEntity) && proxyBiddingEntity.getAuctionListingEntity().equals(auctionListingEntity)) {
                    if (proxyBiddingEntity.getMaximumAmount().compareTo(nextBidAmount) < 0) {
                        proxyBiddingEntity.setEnabled(Boolean.FALSE);
                    }
                } else {

                    if (proxyBiddingEntity.getMaximumAmount().compareTo(nextBidAmount) >= 0) {
                        if(proxyBiddingEntity.getCustomerEntity().getAvailableBalance().compareTo(nextBidAmount) < 0){
                            proxyBiddingEntity.setEnabled(Boolean.FALSE);
                            continue;
                        }
                        BidEntity newBidEntity = new BidEntity();
                        newBidEntity.setBidAmount(nextBidAmount);
                        newBidEntity.setDateTime(Calendar.getInstance());
                        newBidEntity.setCustomerEntity(proxyBiddingEntity.getCustomerEntity());
                        newBidEntity.setAuctionListingEntity(auctionListingEntity);
                        newBidEntity.setProxyBiddingEntity(proxyBiddingEntity);

                        try {
                            newBidEntity = createNewBid(newBidEntity);
                            proxyBiddingEntity.getBidEntities().add(newBidEntity);
                        } catch (InvalidBidException ex) {

                        }

                    } else {
                        proxyBiddingEntity.setEnabled(Boolean.FALSE);
                    }
                }
            }
        }


        /*List<ProxyBiddingEntity> proxyBiddingEntities = query.getResultList();
        if (!proxyBiddingEntities.isEmpty()) {
            int startIndex = 0;
            ProxyBiddingEntity highestProxy = proxyBiddingEntities.get(startIndex);
            System.out.println(highestProxy);
            CustomerEntity highestCustomerEntity = highestProxy.getCustomerEntity();
            System.out.println(highestCustomerEntity);
            if (highestCustomerEntity.equals(customerEntity)) {
                return;
            }
            BigDecimal newBidAmt = BigDecimal.ZERO;
            BigDecimal minimumBidAmt = bidEntity.getBidAmount().add(getBidIncrement(bidEntity.getBidAmount()));
            if (proxyBiddingEntities.get(startIndex).getMaximumAmount().compareTo(minimumBidAmt) > 0) {
                if (proxyBiddingEntities.size() > 1) {
                    BigDecimal secondHighest = proxyBiddingEntities.get(1).getMaximumAmount();
                    BigDecimal increment = getBidIncrement(secondHighest);
                    newBidAmt = increment.add(secondHighest);
                } else {
                    newBidAmt = minimumBidAmt;
                }
                startIndex = 1;
            }

            for (int i = startIndex; i < proxyBiddingEntities.size(); i++) {
                proxyBiddingEntities.get(i).setEnabled(Boolean.FALSE);
                customerEntityControllerLocal.refundCredits(proxyBiddingEntities.get(i).getCustomerEntity().getCustomerId(), proxyBiddingEntities.get(i).getMaximumAmount());
                proxyBiddingEntities.get(i).setMaximumAmount(BigDecimal.ZERO);
            }

            if (startIndex > 0) {
                try {
                    BidEntity newBidEntity = new BidEntity(newBidAmt, Calendar.getInstance());
                    newBidEntity.setCustomerEntity(highestProxy.getCustomerEntity());
                    newBidEntity.setAuctionListingEntity(auctionListingEntity);
                    newBidEntity = createNewBid(newBidEntity);

                    proxyBiddingEntities.get(0).getBidEntities().add(bidEntity);
                    newBidEntity.setProxyBiddingEntity(proxyBiddingEntities.get(0));
                } catch (InvalidBidException ex) {

                }
            }
        }*/
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.BidEntity;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AuctionListingNotFoundException;

@Local(AuctionListingEntityControllerLocal.class)
@Remote(AuctionListingEntityControllerRemote.class)
@Stateless
public class AuctionListingEntityController implements AuctionListingEntityControllerRemote, AuctionListingEntityControllerLocal {

    @EJB
    private TimerSessionBeanLocal timerSessionBeanLocal;
    
    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public AuctionListingEntity createAuctionListing(AuctionListingEntity auctionListingEntity) {
        em.persist(auctionListingEntity);
        em.flush();
        em.refresh(auctionListingEntity);
        timerSessionBeanLocal.createTimers(auctionListingEntity.getAuctionListingId(), auctionListingEntity.getStartDateTime(), "openListing");
        timerSessionBeanLocal.createTimers(auctionListingEntity.getAuctionListingId(), auctionListingEntity.getEndDateTime(), "closeListing");
        return auctionListingEntity;
    }

    @Override
    public AuctionListingEntity retrieveAuctionListingById(Long auctionListingId) throws AuctionListingNotFoundException {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);

        if (auctionListingEntity != null) {
            return auctionListingEntity;
        } else {
            throw new AuctionListingNotFoundException("Auction Listing ID: " + auctionListingId + " does not exist");
        }
    }

    @Override
    public List<AuctionListingEntity> retrieveAllAuctionListings() throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        return auctionListingEntities;
    }

    @Override
    public List<AuctionListingEntity> retrieveAllAuctionListingsBelowReservePrice() {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.bidEntities.bidAmount < a.reservePrice");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        return auctionListingEntities;
    }

    @Override
    public List<AuctionListingEntity> retrieveAllActiveAuctionListings() throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.openListing = true");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        return auctionListingEntities;
    }

    @Override
    public AuctionListingEntity retrieveActiveAuctionListing(Long auctionListingId) throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.auctionListingId = :inAuctionListingId  AND a.openListing = true");
        query.setParameter("inAuctionListingId", auctionListingId);
        
        try{
            return (AuctionListingEntity) query.getSingleResult();
        } catch(NonUniqueResultException | NoResultException ex){
            throw new AuctionListingNotFoundException("No auction listing with ID: " + auctionListingId);
        }
    }

    @Override
    public void removeDeliveryAddress(AddressEntity addressEntity) {
//        Query query = em.createQuery("DELETE a.deliveryAddress FROM AuctionListingEntity a WHERE a.deliveryAddress LIKE addressEntity");
//        em.merge(addressEntity);
//        em.remove(addressEntity);
//        em.flush();
//        em.refresh(addressEntity);
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.deliveryAddress = ?1");
        query.setParameter(1, addressEntity);
        List<AuctionListingEntity> addressesToRemove = query.getResultList();
        for (AuctionListingEntity auctionListingEntity : addressesToRemove) {
            auctionListingEntity.setDeliveryAddress(null);
        }
    }

    @Override
    public void openAuctionListings() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar currentDateTime = Calendar.getInstance();
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.startDateTime <= CURRENT_TIMESTAMP AND a.openListing = false");

        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            auctionListingEntity.setOpenListing(Boolean.TRUE);
        }
    }

    @Override
    public void openAuctionListing(Long auctionListingId) {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);
        auctionListingEntity.setOpenListing(Boolean.TRUE);
    }

    @Override
    public void closeAuctionListings() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar currentDateTime = Calendar.getInstance();
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.endDateTime <= CURRENT_TIMESTAMP AND a.openListing = true");

        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            auctionListingEntity.setOpenListing(Boolean.FALSE);
        }
    }

    @Override
    public void closeAuctionListing(Long auctionListingId) {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);
        auctionListingEntity.setOpenListing(Boolean.FALSE);

        List<BidEntity> bidEntities = auctionListingEntity.getBidEntities();

        if (!bidEntities.isEmpty()) {
            BigDecimal reservePrice = auctionListingEntity.getReservePrice();
            BidEntity bidEntity = bidEntities.get(bidEntities.size() - 1);
            BigDecimal lastBidPrice = bidEntity.getBidAmount();

            if (reservePrice == null || reservePrice.compareTo(lastBidPrice) < 0) {
                bidEntity.setWinningBid(Boolean.TRUE);
                auctionListingEntity.setWinningBidEntity(bidEntity);
                customerEntityControllerLocal.deductCreditBalance(bidEntity.getCustomerEntity().getCustomerId(), lastBidPrice);
            } else if(reservePrice.compareTo(lastBidPrice) >= 0) {
                // should we have a attribute to mark the auction listing as required intervention?
            }
        }
    }
    
    @Override
    public List<AuctionListingEntity> retrieveWonAuctionListings(Long customerId){
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.winningBidEntity.customerEntity.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        
        return query.getResultList();
    }
    
    @Override
    public void updateAuctionListing(AuctionListingEntity auctionListingEntity){
        em.merge(auctionListingEntity);
    }
}

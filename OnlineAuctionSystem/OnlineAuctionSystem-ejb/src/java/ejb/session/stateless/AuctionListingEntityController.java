/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import entity.EmployeeEntity;
import java.math.BigDecimal;
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
import util.enumeration.CreditTransactionTypeEnum;
import util.enumeration.TimerTypeEnum;
import util.exception.AuctionListingNotFoundException;
import util.exception.InvalidAuctionListingException;

@Local(AuctionListingEntityControllerLocal.class)
@Remote(AuctionListingEntityControllerRemote.class)
@Stateless
public class AuctionListingEntityController implements AuctionListingEntityControllerRemote, AuctionListingEntityControllerLocal {

    @EJB
    private CreditTransactionEntityControllerLocal creditTransactionEntityControllerLocal;

    @EJB
    private TimerSessionBeanLocal timerSessionBeanLocal;

    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public AuctionListingEntity createAuctionListing(AuctionListingEntity auctionListingEntity, Long employeeId) throws InvalidAuctionListingException{
        if(auctionListingEntity.getStartDateTime().compareTo(Calendar.getInstance()) <= 0){
            throw new InvalidAuctionListingException("Invalid start date time");
        }
        EmployeeEntity employeeEntity = em.find(EmployeeEntity.class,employeeId);
        auctionListingEntity.setEmployeeEntity(employeeEntity);
        em.persist(auctionListingEntity);
        em.flush();
        em.refresh(auctionListingEntity);
        timerSessionBeanLocal.createTimers(auctionListingEntity.getAuctionListingId(), auctionListingEntity.getStartDateTime(), TimerTypeEnum.OPEN, null, null);
        timerSessionBeanLocal.createTimers(auctionListingEntity.getAuctionListingId(), auctionListingEntity.getEndDateTime(), TimerTypeEnum.CLOSING, null, null);
        
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
        //timerSessionBeanLocal.cancelTimers();
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        System.out.println(cal.get(Calendar.DATE)); //2016/11/16 12:08:43
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            em.refresh(auctionListingEntity);
        }

        return auctionListingEntities;
    }

    @Override
    public List<AuctionListingEntity> retrieveAllAuctionListingsBelowReservePrice() throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.manualAssignment = true");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        if (auctionListingEntities.isEmpty()) {
            throw new AuctionListingNotFoundException("No auction listings below reserve price");
        }

        return auctionListingEntities;
    }

    @Override
    public List<AuctionListingEntity> retrieveAllActiveAuctionListings() throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.openListing = true");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        if (auctionListingEntities.isEmpty()) {
            throw new AuctionListingNotFoundException("No active auction listings");
        }

        return auctionListingEntities;
    }

    @Override
    public AuctionListingEntity retrieveActiveAuctionListing(Long auctionListingId) throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.auctionListingId = :inAuctionListingId  AND a.openListing = true");
        query.setParameter("inAuctionListingId", auctionListingId);

        try {
            return (AuctionListingEntity) query.getSingleResult();
        } catch (NonUniqueResultException | NoResultException ex) {
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
    public void openAuctionListing(Long auctionListingId) {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);
        auctionListingEntity.setOpenListing(Boolean.TRUE);
    }

    @Override
    public void closeAuctionListing(Long auctionListingId) {

        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);
        Query query = em.createQuery("SELECT b FROM BidEntity b WHERE b.auctionListingEntity.auctionListingId = :inAuctionListingEntityId ORDER BY b.bidId");
        query.setParameter("inAuctionListingEntityId", auctionListingId);
        auctionListingEntity.setOpenListing(Boolean.FALSE);

        List<BidEntity> bidEntities = query.getResultList();

        if (!bidEntities.isEmpty()) {
            BigDecimal reservePrice = auctionListingEntity.getReservePrice();
            BidEntity bidEntity = bidEntities.get(bidEntities.size() - 1);
            BigDecimal lastBidPrice = bidEntity.getBidAmount();

            if (reservePrice == null || reservePrice.compareTo(lastBidPrice) < 0) {
                bidEntity.setWinningBid(Boolean.TRUE);
                auctionListingEntity.setWinningBidEntity(bidEntity);
                customerEntityControllerLocal.deductCreditBalance(bidEntity.getCustomerEntity().getCustomerId(), lastBidPrice);

                CreditTransactionEntity creditTransactionEntity = new CreditTransactionEntity(lastBidPrice, CreditTransactionTypeEnum.USAGE);
                creditTransactionEntity.setCustomerEntity(bidEntity.getCustomerEntity());
                creditTransactionEntityControllerLocal.createCreditTransactionEntity(creditTransactionEntity);
            } else if (reservePrice.compareTo(lastBidPrice) >= 0) {
                // should we have a attribute to mark the auction listing as required intervention?
                auctionListingEntity.setManualAssignment(Boolean.TRUE);
            }
        }
    }

    @Override
    public List<AuctionListingEntity> retrieveWonAuctionListings(Long customerId) {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.winningBidEntity.customerEntity.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);

        return query.getResultList();
    }

    @Override
    public void updateAuctionListing(AuctionListingEntity auctionListingEntity) {
        auctionListingEntity = em.merge(auctionListingEntity);
        timerSessionBeanLocal.updateTimer(auctionListingEntity.getAuctionListingId(), auctionListingEntity.getStartDateTime(), TimerTypeEnum.OPEN);
        timerSessionBeanLocal.updateTimer(auctionListingEntity.getAuctionListingId(), auctionListingEntity.getEndDateTime(), TimerTypeEnum.CLOSING);
    }

    @Override
    public void deleteAuctionListing(Long auctionListingId) {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);

        Query query = em.createQuery("SELECT b FROM BidEntity b WHERE b.auctionListingEntity.auctionListingId = :inAuctionListingId ORDER BY b.bidId DESC");
        query.setParameter("inAuctionListingId", auctionListingId);
        List<BidEntity> bidEntities = query.getResultList();
        if (bidEntities.isEmpty()) {
            em.remove(auctionListingEntity);
        } else {
            BigDecimal refundAmount;
            Long customerId;
            BidEntity currentBidEntity = bidEntities.get(0);
            CustomerEntity currentCustomerEntity = currentBidEntity.getCustomerEntity();
            refundAmount = currentBidEntity.getBidAmount();
            customerId = currentBidEntity.getCustomerEntity().getCustomerId();
            if (currentBidEntity.getWinningBid()) {
                currentCustomerEntity.setAvailableBalance(currentCustomerEntity.getAvailableBalance().add(refundAmount));
                currentCustomerEntity.setCreditBalance(currentCustomerEntity.getCreditBalance().add(refundAmount));
            } else {
                customerEntityControllerLocal.refundCredits(customerId, refundAmount);
            }
            CreditTransactionEntity creditTransactionEntity = new CreditTransactionEntity(refundAmount, CreditTransactionTypeEnum.REFUND);
            creditTransactionEntity.setCustomerEntity(currentCustomerEntity);
            creditTransactionEntityControllerLocal.createCreditTransactionEntity(creditTransactionEntity);
            auctionListingEntity.setEnabled(Boolean.FALSE);
            auctionListingEntity.setOpenListing(Boolean.FALSE);
            auctionListingEntity.setWinningBidEntity(null);
            currentBidEntity.setWinningBid(Boolean.FALSE);
        }
    }

    @Override
    public void setWinningBidEntity(Long auctionListingId) {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);
        int lastIndex = auctionListingEntity.getBidEntities().size() - 1;
        BidEntity winningBidEntity = auctionListingEntity.getBidEntities().get(lastIndex);
        auctionListingEntity.setWinningBidEntity(winningBidEntity);
        auctionListingEntity.setManualAssignment(Boolean.FALSE);
        em.persist(auctionListingEntity);
        em.flush();
        em.refresh(auctionListingEntity);
    }

    @Override
    public void noWinningBidEntity(Long auctionListingId) {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);
        auctionListingEntity.setManualAssignment(Boolean.FALSE);
        em.persist(auctionListingEntity);
        em.flush();
        em.refresh(auctionListingEntity);
    }
    
    @Override
    public String getHighestBidder(Long auctionListingId){
        Query query = em.createQuery("SELECT b FROM BidEntity b WHERE b.auctionListingEntity.auctionListingId = :inAuctionListingId ORDER BY b.bidId ASC");
        query.setParameter("inAuctionListingId", auctionListingId);
        List<BidEntity> bidEntities = query.getResultList();
        if(bidEntities.isEmpty()){
            return "";
        }
        BidEntity bidEntity = bidEntities.get(bidEntities.size()- 1);
        String highestBidder = bidEntity.getCustomerEntity().getUsername();
        return highestBidder;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import datamodel.TimerEntity;
import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CustomerEntity;
import entity.ProxyBiddingEntity;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Remote;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AuctionListingNotFoundException;
import util.exception.InvalidBidException;
import util.exception.ProxyBiddingNotFoundException;

/**
 *
 * @author User
 */
@Local(TimerSessionBeanLocal.class)
@Remote(TimerSessionBeanRemote.class)
@Stateless
public class TimerSessionBean implements TimerSessionBeanRemote, TimerSessionBeanLocal {

    @EJB
    private ProxyBiddingEntityControllerLocal proxyBiddingEntityControllerLocal;

    @EJB
    private BidEntityControllerLocal bidEntityControllerLocal;

    @EJB
    private AuctionListingEntityControllerLocal auctionListingEntityControllerLocal;

    @Resource
    private SessionContext sessionContext;
    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    public TimerSessionBean() {
    }

    @Override
    public void createTimers(Long auctionListingId, Calendar dateTime, String type, BigDecimal maxAmt, Long customerId) {
        TimerService timerService = sessionContext.getTimerService();
        ScheduleExpression schedule = new ScheduleExpression();

        schedule.year(dateTime.get(Calendar.YEAR)).month(dateTime.get(Calendar.MONTH) + 1).dayOfMonth(dateTime.get(Calendar.DATE))
                .hour(dateTime.get(Calendar.HOUR_OF_DAY)).minute(dateTime.get(Calendar.MINUTE)).second(dateTime.get(Calendar.SECOND));

        timerService.createCalendarTimer(schedule, new TimerConfig(new TimerEntity(auctionListingId, type, maxAmt, customerId), true));
    }

    @Override
    public void cancelTimers() {
        TimerService timerService = sessionContext.getTimerService();
        Collection<Timer> timers = timerService.getTimers();

        for (Timer timer : timers) {
            try {
                //timer.cancel();
                TimerEntity timerEntity = (TimerEntity) timer.getInfo();

                System.out.println("********** EjbTimerSession.cancelTimers(): " + timerEntity.getAuctionListingId() + " " + timerEntity.getType());
            } catch (NoSuchObjectLocalException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void updateTimer(Long auctionListingId, Calendar dateTime, String type) {
        TimerService timerService = sessionContext.getTimerService();
        Collection<Timer> timers = timerService.getTimers();

        for (Timer timer : timers) {
            try {
                TimerEntity timerEntity = (TimerEntity) timer.getInfo();
                if (timerEntity.getAuctionListingId().equals(auctionListingId) && timerEntity.getType().equals(type)) {
                    timer.cancel();
                }
                createTimers(auctionListingId, dateTime, type, null, null);
            } catch (NoSuchObjectLocalException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Timeout
    public void handleTimeout(Timer timer) {
        TimerEntity timerEntity = (TimerEntity) timer.getInfo();
        if (timerEntity.getType().equals("openListing")) {
            System.out.println("Timeout!");
            auctionListingEntityControllerLocal.openAuctionListing(timerEntity.getAuctionListingId());
        } else if (timerEntity.getType().equals("closeListing")) {
            auctionListingEntityControllerLocal.closeAuctionListing(timerEntity.getAuctionListingId());
        } else if (timerEntity.getType().equals("snipe")) {
            Long auctionListingId = timerEntity.getAuctionListingId();

            CustomerEntity customerEntity = em.find(CustomerEntity.class, timerEntity.getCustomerId());
            AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, timerEntity.getAuctionListingId());
            BigDecimal currentBid = auctionListingEntity.getCurrentBidAmount();

            if (currentBid == null) {
                currentBid = auctionListingEntity.getStartingBidAmount();
            }

            BigDecimal nextMinBid = bidEntityControllerLocal.getBidIncrement(currentBid);

            try {
                ProxyBiddingEntity proxyBiddingEntity = proxyBiddingEntityControllerLocal.retrieveHighestProxyBid(auctionListingId);

                if (proxyBiddingEntity.getMaximumAmount().compareTo(nextMinBid) > 0) {
                    nextMinBid = proxyBiddingEntity.getMaximumAmount();
                }
            } catch (ProxyBiddingNotFoundException ex) {

            }

            BigDecimal makeBid;
            if (timerEntity.getMaxAmount().compareTo(nextMinBid) >= 0) {
                makeBid = nextMinBid;
            } else {
                makeBid = timerEntity.getMaxAmount();
            }
                BidEntity bidEntity = new BidEntity(makeBid, Calendar.getInstance());
                bidEntity.setCustomerEntity(customerEntity);
                bidEntity.setAuctionListingEntity(auctionListingEntity);
            try {
                    bidEntityControllerLocal.createNewBid(bidEntity);
                    System.out.println("bid created");
                } catch (InvalidBidException ex) {

                }
        }

    }

//    @Schedule(hour = "*", minute = "*/5", second = "0", info = "scheduleEvery5Minutes")
//    public void closeExpiredAuctionListing() {
//        auctionListingEntityControllerLocal.closeAuctionListings();
//    }
//    
//    @Schedule(hour = "*", minute = "*/5", second = "0", info = "scheduleEvery5Minutes")
//    public void openAuctionListing(){
//        auctionListingEntityControllerLocal.openAuctionListings();
//    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void persist(Object object) {
        em.persist(object);
    }
}

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
import entity.SnipingEntity;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
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
import javax.persistence.Query;
import javax.persistence.TemporalType;
import util.enumeration.TimerTypeEnum;
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
    public void createTimers(Long auctionListingId, Calendar dateTime, TimerTypeEnum type, BigDecimal maxAmt, Long customerId) {
        TimerService timerService = sessionContext.getTimerService();
        ScheduleExpression schedule = new ScheduleExpression();

        schedule.year(dateTime.get(Calendar.YEAR)).month(dateTime.get(Calendar.MONTH) + 1).dayOfMonth(dateTime.get(Calendar.DATE))
                .hour(dateTime.get(Calendar.HOUR_OF_DAY)).minute(dateTime.get(Calendar.MINUTE)).second(dateTime.get(Calendar.SECOND));

        if (type.equals(TimerTypeEnum.SNIPING)) {
            Query query = em.createQuery("SELECT s FROM SnipingEntity s WHERE s.snipingDate = :inSnipingDate AND s.auctionListingEntity.auctionListingId = :inAuctionListingId");
            query.setParameter("inSnipingDate", dateTime, TemporalType.TIMESTAMP);
            query.setParameter("inAuctionListingId", auctionListingId);

            List<SnipingEntity> snipingEntities = query.getResultList();
            if (snipingEntities.size() > 1) {
                return;
            }

        }
        System.out.println("created!");
        timerService.createCalendarTimer(schedule, new TimerConfig(new TimerEntity(auctionListingId, type, maxAmt, customerId, dateTime), true));
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
    public void updateTimer(Long auctionListingId, Calendar dateTime, TimerTypeEnum type) {
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
        if (timerEntity.getType().equals(TimerTypeEnum.OPEN)) {
            System.out.println("Timeout!");
            auctionListingEntityControllerLocal.openAuctionListing(timerEntity.getAuctionListingId());
        } else if (timerEntity.getType().equals(TimerTypeEnum.CLOSING)) {
            auctionListingEntityControllerLocal.closeAuctionListing(timerEntity.getAuctionListingId());
        } else if (timerEntity.getType().equals(TimerTypeEnum.SNIPING)) {
            Long auctionListingId = timerEntity.getAuctionListingId();
            
            Query query = em.createQuery("SELECT s FROM SnipingEntity s WHERE s.snipingDate = :inSnipingDate AND s.auctionListingEntity.auctionListingId = :inAuctionListingId AND s.enabled = true ORDER BY s.snipingId ASC");
            query.setParameter("inSnipingDate", timerEntity.getRunDate(), TemporalType.TIMESTAMP);
            query.setParameter("inAuctionListingId", auctionListingId);
            
            List<SnipingEntity> snipingEntities = query.getResultList();
            
            if(snipingEntities.isEmpty()){
                return;
            }

            CustomerEntity customerEntity = em.find(CustomerEntity.class, timerEntity.getCustomerId());
            AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, timerEntity.getAuctionListingId());

            for (SnipingEntity snipingEntity : snipingEntities) {
                
                BidEntity bidEntity = new BidEntity(snipingEntity.getAmount(), Calendar.getInstance());
                bidEntity.setCustomerEntity(customerEntity);
                bidEntity.setAuctionListingEntity(auctionListingEntity);
                try {
                    bidEntityControllerLocal.createNewBid(bidEntity);
                    System.out.println("bid created");
                } catch (InvalidBidException ex) {

                }
            }

        }

    }
}

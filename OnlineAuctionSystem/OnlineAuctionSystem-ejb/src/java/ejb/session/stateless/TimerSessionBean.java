/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TimerEntity;
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

/**
 *
 * @author User
 */
@Local(TimerSessionBeanLocal.class)
@Remote(TimerSessionBeanRemote.class)
@Stateless
public class TimerSessionBean implements TimerSessionBeanRemote, TimerSessionBeanLocal {

@EJB
    private AuctionListingEntityControllerLocal auctionListingEntityControllerLocal;

    @Resource
    private SessionContext sessionContext;
    
    

    public TimerSessionBean() {
    }
    
    @Override
    public void createTimers(Long auctionListingId, Calendar dateTime, String type){
        TimerService timerService = sessionContext.getTimerService();
        ScheduleExpression schedule = new ScheduleExpression();

        schedule.year(dateTime.get(Calendar.YEAR)).month(dateTime.get(Calendar.MONTH) + 1).dayOfMonth(dateTime.get(Calendar.DATE))
                .hour(dateTime.get(Calendar.HOUR_OF_DAY)).minute(dateTime.get(Calendar.MINUTE)).second(dateTime.get(Calendar.SECOND));
        
        timerService.createCalendarTimer(schedule, new TimerConfig(new TimerEntity(auctionListingId, type), true));
    }
    
    @Override
    public void cancelTimers(){
        TimerService timerService = sessionContext.getTimerService();
        Collection<Timer> timers = timerService.getTimers();
        
        for(Timer timer:timers){
            try{
                timer.cancel();
                System.out.println("********** EjbTimerSession.cancelTimers(): " + timer.getInfo().toString());
            } catch(NoSuchObjectLocalException ex){
                System.out.println(ex.getMessage());
            }
        }
    }
    
    @Timeout
    public void handleTimeout(Timer timer){
        TimerEntity timerEntity = (TimerEntity) timer.getInfo();
        if(timerEntity.getType().equals("openListing")){
            auctionListingEntityControllerLocal.openAuctionListing(timerEntity.getAuctionListingId());
        } else if(timerEntity.getType().equals("closeListing")){
            auctionListingEntityControllerLocal.closeAuctionListing(timerEntity.getAuctionListingId());
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
}

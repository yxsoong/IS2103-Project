/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.math.BigDecimal;
import java.util.Calendar;

public interface TimerSessionBeanLocal {
    public void createTimers(Long auctionListingId, Calendar dateTime, String type, BigDecimal maxAmt, Long customerId);
    
    public void cancelTimers();
    
    public void updateTimer(Long auctionListingId, Calendar dateTime, String type);
}

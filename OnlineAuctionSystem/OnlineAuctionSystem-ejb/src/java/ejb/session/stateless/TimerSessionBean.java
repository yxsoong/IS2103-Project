/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;

/**
 *
 * @author User
 */
@Stateless
@LocalBean
public class TimerSessionBean {

    @Resource
    private SessionContext sessionContext;

    public TimerSessionBean() {
    }

    @Schedule(hour = "*", minute = "*/5", second = "0", info = "scheduleEvery5Minutes")
    public void closeExpiredAuctionListing() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("check " + dateFormat.format(date));
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadministrationpanel;

import ejb.session.stateless.AuctionListingEntityControllerRemote;
import ejb.session.stateless.BidEntityControllerRemote;
import ejb.session.stateless.CreditPackageEntityControllerRemote;
import ejb.session.stateless.EmployeeEntityControllerRemote;
import javax.ejb.EJB;

/**
 *
 * @author User
 */
public class Main {

    @EJB
    private static AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;
    
    @EJB
    private static BidEntityControllerRemote bidEntityControllerRemote;
    
    @EJB
    private static CreditPackageEntityControllerRemote creditPackageEntityControllerRemote;

    @EJB
    private static EmployeeEntityControllerRemote employeeEntityControllerRemote;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(auctionListingEntityControllerRemote, bidEntityControllerRemote, creditPackageEntityControllerRemote, employeeEntityControllerRemote);
        mainApp.runApp();
    }
    
}

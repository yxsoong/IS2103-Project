/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import ejb.session.stateless.AddressEntityControllerRemote;
import ejb.session.stateless.AuctionListingEntityControllerRemote;
import ejb.session.stateless.BidEntityControllerRemote;
import ejb.session.stateless.CreditPackageEntityControllerRemote;
import ejb.session.stateless.CreditTransactionEntityControllerRemote;
import ejb.session.stateless.CustomerEntityControllerRemote;
import javax.ejb.EJB;

/**
 *
 * @author User
 */
public class Main {
    
    @EJB
    private static AddressEntityControllerRemote addressEntityControllerRemote;

    @EJB
    private static AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;

    @EJB
    private static BidEntityControllerRemote bidEntityControllerRemote;

    @EJB
    private static CustomerEntityControllerRemote customerEntityControllerRemote;

    @EJB
    private static CreditPackageEntityControllerRemote creditPackageEntityControllerRemote;
    
    @EJB
    private static CreditTransactionEntityControllerRemote creditTransactionEntityControllerRemote;
    
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(addressEntityControllerRemote, auctionListingEntityControllerRemote, bidEntityControllerRemote, customerEntityControllerRemote, creditPackageEntityControllerRemote, creditTransactionEntityControllerRemote);
        mainApp.runApp();
    }
    
}

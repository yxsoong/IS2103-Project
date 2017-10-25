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

/**
 *
 * @author User
 */
public class MainApp {
    
    private AddressEntityControllerRemote addressEntityControllerRemote;

    private AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;

    private BidEntityControllerRemote bidEntityControllerRemote;

    private CustomerEntityControllerRemote customerEntityControllerRemote;

    private CreditPackageEntityControllerRemote creditPackageEntityControllerRemote;
    
    private CreditTransactionEntityControllerRemote creditTransactionEntityControllerRemote;

    public MainApp() {
    }

    public MainApp(AddressEntityControllerRemote addressEntityControllerRemote, AuctionListingEntityControllerRemote auctionListingEntityControllerRemote, BidEntityControllerRemote bidEntityControllerRemote, CustomerEntityControllerRemote customerEntityControllerRemote, CreditPackageEntityControllerRemote creditPackageEntityControllerRemote, CreditTransactionEntityControllerRemote creditTransactionEntityControllerRemote) {
        this.addressEntityControllerRemote = addressEntityControllerRemote;
        this.auctionListingEntityControllerRemote = auctionListingEntityControllerRemote;
        this.bidEntityControllerRemote = bidEntityControllerRemote;
        this.customerEntityControllerRemote = customerEntityControllerRemote;
        this.creditPackageEntityControllerRemote = creditPackageEntityControllerRemote;
        this.creditTransactionEntityControllerRemote = creditTransactionEntityControllerRemote;
    }

    
    
    public void runApp(){
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 *
 * @author User
 */
@Local(AuctionListingControllerLocal.class)
@Remote(AuctionListingControllerRemote.class)
@Stateless
public class AuctionTransactionController implements AuctionTransactionControllerRemote, AuctionTransactionControllerLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}

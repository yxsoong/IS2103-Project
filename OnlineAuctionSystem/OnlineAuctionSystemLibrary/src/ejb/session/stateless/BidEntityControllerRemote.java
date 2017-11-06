/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BidEntity;
import java.math.BigDecimal;
import util.exception.InvalidBidException;


public interface BidEntityControllerRemote {

    public BidEntity createNewBid(BidEntity bidEntity)  throws InvalidBidException;

    public BigDecimal getBidIncrement(BigDecimal currentPrice);
    
}

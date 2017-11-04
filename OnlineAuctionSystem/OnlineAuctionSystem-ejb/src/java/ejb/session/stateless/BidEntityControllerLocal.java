/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BidEntity;
import java.math.BigDecimal;

public interface BidEntityControllerLocal {

    public BidEntity createNewBid(BidEntity bidEntity);

    public BigDecimal getBidIncrement(BigDecimal currentPrice);
}

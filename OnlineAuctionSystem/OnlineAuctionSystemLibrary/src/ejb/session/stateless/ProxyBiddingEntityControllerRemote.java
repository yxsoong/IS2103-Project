/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ProxyBiddingEntity;
import util.exception.InsufficientCreditsException;
import util.exception.InvalidProxyBidException;
import util.exception.ProxyBiddingNotFoundException;

public interface ProxyBiddingEntityControllerRemote {

    public ProxyBiddingEntity createProxyBidding(ProxyBiddingEntity proxyBiddingEntity) throws InsufficientCreditsException, InvalidProxyBidException;
    
    public ProxyBiddingEntity retrieveHighestProxyBid(Long auctionListingId) throws ProxyBiddingNotFoundException;
    
    public void disableProxyBids(Long auctionListingId);
}

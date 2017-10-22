/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import java.util.List;
import util.exception.AuctionListingNotFoundException;

public interface AuctionListingEntityControllerLocal {

    public AuctionListingEntity createAuctionListing(AuctionListingEntity auctionListingEntity);

    public AuctionListingEntity retrieveAuctionListingById(Long auctionListingId) throws AuctionListingNotFoundException;
    
    public List<AuctionListingEntity> retrieveAllAuctionListings();
}

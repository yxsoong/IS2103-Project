/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import entity.AuctionListingEntity;
import java.util.List;
import util.exception.AuctionListingNotFoundException;

public interface AuctionListingEntityControllerRemote {

    public AuctionListingEntity retrieveAuctionListingById(Long auctionListingId) throws AuctionListingNotFoundException;

    public List<AuctionListingEntity> retrieveAllActiveAuctionListings() throws AuctionListingNotFoundException;

    public List<AuctionListingEntity> retrieveAllAuctionListings() throws AuctionListingNotFoundException;

    public void removeDeliveryAddress(AddressEntity addressEntity);

    public AuctionListingEntity retrieveActiveAuctionListing(Long auctionListingId) throws AuctionListingNotFoundException;

    public void openAuctionListing(Long auctionListingId);

    public void closeAuctionListing(Long auctionListingId);

    public List<AuctionListingEntity> retrieveWonAuctionListings(Long customerId);

    public List<AuctionListingEntity> retrieveAllAuctionListingsBelowReservePrice() throws AuctionListingNotFoundException;

    public void updateAuctionListing(AuctionListingEntity auctionListingEntity);

    public void deleteAuctionListing(Long auctionListingId);

    public void setWinningBidEntity(Long auctionListingId);

    public void noWinningBidEntity(Long auctionListingId);

    public AuctionListingEntity createAuctionListing(AuctionListingEntity auctionListingEntity, Long employeeId);
}

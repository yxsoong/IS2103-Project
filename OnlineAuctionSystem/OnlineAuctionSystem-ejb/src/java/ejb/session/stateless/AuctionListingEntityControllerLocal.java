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
import util.exception.InvalidAuctionListingException;

public interface AuctionListingEntityControllerLocal {

    public AuctionListingEntity retrieveAuctionListingById(Long auctionListingId) throws AuctionListingNotFoundException;

    public List<AuctionListingEntity> retrieveAllAuctionListings() throws AuctionListingNotFoundException;

    public List<AuctionListingEntity> retrieveAllAuctionListingsBelowReservePrice() throws AuctionListingNotFoundException;

    public List<AuctionListingEntity> retrieveAllActiveAuctionListings() throws AuctionListingNotFoundException;

    public void removeDeliveryAddress(AddressEntity addressEntity);

    public AuctionListingEntity retrieveActiveAuctionListing(Long auctionListingId) throws AuctionListingNotFoundException;

    public void openAuctionListing(Long auctionListingId);

    public void closeAuctionListing(Long auctionListingId);

    public List<AuctionListingEntity> retrieveWonAuctionListings(Long customerId);

    public void updateAuctionListing(AuctionListingEntity auctionListingEntity);

    public void deleteAuctionListing(Long auctionListingId);

    public AuctionListingEntity setWinningBidEntity(Long auctionListingId);

    public AuctionListingEntity noWinningBidEntity(Long auctionListingId);

    public AuctionListingEntity createAuctionListing(AuctionListingEntity auctionListingEntity, Long employeeId) throws InvalidAuctionListingException;

    public String getHighestBidder(Long auctionListingId);
}

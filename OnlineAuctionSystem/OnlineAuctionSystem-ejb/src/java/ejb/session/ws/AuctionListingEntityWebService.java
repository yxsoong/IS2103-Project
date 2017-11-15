/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.AuctionListingEntityControllerLocal;
import entity.AuctionListingEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AuctionListingNotFoundException;

@WebService(serviceName = "AuctionListingEntityWebService")
@Stateless()
public class AuctionListingEntityWebService {

    @EJB
    private AuctionListingEntityControllerLocal auctionListingEntityControllerLocal;

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @WebMethod(operationName = "viewAuctionListingDetails")
    public AuctionListingEntity viewAuctionListingDetails(@WebParam(name = "auctionListingId") Long auctionListingId) throws AuctionListingNotFoundException {
        try {
            AuctionListingEntity auctionListingEntity = auctionListingEntityControllerLocal.retrieveActiveAuctionListing(auctionListingId);
            em.detach(auctionListingEntity);
            auctionListingEntity.setBidEntities(null);
            auctionListingEntity.setDeliveryAddress(null);
            auctionListingEntity.setWinningBidEntity(null);
            auctionListingEntity.setEmployeeEntity(null);
            return auctionListingEntity;
        } catch (AuctionListingNotFoundException ex) {
            throw ex;
        }
    }

    @WebMethod(operationName = "retrieveAllAuctionListings")
    public List<AuctionListingEntity> retrieveAllAuctionListings() throws AuctionListingNotFoundException {
        //TODO write your implementation code here:
        if (auctionListingEntityControllerLocal.retrieveAllActiveAuctionListings().isEmpty()) {
            throw new AuctionListingNotFoundException();
        }
        List<AuctionListingEntity> activeListings = auctionListingEntityControllerLocal.retrieveAllActiveAuctionListings();
        for (AuctionListingEntity auctionListingEntity : activeListings) {
            em.detach(auctionListingEntity);
            auctionListingEntity.setBidEntities(null);
            auctionListingEntity.setDeliveryAddress(null);
            auctionListingEntity.setWinningBidEntity(null);
            auctionListingEntity.setEmployeeEntity(null);
        }
        return activeListings;
    }

    @WebMethod(operationName = "viewWonAuctionListings")
    public List<AuctionListingEntity> viewWonAuctionListings(@WebParam(name = "customerId") Long customerId) throws AuctionListingNotFoundException {
        //TODO write your implementation code here:
        if (auctionListingEntityControllerLocal.retrieveWonAuctionListings(customerId).isEmpty()) {
            throw new AuctionListingNotFoundException("No auctions avaiable at this moment");
        }
        List<AuctionListingEntity> wonListings = auctionListingEntityControllerLocal.retrieveWonAuctionListings(customerId);
        for (AuctionListingEntity auctionListingEntity : wonListings) {
            em.detach(auctionListingEntity);
            auctionListingEntity.setBidEntities(null);
            auctionListingEntity.setDeliveryAddress(null);
            auctionListingEntity.setWinningBidEntity(null);
            auctionListingEntity.setEmployeeEntity(null);
        }
        return wonListings;
    }
}

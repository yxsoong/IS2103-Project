/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import entity.AuctionListingEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AuctionListingNotFoundException;

@Local(AuctionListingEntityControllerLocal.class)
@Remote(AuctionListingEntityControllerRemote.class)
@Stateless
public class AuctionListingEntityController implements AuctionListingEntityControllerRemote, AuctionListingEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;

    @Override
    public AuctionListingEntity createAuctionListing(AuctionListingEntity auctionListingEntity) {
        em.persist(auctionListingEntity);
        em.flush();
        em.refresh(auctionListingEntity);

        return auctionListingEntity;
    }

    @Override
    public AuctionListingEntity retrieveAuctionListingById(Long auctionListingId) throws AuctionListingNotFoundException {
        AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);

        if (auctionListingEntity != null) {
            return auctionListingEntity;
        } else {
            throw new AuctionListingNotFoundException("Auction Listing ID: " + auctionListingId + " does not exist");
        }
    }

    @Override
    public List<AuctionListingEntity> retrieveAllAuctionListings() throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        return auctionListingEntities;
    }

    @Override
    public List<AuctionListingEntity> retrieveAllAuctionListingsBelowReservePrice() {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.bidEntities.bidAmount < a.reservePrice");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        return auctionListingEntities;
    }

    @Override
    public List<AuctionListingEntity> retrieveAllActiveAuctionListings() throws AuctionListingNotFoundException {
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.openListing = true");
        List<AuctionListingEntity> auctionListingEntities = query.getResultList();

        return auctionListingEntities;
    }

    @Override
    public void removeDeliveryAddress(AddressEntity addressEntity) {
//        Query query = em.createQuery("DELETE a.deliveryAddress FROM AuctionListingEntity a WHERE a.deliveryAddress LIKE addressEntity");
//        em.merge(addressEntity);
//        em.remove(addressEntity);
//        em.flush();
//        em.refresh(addressEntity);
        Query query = em.createQuery("SELECT a FROM AuctionListingEntity a WHERE a.deliveryAddress = ?1");
        query.setParameter(1, addressEntity);
        List<AuctionListingEntity> addressesToRemove = query.getResultList();
        for (AuctionListingEntity auctionListingEntity : addressesToRemove) {
            auctionListingEntity.setDeliveryAddress(null);
        }
    }
}

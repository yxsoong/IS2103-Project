/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AuctionListingEntity;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Local(AuctionListingEntityControllerLocal.class)
@Remote(AuctionListingEntityControllerRemote.class)
@Stateless
public class AuctionListingEntityController implements AuctionListingEntityControllerRemote, AuctionListingEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;


    @Override
    public AuctionListingEntity createAuctionListing(AuctionListingEntity auctionListingEntity){
        em.persist(auctionListingEntity);
        em.flush();
        em.refresh(auctionListingEntity);
        
        return auctionListingEntity;
    }
}

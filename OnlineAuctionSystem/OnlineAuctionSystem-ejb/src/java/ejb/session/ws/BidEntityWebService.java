/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.ProxyBiddingEntityControllerLocal;
import ejb.session.stateless.TimerSessionBeanLocal;
import entity.AuctionListingEntity;
import entity.CustomerEntity;
import entity.ProxyBiddingEntity;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.jws.Oneway;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InsufficientCreditsException;

/**
 *
 * @author User
 */
@WebService(serviceName = "BidEntityWebService")
@Stateless()
public class BidEntityWebService {

    @EJB
    private TimerSessionBeanLocal timerSessionBeanLocal;

    @EJB
    private ProxyBiddingEntityControllerLocal proxyBiddingEntityControllerLocal;
    
    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;
    
    @WebMethod(operationName = "createProxyBidding")
    public ProxyBiddingEntity createProxyBidding(@WebParam(name = "proxyBiddingEntity") ProxyBiddingEntity proxyBiddingEntity, @WebParam(name = "customerId") Long customerId, @WebParam(name = "auctionListingId") Long auctionListingId) throws InsufficientCreditsException {
        try {
            CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
            AuctionListingEntity auctionListingEntity = em.find(AuctionListingEntity.class, auctionListingId);
            proxyBiddingEntity.setCustomerEntity(customerEntity);
            proxyBiddingEntity.setAuctionListingEntity(auctionListingEntity);
            proxyBiddingEntity = proxyBiddingEntityControllerLocal.createProxyBidding(proxyBiddingEntity);
            em.detach(proxyBiddingEntity);
            proxyBiddingEntity.setAuctionListingEntity(null);
            proxyBiddingEntity.setBidEntities(null);
            proxyBiddingEntity.setCustomerEntity(null);
            return proxyBiddingEntity;
        } catch (InsufficientCreditsException ex) {
            throw ex;
        }
    }

    @WebMethod(operationName = "createSnipingAuctionListing")
    public void createSnipingAuctionListing(@WebParam(name = "snipingDateTime") Calendar snipingDateTime, @WebParam(name = "auctionListingId") Long auctionListingId, @WebParam(name = "maximumAmount") BigDecimal maximumAmount, @WebParam(name = "customerId") Long customerId) {
        timerSessionBeanLocal.createTimers(auctionListingId, snipingDateTime, "snipe", maximumAmount, customerId);
    }
    
}

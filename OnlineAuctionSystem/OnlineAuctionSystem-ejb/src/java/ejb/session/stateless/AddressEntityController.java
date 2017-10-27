/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import util.exception.AddressNotFoundException;

/**
 *
 * @author lowru
 */
@Local(AddressEntityControllerLocal.class)
@Remote(AddressEntityControllerRemote.class)
@Stateless
public class AddressEntityController implements AddressEntityControllerRemote, AddressEntityControllerLocal {

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public AddressEntity createAddress(AddressEntity addressEntity){
        em.persist(addressEntity);
        em.flush();
        em.refresh(addressEntity);
        
        return addressEntity;
    }
    
    @Override
    public AddressEntity retrieveAddressById(Long addressId, Long customerId) throws AddressNotFoundException{
        Query query = em.createQuery("SELECT a FROM AddressEntity a WHERE a.addressID = :inAddressId AND a.customerEntity.customerId = :inCustomerId");
        query.setParameter("inAddressId", addressId);
        query.setParameter("inCustomerId", customerId);
        
        try{
            AddressEntity addressEntity = (AddressEntity) query.getSingleResult();
            return addressEntity;
        } catch(NonUniqueResultException | NoResultException ex){
            throw new AddressNotFoundException("Address ID: " + addressId + " does not exist");
        }
            
    }
    
    @Override
    public void updateAddress(AddressEntity addressEntity){
        em.merge(addressEntity);
    }
    
    @Override
    public void deleteAddress(Long addressId){
        AddressEntity addressEntity = em.find(AddressEntity.class, addressId);
        try{
            em.remove(addressEntity);
        } catch(ConstraintViolationException ex){
            addressEntity.setEnabled(Boolean.FALSE);
        }
    }
    
}

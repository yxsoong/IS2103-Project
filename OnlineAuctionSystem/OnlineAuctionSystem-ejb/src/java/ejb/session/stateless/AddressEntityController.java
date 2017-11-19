/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import entity.CustomerEntity;
import java.util.List;
import javax.ejb.EJB;
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

    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;

    

    @PersistenceContext(unitName = "OnlineAuctionSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public AddressEntity createAddress(AddressEntity addressEntity, Long customerId){
        em.persist(addressEntity);
        
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        customerEntity.getAddressEntities().add(addressEntity);
        
        em.flush();
        em.refresh(addressEntity);
        
        return addressEntity;
    }
    
    @Override
    public AddressEntity retrieveAddressById(Long addressId, Long customerId) throws AddressNotFoundException{
        Query query = em.createQuery("SELECT a FROM CustomerEntity c JOIN c.addressEntities a WHERE c.customerId = :inCustomerId AND a.addressID = :inAddressId");
        query.setParameter("inCustomerId", customerId);
        query.setParameter("inAddressId", addressId);

        try{
            return (AddressEntity) query.getSingleResult();
        } catch(NonUniqueResultException | NoResultException ex){
            throw new AddressNotFoundException("Address ID: " + addressId + " does not exist");
        }
            
    }
    
    @Override
    public void updateAddress(AddressEntity addressEntity){
        em.merge(addressEntity);
    }
    
    @Override
    public void deleteAddress(Long addressId, Long customerId){
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        AddressEntity addressEntity = em.find(AddressEntity.class, addressId);
        
        
        Query query = em.createQuery("SELECT COUNT(a) FROM AuctionListingEntity a WHERE a.deliveryAddress.addressID = :inAddressId");
        query.setParameter("inAddressId", addressId);
        
        try{
            Long count = (Long) query.getSingleResult();
            customerEntity.getAddressEntities().remove(addressEntity);
            
            if(count.compareTo(0L) > 0 ){
                addressEntity.setEnabled(Boolean.FALSE);
            } else {
                em.remove(addressEntity);
            }
        } catch(Exception ex){
            System.out.println("Error occured while trying to delete address");
        }
    }
    
    @Override
    public List<AddressEntity> retrieveAllAddress(Long customerId) throws AddressNotFoundException{
        Query query = em.createQuery("SELECT a FROM CustomerEntity c JOIN c.addressEntities a WHERE c.customerId = :inCustomerId AND a.enabled = true");
        query.setParameter("inCustomerId", customerId);
        
        if(query.getResultList().isEmpty()){
            throw new AddressNotFoundException("No addresses");
        }
        
        return query.getResultList();
        
    }
    
}

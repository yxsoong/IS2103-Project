/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AddressEntity;
import java.util.List;
import util.exception.AddressNotFoundException;


public interface AddressEntityControllerLocal {
    public AddressEntity createAddress(AddressEntity addressEntity);
    
    public AddressEntity retrieveAddressById(Long addressId, Long customerId) throws AddressNotFoundException;
    
    public void updateAddress(AddressEntity addressEntity);
    
    public void deleteAddress(Long addressId, Long customerId);
    
    public List<AddressEntity> retrieveAllAddress(Long customerId) throws AddressNotFoundException;
}

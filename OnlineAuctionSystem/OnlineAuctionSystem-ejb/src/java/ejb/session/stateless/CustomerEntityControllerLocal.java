/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import util.exception.InvalidLoginCredentialException;


public interface CustomerEntityControllerLocal {
    public CustomerEntity createCustomer(CustomerEntity customerEntity);
    
    public Boolean checkUsername(String username);
    
    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException;
    
    public void updateCustomer(CustomerEntity customerEntity);
}

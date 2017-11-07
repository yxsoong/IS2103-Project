/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CustomerEntityControllerLocal;
import entity.CustomerEntity;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidRegistrationException;

/**
 *
 * @author User
 */
@WebService(serviceName = "CustomerEntityWebService")
@Stateless()
public class CustomerEntityWebService {

    @EJB
    private CustomerEntityControllerLocal customerEntityControllerLocal;
    
    @WebMethod(operationName = "regsterPremiumCustomer")
    public void regsterPremiumCustomer(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws InvalidRegistrationException {
        try{
            CustomerEntity customerEntity = customerEntityControllerLocal.customerLogin(username, password);
            if(!customerEntity.getPremium())
                customerEntity.setPremium(Boolean.TRUE);
            else
                throw new InvalidLoginCredentialException("Already registered as premium");
            
        } catch(InvalidLoginCredentialException ex){
            throw new InvalidRegistrationException(ex.getMessage());
        }
        
    }

    @WebMethod(operationName = "customerLogin")
    public CustomerEntity customerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws InvalidLoginCredentialException {
        //TODO write your implementation code here:
        try{
            CustomerEntity customerEntity = customerEntityControllerLocal.customerLogin(username, password);
            
            if(customerEntity.getPremium()){
                return customerEntity;
            } else {
                throw new InvalidLoginCredentialException("Please register to become a premium member");
            }
        } catch(InvalidLoginCredentialException ex){
            throw ex;
        }
    }

    @WebMethod(operationName = "retrieveCreditBalance")
    public CustomerEntity retrieveCreditBalance(@WebParam(name = "customerId") Long customerId) {
        //TODO write your implementation code here:
        return customerEntityControllerLocal.retrieveCustomerById(customerId);
    }

    



}

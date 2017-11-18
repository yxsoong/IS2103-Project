/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import datamodel.CreditBalance;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import util.exception.InsufficientCreditsException;
import util.exception.InvalidLoginCredentialException;

public interface CustomerEntityControllerLocal {

    public CustomerEntity createCustomer(CustomerEntity customerEntity);

    public Boolean checkUsername(String username);

    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException;

    public void updateCustomer(CustomerEntity customerEntity);

    public CustomerEntity retrieveCustomerById(Long id);

    public void topUpCredits(Long customerId, BigDecimal amount, CreditTransactionEntity creditTransactionEntity);

    public void useCredits(Long customerId, BigDecimal amount) throws InsufficientCreditsException;

    public void refundCredits(Long customerId, BigDecimal amount);

    public void deductCreditBalance(Long customerId, BigDecimal amount);

    public CreditBalance retrieveCreditBalance(Long customerId);

    public Boolean checkIdentificationNumber(String identificationNumber);

    public Boolean checkPhoneNumber(String PhoneNumber);
}

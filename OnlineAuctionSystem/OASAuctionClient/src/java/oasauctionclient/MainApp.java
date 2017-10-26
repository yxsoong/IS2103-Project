/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import ejb.session.stateless.AddressEntityControllerRemote;
import ejb.session.stateless.AuctionListingEntityControllerRemote;
import ejb.session.stateless.BidEntityControllerRemote;
import ejb.session.stateless.CreditPackageEntityControllerRemote;
import ejb.session.stateless.CreditTransactionEntityControllerRemote;
import ejb.session.stateless.CustomerEntityControllerRemote;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author User
 */
public class MainApp {

    private AddressEntityControllerRemote addressEntityControllerRemote;

    private AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;

    private BidEntityControllerRemote bidEntityControllerRemote;

    private CustomerEntityControllerRemote customerEntityControllerRemote;

    private CreditPackageEntityControllerRemote creditPackageEntityControllerRemote;

    private CreditTransactionEntityControllerRemote creditTransactionEntityControllerRemote;

    private CustomerEntity currentCustomerEntity;

    public MainApp() {
    }

    public MainApp(AddressEntityControllerRemote addressEntityControllerRemote, AuctionListingEntityControllerRemote auctionListingEntityControllerRemote, BidEntityControllerRemote bidEntityControllerRemote, CustomerEntityControllerRemote customerEntityControllerRemote, CreditPackageEntityControllerRemote creditPackageEntityControllerRemote, CreditTransactionEntityControllerRemote creditTransactionEntityControllerRemote) {
        this.addressEntityControllerRemote = addressEntityControllerRemote;
        this.auctionListingEntityControllerRemote = auctionListingEntityControllerRemote;
        this.bidEntityControllerRemote = bidEntityControllerRemote;
        this.customerEntityControllerRemote = customerEntityControllerRemote;
        this.creditPackageEntityControllerRemote = creditPackageEntityControllerRemote;
        this.creditTransactionEntityControllerRemote = creditTransactionEntityControllerRemote;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to OAS Auction Client ***\n");
            System.out.println("1: Register");
            System.out.println("2: Login");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                String input = sc.next();

                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

                if (response == 1) {
                    doRegister();
                } else if (response == 2) {
                    try {
                        if (doLogin()) {
                            //mainMenu();
                        }
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential!\n");
                    }
                } else if (response == 3) {
                    break;

                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }

    private void doRegister() {
        System.out.println("*** OAS Auction Client :: Register ***\n");
        Scanner sc = new Scanner(System.in);

        String firstName, lastName, identificationNo, phoneNumber, username, password;
        Boolean isPremium = false;
        BigDecimal credit = BigDecimal.ZERO;
        int count = 0;

        do {
            if (count > 0) {
                System.out.println("First name cannot be empty!\n");
            }
            System.out.print("Enter first name> ");
            firstName = sc.nextLine().trim();
            count++;
        } while (firstName.isEmpty());
        
        count = 0;

        do {
            if (count > 0) {
                System.out.println("Last name cannot be empty!\n");
            }
            System.out.print("Enter last name> ");
            lastName = sc.nextLine().trim();
            count++;
        } while (lastName.isEmpty());
        
        count = 0;

        do {
            if (count > 0) {
                System.out.println("Identification number cannot be empty!\n");
            }
            System.out.print("Enter idendtification number> ");
            identificationNo = sc.nextLine().trim();
            count++;
        } while (identificationNo.isEmpty());
        
        count = 0;

        do {
            if (count > 0) {
                System.out.println("Phone number cannot be empty!\n");
            }
            System.out.print("Enter phone number> ");
            phoneNumber = sc.nextLine().trim();
            count++;
        } while (phoneNumber.isEmpty());
        
        count = 0;
        Boolean isUnique = true;
        do {
            if (!isUnique) {
                System.out.println("Username is not available.\n");
                count = 0;
            }
            if (count > 0) {
                System.out.println("Last name cannot be empty!\n");
                isUnique = true;
            }

            System.out.print("Enter username> ");
            username = sc.nextLine().trim();

            isUnique = !customerEntityControllerRemote.checkUsername(username);

            count++;
        } while (username.isEmpty() || !isUnique);
        
        count = 0;
        Boolean samePassword = true;
        do {

            if (count > 0) {
                System.out.println("Password must not be empty!\n");
                isUnique = true;
            }

            System.out.print("Enter password> ");
            password = sc.nextLine().trim();
            System.out.print("Confirm password> ");
            String confirmPassword = sc.nextLine().trim();

            samePassword = password.equals(confirmPassword);
            count++;
            if (!samePassword) {
                System.out.println("Passwords do not match!\n");
                count = 0;
            }

        } while (password.isEmpty() || !samePassword);
        
        CustomerEntity newCustomerEntity = new CustomerEntity(firstName, lastName, identificationNo, phoneNumber, credit, isPremium, username, password);
        
        newCustomerEntity = customerEntityControllerRemote.createCustomer(newCustomerEntity);
        
        System.out.println("Account created! " + newCustomerEntity.getCustomerId());
    }

    private boolean doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** OAS Administration System :: Login ***\n");
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        while (true) {
            if (username.length() > 0 && password.length() > 0) {
                try {
                    currentCustomerEntity = customerEntityControllerRemote.customerLogin(username, password);
                    System.out.println("Login successful!\n");
                    return true;
                } catch (InvalidLoginCredentialException ex) {
                    throw ex;
                }
            } else {
                System.out.println("Please enter valid username/password!\n");
            }
        }
    }
    
    private void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Administration Panel ***\n");
            System.out.println("Welcome " + currentCustomerEntity.getFirstName() + " " + currentCustomerEntity.getLastName() + "\n");
            System.out.println("1: View Profile");
            System.out.println("2: Update Profile");
            System.out.println("3: View Address Details");
            System.out.println("4: View All Addresses");
            System.out.println("5: View Credit Balance");
            System.out.println("6: View Credit Transaction History");
            System.out.println("7: Purchase Credit Package");
            System.out.println("8: Browse All Auction Listing");
            System.out.println("9. View Auction Listing Details");
            System.out.println("10: Browse Won Auction Listing");
            System.out.println("11: Logout\n");
            response = 0;

            while (response < 1 || response > 11) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    viewProfile();
                } else if (response == 2) {
                } else if (response == 3) {
                } else if (response == 4) {
                } else if (response == 5) {
                } else if (response == 6) {
                } else if (response == 7) {
                } else if (response == 8) {
                } else if (response == 9) {
                } else if (response == 10) {
                } else if (response == 11) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 11) {
                break;
            }
        }
    }
    
    private void viewProfile(){
        
    }

}

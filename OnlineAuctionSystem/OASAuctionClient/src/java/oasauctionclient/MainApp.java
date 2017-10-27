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
import entity.AddressEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.util.Scanner;
import util.exception.AddressNotFoundException;
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
                            mainMenu();
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
            System.out.println("3: Create Address");
            System.out.println("4: View Address Details");
            System.out.println("5: View All Addresses");
            System.out.println("6: View Credit Balance");
            System.out.println("7: View Credit Transaction History");
            System.out.println("8: Purchase Credit Package");
            System.out.println("9: Browse All Auction Listing");
            System.out.println("10. View Auction Listing Details");
            System.out.println("11: Browse Won Auction Listing");
            System.out.println("12: Logout\n");
            response = 0;

            while (response < 1 || response > 12) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    viewProfile();
                } else if (response == 2) {
                    doUpdateProfile();
                } else if (response == 3) {
                    createAddress();
                } else if (response == 4) {
                    viewAddressDetails();
                } else if (response == 5) {
                } else if (response == 6) {
                } else if (response == 7) {
                } else if (response == 8) {
                } else if (response == 9) {
                } else if (response == 10) {
                } else if (response == 11) {
                } else if (response == 12) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 12) {
                break;
            }
        }
    }

    private void viewProfile() {
        Scanner sc = new Scanner(System.in);
        System.out.printf("%11s%20s%20s%25s%14s%15s%8s%20s%20s\n", "Customer ID", "First Name", "Last Name", "Identification Number", "Phone Number", "Credit Balance", "Premium", "Username", "Password");
        System.out.printf("%11s%20s%20s%25s%14s%15s%8s%20s%20s\n", currentCustomerEntity.getCustomerId().toString(), currentCustomerEntity.getFirstName(),
                currentCustomerEntity.getLastName(), currentCustomerEntity.getIdentificationNo(), currentCustomerEntity.getPhoneNumber(),
                currentCustomerEntity.getCreditBalance().toString(), currentCustomerEntity.getPremium().toString(),
                currentCustomerEntity.getUsername(), currentCustomerEntity.getPassword());
        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void doUpdateProfile() {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Client :: Update Profile ***\n");
        System.out.print("Enter First Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentCustomerEntity.setFirstName(input);
        }

        System.out.print("Enter Last Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentCustomerEntity.setLastName(input);
        }
        
        System.out.print("Enter Phone Number (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentCustomerEntity.setPhoneNumber(input);
        }
        
        // I'm not sure if we should let customer change username
        /*System.out.print("Enter Username (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentCustomerEntity.setUsername(input);
        }*/

        while (true) {
            System.out.print("Enter Password (blank if no change)> ");
            input = sc.nextLine().trim();
            if (input.length() > 0) {

                System.out.print("Confirm Password> ");
                String confirmPassword = sc.nextLine().trim();

                if (input.equals(confirmPassword)) {
                    currentCustomerEntity.setPassword(input);
                }
            } else {
                break;
            }
        }

        customerEntityControllerRemote.updateCustomer(currentCustomerEntity);
        System.out.println("Profile updated!");
    }

    private void createAddress() {
        System.out.println("*** OAS Client :: Create Address ***\n");
        Scanner sc = new Scanner(System.in);
        
        String streetAddress, unitNumber, postalCode;
        Boolean enabled = true;

        int count = 0;

        do {
            if (count > 0) {
                System.out.println("Street address cannot be empty!\n");
            }
            System.out.print("Enter street address> ");
            streetAddress = sc.nextLine().trim();
            count++;
        } while (streetAddress.isEmpty());

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Unit number cannot be empty!\n");
            }
            System.out.print("Enter Unit number> ");
            unitNumber = sc.nextLine().trim();
            count++;
        } while (unitNumber.isEmpty());

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Postal code cannot be empty!\n");
            }
            System.out.print("Enter postal code> ");
            postalCode = sc.nextLine().trim();
            count++;
        } while (postalCode.isEmpty());
        
        System.out.println();

        AddressEntity addressEntity = new AddressEntity(streetAddress, unitNumber, postalCode, enabled);
        addressEntity.setCustomerEntity(currentCustomerEntity);
        
        addressEntity = addressEntityControllerRemote.createAddress(addressEntity);
        
        System.out.println("Address created! Address ID: " + addressEntity.getAddressID());
        
    }
    
    private void viewAddressDetails() {
        System.out.println("*** OAS Client :: View Address Details ***\n");
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter Address ID> ");
        Long addressId = sc.nextLong();
        sc.nextLine(); //consume the enter character
        
        try{
            AddressEntity addressEntity = addressEntityControllerRemote.retrieveAddressById(addressId, currentCustomerEntity.getCustomerId());
            
            System.out.printf("%11s%40s%20s%20s%20s\n", "Address ID", "Street Address", "Unit Number", "Postal Code", "Enabled");
            System.out.printf("%11s%40s%20s%20s%20s\n", addressEntity.getAddressID().toString(), addressEntity.getStreetAddress(), addressEntity.getUnitNumber(), 
                    addressEntity.getPostalCode(), addressEntity.getEnabled());
        } catch(AddressNotFoundException ex){
            System.out.println(ex);
        }
        
        System.out.print("Press enter to continue...");
        sc.nextLine();
        System.out.println();
    }
}

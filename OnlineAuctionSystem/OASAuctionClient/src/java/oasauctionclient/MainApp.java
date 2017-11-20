/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasauctionclient;

import datamodel.CreditBalance;
import ejb.session.stateless.AddressEntityControllerRemote;
import ejb.session.stateless.AuctionListingEntityControllerRemote;
import ejb.session.stateless.BidEntityControllerRemote;
import ejb.session.stateless.CreditPackageEntityControllerRemote;
import ejb.session.stateless.CreditTransactionEntityControllerRemote;
import ejb.session.stateless.CustomerEntityControllerRemote;
import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.BidEntity;
import entity.CreditPackageEntity;
import entity.CreditTransactionEntity;
import entity.CustomerEntity;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import util.exception.AddressNotFoundException;
import util.exception.AuctionListingNotFoundException;
import util.exception.CreditPackageNotFoundException;
import util.exception.InvalidBidException;
import util.exception.InvalidLoginCredentialException;

public class MainApp {

    private AddressEntityControllerRemote addressEntityControllerRemote;

    private AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;

    private BidEntityControllerRemote bidEntityControllerRemote;

    private CustomerEntityControllerRemote customerEntityControllerRemote;

    private CreditPackageEntityControllerRemote creditPackageEntityControllerRemote;

    private CreditTransactionEntityControllerRemote creditTransactionEntityControllerRemote;

    private CustomerEntity currentCustomerEntity;

    public static final BigDecimal MAX_BIG_DECIMAL = new BigDecimal(100000000000000.0000);

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
        BigDecimal creditBalance = BigDecimal.ZERO, holdingBalance = BigDecimal.ZERO, ledgerBalance = BigDecimal.ZERO;
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
        Boolean isUnique = true;
        do {
            if (!isUnique) {
                System.out.println("Identification number is not available.\n");
                count = 0;
            }
            if (count > 0) {
                System.out.println("Identification number cannot be empty!\n");
                isUnique = true;
            }
            System.out.print("Enter identification number> ");
            identificationNo = sc.nextLine().trim();
            if (identificationNo.length() != 9 || !identificationNo.matches("^[a-zA-Z]+\\p{Digit}+[a-zA-Z]$")) {
                System.out.println("Identification number should be of length 9 and formatted e.g. S1234567A");
                count = 0;
                identificationNo = "";
                continue;
            }
            isUnique = !customerEntityControllerRemote.checkIdentificationNumber(identificationNo);

            count++;
        } while (identificationNo.isEmpty() || !isUnique);

        count = 0;
        isUnique = true;
        do {
            if (!isUnique) {
                System.out.println("Phone number is not available.\n");
                count = 0;
            }
            if (count > 0) {
                System.out.println("Phone number cannot be empty!\n");
            }
            System.out.print("Enter phone number> ");
            phoneNumber = sc.nextLine().trim();
            if (!phoneNumber.isEmpty()) {
                try {
                    Integer.parseInt(phoneNumber);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    phoneNumber = "";
                    continue;
                }
            }
            if (phoneNumber.length() != 8) {
                System.out.println("Phone number should be of length 8 E.g. 91234567");
                count = 0;
                phoneNumber = "";
                continue;
            }
            isUnique = !customerEntityControllerRemote.checkPhoneNumber(phoneNumber);

            count++;
        } while (phoneNumber.isEmpty() || !isUnique);

        count = 0;
        isUnique = true;
        do {
            if (!isUnique) {
                System.out.println("Username is not available.\n");
                count = 0;
            }
            if (count > 0) {
                System.out.println("Username cannot be empty!\n");
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

        CustomerEntity newCustomerEntity = new CustomerEntity(firstName, lastName, identificationNo, phoneNumber, creditBalance, holdingBalance, ledgerBalance, isPremium, username, password);

        newCustomerEntity = customerEntityControllerRemote.createCustomer(newCustomerEntity);

        System.out.println("Account created! " + newCustomerEntity.getCustomerId());
    }

    private boolean doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";

        while (true) {
            System.out.println("*** OAS Auction Client :: Login ***\n");
            System.out.print("Enter username> ");
            username = sc.nextLine().trim();
            System.out.print("Enter password> ");
            password = sc.nextLine().trim();
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
        Scanner sc = new Scanner(System.in);
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

                try {
                    response = Integer.parseInt(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values\n");
                    continue;
                }

                if (response == 1) {
                    viewProfile();
                } else if (response == 2) {
                    doUpdateProfile();
                } else if (response == 3) {
                    createAddress();
                } else if (response == 4) {
                    viewAddressDetails();
                } else if (response == 5) {
                    viewAllAddresses();
                } else if (response == 6) {
                    viewCreditBalance();
                } else if (response == 7) {
                    viewCreditTransactionHistory();
                } else if (response == 8) {
                    purchaseCreditPacakge();
                } else if (response == 9) {
                    viewAllAuctionListings();
                } else if (response == 10) {
                    viewAuctionListingDetails();
                } else if (response == 11) {
                    browseWonAuctionListing();
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
        System.out.printf("%11s%20s%20s%25s%14s%8s%20s%20s\n", "Customer ID", "First Name", "Last Name", "Identification Number", "Phone Number", "Premium", "Username", "Password");
        System.out.printf("%11s%20s%20s%25s%14s%8s%20s%20s\n", currentCustomerEntity.getCustomerId().toString(), currentCustomerEntity.getFirstName(),
                currentCustomerEntity.getLastName(), currentCustomerEntity.getIdentificationNo(), currentCustomerEntity.getPhoneNumber(),
                currentCustomerEntity.getPremium().toString(), currentCustomerEntity.getUsername(), currentCustomerEntity.getPassword());
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

        boolean isUnique = true;
        int count = 0;
        while (true) {
            if (!isUnique) {
                System.out.println("Phone number is not available.\n");
                count = 0;
            }
            if (count > 0) {
                System.out.println("Phone number cannot be empty!\n");
            }
            System.out.print("Enter Phone Number (blank if no change)> ");
            input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                try {
                    Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    input = "";
                    continue;
                }
            } else {
                break;
            }

            if (input.length() != 8) {
                System.out.println("Phone number should be of length 8 E.g. 91234567");
                count = 0;
                input = "";
                continue;
            }
            isUnique = !customerEntityControllerRemote.checkPhoneNumber(input);

            count++;

            if (isUnique) {
                currentCustomerEntity.setPhoneNumber(input);
                break;
            }
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
                    break;
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

        System.out.print("Enter Unit number> ");
        unitNumber = sc.nextLine().trim();

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Postal code cannot be empty!\n");
            }
            System.out.print("Enter postal code> ");
            postalCode = sc.nextLine().trim();

            count++;

            boolean isValid = true;

            try {
                Integer.parseInt(postalCode);
            } catch (NumberFormatException ex) {
                isValid = false;
            }

            if (postalCode.length() != 6) {
                isValid = false;
            }

            if (!isValid) {
                System.out.println("Postal code must be 6 digits long");
                postalCode = "";
                count = 0;
            }

        } while (postalCode.isEmpty() || postalCode.length() != 6);

        System.out.println();

        AddressEntity addressEntity = new AddressEntity(streetAddress, unitNumber, postalCode, enabled);

        addressEntity = addressEntityControllerRemote.createAddress(addressEntity, currentCustomerEntity.getCustomerId());

        System.out.println("Address created! Address ID: " + addressEntity.getAddressID());

    }

    private void viewAddressDetails() {
        System.out.println("*** OAS Client :: View Address Details ***\n");
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Address ID> ");
        Long addressId = sc.nextLong();
        sc.nextLine(); //consume the enter character

        try {
            AddressEntity addressEntity = addressEntityControllerRemote.retrieveAddressById(addressId, currentCustomerEntity.getCustomerId());

            System.out.printf("%11s%40s%20s%20s%20s\n", "Address ID", "Street Address", "Unit Number", "Postal Code", "Enabled");
            System.out.printf("%11s%40s%20s%20s%20s\n", addressEntity.getAddressID().toString(), addressEntity.getStreetAddress(), addressEntity.getUnitNumber(),
                    addressEntity.getPostalCode(), addressEntity.getEnabled());
            System.out.println("------------------------");
            System.out.println("1: Update Address");
            System.out.println("2: Delete Address");
            System.out.println("3: Back\n");

            Integer response = 0;
            while (true) {
                try {
                    response = Integer.parseInt(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

                if (response >= 1 && response <= 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 1) {
                doUpdateAddress(addressEntity);
            } else if (response == 2) {
                doDeleteAddress(addressEntity);
            }

        } catch (AddressNotFoundException ex) {
            System.out.println(ex.getMessage());
            System.out.print("Press enter to continue...");
            sc.nextLine();
        }

    }

    private void doUpdateAddress(AddressEntity addressEntity) {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Client :: Update Address ***\n");
        System.out.print("Enter Street Address (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            addressEntity.setStreetAddress(input);
        }

        System.out.print("Enter Unit Number (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            addressEntity.setUnitNumber(input);
        }

        System.out.print("Enter Postal Code (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            addressEntity.setPostalCode(input);
        }

        addressEntityControllerRemote.updateAddress(addressEntity);
        System.out.println("Address updated successfully!\n");
    }

    private void doDeleteAddress(AddressEntity addressEntity) {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Client Panel :: Delete Address ***\n");
        System.out.printf("Confirm Delete Address %s %s %s(Address ID: %d) (Enter 'Y' to Delete)> ", addressEntity.getStreetAddress(), addressEntity.getUnitNumber(), addressEntity.getPostalCode(), addressEntity.getAddressID());
        input = sc.nextLine().trim();

        if (input.equalsIgnoreCase("Y")) {
            addressEntityControllerRemote.deleteAddress(addressEntity.getAddressID(), currentCustomerEntity.getCustomerId());
            System.out.println("Address deleted successfully!\n");
        } else {
            System.out.println("Address NOT deleted!\n");
        }
    }

    private void viewAllAddresses() {
        Scanner sc = new Scanner(System.in);
        try {
            List<AddressEntity> addressEntities = addressEntityControllerRemote.retrieveAllAddress(currentCustomerEntity.getCustomerId());

            System.out.printf("%11s%40s%20s%20s%20s\n", "Address ID", "Street Address", "Unit Number", "Postal Code", "Enabled");
            for (AddressEntity addressEntity : addressEntities) {
                System.out.printf("%11s%40s%20s%20s%20s\n", addressEntity.getAddressID().toString(), addressEntity.getStreetAddress(), addressEntity.getUnitNumber(),
                        addressEntity.getPostalCode(), addressEntity.getEnabled());
            }
        } catch (AddressNotFoundException ex) {
            System.out.println("No addresses found.");
        }

        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void viewCreditBalance() {
        Scanner sc = new Scanner(System.in);

        CreditBalance creditBalance = customerEntityControllerRemote.retrieveCreditBalance(currentCustomerEntity.getCustomerId());

        System.out.println("Credit balance: " + creditBalance.getCreditBalance());
        System.out.println("Holding balance: " + creditBalance.getHoldingBalance());
        System.out.println("Available balance: " + creditBalance.getAvailableBalance() + "\n");

        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void viewCreditTransactionHistory() {
        Scanner sc = new Scanner(System.in);
        List<CreditTransactionEntity> creditTransactionEntities = creditTransactionEntityControllerRemote.retrieveCreditTransactions(currentCustomerEntity.getCustomerId());
        if (!creditTransactionEntities.isEmpty()) {
            System.out.printf("%20s%10s%20s\n", "Transaction Id", "Credits", "Transaction Type");
            for (CreditTransactionEntity creditTransactionEntity : creditTransactionEntities) {
                System.out.printf("%20s%10s%20s\n", creditTransactionEntity.getCreditPackageTransactionId(), creditTransactionEntity.getNumberOfCredits(), creditTransactionEntity.getTransactionType());
            }
        } else {
            System.out.println("No credit transactions.");
        }

        System.out.println("Press enter to continue...");
        sc.nextLine();
    }

    private void purchaseCreditPacakge() {
        Scanner sc = new Scanner(System.in);

        //need to edit. should only retrieve the enabled ones\
        try {
            List<CreditPackageEntity> creditPackageEntities = creditPackageEntityControllerRemote.retrieveAllCreditPackages();

            int quantityToPurchase;

            HashMap<Long, CreditPackageEntity> enabledPackages = new HashMap();
            //Print out all enabled credit packages for customer to choose
            System.out.printf("%20s%25s%20s%20s\n", "Credit Package Id", "Credit Package Name", "Number of Credits", "Price");
            for (CreditPackageEntity creditPackageEntity : creditPackageEntities) {
                if (creditPackageEntity.getEnabled() == true) {
                    System.out.printf("%20s%25s%20s%20s\n", creditPackageEntity.getCreditPackageId(), creditPackageEntity.getCreditPackageName(), creditPackageEntity.getNumberOfCredits(), creditPackageEntity.getPrice());
                    enabledPackages.put(creditPackageEntity.getCreditPackageId(), creditPackageEntity);
                }
            }

            Long response = 0L;

            CreditPackageEntity creditPackageEntity;
            //NEED TO ROLLBACK. HOWHOW?
            while (true) {
                System.out.print("Enter Credit Package ID for Purchase> ");
                try {
                    response = Long.parseLong(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

                //Check if exist in enabled
                for (int i = 0; i < enabledPackages.size(); i++) {
                    if (enabledPackages.containsKey(response)) {
                        break;
                    }
                    if (i == enabledPackages.size() - 1) {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                creditPackageEntity = enabledPackages.get(response);
                System.out.print("Enter required quantity for " + creditPackageEntity.getCreditPackageName() + "> ");
                quantityToPurchase = sc.nextInt();

                if (quantityToPurchase > 0) {
                    sc.nextLine(); // consume enter character
                    //call the purchase thing here
                    creditTransactionEntityControllerRemote.purchaseCreditPackage(creditPackageEntity, quantityToPurchase, currentCustomerEntity.getCustomerId());
                    System.out.println(creditPackageEntity.getCreditPackageName() + " purchased successfully!: " + quantityToPurchase + " unit of " + creditPackageEntity.getCreditPackageName() + "\n");
                    System.out.print("Press enter to continue...");
                    sc.nextLine();
                    return;
                } else {
                    System.out.println("Invalid quantity!\n");
                }
            }
        } catch (CreditPackageNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void viewAllAuctionListings() {
        Scanner sc = new Scanner(System.in);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal;

        try {
            List<AuctionListingEntity> auctionListingEntities = auctionListingEntityControllerRemote.retrieveAllActiveAuctionListings();

            System.out.printf("%20s%15s%20s%25s\n", "Auction Listing Id", "Item Name", "Starting Bid Amount", "End Date Time");
            for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
                if (auctionListingEntity.getEnabled() == true) {

                    cal = auctionListingEntity.getEndDateTime();
                    System.out.printf("%20s%15s%20s%25s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(), auctionListingEntity.getStartingBidAmount(), dateFormat.format(cal.getTime()));
                }
            }
        } catch (AuctionListingNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void viewAuctionListingDetails() {
        System.out.println("*** OAS Client :: View Auction Listing Details ***\n");
        Scanner sc = new Scanner(System.in);

        Long auctionListingId = new Long(-1);
        AuctionListingEntity auctionListingEntity;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal;

        try {
            do {
                System.out.print("Enter Auction Listing ID> ");
                try {
                    auctionListingId = Long.parseLong(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } while (auctionListingId.equals(-1));

            sc.nextLine(); // consume enter character 
            try {
                auctionListingEntity = auctionListingEntityControllerRemote.retrieveActiveAuctionListing(auctionListingId);

                cal = auctionListingEntity.getEndDateTime();

                Integer response = 0;
                while (true) {
                    BigDecimal curentBidAmount = ((auctionListingEntity.getCurrentBidAmount() == null) ? auctionListingEntity.getStartingBidAmount() : auctionListingEntity.getCurrentBidAmount());
                    String highestBidder = auctionListingEntityControllerRemote.getHighestBidder(auctionListingEntity.getAuctionListingId());
                    System.out.printf("%20s%15s%25s%25s%25s%25s\n", "Auction Listing Id", "Item Name", "Starting Bid Amount", "Current Bid Amount", "Highest Bidder", "End Date Time");
                    System.out.printf("%20s%15s%25s%25s%25s%25s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(), auctionListingEntity.getStartingBidAmount(), curentBidAmount, highestBidder, dateFormat.format(cal.getTime()));
                    System.out.println("------------------------");
                    System.out.println("1: Place New Bid");
                    System.out.println("2: Refresh Auction Listing Bids");
                    System.out.println("3: Back\n");
                    System.out.print("> ");

                    try {
                        response = Integer.parseInt(sc.next());
                    } catch (NumberFormatException ex) {
                        System.out.println("Please enter numeric values.\n");
                        continue;
                    }

                    if (response == 1) {
                        placeNewBid(auctionListingEntity);
                        break;
                    } else if (response == 2) {
                        auctionListingEntity = auctionListingEntityControllerRemote.retrieveActiveAuctionListing(auctionListingId);
                    } else if (response == 3) {
                        return;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                sc.nextLine(); // consume enter character 
            } catch (AuctionListingNotFoundException ex) {
                System.out.println(ex.getMessage());
                System.out.print("Press enter to continue...");
                sc.nextLine();
            }
        } catch (NullPointerException ex) {
            System.out.println(ex);
        }
    }

    private void placeNewBid(AuctionListingEntity auctionListingEntity) {
        System.out.println("*** OAS Auction Client :: Place Bid ***\n");
        Scanner sc = new Scanner(System.in);

        BigDecimal userBid, currentBidAmount, nextIncrement, nextExpectedBid;
        BidEntity bidEntity;
        Calendar currentTimestamp = Calendar.getInstance();

        currentBidAmount = ((auctionListingEntity.getCurrentBidAmount() == null) ? auctionListingEntity.getStartingBidAmount() : auctionListingEntity.getCurrentBidAmount());
        nextIncrement = bidEntityControllerRemote.getBidIncrement(currentBidAmount);
        nextExpectedBid = currentBidAmount.add(nextIncrement);

        do {
            System.out.println("Minimum bid price is " + nextExpectedBid.doubleValue() + "\n");

            System.out.print("Enter Bid Amount> ");
            userBid = sc.nextBigDecimal();

            if (userBid.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
            }

        } while (userBid.compareTo(BigDecimal.ZERO) <= 0 || userBid.compareTo(nextExpectedBid) < 0 || userBid.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0);

        sc.nextLine(); //consume enter character

        bidEntity = new BidEntity(userBid, currentTimestamp);
        bidEntity.setCustomerEntity(currentCustomerEntity);
        bidEntity.setAuctionListingEntity(auctionListingEntity);
        try {
            bidEntity = bidEntityControllerRemote.createNewBid(bidEntity);
//            try{
//            customerEntityControllerRemote.useCredits(currentCustomerEntity.getCustomerId(), userBid);
//            } catch(InsufficientCreditsException ex){
//                System.out.println(ex.getMessage());
//            }
            System.out.println("Bid placed! Bid ID: " + bidEntity.getBidId());
        } catch (InvalidBidException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void browseWonAuctionListing() {
        Scanner sc = new Scanner(System.in);

        List<AuctionListingEntity> auctionListingEntities = auctionListingEntityControllerRemote.retrieveWonAuctionListings(currentCustomerEntity.getCustomerId());

        if (auctionListingEntities.isEmpty()) {
            System.out.println("No won listings.");
            System.out.println("Press enter to continue...");
            sc.nextLine();
            return;
        }

        System.out.printf("%5s%25s%20s%20s\n", "Row", "Auction Listing ID", "Item name", "Bid Amount");
        int i = 1;
        for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
            System.out.printf("%5s%25s%20s%20s\n", i, auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(), auctionListingEntity.getWinningBidEntity().getBidAmount());
            i++;
        }
        System.out.println("------------------------");
        int response = 0;
        while (true) {
            System.out.println("1: Select Delivery Address for Won Auction Listing");
            System.out.println("2: Back\n");
            System.out.print("> ");

            response = sc.nextInt();

            if (response == 1) {
                selectDeliveryAddress(auctionListingEntities);
                break;
            } else if (response == 2) {
                return;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }

        }

    }

    private void selectDeliveryAddress(List<AuctionListingEntity> auctionListingEntities) {
        Scanner sc = new Scanner(System.in);

        int row, count = 0;
        while (true) {
            System.out.print("Select item row> ");
            String input = sc.nextLine();
            try {
                row = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Enter numeric values!\n");
                continue;
            }
            if (row > auctionListingEntities.size() || row <= 0) {
                System.out.println("Please enter valid rows.");
                continue;
            }
            break;
        }

        AuctionListingEntity auctionListingEntity = auctionListingEntities.get(row - 1);

        if (auctionListingEntity.getDeliveryAddress() != null) {
            System.out.println("This auction listing has a delivery address");
            System.out.print("Press enter to continue...");
            sc.nextLine();
        } else {
            try {
                List<AddressEntity> addressEntities = addressEntityControllerRemote.retrieveAllAddress(currentCustomerEntity.getCustomerId());
                System.out.printf("%5s%11s%40s%20s%20s\n", "Row", "Address ID", "Street Address", "Unit Number", "Postal Code");
                int i = 1;
                for (AddressEntity addressEntity : addressEntities) {
                    System.out.printf("%5s%11s%40s%20s%20s\n", i, addressEntity.getAddressID().toString(),
                            addressEntity.getStreetAddress(), addressEntity.getUnitNumber(), addressEntity.getPostalCode());
                    i++;
                }

                int addressRow;
                while (true) {
                    if (count > 0) {
                        System.out.println("Please enter valid rows.");
                    }

                    System.out.print("Select address row> ");
                    String input = sc.nextLine();
                    try {
                        addressRow = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        System.out.println("Please enter numeric values!\n");
                        continue;
                    }
                    if (addressRow > addressEntities.size() || addressRow <= 0) {
                        System.out.println("Please enter valid rows.");
                        continue;
                    }
                    break;
                }

                auctionListingEntity.setDeliveryAddress(addressEntities.get(addressRow - 1));
                auctionListingEntityControllerRemote.updateAuctionListing(auctionListingEntity);

                System.out.println("Delivery address added!");
                System.out.print("Press enter to continue...");
                sc.nextLine();
            } catch (AddressNotFoundException ex) {
                System.out.println("Please create addresses first.");
                System.out.println("Press enter to continue...");
                sc.nextLine();
            }
        }

    }
}

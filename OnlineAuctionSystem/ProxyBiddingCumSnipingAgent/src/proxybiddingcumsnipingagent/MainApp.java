/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxybiddingcumsnipingagent;

import ejb.session.ws.AuctionListingEntity;
import ejb.session.ws.AuctionListingNotFoundException_Exception;
import ejb.session.ws.CreditBalance;
import ejb.session.ws.CustomerEntity;
import ejb.session.ws.InsufficientCreditsException_Exception;
import ejb.session.ws.InvalidLoginCredentialException_Exception;
import ejb.session.ws.InvalidRegistrationException_Exception;
import ejb.session.ws.InvalidSnipingException_Exception;
import ejb.session.ws.ProxyBiddingEntity;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author User
 */
public class MainApp {

    private CustomerEntity currentCustomerEntity;

    public MainApp() {
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Proxy Bidding cum Sniping Agent ***\n");
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
                    } catch (InvalidLoginCredentialException_Exception ex) {
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
        String username = "";
        String password = "";

        System.out.println("*** Proxy Bidding cum Sniping Agent :: Login ***\n");
        while (true) {
            System.out.print("Enter username> ");
            username = sc.nextLine().trim();
            System.out.print("Enter password> ");
            password = sc.nextLine().trim();
            if (username.length() > 0 && password.length() > 0) {
                try {
                    regsterPremiumCustomer(username, password);
                    System.out.println("Registration successful!\n");
                } catch (InvalidRegistrationException_Exception ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
                return;
            } else {
                System.out.println("Please enter valid username/password!\n");
            }
        }
    }

    private boolean doLogin() throws InvalidLoginCredentialException_Exception {
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";

        while (true) {
            System.out.println("*** Proxy Bidding cum Sniping Agent :: Login ***\n");
            System.out.print("Enter username> ");
            username = sc.nextLine().trim();
            System.out.print("Enter password> ");
            password = sc.nextLine().trim();
            if (username.length() > 0 && password.length() > 0) {
                try {
                    currentCustomerEntity = customerLogin(username, password);
                    System.out.println("Login successful!\n");
                    return true;
                } catch (InvalidLoginCredentialException_Exception ex) {
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
            System.out.println("*** Proxy Bidding cum Sniping Agent ***\n");
            System.out.println("Welcome " + currentCustomerEntity.getFirstName() + " " + currentCustomerEntity.getLastName() + "\n");
            System.out.println("1: View Credit Balance");
            System.out.println("2: View Auction Listing Details");
            System.out.println("3: Browse All Auction Listings");
            System.out.println("4: View Won Auction Listings");
            System.out.println("5: Logout\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    viewCreditBalance();
                } else if (response == 2) {
                    viewAuctionListingDetails();
                } else if (response == 3) {
                    browseAllAuctionListings();
                } else if (response == 4) {
                    retrieveWonListings();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }

    private void viewCreditBalance() {
        Scanner sc = new Scanner(System.in);

        CreditBalance creditBalance = retrieveCreditBalance(currentCustomerEntity.getCustomerId());

        System.out.println("Credit balance: " + creditBalance.getCreditBalance());
        System.out.println("Holding balance: " + creditBalance.getHoldingBalance());
        System.out.println("Available balance: " + creditBalance.getAvailableBalance() + "\n");

        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void viewAuctionListingDetails() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter Auction Listing Id> ");
            Long auctionListingId = sc.nextLong();

            Integer response = 0;
            while (true) {
                AuctionListingEntity auctionListingEntity = viewAuctionListingDetails(auctionListingId);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                BigDecimal curentBidAmount = ((auctionListingEntity.getCurrentBidAmount() == null) ? auctionListingEntity.getStartingBidAmount() : auctionListingEntity.getCurrentBidAmount());
                Calendar cal = auctionListingEntity.getEndDateTime().toGregorianCalendar();

                System.out.printf("%20s%15s%25s%25s%25s\n", "Auction Listing Id", "Item Name", "Starting Bid Amount", "Current Bid Amount", "End Date Time");
                System.out.printf("%20s%15s%25s%25s%25s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(), auctionListingEntity.getStartingBidAmount(), curentBidAmount, dateFormat.format(cal.getTime()));

                System.out.println("------------------------");
                System.out.println("1: Configure Proxy Bidding for Auction Listing");
                System.out.println("2: Configure Sniping for Auction Listing");
                System.out.println("3: Back\n");
                System.out.print("> ");

                try {
                    response = Integer.parseInt(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

                if (response == 1) {
                    doProxyBidding(auctionListingEntity);
                } else if (response == 2) {
                    doSniping(auctionListingEntity);
                } else if (response == 3) {
                    return;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
        } catch (AuctionListingNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
        sc.nextLine(); // consume enter character
        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void browseAllAuctionListings() {
        Scanner sc = new Scanner(System.in);

        try {
            List<AuctionListingEntity> auctionListings = retrieveAllAuctionListings();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal;

            System.out.printf("%20s%15s%20s%25s\n", "Auction Listing Id", "Item Name", "Starting Bid Amount", "End Date Time");
            for (AuctionListingEntity auctionListingEntity : auctionListings) {
                cal = auctionListingEntity.getEndDateTime().toGregorianCalendar();
                System.out.printf("%20s%15s%20s%25s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(), auctionListingEntity.getStartingBidAmount(), dateFormat.format(cal.getTime()));
            }
        } catch (AuctionListingNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void retrieveWonListings() {
        Scanner sc = new Scanner(System.in);

        try {
            Long customerId = currentCustomerEntity.getCustomerId();
            List<AuctionListingEntity> wonListings = viewWonAuctionListings(customerId);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal;

            System.out.printf("%20s%15s%20s%25s\n", "Auction Listing Id", "Item Name", "Starting Bid Amount", "End Date Time");
            for (AuctionListingEntity auctionListingEntity : wonListings) {
                cal = auctionListingEntity.getEndDateTime().toGregorianCalendar();
                System.out.printf("%20s%15s%20s%25s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(), auctionListingEntity.getStartingBidAmount(), dateFormat.format(cal.getTime()));
            }
        } catch (AuctionListingNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private void doProxyBidding(AuctionListingEntity auctionListingEntity) {
        Scanner sc = new Scanner(System.in);

        ProxyBiddingEntity proxyBiddingEntity = new ProxyBiddingEntity();

        System.out.print("Insert maximum bid> ");
        proxyBiddingEntity.setMaximumAmount(sc.nextBigDecimal());
        proxyBiddingEntity.setEnabled(true);
        sc.nextLine(); //consume enter character
        try {
            proxyBiddingEntity = createProxyBidding(proxyBiddingEntity, currentCustomerEntity.getCustomerId(), auctionListingEntity.getAuctionListingId());
            System.out.println("Proxy bid created: " + proxyBiddingEntity.getProxyBiddingId());
        } catch (InsufficientCreditsException_Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("Press enter to continue...");
        sc.nextLine();
    }

    private void doSniping(AuctionListingEntity auctionListingEntity) {
        Scanner sc = new Scanner(System.in);

        int year, month, day, hour, min, sec;

        String snipeString;

        Calendar startDateTime = auctionListingEntity.getStartDateTime().toGregorianCalendar();
        Calendar endDateTime = auctionListingEntity.getEndDateTime().toGregorianCalendar();
        GregorianCalendar snipingDateTime = new GregorianCalendar();
        int count = 0;

        BigDecimal currentBidAmt = auctionListingEntity.getCurrentBidAmount();

        if (currentBidAmt == null) {
            currentBidAmt = auctionListingEntity.getStartingBidAmount();
        }

        BigDecimal nextBid = currentBidAmt.add(getBidIncrement(currentBidAmt));

        while (true) {
            if (count > 0) {
                System.out.println("Sniping date time must be later than start date time and earlier than end date time!\n");
            }

            System.out.print("Enter end date and time (yyyymmddhhmmss)> ");
            snipeString = sc.nextLine().trim();

            count++;
            if (snipeString.isEmpty()) {
                System.out.println("Snipe date time cannot be empty!");
                count = 0;
                continue;
            } else if (snipeString.length() != 14) {
                System.out.println("Invalid date time!");
                count = 0;
                snipeString = "";
                continue;
            }

            year = Integer.parseInt(snipeString.substring(0, 4).trim());
            month = Integer.parseInt(snipeString.substring(4, 6).trim());
            day = Integer.parseInt(snipeString.substring(6, 8).trim());
            hour = Integer.parseInt(snipeString.substring(8, 10).trim());
            min = Integer.parseInt(snipeString.substring(10, 12).trim());
            sec = Integer.parseInt(snipeString.substring(12, 14).trim());
            snipingDateTime.clear();
            snipingDateTime.set(year, month - 1, day, hour, min, sec);

            if (snipingDateTime.compareTo(endDateTime) < 0 || snipingDateTime.compareTo(startDateTime) > 0) {
                break;
            }
        }

        BigDecimal maxAmount;
        count = 0;
        do {
            System.out.println("Minimum bid price is " + nextBid.doubleValue() + "\n");
            
            System.out.print("Enter maximum amount> ");
            maxAmount = sc.nextBigDecimal();
            
        } while (maxAmount.compareTo(nextBid) < 0);
        sc.nextLine(); //consume enter character

        XMLGregorianCalendar xc;
        try {
            xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(snipingDateTime);
            try {
                createSnipingAuctionListing(xc, auctionListingEntity.getAuctionListingId(), maxAmount, currentCustomerEntity.getCustomerId());
                System.out.println("Snipe created! Ensure you have enough credits for sniping!");
            } catch (InvalidSnipingException_Exception exception) {
                System.out.println(exception.getMessage());
            }
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Error!");
        }

        System.out.print("Press enter to continue...");
        sc.nextLine();
    }

    private static void regsterPremiumCustomer(java.lang.String username, java.lang.String password) throws InvalidRegistrationException_Exception {
        ejb.session.ws.CustomerEntityWebService_Service service = new ejb.session.ws.CustomerEntityWebService_Service();
        ejb.session.ws.CustomerEntityWebService port = service.getCustomerEntityWebServicePort();
        port.regsterPremiumCustomer(username, password);
    }

    private static CustomerEntity customerLogin(java.lang.String username, java.lang.String password) throws InvalidLoginCredentialException_Exception {
        ejb.session.ws.CustomerEntityWebService_Service service = new ejb.session.ws.CustomerEntityWebService_Service();
        ejb.session.ws.CustomerEntityWebService port = service.getCustomerEntityWebServicePort();
        return port.customerLogin(username, password);
    }

    private static CreditBalance retrieveCreditBalance(java.lang.Long customerId) {
        ejb.session.ws.CustomerEntityWebService_Service service = new ejb.session.ws.CustomerEntityWebService_Service();
        ejb.session.ws.CustomerEntityWebService port = service.getCustomerEntityWebServicePort();
        return port.retrieveCreditBalance(customerId);
    }

    private static AuctionListingEntity viewAuctionListingDetails(java.lang.Long auctionListingId) throws AuctionListingNotFoundException_Exception {
        ejb.session.ws.AuctionListingEntityWebService_Service service = new ejb.session.ws.AuctionListingEntityWebService_Service();
        ejb.session.ws.AuctionListingEntityWebService port = service.getAuctionListingEntityWebServicePort();
        return port.viewAuctionListingDetails(auctionListingId);
    }

    private static java.util.List<ejb.session.ws.AuctionListingEntity> retrieveAllAuctionListings() throws AuctionListingNotFoundException_Exception {
        ejb.session.ws.AuctionListingEntityWebService_Service service = new ejb.session.ws.AuctionListingEntityWebService_Service();
        ejb.session.ws.AuctionListingEntityWebService port = service.getAuctionListingEntityWebServicePort();
        return port.retrieveAllAuctionListings();
    }

    private static java.util.List<ejb.session.ws.AuctionListingEntity> viewWonAuctionListings(java.lang.Long customerId) throws AuctionListingNotFoundException_Exception {
        ejb.session.ws.AuctionListingEntityWebService_Service service = new ejb.session.ws.AuctionListingEntityWebService_Service();
        ejb.session.ws.AuctionListingEntityWebService port = service.getAuctionListingEntityWebServicePort();
        return port.viewWonAuctionListings(customerId);
    }

    private static ProxyBiddingEntity createProxyBidding(ejb.session.ws.ProxyBiddingEntity proxyBiddingEntity, java.lang.Long customerId, java.lang.Long auctionListingId) throws InsufficientCreditsException_Exception {
        ejb.session.ws.BidEntityWebService_Service service = new ejb.session.ws.BidEntityWebService_Service();
        ejb.session.ws.BidEntityWebService port = service.getBidEntityWebServicePort();
        return port.createProxyBidding(proxyBiddingEntity, customerId, auctionListingId);
    }

    private static void createSnipingAuctionListing(javax.xml.datatype.XMLGregorianCalendar snipingDateTime, java.lang.Long auctionListingId, java.math.BigDecimal maximumAmount, java.lang.Long customerId) throws InvalidSnipingException_Exception {
        ejb.session.ws.BidEntityWebService_Service service = new ejb.session.ws.BidEntityWebService_Service();
        ejb.session.ws.BidEntityWebService port = service.getBidEntityWebServicePort();
        port.createSnipingAuctionListing(snipingDateTime, auctionListingId, maximumAmount, customerId);
    }

    private static BigDecimal getBidIncrement(java.math.BigDecimal currentPrice) {
        ejb.session.ws.BidEntityWebService_Service service = new ejb.session.ws.BidEntityWebService_Service();
        ejb.session.ws.BidEntityWebService port = service.getBidEntityWebServicePort();
        return port.getBidIncrement(currentPrice);
    }

}

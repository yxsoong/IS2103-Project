/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxybiddingcumsnipingagent;

import ejb.session.ws.CustomerEntity;
import ejb.session.ws.InvalidLoginCredentialException_Exception;
import ejb.session.ws.InvalidRegistrationException_Exception;
import java.math.BigDecimal;
import java.util.Scanner;

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
                
                if(response == 1){
                    viewCreditBalance();
                }
            }

            if (response == 5) {
                break;
            }
        }
    }
    
    private void viewCreditBalance(){
        Scanner sc = new Scanner(System.in);
        
        currentCustomerEntity = retrieveCreditBalance(currentCustomerEntity.getCustomerId());
        
        BigDecimal creditBalance = currentCustomerEntity.getCreditBalance();
        BigDecimal holdingBalance = currentCustomerEntity.getHoldingBalance();

        System.out.println("Credit balance: " + creditBalance);
        System.out.println("Holding balance: " + holdingBalance);
        System.out.println("Available balance: " + creditBalance.subtract(holdingBalance) + "\n");

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

    private static CustomerEntity retrieveCreditBalance(java.lang.Long customerId) {
        ejb.session.ws.CustomerEntityWebService_Service service = new ejb.session.ws.CustomerEntityWebService_Service();
        ejb.session.ws.CustomerEntityWebService port = service.getCustomerEntityWebServicePort();
        return port.retrieveCreditBalance(customerId);
    }
    
    
    
}

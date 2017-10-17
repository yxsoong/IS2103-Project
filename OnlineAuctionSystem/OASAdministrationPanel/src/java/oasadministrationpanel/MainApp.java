/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oasadministrationpanel;

import ejb.session.stateless.EmployeeEntityControllerRemote;
import entity.EmployeeEntity;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author User
 */
public class MainApp {

    private EmployeeEntityControllerRemote employeeEntityControllerRemote;

    private EmployeeEntity currentEmployeeEntity;

    public MainApp() {
    }

    public MainApp(EmployeeEntityControllerRemote employeeEntityControllerRemote) {
        this.employeeEntityControllerRemote = employeeEntityControllerRemote;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to OAS Administration Panel ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {

                    try {
                        if (doLogin()) {
                            mainMenu();
                        }
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential!\n");
                    }
                } else if (response == 2) {
                    break;

                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }

    }

    private void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Administration Panel ***\n");
            System.out.println("You are login as " + currentEmployeeEntity.getFirstName() + " " + currentEmployeeEntity.getLastName() + " with " + currentEmployeeEntity.getAccessRight() + " rights\n");
            System.out.println("1: System Administrator");
            System.out.println("2: Finance Staff");
            System.out.println("3: Sales Staff");
            System.out.println("4: Change Password");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    systemAdminMenu();
                } else if (response == 2) {
                    financeStaffMenu();
                } else if (response == 3) {
                    salesStaffMenu();
                } else if (response == 4) {
                    System.out.print("Provide current password> ");
                    String currentPassword = scanner.nextLine().trim();
                    if (currentPassword.equals(currentEmployeeEntity.getPassword())) {
                        changePassword();
                    } else {
                        System.out.println("Password do not match!");
                    }
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
                    currentEmployeeEntity = employeeEntityControllerRemote.employeeLogin(username, password);
                    System.out.println("Login successful!\n");
                    return true;
                } catch (InvalidLoginCredentialException ex) {
                    throw new InvalidLoginCredentialException();
                }
            } else {
                System.out.println("Please enter valid username/password!\n");
            }
        }
    }

    private void systemAdminMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS System Administrator Menu ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View Employee Details");
            System.out.println("3: View All Employees");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    createNewEmployee();
                } else if (response == 2) {
                    viewEmployeeDetails();
                } else if (response == 3) {
                    viewAllEmployees();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    private void financeStaffMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Finance Staff Menu ***\n");
            System.out.println("1: Create Credit Package");
            System.out.println("2: View Credit Package Details");
            System.out.println("3: View All Credit Packages");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    createNewCreditPackage();
                } else if (response == 2) {
                    viewCreditPackageDetails();
                } else if (response == 3) {
                    viewAllCreditPackages();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    private void salesStaffMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** OAS Sales Staff Menu ***\n");
            System.out.println("1: Create Auction Listing");
            System.out.println("2: View Auction Listing Details");
            System.out.println("3: View All Auction Listings");
            System.out.println("4: View All Auction Listings with Bids Below Reserve Price");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    createAuctionListing();
                } else if (response == 2) {
                    viewAuctionListingDetails();
                } else if (response == 3) {
                    viewAllAuctionListings();
                } else if (response == 4) {
                    viewListingsBelowReservePrice();
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

    private void changePassword() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter New Password> ");
        String newPin = scanner.nextLine().trim();
        System.out.print("Confirm New Password> ");
        String newPinConfirm = scanner.nextLine().trim();

        currentEmployeeEntity.setPassword(newPin);

        employeeEntityControllerRemote.changePassword(currentEmployeeEntity);
        System.out.println("Password Changed Successfully!");
    }

    private void createNewEmployee() {

    }

    private void viewEmployeeDetails() {

    }

    private void viewAllEmployees() {

    }
    
    private void createNewCreditPackage(){
        
    }
    
    private void viewCreditPackageDetails(){
        
    }
    
    private void viewAllCreditPackages(){
        
    }
    
    private void createAuctionListing(){
        
    }
    
    private void viewAuctionListingDetails(){
        
    }
    
    private void viewAllAuctionListings(){
        
    }
    
    private void viewListingsBelowReservePrice(){
        
    }
}

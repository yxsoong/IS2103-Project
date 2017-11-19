package oasadministrationpanel;

import ejb.session.stateless.AuctionListingEntityControllerRemote;
import ejb.session.stateless.BidEntityControllerRemote;
import ejb.session.stateless.CreditPackageEntityControllerRemote;
import ejb.session.stateless.EmployeeEntityControllerRemote;
import entity.EmployeeEntity;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

public class MainApp {

    private AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;
    private BidEntityControllerRemote bidEntityControllerRemote;
    private CreditPackageEntityControllerRemote creditPackageEntityControllerRemote;
    private EmployeeEntityControllerRemote employeeEntityControllerRemote;
    private SystemAdministratorModule systemAdministratorModule;
    private FinanceStaffModule financeStaffModule;
    private SalesStaffModule salesStaffModule;

    private EmployeeEntity currentEmployeeEntity;

    public MainApp() {
    }

    public MainApp(AuctionListingEntityControllerRemote auctionListingEntityControllerRemote, BidEntityControllerRemote bidEntityControllerRemote, CreditPackageEntityControllerRemote creditPackageEntityControllerRemote, EmployeeEntityControllerRemote employeeEntityControllerRemote) {
        this.auctionListingEntityControllerRemote = auctionListingEntityControllerRemote;
        this.bidEntityControllerRemote = bidEntityControllerRemote;
        this.creditPackageEntityControllerRemote = creditPackageEntityControllerRemote;
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

                String input = sc.next();

                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

                if (response == 1) {

                    try {
                        if (doLogin()) {
                            systemAdministratorModule = new SystemAdministratorModule(employeeEntityControllerRemote, currentEmployeeEntity);
                            financeStaffModule = new FinanceStaffModule(creditPackageEntityControllerRemote, currentEmployeeEntity);
                            salesStaffModule = new SalesStaffModule(auctionListingEntityControllerRemote, currentEmployeeEntity);
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
        Scanner sc = new Scanner(System.in);
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

                String input = sc.nextLine().trim();
                
                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

                if (response == 1) {
                    try {
                        systemAdministratorModule.menuSystemAdministration();
                    } catch (InvalidAccessRightException ex) {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    try {
                        financeStaffModule.financeStaffMenu();
                    } catch (InvalidAccessRightException ex) {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                } else if (response == 3) {
                    try {
                        salesStaffModule.salesStaffMenu();
                    } catch (InvalidAccessRightException ex) {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                } else if (response == 4) {
                    changePassword();
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

        while (true) {
            System.out.println("*** OAS Administration System :: Login ***\n");
            System.out.print("Enter username> ");
            username = sc.nextLine().trim();
            System.out.print("Enter password> ");
            password = sc.nextLine().trim();
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

    private void changePassword() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Provide current password> ");
        //sc.nextLine();  // consume the enter character
        String currentPassword = sc.nextLine().trim();
        if (currentPassword.equals(currentEmployeeEntity.getPassword())) {
            System.out.print("Enter New Password> ");
            String newPassword = sc.nextLine().trim();
            System.out.print("Confirm New Password> ");
            String newPasswordConfirm = sc.nextLine().trim();

            if (newPassword.equals(newPasswordConfirm)) {
                currentEmployeeEntity.setPassword(newPassword);
                employeeEntityControllerRemote.changePassword(currentEmployeeEntity);
            } else {
                System.out.println("Passwords do not match!");
            }
            System.out.println("Password Changed Successfully!");
        } else {
            System.out.println("Password do not match!");
        }

    }

}

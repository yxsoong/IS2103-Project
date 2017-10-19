package oasadministrationpanel;

import ejb.session.stateless.EmployeeEntityControllerRemote;
import entity.EmployeeEntity;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

public class MainApp {

    private EmployeeEntityControllerRemote employeeEntityControllerRemote;
    private SystemAdministratorModule systemAdministratorModule;
    private FinanceStaffModule financeStaffModule;
    private SalesStaffModule salesStaffModule;

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
                
                String input = sc.next();
                
                try{
                    response = Integer.parseInt(input);
                } catch(NumberFormatException ex){
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }
                

                if (response == 1) {

                    try {
                        if (doLogin()) {
                            EmployeeAccessRightEnum accessRight = currentEmployeeEntity.getAccessRight();
                            if(accessRight == EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR)
                                systemAdministratorModule = new SystemAdministratorModule(employeeEntityControllerRemote, currentEmployeeEntity);
                            else if(accessRight == EmployeeAccessRightEnum.FINANCE)
                                financeStaffModule = new FinanceStaffModule(currentEmployeeEntity);
                            else if(accessRight == EmployeeAccessRightEnum.SALES)
                                salesStaffModule = new SalesStaffModule(currentEmployeeEntity);
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
                    System.out.print("Provide current password> ");
                    scanner.next();
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

}

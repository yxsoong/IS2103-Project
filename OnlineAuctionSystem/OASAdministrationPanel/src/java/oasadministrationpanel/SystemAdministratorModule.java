package oasadministrationpanel;

import entity.EmployeeEntity;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidAccessRightException;

public class SystemAdministratorModule {

    private EmployeeEntity currentEmployeeEntity;

    public void menuSystemAdministration() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getAccessRight() != EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR) {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMINISTRATOR rights to access the system administrator module.");
        }

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

    private void createNewEmployee() {

    }

    private void viewEmployeeDetails() {

    }

    private void viewAllEmployees() {

    }
}

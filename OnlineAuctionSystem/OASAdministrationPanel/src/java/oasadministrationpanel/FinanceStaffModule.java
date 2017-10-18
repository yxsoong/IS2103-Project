package oasadministrationpanel;

import entity.EmployeeEntity;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidAccessRightException;


public class FinanceStaffModule {
    private EmployeeEntity currentEmployeeEntity;

    public FinanceStaffModule() {
    }

    public FinanceStaffModule(EmployeeEntity currentEmployeeEntity) {
        this.currentEmployeeEntity = currentEmployeeEntity;
    }

    
    
    public void financeStaffMenu() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getAccessRight() != EmployeeAccessRightEnum.FINANCE) {
            throw new InvalidAccessRightException("You don't have FINANCE rights to access the system administrator module.");
        }
        
        
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
    
     private void createNewCreditPackage() {

    }

    private void viewCreditPackageDetails() {

    }

    private void viewAllCreditPackages() {

    }

}

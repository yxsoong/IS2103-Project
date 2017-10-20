package oasadministrationpanel;

import ejb.session.stateless.EmployeeEntityControllerRemote;
import entity.EmployeeEntity;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidAccessRightException;

public class SystemAdministratorModule {

    private EmployeeEntityControllerRemote employeeEntityControllerRemote;

    private EmployeeEntity currentEmployeeEntity;

    public SystemAdministratorModule() {
    }

    public SystemAdministratorModule(EmployeeEntityControllerRemote employeeEntityControllerRemote, EmployeeEntity currentEmployeeEntity) {
        this.employeeEntityControllerRemote = employeeEntityControllerRemote;
        this.currentEmployeeEntity = currentEmployeeEntity;
    }

    public void menuSystemAdministration() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getAccessRight() != EmployeeAccessRightEnum.SYSADMIN) {
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
        System.out.println("*** OAS Administration Panel :: System Administration :: Create New Employee ***\n");
        Scanner sc = new Scanner(System.in);

        String firstName, lastName, username, password;
        EmployeeAccessRightEnum accessRight;
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

        while (true) {
            System.out.print("Select Access Right (1: System Administrator, 2: Finance, 3: Sales)> ");
            Integer accessRightInt = 0;
            try {
                accessRightInt = Integer.parseInt(sc.next());
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.\n");
                continue;
            }

            if (accessRightInt >= 1 && accessRightInt <= 3) {
                accessRight = EmployeeAccessRightEnum.values()[accessRightInt - 1];
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        sc.nextLine(); //consume the enter character
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

            isUnique = !employeeEntityControllerRemote.checkUsername(username);

            count++;
        } while (lastName.isEmpty() || !isUnique);

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

        System.out.println();

        EmployeeEntity newEmployeeEntity = new EmployeeEntity(firstName, lastName, accessRight, username, password);

        newEmployeeEntity = employeeEntityControllerRemote.createEmployee(newEmployeeEntity);

        System.out.println("Employee created! Employee ID: " + newEmployeeEntity.getEmployeeID() + "\n\n");

    }

    private void viewEmployeeDetails() {
        System.out.println("*** OAS Administration Panel :: System Administration :: View Employee Details ***\n");
        Scanner sc = new Scanner(System.in);

        Long employeeId = new Long(-1);

        do {
            System.out.print("Enter Employee ID> ");
            try {
                employeeId = Long.parseLong(sc.next());
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
            }
        } while (employeeId.equals(-1));

        try {
            EmployeeEntity employeeEntity = employeeEntityControllerRemote.retrieveEmployeeById(employeeId);
            System.out.println("Employee ID\tFirst Name\tLast Name\tAccess Right\t\t\tUsername\tPassword");
            System.out.println(employeeEntity.getEmployeeID().toString()+"\t\t"+employeeEntity.getFirstName()+"\t\t"+employeeEntity.getLastName()+"\t\t"+employeeEntity.getAccessRight().toString()+" \t\t\t"+employeeEntity.getUsername()+"\t\t"+employeeEntity.getPassword());
            System.out.println("------------------------");
            System.out.println("1: Update Employee");
            System.out.println("2: Delete Employee");
            System.out.println("3: Back\n");
            System.out.print("> ");
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
                doUpdateEmployee(employeeEntity);
            } else if (response == 2) {
                doDeleteEmployee(employeeEntity);
            } else if (response == 3) {
                return;
            }

        } catch (EmployeeNotFoundException ex) {
            System.out.println(ex);
        }

    }

    private void doUpdateEmployee(EmployeeEntity employeeEntity) {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: System Administration :: Update Employee ***\n");
        System.out.print("Enter First Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            employeeEntity.setFirstName(input);
        }

        System.out.print("Enter Last Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            employeeEntity.setLastName(input);
        }

        while (true) {
            System.out.print("Select Access Right (0: No Change, 1: System Administrator, 2: Finance, 3: Sales)> ");

            Integer accessRightInt = 0;
            try {
                accessRightInt = Integer.parseInt(sc.next());
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.\n");
                continue;
            }

            if (accessRightInt >= 1 && accessRightInt <= 3) {
                employeeEntity.setAccessRight(EmployeeAccessRightEnum.values()[accessRightInt - 1]);
                break;
            } else if (accessRightInt == 0) {
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        sc.nextLine();
        System.out.print("Enter Username (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            employeeEntity.setUsername(input);
        }

        System.out.print("Enter Password (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            employeeEntity.setPassword(input);
        }

        employeeEntityControllerRemote.updateEmployee(employeeEntity);
        System.out.println("Employee updated successfully!\n");
    }

    private void doDeleteEmployee(EmployeeEntity employeeEntity) {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("*** OAS Administration Panel :: System Administration :: Delete Employee ***\n");

        System.out.printf("Confirm Delete Employee %s %s (Employee ID: %d) (Enter 'Y' to Delete)> ", employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getEmployeeID());
        input = sc.nextLine().trim();

        if (input.equals("Y")) {
            employeeEntityControllerRemote.deleteEmployee(employeeEntity.getEmployeeID());
            System.out.println("Employee deleted successfully!\n");

        } else {
            System.out.println("Employee NOT deleted!\n");
        }
    }

    private void viewAllEmployees() {
        Scanner sc = new Scanner(System.in);

        System.out.println("*** OAS Administration Panel :: System Administration :: View All Employees ***\n");

        List<EmployeeEntity> employeeEntities = employeeEntityControllerRemote.retrieveAllEmployees();
        System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Employee ID", "First Name", "Last Name", "Access Right", "Username", "Password");

        for (EmployeeEntity employeeEntity : employeeEntities) {
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", employeeEntity.getEmployeeID().toString(), employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getAccessRight().toString(), employeeEntity.getUsername(), employeeEntity.getPassword());
        }

        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }
}

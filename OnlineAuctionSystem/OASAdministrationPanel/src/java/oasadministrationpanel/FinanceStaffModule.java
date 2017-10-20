package oasadministrationpanel;

import ejb.session.stateless.CreditPackageEntityControllerRemote;
import entity.CreditPackageEntity;
import entity.EmployeeEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.CreditPackageNotFoundException;
import util.exception.InvalidAccessRightException;

public class FinanceStaffModule {

    private EmployeeEntity currentEmployeeEntity;

    private CreditPackageEntityControllerRemote creditPackageEntityControllerRemote;

    public FinanceStaffModule() {
    }

    public FinanceStaffModule(CreditPackageEntityControllerRemote creditPackageEntityControllerRemote, EmployeeEntity currentEmployeeEntity) {
        this.currentEmployeeEntity = currentEmployeeEntity;
        this.creditPackageEntityControllerRemote = creditPackageEntityControllerRemote;
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
        System.out.println("*** OAS Administration Panel :: Finance Staff :: Create New Credit Package ***\n");
        Scanner sc = new Scanner(System.in);

        String creditPackageName;
        BigDecimal price, numberOfCredits;

        CreditPackageEntity newCreditPackage;

        int count = 0;

        do {
            if (count > 0) {
                System.out.println("Credit package name cannot be empty!\n");
            }
            System.out.print("Enter credit package name> ");
            creditPackageName = sc.nextLine().trim();
            count++;
        } while (creditPackageName.isEmpty());

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Price cannot be negative or zero!\n");
            }
            System.out.print("Enter price> ");
            price = sc.nextBigDecimal();
            count++;
        } while (price.compareTo(BigDecimal.ZERO) <= 0);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Number of credits cannot be negative or zero!\n");
            }
            System.out.print("Enter number of credits> ");
            numberOfCredits = sc.nextBigDecimal();
            count++;
        } while (numberOfCredits.compareTo(BigDecimal.ZERO) <= 0);

        newCreditPackage = new CreditPackageEntity(creditPackageName, price, numberOfCredits, true);

        newCreditPackage = creditPackageEntityControllerRemote.createNewCreditPackage(newCreditPackage);

        System.out.println("Credit package created! Credit Package ID: " + newCreditPackage.getCreditPackageId() + "\n\n");
    }

    private void viewCreditPackageDetails() {
        System.out.println("*** OAS Administration Panel :: Finance Staff :: View Credit Package Details ***\n");
        Scanner sc = new Scanner(System.in);

        Long creditPackageId = new Long(-1);

        try {
            do {
                System.out.print("Enter Credit Package ID> ");
                try {
                    creditPackageId = Long.parseLong(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } while (creditPackageId.equals(-1));

            try {
                CreditPackageEntity creditPackageEntity = creditPackageEntityControllerRemote.retrieveCreditPackageById(creditPackageId);
                System.out.println("Credit Package ID\tCredit Package Name\tPrice\tNumber of Credits\tEnable");
                System.out.println("\t" + creditPackageEntity.getCreditPackageId().toString() + "\t\t\t" + creditPackageEntity.getCreditPackageName() + "\t" + creditPackageEntity.getPrice() + "\t\t" + creditPackageEntity.getNumberOfCredits() + "\t\t" + creditPackageEntity.getEnabled());
                System.out.println("------------------------");
                System.out.println("1: Update Credit Package");
                System.out.println("2: Delete Credit Package");
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
                    doUpdateCreditPackage(creditPackageEntity);
                } else if (response == 2) {
                    doDeleteCreditPackage(creditPackageEntity);
                } else if (response == 3) {
                    return;
                }

            } catch (CreditPackageNotFoundException ex) {
                System.out.println(ex);
            }
        } catch (NullPointerException ex) {
            System.out.println(ex);
        }

    }

    private void viewAllCreditPackages() {
        System.out.println("*** OAS Administration Panel :: Finance Staff :: View All Credit Packages ***\n");

        Scanner sc = new Scanner(System.in);

        try {
            List<CreditPackageEntity> creditPackageEntities = creditPackageEntityControllerRemote.retrieveAllCreditPackages();
            System.out.printf("%20s%23s%8s%20s%8s\n", "Credit Package ID", "Credit Package Name", "Price", "Number of Credits", "Enable");

            for (CreditPackageEntity creditPackageEntity : creditPackageEntities) {
                System.out.printf("%12s%25s%12s%20s%10s\n", creditPackageEntity.getCreditPackageId().toString(), creditPackageEntity.getCreditPackageName(), creditPackageEntity.getPrice().toString(), creditPackageEntity.getNumberOfCredits().toString(), creditPackageEntity.getEnabled());
            }
        } catch (NullPointerException ex) {
            System.out.println(ex);
        }

        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }

    private void doUpdateCreditPackage(CreditPackageEntity creditPackageEntity) {
        System.out.println("*** OAS Administration Panel :: Finance Staff :: Update Credit Packages ***\n");

        Scanner sc = new Scanner(System.in);
        String input;
        BigDecimal bigDecInput;

        System.out.print("Enter Credit Package Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            creditPackageEntity.setCreditPackageName(input);
        }

        System.out.print("Enter Price (Enter -1 if no change)> ");
        bigDecInput = sc.nextBigDecimal();
        if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
            creditPackageEntity.setPrice(bigDecInput);
        }

        System.out.print("Enter Number of Credits (Enter -1 if no change)> ");
        bigDecInput = sc.nextBigDecimal();
        if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
            creditPackageEntity.setNumberOfCredits(bigDecInput);
        }

        System.out.print("Enable Package (Enter Y, N, or blank if no change)> ");
        sc.nextLine(); //Consume enter character
        input = sc.nextLine();
        if (input.equals("Y")) {
            creditPackageEntity.setEnabled(true);
        } else if (input.equals("N")) {
            creditPackageEntity.setEnabled(false);
        }

        creditPackageEntityControllerRemote.updateCreditPackage(creditPackageEntity);
        System.out.println("Credit Package updated successfully!\n");
    }

    private void doDeleteCreditPackage(CreditPackageEntity creditPackageEntity) {
        System.out.println("*** OAS Administration Panel :: Finance Staff :: View All Credit Packages ***\n");

        Scanner sc = new Scanner(System.in);
        String input;

        System.out.printf("Confirm Delete Credit Package %s (Credit Package ID: %d) (Enter 'Y' to Delete)> ", creditPackageEntity.getCreditPackageName(), creditPackageEntity.getCreditPackageId());
        input = sc.nextLine().trim();

        if (input.equals("Y")) {
            creditPackageEntityControllerRemote.deleteCreditPackage(creditPackageEntity.getCreditPackageId());
            System.out.println("Credit Package deleted successfully!\n");

        } else {
            System.out.println("Credit Package NOT deleted!\n");
        }
    }
}

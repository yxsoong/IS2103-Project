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

                String input = scanner.nextLine();

                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

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

        String creditPackageName, input;
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

        while (true) {
            if (count > 0) {
                System.out.println("Price cannot be negative or zero!\n");
            }
            System.out.print("Enter price> ");
            input = sc.nextLine();
            try {
                price = new BigDecimal(input);
                if (price.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                    System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.\n");
                continue;
            }
            count++;
            if (price.compareTo(BigDecimal.ZERO) > 0) {
                break;
            }
        }

        count = 0;

        while (true) {
            if (count > 0) {
                System.out.println("Number of credits cannot be negative or zero!\n");
            }
            System.out.print("Enter number of credits> ");
            input = sc.nextLine();
            try {
                numberOfCredits = new BigDecimal(input);
                if (numberOfCredits.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                    System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.\n");
                numberOfCredits = BigDecimal.ZERO;
                continue;
            }
            count++;
            if (numberOfCredits.compareTo(BigDecimal.ZERO) > 0) {
                break;
            }
        }

        newCreditPackage = new CreditPackageEntity(creditPackageName, price, numberOfCredits, true);

        newCreditPackage.setEmployeeEntity(currentEmployeeEntity);

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
                System.out.printf("%20s%20s%15s%20s%15s\n", "Credit Package ID", "Credit Package Name", "Price", "Number of Credits", "Enable");
                System.out.printf("%20s%20s%15.4f%20.4f%15s\n", creditPackageEntity.getCreditPackageId().toString(), creditPackageEntity.getCreditPackageName(), creditPackageEntity.getPrice(), creditPackageEntity.getNumberOfCredits(), creditPackageEntity.getEnabled());
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
            System.out.printf("%20s%23s%15s%20s%8s\n", "Credit Package ID", "Credit Package Name", "Price", "Number of Credits", "Enable");

            for (CreditPackageEntity creditPackageEntity : creditPackageEntities) {
                System.out.printf("%20s%23s%15.2f%20.4f%8s\n", creditPackageEntity.getCreditPackageId().toString(), creditPackageEntity.getCreditPackageName(), creditPackageEntity.getPrice(), creditPackageEntity.getNumberOfCredits(), creditPackageEntity.getEnabled());
            }
        } catch (CreditPackageNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.print("Press enter to continue...> ");
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

        while (true) {
            System.out.print("Enter Price (Enter -1 if no change)> ");
            input = sc.nextLine();
            try {
                bigDecInput = new BigDecimal(input);
                if (bigDecInput.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                    System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please input numeric values!\n");
                continue;
            }
            if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
                creditPackageEntity.setPrice(bigDecInput);
            }
            break;
        }

        while (true) {
            System.out.print("Enter Number of Credits (Enter -1 if no change)> ");
            input = sc.nextLine();
            try {
                bigDecInput = new BigDecimal(input);
                if (bigDecInput.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                    System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please input numeric values!\n");
                continue;
            }
            if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
                creditPackageEntity.setNumberOfCredits(bigDecInput);
            }
            break;
        }

        while (true) {
            System.out.print("Enable Package (Enter Y, N, or blank if no change)> ");
            input = sc.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                creditPackageEntity.setEnabled(true);
                break;
            } else if (input.equalsIgnoreCase("N")) {
                creditPackageEntity.setEnabled(false);
                break;
            } else if (input.isEmpty()) {
                break;
            } else {
                continue;
            }
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

        if (input.equalsIgnoreCase("Y")) {
            creditPackageEntityControllerRemote.deleteCreditPackage(creditPackageEntity.getCreditPackageId());
            System.out.println("Credit Package deleted successfully!\n");

        } else {
            System.out.println("Credit Package NOT deleted!\n");
        }
    }
}

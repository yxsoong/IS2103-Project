package oasadministrationpanel;

import entity.EmployeeEntity;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidAccessRightException;

public class SalesStaffModule {

    private EmployeeEntity currentEmployeeEntity;

    public SalesStaffModule() {
    }

    public SalesStaffModule(EmployeeEntity currentEmployeeEntity) {
        this.currentEmployeeEntity = currentEmployeeEntity;
    }
    
    

    public void salesStaffMenu() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getAccessRight() != EmployeeAccessRightEnum.SALES) {
            throw new InvalidAccessRightException("You don't have SALES rights to access the system administrator module.");
        }

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

    private void createAuctionListing() {
        
    }

    private void viewAuctionListingDetails() {

    }

    private void viewAllAuctionListings() {

    }

    private void viewListingsBelowReservePrice() {

    }
}

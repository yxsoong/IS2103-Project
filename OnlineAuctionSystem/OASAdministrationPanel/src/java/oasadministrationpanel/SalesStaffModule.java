package oasadministrationpanel;

import ejb.session.stateless.AuctionListingEntityControllerRemote;
import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.EmployeeEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidAccessRightException;

public class SalesStaffModule {

    private AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;
    private EmployeeEntity currentEmployeeEntity;

    public SalesStaffModule() {
    }

    public SalesStaffModule(AuctionListingEntityControllerRemote auctionListingEntityControllerRemote, EmployeeEntity currentEmployeeEntity) {
        this.auctionListingEntityControllerRemote = auctionListingEntityControllerRemote;
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
        System.out.println("*** OAS Administration Panel :: Sales Staff :: Create New Auction Listing ***\n");
        Scanner sc = new Scanner(System.in);

        String itemName, sDateTime, eDateTime, result;
        BigDecimal startingBidAmount, reservePrice;
        Date startDateTime, endDateTime;
        Boolean open;
        AddressEntity deliveryAddress = null;

        AuctionListingEntity newAuctionListingEntity;

        int count = 0;

        do {
            if (count > 0) {
                System.out.println("Auction item name cannot be empty!\n");
            }
            System.out.print("Enter auction item name name> ");
            itemName = sc.nextLine().trim();
            count++;
        } while (itemName.isEmpty());

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Starting bid amount cannot be negative or zero!\n");
            }
            System.out.print("Enter starting bid amount> ");
            startingBidAmount = sc.nextBigDecimal();
            count++;
        } while (startingBidAmount.compareTo(BigDecimal.ZERO) <= 0);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Start date time cannot be empty!\n");
            }

            System.out.print("Enter starting date and time (yyyymmddhhmm)> ");
            sc.nextLine();      //吃 the enter character
            sDateTime = sc.nextLine().trim();
            count++;
        } while (sDateTime.isEmpty());
        int year = Integer.parseInt(sDateTime.substring(0, 4).trim());
        int month = Integer.parseInt(sDateTime.substring(4, 6).trim());
        int day = Integer.parseInt(sDateTime.substring(6, 8).trim());
        int hour = Integer.parseInt(sDateTime.substring(8, 10).trim());
        int min = Integer.parseInt(sDateTime.substring(10, 12).trim());
        startDateTime = new Date(year, month, day, hour, min);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("End date time must be later than start date time and it cannot be empty!\n");
            }

            System.out.print("Enter end date and time (yyyymmddhhmm)> ");
            eDateTime = sc.nextLine().trim();
            count++;
        } while (eDateTime.isEmpty() || Long.parseLong(eDateTime) < Long.parseLong(sDateTime));
        year = Integer.parseInt(eDateTime.substring(0, 4).trim());
        month = Integer.parseInt(eDateTime.substring(4, 6).trim());
        day = Integer.parseInt(eDateTime.substring(6, 8).trim());
        hour = Integer.parseInt(eDateTime.substring(8, 10).trim());
        min = Integer.parseInt(eDateTime.substring(10, 12).trim());
        endDateTime = new Date(year, month, day, hour, min);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Reserve price cannot be negative!\n");
            }
            System.out.print("Enter reserve price (0 for no reserve price)> ");
            reservePrice = sc.nextBigDecimal();
            count++;
        } while (reservePrice.compareTo(BigDecimal.ZERO) < 0);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Set a status for listing!\n");
            }
            System.out.print("Open listing(Enter Y or N)> ");
            sc.nextLine(); //consume the next enter character
            result = sc.nextLine().trim();
            if (result.equals("Y")) {
                open = true;
            } else if (result.equals("N")) {
                open = false;
            } else {
                open = null;
            }
            count++;
        } while (result.isEmpty());

        newAuctionListingEntity = new AuctionListingEntity(itemName, startingBidAmount, startDateTime, endDateTime, reservePrice, open, true, deliveryAddress);

        newAuctionListingEntity = auctionListingEntityControllerRemote.createAuctionListing(newAuctionListingEntity);

        System.out.println("Auction Listing created! Auction Listing ID: " + newAuctionListingEntity.getAuctionListingId() + "\n\n");

    }

    private void viewAuctionListingDetails() {

    }

    private void viewAllAuctionListings() {

    }

    private void viewListingsBelowReservePrice() {

    }
}

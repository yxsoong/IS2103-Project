package oasadministrationpanel;

import ejb.session.stateless.AuctionListingEntityControllerRemote;
import ejb.session.stateless.CustomerEntityControllerRemote;
import ejb.session.stateless.TimerSessionBeanRemote;
import entity.AddressEntity;
import entity.AuctionListingEntity;
import entity.EmployeeEntity;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.AuctionListingNotFoundException;
import util.exception.InvalidAccessRightException;

public class SalesStaffModule {

    private AuctionListingEntityControllerRemote auctionListingEntityControllerRemote;
    private CustomerEntityControllerRemote customerEntityControllerRemote;
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
        BigDecimal startingBidAmount, reservePrice, currentBidAmount = null;
        Calendar startDateTime = Calendar.getInstance();
        Calendar endDateTime = Calendar.getInstance();
        Boolean open = false, enabled = true, manualAssignment = false;
        int year, month, day, hour, min;

        AuctionListingEntity newAuctionListingEntity;

        Calendar now = Calendar.getInstance();

        int count = 0;

        do {
            if (count > 0) {
                System.out.println("Auction item name cannot be empty!\n");
            }
            System.out.print("Enter auction item name> ");
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
                System.out.println("Start date time cannot be before current date time!\n");
            }

            System.out.print("Enter starting date and time (yyyymmddhhmm)> ");
            if (count == 0) {
                sc.nextLine();      //consume the enter character
            }
            sDateTime = sc.nextLine().trim();
            count++;
            if (sDateTime.isEmpty()) {
                System.out.println("Start date time cannot be empty!\n");
                count = -1;
                continue;
            }

            year = Integer.parseInt(sDateTime.substring(0, 4).trim());
            month = Integer.parseInt(sDateTime.substring(4, 6).trim());
            day = Integer.parseInt(sDateTime.substring(6, 8).trim());
            hour = Integer.parseInt(sDateTime.substring(8, 10).trim());
            min = Integer.parseInt(sDateTime.substring(10).trim());
            startDateTime.clear();
            startDateTime.set(year, month - 1, day, hour, min);
        } while (now.compareTo(startDateTime) > 0);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("End date time must be later than start date time!\n");
            }

            System.out.print("Enter end date and time (yyyymmddhhmm)> ");
            eDateTime = sc.nextLine().trim();

            count++;
            if (eDateTime.isEmpty()) {
                System.out.println("End date time cannot be empty!");
                count = 0;
                continue;
            }

            year = Integer.parseInt(eDateTime.substring(0, 4).trim());
            month = Integer.parseInt(eDateTime.substring(4, 6).trim());
            day = Integer.parseInt(eDateTime.substring(6, 8).trim());
            hour = Integer.parseInt(eDateTime.substring(8, 10).trim());
            min = Integer.parseInt(eDateTime.substring(10, 12).trim());
            endDateTime.clear();
            endDateTime.set(year, month - 1, day, hour, min);

        } while (now.compareTo(endDateTime) > 0 || Long.parseLong(eDateTime) < Long.parseLong(sDateTime));

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

        if (now.compareTo(startDateTime) >= 0 && now.compareTo(endDateTime) < 0) {
            open = true;
        } else {
            open = false;
        }

        newAuctionListingEntity = new AuctionListingEntity(itemName, startingBidAmount, currentBidAmount, startDateTime, endDateTime, reservePrice, open, enabled, manualAssignment);

        newAuctionListingEntity.setEmployeeEntity(currentEmployeeEntity);

        newAuctionListingEntity = auctionListingEntityControllerRemote.createAuctionListing(newAuctionListingEntity);

        System.out.println("Auction Listing created! Auction Listing ID: " + newAuctionListingEntity.getAuctionListingId() + "\n\n");

    }

    private void viewAuctionListingDetails() {
        System.out.println("*** OAS Administration Panel :: Sales Staff :: View Auction Listing Details ***\n");
        Scanner sc = new Scanner(System.in);

        Long auctionListingId = new Long(-1);

        try {
            do {
                System.out.print("Enter Auction Listing ID> ");
                try {
                    auctionListingId = Long.parseLong(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } while (auctionListingId.equals(-1));

            try {
                AuctionListingEntity auctionListingEntity = auctionListingEntityControllerRemote.retrieveAuctionListingById(auctionListingId);
                //System.out.println("Auction Listing ID\tItem Name\tStarting Bid Amount\tStart Date Time\tEnd Date Time\tReserve Price\tOpen\tEnabled\tDelivery Address");
                System.out.printf("%20s%20s%14s%26s%26s%16s%14s%8s%20s\n", "Auction Listing ID", "Item Name", "Starting Bid", "Start Date", "End Date", "Reserve Price", "Open Listing", "Enable", "Delivery Address");

                String startDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(auctionListingEntity.getStartDateTime().getTime());
                String endDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(auctionListingEntity.getEndDateTime().getTime());

                String deliveryAddress;
                if (auctionListingEntity.getDeliveryAddress() == null) {
                    deliveryAddress = "nil";
                } else {
                    deliveryAddress = auctionListingEntity.getDeliveryAddress().getAddressID().toString();
                }

                System.out.printf("%20s%20s%14s%26s%26s%12.4f%14s%10s%20s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(),
                        auctionListingEntity.getStartingBidAmount(), startDate, endDate, auctionListingEntity.getReservePrice(),
                        auctionListingEntity.getOpenListing(), auctionListingEntity.getEnabled(), deliveryAddress);

                //System.out.println("\t" + auctionListingEntity.getAuctionListingId() + "\t\t" + auctionListingEntity.getItemName() + "\t\t" + auctionListingEntity.getStartingBidAmount() + "\t\t" + auctionListingEntity.getStartDateTime().toString() + "\t\t" + auctionListingEntity.getEndDateTime().toString() + "\t\t" + auctionListingEntity.getReservePrice() + "\t\t" + auctionListingEntity.getOpenListing() + "\t\t" + auctionListingEntity.getDeliveryAddress());
                System.out.println("------------------------");
                System.out.println("1: Update Auction Listing");
                System.out.println("2: Delete Auction Listing");
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
                    doUpdateAuctionListing(auctionListingEntity);
                } else if (response == 2) {
                    doDeleteAuctionListing(auctionListingEntity);
                } else if (response == 3) {
                    return;
                }

            } catch (AuctionListingNotFoundException ex) {
                System.out.println(ex);
            }
        } catch (NullPointerException ex) {
            System.out.println(ex);
        }

    }

    private void viewAllAuctionListings() {
        Scanner sc = new Scanner(System.in);

        try {
            List<AuctionListingEntity> auctionListingEntities = auctionListingEntityControllerRemote.retrieveAllAuctionListings();

            System.out.printf("%20s%20s%14s%26s%26s%16s%14s%8s%20s\n", "Auction Listing ID", "Item Name", "Starting Bid", "Start Date", "End Date", "Reserve Price", "Open Listing", "Enable", "Delivery Address");
            for (AuctionListingEntity auctionListingEntity : auctionListingEntities) {
                String startDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(auctionListingEntity.getStartDateTime().getTime());
                String endDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(auctionListingEntity.getEndDateTime().getTime());

                String deliveryAddress;

                if (auctionListingEntity.getDeliveryAddress() == null) {
                    deliveryAddress = "nil";
                } else {
                    deliveryAddress = auctionListingEntity.getDeliveryAddress().getAddressID().toString();
                }

                System.out.printf("%20s%20s%14s%26s%26s%12.4f%14s%10s%20s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(),
                        auctionListingEntity.getStartingBidAmount(), startDate, endDate, auctionListingEntity.getReservePrice(),
                        auctionListingEntity.getOpenListing(), auctionListingEntity.getEnabled(), deliveryAddress);
            }

            System.out.print("Press enter to continue...> ");
            sc.nextLine();
        } catch (AuctionListingNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void viewListingsBelowReservePrice() {
        Scanner sc = new Scanner(System.in);

        try {
            List<AuctionListingEntity> listingsBelowReserve = auctionListingEntityControllerRemote.retrieveAllAuctionListingsBelowReservePrice();
            System.out.printf("%20s%20s%14s%26s%26s%16s%14s%8s\n", "Auction Listing ID", "Item Name", "Highest Bid", "Start Date", "End Date", "Reserve Price", "Open Listing", "Enable");
            for (AuctionListingEntity auctionListingEntity : listingsBelowReserve) {
                 String startDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(auctionListingEntity.getStartDateTime().getTime());
                String endDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(auctionListingEntity.getEndDateTime().getTime());

                System.out.printf("%20s%20s%14s%26s%26s%16s%14s%8s\n", auctionListingEntity.getAuctionListingId(), auctionListingEntity.getItemName(), auctionListingEntity.getCurrentBidAmount(), startDate, endDate, auctionListingEntity.getReservePrice(), auctionListingEntity.getOpenListing(), auctionListingEntity.getEnabled());
            }
        } catch (AuctionListingNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void doUpdateAuctionListing(AuctionListingEntity auctionListingEntity) {
        System.out.println("*** OAS Administration Panel :: Sales Staff :: Update Auction Listing ***\n");

        Scanner sc = new Scanner(System.in);
        String input, dateTime, type;
        BigDecimal bigDecInput;
        int year, month, day, hour, min;
        int count = 0;
        Calendar startDateTime = Calendar.getInstance();
        Calendar endDateTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        System.out.print("Enter Auction Listing Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            auctionListingEntity.setItemName(input);
        }

        System.out.print("Enter Starting Bid Amount (-1 if no change)> ");
        bigDecInput = sc.nextBigDecimal();
        if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
            auctionListingEntity.setStartingBidAmount(bigDecInput);
        }
        sc.nextLine(); //consume enter character

        count = 0;
        do {
            if (count > 0) {
                System.out.println("Start date time cannot be before current date time!\n");
            } else if (count == -1) {
                System.out.println("Start date time has 12 characters!\n");
                count = 0;
            }

            System.out.print("Enter Start Date Time (format to yyyymmddhhmm; blank if no change)> ");
            dateTime = sc.nextLine().trim();
            if (dateTime.isEmpty()) {
                break;
            }
            if (dateTime.length() == 12) {
                count++;

                year = Integer.parseInt(dateTime.substring(0, 4).trim());
                month = Integer.parseInt(dateTime.substring(4, 6).trim());
                day = Integer.parseInt(dateTime.substring(6, 8).trim());
                hour = Integer.parseInt(dateTime.substring(8, 10).trim());
                min = Integer.parseInt(dateTime.substring(10, 12).trim());
                startDateTime.clear();
                startDateTime.set(year, month - 1, day, hour, min);
            } else {
                count = -1;
            }
        } while (now.compareTo(startDateTime) > 0 || count < 0);
        if (!dateTime.isEmpty()) {
            auctionListingEntity.setStartDateTime(startDateTime);
        }

        count = 0;
        do {
            if (count > 0) {
                System.out.println("End date time must be later than start date time!\n");
            } else if (count == -1) {
                System.out.println("End date time has 12 characters!\n");
                count = 0;
            }

            System.out.print("Enter End Date Time (format to yyyymmddhhmm; blank if no change)> ");
            dateTime = sc.nextLine().trim();
            if (dateTime.isEmpty()) {
                break;
            }
            if (dateTime.length() == 12) {
                count++;
                year = Integer.parseInt(dateTime.substring(0, 4).trim());
                month = Integer.parseInt(dateTime.substring(4, 6).trim());
                day = Integer.parseInt(dateTime.substring(6, 8).trim());
                hour = Integer.parseInt(dateTime.substring(8, 10).trim());
                min = Integer.parseInt(dateTime.substring(10, 12).trim());
                endDateTime.clear();
                endDateTime.set(year, month - 1, day, hour, min);
            } else {
                count = -1;
            }
        } while (endDateTime.before(startDateTime) || count == -1);
        if (!dateTime.isEmpty()) {
            auctionListingEntity.setEndDateTime(endDateTime);
        }

        System.out.print("Enter Reserve Price (-1 if no change)> ");
        bigDecInput = sc.nextBigDecimal();
        if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
            auctionListingEntity.setReservePrice(bigDecInput);
        }
        sc.nextLine(); //consume enter character

        System.out.print("Enable Listing? (Enter Y, N, or blank if no change)> ");
        input = sc.nextLine();
        if (input.equals("Y")) {
            auctionListingEntity.setEnabled(Boolean.TRUE);
        } else if (input.equals("N")) {
            auctionListingEntity.setEnabled(Boolean.FALSE);
        }

        auctionListingEntityControllerRemote.updateAuctionListing(auctionListingEntity);
        System.out.println("Auction listing updated successfully!\n");
    }

    private void doDeleteAuctionListing(AuctionListingEntity auctionListingEntity) {
        System.out.println("*** OAS Administration Panel :: Sales Staff :: Delete Auction Listing ***\n");
        auctionListingEntityControllerRemote.deleteAuctionListing(auctionListingEntity.getAuctionListingId());

        System.out.println("Auction Listing disabled/deleted successfully.");
    }
}

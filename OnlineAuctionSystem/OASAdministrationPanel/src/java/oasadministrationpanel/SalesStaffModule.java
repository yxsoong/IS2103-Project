package oasadministrationpanel;

import ejb.session.stateless.AuctionListingEntityControllerRemote;
import ejb.session.stateless.CustomerEntityControllerRemote;
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

        Scanner sc = new Scanner(System.in);
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

                String input = sc.nextLine().trim();

                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

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

        String itemName, sDateTime, eDateTime;
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

        while (true) {
            if (count > 0) {
                System.out.println("Starting bid amount cannot be negative or zero!\n");
            }
            System.out.print("Enter starting bid amount> ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Starting bid cannot be empty!");
                continue;
            } else {
                try {
                    startingBidAmount = new BigDecimal(input);
                    if (startingBidAmount.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                        System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
                        continue;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }
            }
            count++;
            if (startingBidAmount.compareTo(BigDecimal.ZERO) > 0) {
                break;
            }
        }

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Start date time cannot be before current date time!\n");
            }

            System.out.print("Enter starting date and time (yyyymmddhhmm)> ");
            sDateTime = sc.nextLine().trim();

            if (sDateTime.isEmpty() || sDateTime.length() != 12) {
                System.out.println("Start date time cannot be empty and should be length 12!");
                count = -1;
                //dummy value to loop
                endDateTime.set(1990, 0, 1, 0, 0);
                continue;
            }

            try {
                year = Integer.parseInt(sDateTime.substring(0, 4).trim());
                month = Integer.parseInt(sDateTime.substring(4, 6).trim());
                day = Integer.parseInt(sDateTime.substring(6, 8).trim());
                hour = Integer.parseInt(sDateTime.substring(8, 10).trim());
                min = Integer.parseInt(sDateTime.substring(10).trim());
                startDateTime.clear();
                startDateTime.set(year, month - 1, day, hour, min);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
                //dummy value to loop
                startDateTime.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            count++;
        } while (now.compareTo(startDateTime) > 0);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("End date time must be later than start date time!\n");
            }

            System.out.print("Enter end date and time (yyyymmddhhmm)> ");
            eDateTime = sc.nextLine().trim();

            if (eDateTime.isEmpty() || eDateTime.length() < 12) {
                System.out.println("End date time cannot be empty and should be length 12!");
                //dummy value to loop
                endDateTime.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }

            try {
                year = Integer.parseInt(eDateTime.substring(0, 4).trim());
                month = Integer.parseInt(eDateTime.substring(4, 6).trim());
                day = Integer.parseInt(eDateTime.substring(6, 8).trim());
                hour = Integer.parseInt(eDateTime.substring(8, 10).trim());
                min = Integer.parseInt(eDateTime.substring(10, 12).trim());
                endDateTime.clear();
                endDateTime.set(year, month - 1, day, hour, min);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
                //dummy value to loop
                endDateTime.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            count++;
        } while (now.compareTo(endDateTime) > 0 || Long.parseLong(eDateTime) < Long.parseLong(sDateTime));

        count = 0;

        while (true) {
            if (count > 0) {
                System.out.println("Reserve price cannot be negative!\n");
            }
            count++;
            System.out.print("Enter reserve price (0 for no reserve price)> ");
            String input = sc.nextLine().trim();
            try {
                reservePrice = new BigDecimal(input);
                if (reservePrice.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                    System.out.println("Amount is too large. Max Digits: 14 + 4 decimal places.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Enter numeric values.");
                continue;
            }
            if (reservePrice.compareTo(BigDecimal.ZERO) >= 0) {
                break;
            }
        }

        count = 0;

        if (now.compareTo(startDateTime) >= 0 && now.compareTo(endDateTime) < 0) {
            open = true;
        } else {
            open = false;
        }

        newAuctionListingEntity = new AuctionListingEntity(itemName, startingBidAmount, currentBidAmount, startDateTime, endDateTime, reservePrice, open, enabled, manualAssignment);

        newAuctionListingEntity = auctionListingEntityControllerRemote.createAuctionListing(newAuctionListingEntity, currentEmployeeEntity.getEmployeeID());

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

        System.out.println("Press enter to continue...");
        sc.nextLine();
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
            if (listingsBelowReserve.isEmpty()) {
                System.out.print("Press Enter to continue...");
                sc.nextLine();
                return;
            }
            System.out.println("------------------------");
            System.out.println("1: Manual Assignment of Winning Bid");
            System.out.println("2: Back\n");
            System.out.print("> ");
            Integer response = 0;

            while (true) {
                try {
                    response = Integer.parseInt(sc.next());
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.\n");
                    continue;
                }

                if (response >= 1 && response <= 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 1) {
                Long listingId;
                while (true) {
                    System.out.print("Enter Auction Listing Id> ");
                    listingId = sc.nextLong();
                    AuctionListingEntity selectedListing = auctionListingEntityControllerRemote.retrieveAuctionListingById(listingId);

                    if (!listingsBelowReserve.contains(selectedListing)) {
                        System.out.println("Invalid Auction Listing Id");
                        continue;
                    } else {
                        manualAssignmentWinningBid(selectedListing);
                        break;
                    }
                }
            } else if (response == 2) {
                return;
            }

        } catch (AuctionListingNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void manualAssignmentWinningBid(AuctionListingEntity auctionListingEntity) {
        System.out.println("*** OAS Administration Panel :: Sales Staff :: Manual Assignment of Winning Bid for auction listing " + auctionListingEntity.getAuctionListingId() + "***\n");
        System.out.println("1: Mark Highest Bid as Winning Bid");
        System.out.println("2: Mark Listing with No Winning Bid");
        System.out.println("3: Back\n");
        Scanner sc = new Scanner(System.in);

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
            auctionListingEntityControllerRemote.setWinningBidEntity(auctionListingEntity.getAuctionListingId());
            customerEntityControllerRemote.deductCreditBalance(auctionListingEntity.getWinningBidEntity().getCustomerEntity().getCustomerId(), auctionListingEntity.getCurrentBidAmount());
        } else if (response == 2) {
            auctionListingEntityControllerRemote.noWinningBidEntity(auctionListingEntity.getAuctionListingId());
            customerEntityControllerRemote.refundCredits(auctionListingEntity.getWinningBidEntity().getCustomerEntity().getCustomerId(), auctionListingEntity.getCurrentBidAmount());
            System.out.println("No winning bid for auction listing " + auctionListingEntity.getAuctionListingId());
            System.out.println(auctionListingEntity.getManualAssignment());
        } else if (response == 3) {
            return;
        }

        System.out.println("Press enter to continue...");
        sc.nextLine();
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

        while (true) {
            System.out.print("Enter Starting Bid Amount (-1 if no change)> ");
            input = sc.nextLine().trim();
            try {
                bigDecInput = new BigDecimal(input);
                if (bigDecInput.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                    System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.\n");
                continue;
            }
            if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
                auctionListingEntity.setStartingBidAmount(bigDecInput);
            }
            break;
        }

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
                try {
                    year = Integer.parseInt(dateTime.substring(0, 4).trim());
                    month = Integer.parseInt(dateTime.substring(4, 6).trim());
                    day = Integer.parseInt(dateTime.substring(6, 8).trim());
                    hour = Integer.parseInt(dateTime.substring(8, 10).trim());
                    min = Integer.parseInt(dateTime.substring(10, 12).trim());
                    startDateTime.clear();
                    startDateTime.set(year, month - 1, day, hour, min);
                } catch (NumberFormatException ex) {
                    System.out.println("Enter numeric values!\n");
                    count = 0;
                    //Dummy
                    startDateTime.set(1990, 0, 1, 0, 0);
                    continue;
                }
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
                try {
                    year = Integer.parseInt(dateTime.substring(0, 4).trim());
                    month = Integer.parseInt(dateTime.substring(4, 6).trim());
                    day = Integer.parseInt(dateTime.substring(6, 8).trim());
                    hour = Integer.parseInt(dateTime.substring(8, 10).trim());
                    min = Integer.parseInt(dateTime.substring(10, 12).trim());
                    endDateTime.clear();
                    endDateTime.set(year, month - 1, day, hour, min);
                } catch (NumberFormatException ex) {
                    System.out.println("Enter numeric values!\n");
                    count = 0;
                    //Dummy
                    endDateTime.set(1990, 0, 1, 0, 0);
                    continue;
                }
            } else {
                count = -1;
            }
        } while (endDateTime.before(startDateTime) || count <= 0);
        if (!dateTime.isEmpty()) {
            auctionListingEntity.setEndDateTime(endDateTime);
        }

        while (true) {
            System.out.print("Enter Reserve Price (-1 if no change)> ");
            input = sc.nextLine();
            try {
                bigDecInput = new BigDecimal(input);
                if (bigDecInput.compareTo(MainApp.MAX_BIG_DECIMAL) >= 0) {
                    System.out.println("Amount is too large. Max digits: 14 + 4 decimal places.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values!\n");
                continue;
            }
            if (bigDecInput.compareTo(BigDecimal.ZERO) > -1) {
                auctionListingEntity.setReservePrice(bigDecInput);
            }
            break;
        }

        while (true) {
            System.out.print("Enable Listing? (Enter Y, N, or blank if no change)> ");
            input = sc.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                auctionListingEntity.setEnabled(Boolean.TRUE);
                break;
            } else if (input.equalsIgnoreCase("N")) {
                auctionListingEntity.setEnabled(Boolean.FALSE);
                break;
            } else if (input.isEmpty()) {
                break;
            } else {
                continue;
            }
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

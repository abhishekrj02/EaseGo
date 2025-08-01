package easego;

import easego.entities.Train;
import easego.entities.User;
import easego.services.UserBookingService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);

        UserBookingService bookingService;
        try {
            bookingService = new UserBookingService();
        } catch (IOException e) {
            System.err.println("Failed to load data: " + e.getMessage());
            return;
        }

        Train selectedTrain = null;
        String selectedSource = null, selectedDest = null, travelDate = null;

        while (true) {
            System.out.println("\nChoose option:");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit");

            int option = scanner.nextInt();
            scanner.nextLine();  // consume newline

            try {
                switch (option) {
                    case 1 -> {
                        System.out.print("Enter username: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String pwd = scanner.nextLine();
                        User u = new User(name, pwd, null, null, null);
                        bookingService.signUp(u);
                        System.out.println("Signup successful. You may login now.");
                    }
                    case 2 -> {
                        System.out.print("Enter username: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String pwd = scanner.nextLine();
                        if (bookingService.loginUser(name, pwd)) {
                            System.out.println("Login successful.");
                        } else {
                            System.out.println("Login failed. Invalid credentials.");
                        }
                    }
                    case 3 -> bookingService.fetchBookings();
                    case 4 -> {
                        System.out.print("Source station: ");
                        selectedSource = scanner.nextLine().toLowerCase();
                        System.out.print("Destination station: ");
                        selectedDest = scanner.nextLine().toLowerCase();
                        System.out.print("Travel date (YYYY-MM-DD): ");
                        travelDate = scanner.nextLine();

                        List<Train> trains = bookingService.getTrains(selectedSource, selectedDest);
                        if (trains.isEmpty()) {
                            System.out.println("No trains found.");
                            break;
                        }
                        for (int i = 0; i < trains.size(); i++) {
                            Train t = trains.get(i);
                            System.out.printf("%d) %s – stations: %s\n", i + 1,
                                    t.getTrainInfo(), t.getStations());
                            System.out.println("   Times: " + t.getStationTimes());
                        }
                        System.out.print("Select train number: ");
                        int idx = scanner.nextInt();
                        scanner.nextLine();
                        selectedTrain = trains.get(idx - 1);
                        System.out.println("Selected: " + selectedTrain.getTrainInfo());
                    }
                    case 5 -> {
                        if (selectedTrain == null) {
                            System.out.println("No train selected. Search first.");
                            break;
                        }
                        List<List<Integer>> seats = bookingService.fetchSeats(selectedTrain);
                        System.out.println("Seat map (0=free,1=booked):");
                        for (List<Integer> row : seats) {
                            row.forEach(val -> System.out.print(val + " "));
                            System.out.println();
                        }
                        System.out.print("Enter row index: ");
                        int r = scanner.nextInt();
                        System.out.print("Enter column index: ");
                        int c = scanner.nextInt();
                        scanner.nextLine();

                        boolean ok = bookingService.bookTrainSeat(
                                selectedTrain, selectedSource, selectedDest, travelDate, r, c);
                        System.out.println(ok ? "Booked! Enjoy your journey." : "Seat cannot be booked.");
                    }
                    case 6 -> {
                        System.out.print("Enter ticket ID to cancel: ");
                        String tid = scanner.nextLine();
                        boolean ok = bookingService.cancelBooking(tid);
                        System.out.println(ok ? "Cancelled." : "Ticket not found or not logged in.");
                    }
                    case 7 -> {
                        System.out.println("Exiting. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HotelReservationSystem {
    private JFrame frame;
    private List<HotelRoom> hotelRooms;

    public HotelReservationSystem() {
        frame = new JFrame("Hotel Reservation System");
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton searchRoomsButton = new JButton("Search Rooms");
        JButton makeReservationButton = new JButton("Make Reservation");

        searchRoomsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRooms();
            }
        });

        makeReservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeReservation();
            }
        });

        frame.add(searchRoomsButton);
        frame.add(makeReservationButton);
        frame.setVisible(true);

        // Initialize hotel rooms
        hotelRooms = new ArrayList<>();
        hotelRooms.add(new HotelRoom("Standard Room", 100, true));
        hotelRooms.add(new HotelRoom("Deluxe Room", 150, true));
        hotelRooms.add(new HotelRoom("Suite", 200, false));
    }

    private void searchRooms() {
        String searchTerm = JOptionPane.showInputDialog(frame, "Enter your room preferences:");
        List<HotelRoom> searchResults = new ArrayList<>();
        for (HotelRoom room : hotelRooms) {
            if (room.getRoomType().toLowerCase().contains(searchTerm.toLowerCase())) {
                searchResults.add(room);
            }
        }

        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No rooms found matching the search term.");
        } else {
            StringBuilder searchResultText = new StringBuilder("Search results:\n");
            for (HotelRoom room : searchResults) {
                searchResultText.append(room.getRoomDetails()).append("\n");
            }
            JOptionPane.showMessageDialog(frame, searchResultText.toString());
        }
    }

    private void makeReservation() {
        int selectedRoomIndex = JOptionPane.showOptionDialog(frame, "Select a room:", "Make Reservation",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                hotelRooms.toArray(), hotelRooms.get(0));

        if (selectedRoomIndex == -1) {
            return; // User canceled the reservation
        }

        HotelRoom selectedRoom = hotelRooms.get(selectedRoomIndex);

        if (!selectedRoom.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Room is not available. Please choose another room.");
            return;
        }

        String checkInDateStr = JOptionPane.showInputDialog(frame, "Enter the check-in date (YYYY-MM-DD):");
        LocalDate checkInDate = parseDate(checkInDateStr);
        if (checkInDate == null) {
            JOptionPane.showMessageDialog(frame, "Invalid check-in date. Please enter a valid date in the format YYYY-MM-DD.");
            return;
        }

        String checkOutDateStr = JOptionPane.showInputDialog(frame, "Enter the check-out date (YYYY-MM-DD):");
        LocalDate checkOutDate = parseDate(checkOutDateStr);
        if (checkOutDate == null) {
            JOptionPane.showMessageDialog(frame, "Invalid check-out date. Please enter a valid date in the format YYYY-MM-DD.");
            return;
        }

        if (checkOutDate.isBefore(checkInDate)) {
            JOptionPane.showMessageDialog(frame, "Invalid date range. Check-out date must be after the check-in date.");
            return;
        }

        String guestName = JOptionPane.showInputDialog(frame, "Enter the guest name:");
        if (guestName == null || guestName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid guest name. Please enter a valid name.");
            return;
        }

        int numNights = (int) checkInDate.until(checkOutDate).getDays();
        int totalCost = numNights * selectedRoom.getRate();
        String formattedCheckInDate = formatDate(checkInDate);
        String formattedCheckOutDate = formatDate(checkOutDate);

        JOptionPane.showMessageDialog(frame, "Reservation Details:\n" +
                "Room: " + selectedRoom.getRoomDetails() + "\n" +
                "Check-In: " + formattedCheckInDate + "\n" +
                "Check-Out: " + formattedCheckOutDate + "\n" +
                "Guest Name: " + guestName + "\n" +
                "Total Cost: $" + totalCost);

        selectedRoom.setAvailable(false);
        writeToFile("Reservation Details:\n" +
                "Room: " + selectedRoom.getRoomDetails() + "\n" +
                "Check-In: " + formattedCheckInDate + "\n" +
                "Check-Out: " + formattedCheckOutDate + "\n" +
                "Guest Name: " + guestName + "\n" +
                "Total Cost: $" + totalCost);
    }

    private void writeToFile(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reservation.txt", true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return date.format(formatter);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HotelReservationSystem();
            }
        });
    }
}

class HotelRoom {
    private String roomType;
    private int rate;
    private boolean available;

    public HotelRoom(String roomType, int rate, boolean available) {
        this.roomType = roomType;
        this.rate = rate;
        this.available = available;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getRate() {
        return rate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getRoomDetails() {
        return roomType + " ($" + rate + "/night)";
    }

    @Override
    public String toString() {
        return roomType;
    }
}

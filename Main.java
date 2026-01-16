import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            Connection con = DBConnection.getConnection();

            System.out.print("Enter Consumer ID: ");
            int cid = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Consumer Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Address: ");
            String address = sc.nextLine();

            PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO consumer VALUES (?, ?, ?)"
            );
            ps1.setInt(1, cid);
            ps1.setString(2, name);
            ps1.setString(3, address);
            ps1.executeUpdate();

            System.out.print("Enter Previous Meter Reading: ");
            int prev = sc.nextInt();

            System.out.print("Enter Current Meter Reading: ");
            int curr = sc.nextInt();

            int units = curr - prev;
            double amount;

            if (units <= 100)
                amount = units * 1.5;
            else if (units <= 200)
                amount = units * 2.5;
            else
                amount = units * 4.0;

            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO bill (consumer_id, previous_reading, current_reading, units, amount) VALUES (?, ?, ?, ?, ?)",
                new String[] { "bill_id" }
            );

            ps2.setInt(1, cid);
            ps2.setInt(2, prev);
            ps2.setInt(3, curr);
            ps2.setInt(4, units);
            ps2.setDouble(5, amount);
            ps2.executeUpdate();

            ResultSet rs = ps2.getGeneratedKeys();
            rs.next();
            int billId = rs.getInt(1);

            sc.nextLine();
            System.out.print("Enter Payment Mode (Cash / UPI / Online): ");
            String mode = sc.nextLine();

            PreparedStatement ps3 = con.prepareStatement(
                "INSERT INTO payment (bill_id, payment_mode, status) VALUES (?, ?, ?)"
            );
            ps3.setInt(1, billId);
            ps3.setString(2, mode);
            ps3.setString(3, "PAID");
            ps3.executeUpdate();

            System.out.println("\n--- ELECTRICITY BILL DETAILS ---");
            System.out.println("Units Consumed: " + units);
            System.out.println("Total Amount: Rs." + amount);
            System.out.println("Payment Status: SUCCESS");

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        sc.close();
    }
}

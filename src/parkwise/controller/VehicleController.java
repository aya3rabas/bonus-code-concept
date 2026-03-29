package parkwise.controller;

import parkwise.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VehicleController {

    public Object[] getVehicleByNumber(String vehicleNumber) {
        String sql = "SELECT vehicleNumber, typeText, color, size, weightKg, clientId " +
                     "FROM Vehicle WHERE vehicleNumber = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getString("vehicleNumber"),
                            rs.getString("typeText"),
                            rs.getString("color"),
                            rs.getString("size"),
                            rs.getDouble("weightKg"),
                            rs.getInt("clientId")
                    };
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean vehicleExists(String vehicleNumber) {
        return getVehicleByNumber(vehicleNumber) != null;
    }

    public boolean addVehicle(String vehicleNumber, String typeText, String color,
                              String size, double weightKg, int clientId) {

        String sql = "INSERT INTO Vehicle (vehicleNumber, typeText, color, size, weightKg, clientId) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);
            ps.setString(2, typeText);
            ps.setString(3, color);
            ps.setString(4, size);
            ps.setDouble(5, weightKg);
            ps.setInt(6, clientId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateVehicleClient(String vehicleNumber, int clientId) {
        String sql = "UPDATE Vehicle SET clientId = ? WHERE vehicleNumber = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ps.setString(2, vehicleNumber);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
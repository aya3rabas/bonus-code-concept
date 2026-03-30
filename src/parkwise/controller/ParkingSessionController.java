package parkwise.controller;

import parkwise.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSessionController {

	public boolean startSession(String vehicleNumber, int lotId, int clientId, String phone) {

	    String normalizedVehicle = vehicleNumber == null ? "" : vehicleNumber.trim().toUpperCase();
	    String normalizedPhone = phone == null ? "" : phone.replaceAll("\\s+", "").trim();

	    if (hasActiveSession(normalizedVehicle)) {
	        return false;
	    }

	    String checkSpacesSql = "SELECT availableSpaces FROM ParkingLot WHERE lotId = ?";
	    String insertSessionSql = "INSERT INTO ParkingSession " +
	            "(lotId, vehicleNumber, clientId, startTime, endTime, totalHours, finalAmount, " +
	            "isFreeByClubRule, discountAmount, clientPhoneAtEntry, paymentId, currentConveyorId) " +
	            "VALUES (?, ?, ?, Now(), NULL, 0, 0, false, 0, ?, NULL, NULL)";
	    String decreaseSpacesSql = "UPDATE ParkingLot SET availableSpaces = availableSpaces - 1 " +
	            "WHERE lotId = ? AND availableSpaces > 0";

	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);

	        try {
	            int availableSpaces = 0;

	            try (PreparedStatement ps = conn.prepareStatement(checkSpacesSql)) {
	                ps.setInt(1, lotId);

	                try (ResultSet rs = ps.executeQuery()) {
	                    if (rs.next()) {
	                        availableSpaces = rs.getInt("availableSpaces");
	                    } else {
	                        conn.rollback();
	                        return false;
	                    }
	                }
	            }

	            if (availableSpaces <= 0) {
	                conn.rollback();
	                return false;
	            }

	            try (PreparedStatement ps = conn.prepareStatement(insertSessionSql)) {
	                ps.setInt(1, lotId);
	                ps.setString(2, normalizedVehicle);
	                ps.setInt(3, clientId);
	                ps.setString(4, normalizedPhone);

	                if (ps.executeUpdate() <= 0) {
	                    conn.rollback();
	                    return false;
	                }
	            }

	            try (PreparedStatement ps = conn.prepareStatement(decreaseSpacesSql)) {
	                ps.setInt(1, lotId);

	                if (ps.executeUpdate() <= 0) {
	                    conn.rollback();
	                    return false;
	                }
	            }

	            conn.commit();
	            return true;

	        } catch (Exception e) {
	            conn.rollback();
	            e.printStackTrace();
	            return false;
	        } finally {
	            conn.setAutoCommit(true);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	
    }

    public Object[] getActiveSession(String vehicleNumber) {
        String sql = "SELECT * FROM ParkingSession WHERE vehicleNumber = ? AND endTime IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("sessionId"),
                            rs.getString("vehicleNumber"),
                            rs.getInt("clientId"),
                            rs.getInt("lotId"),
                            rs.getTimestamp("startTime")
                    };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean endSession(int sessionId) {
        String sql = "UPDATE ParkingSession SET endTime = Now() WHERE sessionId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePayment(int sessionId, int paymentId, double amount) {
        String sql = "UPDATE ParkingSession SET paymentId = ?, finalAmount = ? WHERE sessionId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, paymentId);
            ps.setDouble(2, amount);
            ps.setInt(3, sessionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasActiveSession(String vehicleNumber) {
        String normalizedVehicle = vehicleNumber == null ? "" : vehicleNumber.trim().toUpperCase();

        String sql = "SELECT COUNT(*) FROM ParkingSession WHERE UCASE(TRIM(vehicleNumber)) = ? AND endTime IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, normalizedVehicle);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Object[] getActiveSessionByVehicleAndPhone(String vehicleNumber, String phone) {
        String normalizedVehicle = vehicleNumber == null ? "" : vehicleNumber.trim().toUpperCase();
        String normalizedPhone = phone == null ? "" : phone.replaceAll("\\s+", "").trim();

        String sql = "SELECT * FROM ParkingSession " +
                "WHERE UCASE(TRIM(vehicleNumber)) = ? " +
                "AND TRIM(clientPhoneAtEntry) = ? " +
                "AND endTime IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, normalizedVehicle);
            ps.setString(2, normalizedPhone);
            System.out.println("Searching session with vehicle = [" + normalizedVehicle + "]");
            System.out.println("Searching session with phone = [" + normalizedPhone + "]");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("sessionId"),
                            rs.getInt("lotId"),
                            rs.getString("vehicleNumber"),
                            rs.getInt("clientId"),
                            rs.getTimestamp("startTime"),
                            rs.getString("clientPhoneAtEntry")
                    };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean completeSession(int sessionId, double totalHours, double finalAmount,
                                   int paymentId, double discountAmount, boolean isFreeByClubRule) {

        String getLotSql = "SELECT lotId FROM ParkingSession WHERE sessionId = ?";
        String updateSessionSql = "UPDATE ParkingSession " +
                "SET endTime = Now(), totalHours = ?, finalAmount = ?, paymentId = ?, " +
                "discountAmount = ?, isFreeByClubRule = ? " +
                "WHERE sessionId = ?";
        String increaseSpacesSql = "UPDATE ParkingLot SET availableSpaces = availableSpaces + 1 WHERE lotId = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                Integer lotId = null;

                try (PreparedStatement ps = conn.prepareStatement(getLotSql)) {
                    ps.setInt(1, sessionId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            lotId = rs.getInt("lotId");
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(updateSessionSql)) {
                    ps.setDouble(1, totalHours);
                    ps.setDouble(2, finalAmount);
                    ps.setInt(3, paymentId);
                    ps.setDouble(4, discountAmount);
                    ps.setBoolean(5, isFreeByClubRule);
                    ps.setInt(6, sessionId);

                    if (ps.executeUpdate() <= 0) {
                        conn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(increaseSpacesSql)) {
                    ps.setInt(1, lotId);

                    if (ps.executeUpdate() <= 0) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
package parkwise.controller;

import parkwise.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ConveyorController {

    public List<Object[]> getConveyorsByLotId(int lotId) {
        List<Object[]> conveyors = new ArrayList<>();

        String sql = "SELECT conveyorId, lotId, floor, x, y, maxWeightKg, state, "
                + "electronicCheckStatus, mechanicalCheckStatus, checkTimerText, "
                + "failedAttempts, previousState, weightChangePending "
                + "FROM Conveyor WHERE lotId = ? ORDER BY conveyorId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    conveyors.add(new Object[]{
                            rs.getInt("conveyorId"),
                            rs.getInt("lotId"),
                            rs.getInt("floor"),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("maxWeightKg"),
                            rs.getString("state"),
                            rs.getString("electronicCheckStatus"),
                            rs.getString("mechanicalCheckStatus"),
                            rs.getString("checkTimerText"),
                            rs.getInt("failedAttempts"),
                            rs.getString("previousState"),
                            rs.getBoolean("weightChangePending")
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return conveyors;
    }

    public List<Object[]> getAllConveyors() {
        List<Object[]> conveyors = new ArrayList<>();

        String sql = "SELECT conveyorId, lotId, floor, x, y, maxWeightKg, state, "
                + "electronicCheckStatus, mechanicalCheckStatus, checkTimerText, "
                + "failedAttempts, previousState, weightChangePending "
                + "FROM Conveyor ORDER BY conveyorId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                conveyors.add(new Object[]{
                        rs.getInt("conveyorId"),
                        rs.getInt("lotId"),
                        rs.getInt("floor"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("maxWeightKg"),
                        rs.getString("state"),
                        rs.getString("electronicCheckStatus"),
                        rs.getString("mechanicalCheckStatus"),
                        rs.getString("checkTimerText"),
                        rs.getInt("failedAttempts"),
                        rs.getString("previousState"),
                        rs.getBoolean("weightChangePending")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return conveyors;
    }

    public boolean addConveyor(int lotId, int floor, double x, double y, double maxWeightKg) {
        String sql = "INSERT INTO Conveyor "
                + "(lotId, floor, x, y, maxWeightKg, state, electronicCheckStatus, mechanicalCheckStatus, "
                + "checkTimerText, failedAttempts, previousState, weightChangePending) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);
            ps.setInt(2, floor);
            ps.setDouble(3, x);
            ps.setDouble(4, y);
            ps.setDouble(5, maxWeightKg);
            ps.setString(6, "OFF");
            ps.setString(7, "NOT_STARTED");
            ps.setString(8, "NOT_STARTED");
            ps.setString(9, "00:00");
            ps.setInt(10, 0);
            ps.setString(11, "OFF");
            ps.setBoolean(12, false);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateConveyor(int conveyorId, int lotId, int floor, double x, double y, double maxWeightKg) {
        String sql = "UPDATE Conveyor SET lotId = ?, floor = ?, x = ?, y = ?, maxWeightKg = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);
            ps.setInt(2, floor);
            ps.setDouble(3, x);
            ps.setDouble(4, y);
            ps.setDouble(5, maxWeightKg);
            ps.setInt(6, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateConveyorWithWeightChange(int conveyorId, int lotId, int floor, double x, double y, double maxWeightKg) {
        String sql = "UPDATE Conveyor SET lotId = ?, floor = ?, x = ?, y = ?, maxWeightKg = ?, "
                + "state = ?, weightChangePending = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);
            ps.setInt(2, floor);
            ps.setDouble(3, x);
            ps.setDouble(4, y);
            ps.setDouble(5, maxWeightKg);
            ps.setString(6, "WEIGHT_CHANGE");
            ps.setBoolean(7, true);
            ps.setInt(8, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateConveyorState(int conveyorId,
                                       String state,
                                       String electronicCheckStatus,
                                       String mechanicalCheckStatus,
                                       String checkTimerText,
                                       int failedAttempts,
                                       String previousState,
                                       boolean weightChangePending) {

        String sql = "UPDATE Conveyor SET state = ?, electronicCheckStatus = ?, mechanicalCheckStatus = ?, "
                + "checkTimerText = ?, failedAttempts = ?, previousState = ?, weightChangePending = ? "
                + "WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, state);
            ps.setString(2, electronicCheckStatus);
            ps.setString(3, mechanicalCheckStatus);
            ps.setString(4, checkTimerText);
            ps.setInt(5, failedAttempts);
            ps.setString(6, previousState);
            ps.setBoolean(7, weightChangePending);
            ps.setInt(8, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean restartConveyor(int conveyorId) {
        String sql = "UPDATE Conveyor SET state = ?, failedAttempts = 0, checkTimerText = ?, "
                + "electronicCheckStatus = ?, mechanicalCheckStatus = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "INTEGRITY_CHECK");
            ps.setString(2, "00:30");
            ps.setString(3, "SENSORS_CHECK");
            ps.setString(4, "BELT_CHECK");
            ps.setInt(5, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean changeConveyorLot(int conveyorId, int newLotId) {
        String sql = "UPDATE Conveyor SET lotId = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newLotId);
            ps.setInt(2, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean startIntegrityCheckForAll() {
        String sql = "UPDATE Conveyor SET state = ?, electronicCheckStatus = ?, mechanicalCheckStatus = ?, " +
                     "checkTimerText = ?, faultType = ?, faultDetails = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "INTEGRITY_CHECK");
            ps.setString(2, "SENSORS_CHECK");
            ps.setString(3, "BELT_CHECK");
            ps.setString(4, "00:30");
            ps.setString(5, null);
            ps.setString(6, null);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateMechanicalStatus(int conveyorId, String status) {
        String sql = "UPDATE Conveyor SET mechanicalCheckStatus = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateElectronicStatus(int conveyorId, String status) {
        String sql = "UPDATE Conveyor SET electronicCheckStatus = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateCheckTimer(int conveyorId, String timerText) {
        String sql = "UPDATE Conveyor SET checkTimerText = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, timerText);
            ps.setInt(2, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean finishIntegrityCheck(int conveyorId) {
        String sql = "UPDATE Conveyor SET state = ?, electronicCheckStatus = ?, mechanicalCheckStatus = ?, checkTimerText = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "WAITING_FOR_COMMANDS");
            ps.setString(2, "COMPLETE");
            ps.setString(3, "COMPLETE");
            ps.setString(4, "00:00");
            ps.setInt(5, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public boolean failConveyor(int conveyorId, String faultType, String faultDetails, String electronicStatus, String mechanicalStatus) {
        String sql = "UPDATE Conveyor SET state = ?, faultType = ?, faultDetails = ?, " +
                     "electronicCheckStatus = ?, mechanicalCheckStatus = ? WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "PAUSE");
            ps.setString(2, faultType);
            ps.setString(3, faultDetails);
            ps.setString(4, electronicStatus);
            ps.setString(5, mechanicalStatus);
            ps.setInt(6, conveyorId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public List<Object[]> getPausedConveyorsWithFaults() {
        List<Object[]> faults = new ArrayList<>();

        String sql = "SELECT conveyorId, lotId, faultType, faultDetails FROM Conveyor WHERE state = 'PAUSE' ORDER BY conveyorId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                faults.add(new Object[] {
                        rs.getInt("conveyorId"),
                        rs.getInt("lotId"),
                        rs.getString("faultType"),
                        rs.getString("faultDetails")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return faults;
    }
    public boolean canTurnOffAll() {
        String sql = "SELECT COUNT(*) FROM Conveyor WHERE state = 'ROUTING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
    public boolean turnOffAllConveyors() {
        String sql = "UPDATE Conveyor SET state = 'OFF', previousState = state";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public boolean turnOnAllConveyors() {
        String sql = "UPDATE Conveyor SET state = 'INTEGRITY_CHECK', " +
                "electronicCheckStatus = 'SENSORS_CHECK', " +
                "mechanicalCheckStatus = 'BELT_CHECK', " +
                "checkTimerText = '00:30' " +
                "WHERE weightChangePending = false";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
    }
    public boolean confirmWeightChange(int conveyorId) {
        String sql = "UPDATE Conveyor SET weightChangePending = false, state = 'OFF' WHERE conveyorId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, conveyorId);
            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
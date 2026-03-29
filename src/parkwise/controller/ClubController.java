package parkwise.controller;

import parkwise.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClubController {

    public Integer getClientIdByPhone(String phone) {
        String sql = "SELECT clientId FROM Client WHERE mobilePhone = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("clientId");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean clientExistsByPhone(String phone) {
        return getClientIdByPhone(phone) != null;
    }

    public Integer getMemberIdByClientId(int clientId) {
        String sql = "SELECT memberId FROM ClubMembership WHERE clientId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("memberId");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean isMemberByClientId(int clientId) {
        return getMemberIdByClientId(clientId) != null;
    }

    public boolean joinClub(int clientId) {
        if (isMemberByClientId(clientId)) {
            return true;
        }

        String sql = "INSERT INTO ClubMembership (clientId, joinDate) VALUES (?, Date())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getAllParkingLots() {
        List<Object[]> lots = new ArrayList<>();

        String sql = "SELECT lotId, name, city FROM ParkingLot ORDER BY lotId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lots.add(new Object[]{
                        rs.getInt("lotId"),
                        rs.getString("name"),
                        rs.getString("city")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lots;
    }

    public boolean hasPreferredLot(int memberId, int lotId) {
        String sql = "SELECT COUNT(*) FROM PreferredLotSelection WHERE memberId = ? AND lotId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ps.setInt(2, lotId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean addPreferredLot(int memberId, int lotId) {
        if (hasPreferredLot(memberId, lotId)) {
            return true;
        }

        String sql = "INSERT INTO PreferredLotSelection (memberId, lotId, selectionDate) VALUES (?, ?, Date())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ps.setInt(2, lotId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Integer> getPreferredLotIds(int memberId) {
        List<Integer> ids = new ArrayList<>();

        String sql = "SELECT lotId FROM PreferredLotSelection WHERE memberId = ? ORDER BY lotId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("lotId"));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ids;
    }
}
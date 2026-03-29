package parkwise.controller;

import parkwise.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientController {

    public Object[] getClientByPhone(String mobilePhone) {
        String sql = "SELECT clientId, firstName, lastName, mobilePhone " +
                     "FROM Client WHERE mobilePhone = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mobilePhone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("clientId"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("mobilePhone")
                    };
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean clientExists(String mobilePhone) {
        return getClientByPhone(mobilePhone) != null;
    }

    public Integer getClientIdByPhone(String mobilePhone) {
        Object[] client = getClientByPhone(mobilePhone);
        if (client == null) {
            return null;
        }
        return (Integer) client[0];
    }

    public boolean addClient(String firstName, String lastName, String mobilePhone) {
        String sql = "INSERT INTO Client (firstName, lastName, mobilePhone) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, mobilePhone);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
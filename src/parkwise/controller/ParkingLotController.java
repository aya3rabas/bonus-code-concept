package parkwise.controller;

import parkwise.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class ParkingLotController {

    public List<Object[]> getAllParkingLots() {
        List<Object[]> lots = new ArrayList<>();

        String sql = "SELECT lotId, name, address, city, maxSpaces, availableSpaces " +
                     "FROM ParkingLot " +
                     "ORDER BY lotId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lots.add(new Object[]{
                        rs.getInt("lotId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getInt("maxSpaces"),
                        rs.getInt("availableSpaces")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lots;
    }

    public Object[] getCurrentPriceListForLot(int lotId) {
        String sql = "SELECT TOP 1 priceListId, effectiveDate, firstHourPrice, additionalHourPrice, fullDayPrice " +
                     "FROM PriceList " +
                     "WHERE lotId = ? " +
                     "ORDER BY effectiveDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("priceListId"),
                            rs.getDate("effectiveDate"),
                            rs.getDouble("firstHourPrice"),
                            rs.getDouble("additionalHourPrice"),
                            rs.getDouble("fullDayPrice")
                    };
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public List<Object[]> getPriceHistory(int lotId) {
        List<Object[]> history = new ArrayList<>();

        String sql = "SELECT effectiveDate, firstHourPrice, additionalHourPrice, fullDayPrice " +
                     "FROM PriceList " +
                     "WHERE lotId = ? " +
                     "ORDER BY effectiveDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new Object[]{
                            rs.getDate("effectiveDate"),
                            rs.getDouble("firstHourPrice"),
                            rs.getDouble("additionalHourPrice"),
                            rs.getDouble("fullDayPrice")
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return history;
    }

    public boolean addPriceList(int lotId, String effectiveDate, double firstHour, double additionalHour, double fullDay) {
        String sql = "INSERT INTO PriceList (lotId, effectiveDate, firstHourPrice, additionalHourPrice, fullDayPrice) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);
            ps.setDate(2, Date.valueOf(effectiveDate));
            ps.setDouble(3, firstHour);
            ps.setDouble(4, additionalHour);
            ps.setDouble(5, fullDay);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean addParkingLot(String name, String address, String city, int maxSpaces, int availableSpaces) {
        String sql = "INSERT INTO ParkingLot (name, address, city, maxSpaces, availableSpaces) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, city);
            ps.setInt(4, maxSpaces);
            ps.setInt(5, availableSpaces);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateParkingLot(int lotId, String name, String address, String city, int maxSpaces, int availableSpaces) {
        String sql = "UPDATE ParkingLot " +
                     "SET name = ?, address = ?, city = ?, maxSpaces = ?, availableSpaces = ? " +
                     "WHERE lotId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, city);
            ps.setInt(4, maxSpaces);
            ps.setInt(5, availableSpaces);
            ps.setInt(6, lotId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean removeParkingLot(int lotId) {
        String sql = "DELETE FROM ParkingLot WHERE lotId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);
            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getAllPriceLists() {
        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT priceListId, firstHourPrice, additionalHourPrice, fullDayPrice " +
                     "FROM PriceList " +
                     "ORDER BY priceListId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("priceListId"),
                        rs.getDouble("firstHourPrice"),
                        rs.getDouble("additionalHourPrice"),
                        rs.getDouble("fullDayPrice")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public List<Object[]> getPriceHistoryForLot(int lotId) {
        List<Object[]> history = new ArrayList<>();

        String sql = "SELECT priceListId, firstHourPrice, additionalHourPrice, fullDayPrice, effectiveDate " +
                     "FROM PriceList " +
                     "WHERE lotId = ? " +
                     "ORDER BY effectiveDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new Object[]{
                            rs.getInt("priceListId"),
                            rs.getDouble("firstHourPrice"),
                            rs.getDouble("additionalHourPrice"),
                            rs.getDouble("fullDayPrice"),
                            rs.getDate("effectiveDate")
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return history;
    }

    public boolean existsPriceListForDate(int lotId, Date effectiveDate) {
        String sql = "SELECT COUNT(*) FROM PriceList WHERE lotId = ? AND effectiveDate = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);
            ps.setDate(2, effectiveDate);

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

    public boolean adaptPriceListToLot(int lotId, int priceListId, Date effectiveDate) {
        String sql =
                "INSERT INTO PriceList (lotId, effectiveDate, firstHourPrice, additionalHourPrice, fullDayPrice) " +
                "SELECT ?, ?, firstHourPrice, additionalHourPrice, fullDayPrice " +
                "FROM PriceList WHERE priceListId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);
            ps.setDate(2, effectiveDate);
            ps.setInt(3, priceListId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
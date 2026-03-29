package parkwise.controller;

import parkwise.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class PaymentController {

    private static final double CLUB_DISCOUNT_RATE = 0.10; // 10%

    public static class PaymentResult {
        private final double baseAmount;
        private final double finalAmount;
        private final double discountAmount;
        private final boolean freeByClubRule;

        public PaymentResult(double baseAmount, double finalAmount, double discountAmount, boolean freeByClubRule) {
            this.baseAmount = baseAmount;
            this.finalAmount = finalAmount;
            this.discountAmount = discountAmount;
            this.freeByClubRule = freeByClubRule;
        }

        public double getBaseAmount() {
            return baseAmount;
        }

        public double getFinalAmount() {
            return finalAmount;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }

        public boolean isFreeByClubRule() {
            return freeByClubRule;
        }
    }

    public double calculateHours(Timestamp startTime, Timestamp endTime) {
        if (startTime == null || endTime == null) {
            return 0;
        }

        LocalDateTime start = startTime.toLocalDateTime();
        LocalDateTime end = endTime.toLocalDateTime();

        long minutes = Duration.between(start, end).toMinutes();
        double hours = minutes / 60.0;

        if (hours <= 1) {
            return 1;
        }

        return Math.ceil(hours);
    }

    public Object[] getCurrentPriceListForLot(int lotId) {
        String sql =
                "SELECT TOP 1 priceListId, firstHourPrice, additionalHourPrice, fullDayPrice " +
                "FROM PriceList WHERE lotId = ? ORDER BY effectiveDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("priceListId"),
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

    public double calculateAmount(double totalHours, double firstHourPrice,
                                  double additionalHourPrice, double fullDayPrice) {

        if (totalHours <= 1) {
            return firstHourPrice;
        }

        if (totalHours >= 24) {
            int fullDays = (int) (totalHours / 24);
            double remainingHours = totalHours % 24;

            double amount = fullDays * fullDayPrice;

            if (remainingHours > 0) {
                if (remainingHours <= 1) {
                    amount += firstHourPrice;
                } else {
                    amount += firstHourPrice + ((Math.ceil(remainingHours) - 1) * additionalHourPrice);
                }

                if (amount > (fullDays + 1) * fullDayPrice) {
                    amount = (fullDays + 1) * fullDayPrice;
                }
            }

            return amount;
        }

        double amount = firstHourPrice + ((totalHours - 1) * additionalHourPrice);
        return Math.min(amount, fullDayPrice);
    }

    public PaymentResult calculateFinalPaymentWithClubBenefits(
            int clientId,
            int lotId,
            double totalHours,
            double firstHourPrice,
            double additionalHourPrice,
            double fullDayPrice
    ) {
        double baseAmount = calculateAmount(totalHours, firstHourPrice, additionalHourPrice, fullDayPrice);

        if (!isClientClubMember(clientId)) {
            return new PaymentResult(baseAmount, baseAmount, 0, false);
        }

        if (isEligibleForFreeSession(clientId, lotId)) {
            return new PaymentResult(baseAmount, 0, baseAmount, true);
        }

        double discountAmount = baseAmount * CLUB_DISCOUNT_RATE;
        double finalAmount = baseAmount - discountAmount;

        return new PaymentResult(baseAmount, finalAmount, discountAmount, false);
    }

    public Integer createPayment(double amount) {
        String sql = "INSERT INTO Payment (amount, status, externalTransactionId, paymentTime) " +
                     "VALUES (?, ?, ?, Now())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setString(2, "APPROVED");
            ps.setString(3, "TXN-" + System.currentTimeMillis());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                return getLastPaymentId(conn);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private Integer getLastPaymentId(Connection conn) {
        String sql = "SELECT MAX(paymentId) AS lastId FROM Payment";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("lastId");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private boolean isClientClubMember(int clientId) {
        String sql = "SELECT COUNT(*) FROM ClubMembership WHERE clientId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);

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

    private Integer getMemberIdByClientId(int clientId) {
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

    private Timestamp getJoinDate(int clientId) {
        String sql = "SELECT joinDate FROM ClubMembership WHERE clientId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("joinDate");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private boolean isPreferredLot(int memberId, int lotId) {
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

    private boolean alreadyUsedFreeSessionInJoinMonth(int clientId, Timestamp joinDate) {
        String sql =
                "SELECT COUNT(*) FROM ParkingSession " +
                "WHERE clientId = ? AND isFreeByClubRule = true " +
                "AND Month(startTime) = ? AND Year(startTime) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDateTime join = joinDate.toLocalDateTime();

            ps.setInt(1, clientId);
            ps.setInt(2, join.getMonthValue());
            ps.setInt(3, join.getYear());

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

    private boolean isEligibleForFreeSession(int clientId, int lotId) {
        Integer memberId = getMemberIdByClientId(clientId);
        if (memberId == null) {
            return false;
        }

        Timestamp joinDate = getJoinDate(clientId);
        if (joinDate == null) {
            return false;
        }

        LocalDateTime join = joinDate.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        // الجلسة المجانية فقط في نفس شهر/سنة الانضمام
        if (join.getMonthValue() != now.getMonthValue() || join.getYear() != now.getYear()) {
            return false;
        }

        if (!isPreferredLot(memberId, lotId)) {
            return false;
        }

        return !alreadyUsedFreeSessionInJoinMonth(clientId, joinDate);
    }
}
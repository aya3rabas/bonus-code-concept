package parkwise.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import parkwise.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerClubGrowthReportFrame extends JFrame {

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Month", "New Members", "Cancellations", "Active Members", "Growth %"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    private final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    public CustomerClubGrowthReportFrame() {
        setTitle("Customer Club Growth Report");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color offWhite = new Color(249, 248, 244);
        Color dark = new Color(28, 28, 28);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(offWhite);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Customer Club Growth Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(dark);
        root.add(titleLabel, BorderLayout.NORTH);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        root.add(scrollPane, BorderLayout.CENTER);

        initializeMonths();
        loadReportData();

        setContentPane(root);
    }

    private void initializeMonths() {
        tableModel.setRowCount(0);

        for (String month : months) {
            tableModel.addRow(new Object[]{month, 0, 0, 0, "0.0%"});
        }
    }

    private void loadReportData() {
        String joinSql =
                "SELECT MONTH(joinDate) AS monthNumber, COUNT(memberId) AS newMembers " +
                "FROM ClubMembership " +
                "WHERE YEAR(joinDate) = YEAR(Date()) " +
                "GROUP BY MONTH(joinDate) " +
                "ORDER BY MONTH(joinDate)";

        String cancelSql =
                "SELECT MONTH(cancelDate) AS monthNumber, COUNT(memberId) AS cancellations " +
                "FROM ClubMembership " +
                "WHERE cancelDate IS NOT NULL AND YEAR(cancelDate) = YEAR(Date()) " +
                "GROUP BY MONTH(cancelDate) " +
                "ORDER BY MONTH(cancelDate)";

        try (Connection conn = DBConnection.getConnection()) {

            int[] newMembersByMonth = new int[12];
            int[] cancellationsByMonth = new int[12];

            try (PreparedStatement ps = conn.prepareStatement(joinSql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int monthNumber = rs.getInt("monthNumber");
                    int newMembers = rs.getInt("newMembers");

                    if (monthNumber >= 1 && monthNumber <= 12) {
                        newMembersByMonth[monthNumber - 1] = newMembers;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(cancelSql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int monthNumber = rs.getInt("monthNumber");
                    int cancellations = rs.getInt("cancellations");

                    if (monthNumber >= 1 && monthNumber <= 12) {
                        cancellationsByMonth[monthNumber - 1] = cancellations;
                    }
                }
            }

            int cumulativeJoined = 0;
            int cumulativeCancelled = 0;
            int previousActive = 0;

            for (int i = 0; i < 12; i++) {
                int newMembers = newMembersByMonth[i];
                int cancellations = cancellationsByMonth[i];

                cumulativeJoined += newMembers;
                cumulativeCancelled += cancellations;

                int activeMembers = cumulativeJoined - cumulativeCancelled;
                if (activeMembers < 0) {
                    activeMembers = 0;
                }

                double growthPercent;
                if (i == 0 || previousActive == 0) {
                    growthPercent = 0.0;
                } else {
                    growthPercent = ((double) (activeMembers - previousActive) / previousActive) * 100.0;
                }

                tableModel.setValueAt(newMembers, i, 1);
                tableModel.setValueAt(cancellations, i, 2);
                tableModel.setValueAt(activeMembers, i, 3);
                tableModel.setValueAt(String.format("%.1f%%", growthPercent), i, 4);

                previousActive = activeMembers;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Customer Club Growth Report from database.");
        }
    }
}
package parkwise.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import parkwise.db.DBConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CustomerClubGrowthReportFrame extends JFrame {

    private final JTextField yearField = new JTextField(10);
    private int selectedYear = Year.now().getValue();

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

    private final List<ClubGrowthRow> reportRows = new ArrayList<>();
    private final JLabel yearDisplayLabel = new JLabel("Year: " + selectedYear, SwingConstants.CENTER);

    public CustomerClubGrowthReportFrame() {
        setTitle("Customer Club Growth Report");
        setSize(1050, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color offWhite = new Color(249, 248, 244);
        Color matcha = new Color(92, 146, 82);
        Color dark = new Color(28, 28, 28);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(offWhite);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(offWhite);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Customer Club Growth Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(dark);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        yearDisplayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        yearDisplayLabel.setForeground(dark);
        yearDisplayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(yearDisplayLabel);

        root.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(offWhite);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        topPanel.setBackground(offWhite);

        JLabel yearLabelInput = new JLabel("Enter Year:");
        yearLabelInput.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        yearField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        yearField.setPreferredSize(new Dimension(120, 36));
        yearField.setText(String.valueOf(selectedYear));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        refreshButton.setBackground(matcha);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        JButton exportXmlButton = new JButton("Export to XML");
        exportXmlButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        exportXmlButton.setBackground(matcha);
        exportXmlButton.setForeground(Color.WHITE);
        exportXmlButton.setFocusPainted(false);

        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        exportPdfButton.setBackground(matcha);
        exportPdfButton.setForeground(Color.WHITE);
        exportPdfButton.setFocusPainted(false);

        refreshButton.addActionListener(e -> {
            String yearText = yearField.getText().trim();

            if (!yearText.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(this, "Enter valid 4-digit year.");
                return;
            }

            selectedYear = Integer.parseInt(yearText);
            yearDisplayLabel.setText("Year: " + selectedYear);

            initializeMonths();
            loadReportData();
        });

        exportXmlButton.addActionListener(e -> exportToXml());
        exportPdfButton.addActionListener(e -> exportToPdf());

        topPanel.add(yearLabelInput);
        topPanel.add(yearField);
        topPanel.add(refreshButton);
        topPanel.add(exportXmlButton);
        topPanel.add(exportPdfButton);

        centerPanel.add(topPanel, BorderLayout.NORTH);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        root.add(centerPanel, BorderLayout.CENTER);

        initializeMonths();
        loadReportData();

        setContentPane(root);
    }

    private void initializeMonths() {
        tableModel.setRowCount(0);
        reportRows.clear();

        for (String month : months) {
            tableModel.addRow(new Object[]{month, 0, 0, 0, "0.0%"});
        }
    }

    private void loadReportData() {
        String joinSql =
                "SELECT MONTH(joinDate) AS monthNumber, COUNT(memberId) AS newMembers " +
                "FROM ClubMembership " +
                "WHERE YEAR(joinDate) = ? " +
                "GROUP BY MONTH(joinDate) " +
                "ORDER BY MONTH(joinDate)";

        String cancelSql =
                "SELECT MONTH(cancelDate) AS monthNumber, COUNT(memberId) AS cancellations " +
                "FROM ClubMembership " +
                "WHERE cancelDate IS NOT NULL AND YEAR(cancelDate) = ? " +
                "GROUP BY MONTH(cancelDate) " +
                "ORDER BY MONTH(cancelDate)";

        try (Connection conn = DBConnection.getConnection()) {

            int[] newMembersByMonth = new int[12];
            int[] cancellationsByMonth = new int[12];

            try (PreparedStatement ps = conn.prepareStatement(joinSql)) {
                ps.setInt(1, selectedYear);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int monthNumber = rs.getInt("monthNumber");
                        int newMembers = rs.getInt("newMembers");

                        if (monthNumber >= 1 && monthNumber <= 12) {
                            newMembersByMonth[monthNumber - 1] = newMembers;
                        }
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(cancelSql)) {
                ps.setInt(1, selectedYear);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int monthNumber = rs.getInt("monthNumber");
                        int cancellations = rs.getInt("cancellations");

                        if (monthNumber >= 1 && monthNumber <= 12) {
                            cancellationsByMonth[monthNumber - 1] = cancellations;
                        }
                    }
                }
            }

            int cumulativeJoined = 0;
            int cumulativeCancelled = 0;
            int previousActive = 0;

            reportRows.clear();

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

                reportRows.add(new ClubGrowthRow(
                        months[i],
                        newMembers,
                        cancellations,
                        activeMembers,
                        growthPercent
                ));

                previousActive = activeMembers;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Customer Club Growth Report from database.");
        }
    }

    private void exportToXml() {
        if (reportRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report data to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("CustomerClubGrowthReport_" + selectedYear + ".xml"));

        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("customerClubGrowthReport");
            root.setAttribute("year", String.valueOf(selectedYear));
            doc.appendChild(root);

            for (ClubGrowthRow row : reportRows) {
                Element monthElement = doc.createElement("monthReport");
                root.appendChild(monthElement);

                appendChild(doc, monthElement, "month", row.month);
                appendChild(doc, monthElement, "newMembers", String.valueOf(row.newMembers));
                appendChild(doc, monthElement, "cancellations", String.valueOf(row.cancellations));
                appendChild(doc, monthElement, "activeMembers", String.valueOf(row.activeMembers));
                appendChild(doc, monthElement, "growthPercent", String.format("%.1f", row.growthPercent));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(new DOMSource(doc), new StreamResult(file));

            JOptionPane.showMessageDialog(this, "XML file exported successfully.");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting XML:\n" + ex.getMessage());
        }
    }

    private void exportToPdf() {
        if (reportRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report data to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("CustomerClubGrowthReport_" + selectedYear + ".pdf"));

        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(50, 750);
                content.showText("Customer Club Growth Report");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 14);
                content.newLineAtOffset(50, 725);
                content.showText("Year: " + selectedYear);
                content.endText();

                float y = 690;

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 10);
                content.newLineAtOffset(50, y);
                content.showText("Month | New Members | Cancellations | Active Members | Growth %");
                content.endText();

                y -= 20;

                for (ClubGrowthRow row : reportRows) {
                    String line = row.month + " | " +
                            row.newMembers + " | " +
                            row.cancellations + " | " +
                            row.activeMembers + " | " +
                            String.format("%.1f%%", row.growthPercent);

                    if (y < 50) {
                        break;
                    }

                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA, 9);
                    content.newLineAtOffset(50, y);
                    content.showText(trimForPdf(line, 110));
                    content.endText();

                    y -= 16;
                }
            }

            document.save(new FileOutputStream(file));
            JOptionPane.showMessageDialog(this, "PDF file exported successfully.");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting PDF:\n" + ex.getMessage());
        }
    }

    private void appendChild(Document doc, Element parent, String tagName, String value) {
        Element child = doc.createElement(tagName);
        child.appendChild(doc.createTextNode(value == null ? "" : value));
        parent.appendChild(child);
    }

    private String trimForPdf(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private static class ClubGrowthRow {
        String month;
        int newMembers;
        int cancellations;
        int activeMembers;
        double growthPercent;

        ClubGrowthRow(String month, int newMembers, int cancellations, int activeMembers, double growthPercent) {
            this.month = month;
            this.newMembers = newMembers;
            this.cancellations = cancellations;
            this.activeMembers = activeMembers;
            this.growthPercent = growthPercent;
        }
    }
}
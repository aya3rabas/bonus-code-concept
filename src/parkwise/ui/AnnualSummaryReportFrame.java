package parkwise.ui;

import parkwise.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

public class AnnualSummaryReportFrame extends JFrame {

    private final JTextField yearField = new JTextField(10);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Parking Lot Number", "Parking Lot Name", "Address", "City", "Total Amount (₪)", "Other Vehicle Count"},
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final JLabel yearDisplayLabel = new JLabel("Year: ", SwingConstants.CENTER);

    private final List<ReportRow> reportRows = new ArrayList<>();
    private int generatedYear = -1;

    public AnnualSummaryReportFrame() {
        setTitle("Annual Summary Report");
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

        JLabel titleLabel = new JLabel("Annual Summary Report", SwingConstants.CENTER);
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

        JLabel yearLabel = new JLabel("Enter Year:");
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        yearField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        yearField.setPreferredSize(new Dimension(120, 36));

        JButton generateButton = new JButton("Generate");
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        generateButton.setBackground(matcha);
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);

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

        generateButton.addActionListener(e -> generateReport());
        exportXmlButton.addActionListener(e -> exportToXml());
        exportPdfButton.addActionListener(e -> exportToPdf());
    
        
        topPanel.add(yearLabel);
        topPanel.add(yearField);
        topPanel.add(generateButton);
        topPanel.add(exportXmlButton);
        topPanel.add(exportPdfButton);

        centerPanel.add(topPanel, BorderLayout.NORTH);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        root.add(centerPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void generateReport() {
        String yearText = yearField.getText().trim();

        if (!yearText.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 4-digit year.");
            return;
        }

        int year = Integer.parseInt(yearText);
        generatedYear = year;
        yearDisplayLabel.setText("Year: " + year);

        tableModel.setRowCount(0);
        reportRows.clear();

        String sql =
                "SELECT p.lotId, p.name, p.address, p.city, " +
                "       IIF(a.totalAmount IS NULL, 0, a.totalAmount) AS totalAmount, " +
                "       IIF(o.otherVehicleCount IS NULL, 0, o.otherVehicleCount) AS otherVehicleCount " +
                "FROM (ParkingLot AS p " +
                "LEFT JOIN " +
                "   (SELECT lotId, SUM(finalAmount) AS totalAmount " +
                "    FROM ParkingSession " +
                "    WHERE YEAR(startTime) = ? " +
                "    GROUP BY lotId) AS a " +
                "ON p.lotId = a.lotId) " +
                "LEFT JOIN " +
                "   (SELECT s.lotId, COUNT(*) AS otherVehicleCount " +
                "    FROM ParkingSession AS s " +
                "    INNER JOIN Vehicle AS v ON s.vehicleNumber = v.vehicleNumber " +
                "    WHERE YEAR(s.startTime) = ? AND UCASE(v.color) = 'OTHER' " +
                "    GROUP BY s.lotId) AS o " +
                "ON p.lotId = o.lotId " +
                "ORDER BY p.city DESC, IIF(a.totalAmount IS NULL, 0, a.totalAmount) ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, year);

            DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportRow row = new ReportRow(
                            rs.getInt("lotId"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("city"),
                            rs.getDouble("totalAmount"),
                            rs.getInt("otherVehicleCount")
                    );

                    reportRows.add(row);

                    tableModel.addRow(new Object[]{
                            row.lotId,
                            row.name,
                            row.address,
                            row.city,
                            "₪ " + moneyFormat.format(row.totalAmount),
                            row.otherVehicleCount
                    });
                }
            }

            if (reportRows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data found for year " + year + ".");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report from database:\n" + ex.getMessage());
        }
    }

    private void exportToXml() {
        if (reportRows.isEmpty() || generatedYear == -1) {
            JOptionPane.showMessageDialog(this, "Generate the report first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("AnnualSummaryReport_" + generatedYear + ".xml"));

        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("annualSummaryReport");
            root.setAttribute("year", String.valueOf(generatedYear));
            doc.appendChild(root);

            for (ReportRow row : reportRows) {
                Element lotElement = doc.createElement("parkingLot");
                root.appendChild(lotElement);

                appendChild(doc, lotElement, "parkingLotNumber", String.valueOf(row.lotId));
                appendChild(doc, lotElement, "parkingLotName", row.name);
                appendChild(doc, lotElement, "address", row.address);
                appendChild(doc, lotElement, "city", row.city);
                appendChild(doc, lotElement, "totalAmount", String.valueOf(row.totalAmount));
                appendChild(doc, lotElement, "otherVehicleCount", String.valueOf(row.otherVehicleCount));
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
        if (reportRows.isEmpty() || generatedYear == -1) {
            JOptionPane.showMessageDialog(this, "Generate the report first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("AnnualSummaryReport_" + generatedYear + ".pdf"));

        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(50, 750);
                content.showText("Annual Summary Report");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 14);
                content.newLineAtOffset(50, 725);
                content.showText("Year: " + generatedYear);
                content.endText();

                float y = 690;

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 10);
                content.newLineAtOffset(50, y);
                content.showText("Lot No. | Name | Address | City | Total Amount | Other Count");
                content.endText();

                y -= 20;

                for (ReportRow row : reportRows) {
                    String line = row.lotId + " | " +
                            safe(row.name) + " | " +
                            safe(row.address) + " | " +
                            safe(row.city) + " | " +
                            "ILS " + moneyFormat.format(row.totalAmount) + " | " +
                            row.otherVehicleCount;

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

    private String safe(String value) {
        return value == null ? "" : value;
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

    private static class ReportRow {
        int lotId;
        String name;
        String address;
        String city;
        double totalAmount;
        int otherVehicleCount;

        ReportRow(int lotId, String name, String address, String city, double totalAmount, int otherVehicleCount) {
            this.lotId = lotId;
            this.name = name;
            this.address = address;
            this.city = city;
            this.totalAmount = totalAmount;
            this.otherVehicleCount = otherVehicleCount;
        }
    }
}
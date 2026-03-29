package parkwise.ui;

import parkwise.controller.ParkingLotController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class AdaptPriceListFrame extends JFrame {

    private final Color OFF_WHITE = new Color(249, 248, 244);
    private final Color MATCHA_1 = new Color(123, 171, 106);
    private final Color MATCHA_2 = new Color(92, 146, 82);
    private final Color TEXT_DARK = new Color(28, 28, 28);

    private final JComboBox<String> lotComboBox = new JComboBox<>();
    private final JComboBox<String> priceListComboBox = new JComboBox<>();
    private final JTextField effectiveDateField = new JTextField(
            java.time.LocalDate.now().toString()
    );

    private final JTextField firstHourField = new JTextField();
    private final JTextField additionalHourField = new JTextField();
    private final JTextField fullDayField = new JTextField();

    private final JLabel currentLabel = new JLabel("Current Active Price List: ");

    private final DefaultTableModel historyModel = new DefaultTableModel(
            new String[]{"Price List ID", "First Hour", "Additional Hour", "Full Day", "Effective Date"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable historyTable = new JTable(historyModel);

    private final ParkingLotController controller = new ParkingLotController();

    private List<Object[]> allLots;
    private List<Object[]> allPriceLists;

    public AdaptPriceListFrame() {
        setTitle("ParkWise - Adapt Price List");
        setSize(1000, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setContentPane(createMainPanel());

        loadLots();
        loadPriceLists();
        registerListeners();

        if (lotComboBox.getItemCount() > 0) {
            lotComboBox.setSelectedIndex(0);
            refreshForSelectedLot();
            if (historyTable.getRowCount() > 0) {
                historyTable.setRowSelectionInterval(0, 0);
            }
        }
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(OFF_WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Adapt Price List for Parking Lot", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT_DARK);

        currentLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        currentLabel.setForeground(MATCHA_2);
        currentLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(currentLabel, BorderLayout.SOUTH);

        root.add(topPanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setOpaque(false);

        center.add(createFormPanel(), BorderLayout.NORTH);
        center.add(createHistoryPanel(), BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(createButtonsPanel(), BorderLayout.SOUTH);

        return root;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 210), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lotLabel = new JLabel("Parking Lot:");
        JLabel priceListLabel = new JLabel("Price List:");
        JLabel dateLabel = new JLabel("Effective Date (yyyy-mm-dd):");
        JLabel firstHourLabel = new JLabel("First Hour Price:");
        JLabel additionalHourLabel = new JLabel("Additional Hour Price:");
        JLabel fullDayLabel = new JLabel("Full Day Price:");

        styleLabel(lotLabel);
        styleLabel(priceListLabel);
        styleLabel(dateLabel);
        styleLabel(firstHourLabel);
        styleLabel(additionalHourLabel);
        styleLabel(fullDayLabel);

        styleField(effectiveDateField);
        styleField(firstHourField);
        styleField(additionalHourField);
        styleField(fullDayField);

        firstHourField.setEditable(false);
        additionalHourField.setEditable(false);
        fullDayField.setEditable(false);

        lotComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        priceListComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lotLabel, gbc);
        gbc.gridx = 1;
        panel.add(lotComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(priceListLabel, gbc);
        gbc.gridx = 1;
        panel.add(priceListComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(effectiveDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(firstHourLabel, gbc);
        gbc.gridx = 1;
        panel.add(firstHourField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(additionalHourLabel, gbc);
        gbc.gridx = 1;
        panel.add(additionalHourField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(fullDayLabel, gbc);
        gbc.gridx = 1;
        panel.add(fullDayField, gbc);

        return panel;
    }

    private JScrollPane createHistoryPanel() {
        historyTable.setRowHeight(24);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Parking Lot Price History"));
        return scrollPane;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        JButton adaptButton = new JButton("Adapt Price List");
        JButton closeButton = new JButton("Close");

        styleButton(refreshButton, false);
        styleButton(adaptButton, true);
        styleButton(closeButton, false);

        refreshButton.addActionListener(e -> refreshForSelectedLot());
        adaptButton.addActionListener(e -> adaptPriceList());
        closeButton.addActionListener(e -> dispose());

        panel.add(refreshButton);
        panel.add(adaptButton);
        panel.add(closeButton);

        return panel;
    }

    private void registerListeners() {
        lotComboBox.addActionListener(e -> refreshForSelectedLot());
        priceListComboBox.addActionListener(e -> showSelectedPriceListDetails());
    }

    private void loadLots() {
        allLots = controller.getAllParkingLots();
        lotComboBox.removeAllItems();

        for (Object[] lot : allLots) {
            int lotId = (Integer) lot[0];
            String name = String.valueOf(lot[1]);
            String city = String.valueOf(lot[2]);
            lotComboBox.addItem(lotId + " - " + name + " (" + city + ")");
        }
    }

    private void loadPriceLists() {
        allPriceLists = controller.getAllPriceLists();
        priceListComboBox.removeAllItems();

        for (Object[] pl : allPriceLists) {
            int priceListId = (Integer) pl[0];
            double firstHour = (Double) pl[1];
            double additional = (Double) pl[2];
            double fullDay = (Double) pl[3];

            priceListComboBox.addItem(
                    priceListId + " | First: ₪" + firstHour +
                            " | Add: ₪" + additional +
                            " | Day: ₪" + fullDay
            );
        }

        showSelectedPriceListDetails();
    }

    private void showSelectedPriceListDetails() {
        int index = priceListComboBox.getSelectedIndex();

        if (index < 0 || allPriceLists == null || index >= allPriceLists.size()) {
            firstHourField.setText("");
            additionalHourField.setText("");
            fullDayField.setText("");
            return;
        }

        Object[] pl = allPriceLists.get(index);
        firstHourField.setText(String.valueOf(pl[1]));
        additionalHourField.setText(String.valueOf(pl[2]));
        fullDayField.setText(String.valueOf(pl[3]));
    }

    private void refreshForSelectedLot() {
        int lotIndex = lotComboBox.getSelectedIndex();

        if (lotIndex < 0 || allLots == null || lotIndex >= allLots.size()) {
            currentLabel.setText("No active price list for this parking lot.");
            return;
        }

        int lotId = (Integer) allLots.get(lotIndex)[0];

        historyModel.setRowCount(0);
        List<Object[]> history = controller.getPriceHistoryForLot(lotId);

        for (Object[] row : history) {
            historyModel.addRow(row);
        }

        if (!history.isEmpty()) {
            Object[] latest = history.get(0); // because ordered DESC by effectiveDate
            currentLabel.setText(
                    "Current Active Price List → ID: " + latest[0] +
                            " | First: ₪" + latest[1] +
                            " | Add: ₪" + latest[2] +
                            " | Day: ₪" + latest[3] +
                            " | From: " + latest[4]
            );
        } else {
            currentLabel.setText("No active price list for this parking lot.");
        }
    }

    private void adaptPriceList() {
        int lotIndex = lotComboBox.getSelectedIndex();
        int priceListIndex = priceListComboBox.getSelectedIndex();

        if (lotIndex < 0 || priceListIndex < 0) {
        	StyledMessageDialog.showMessage(
        	        this,
        	        "Please select a parking lot and a price list.",
        	        "Warning"
        	);
            return;
        }

        String dateText = effectiveDateField.getText().trim();
        if (dateText.isEmpty()) {
        	StyledMessageDialog.showMessage(
        	        this,
        	        "Please enter effective date.",
        	        "Warning"
        	);
            return;
        }

        try {
            int lotId = (Integer) allLots.get(lotIndex)[0];
            int priceListId = (Integer) allPriceLists.get(priceListIndex)[0];
            Date effectiveDate = Date.valueOf(dateText);

            if (controller.existsPriceListForDate(lotId, effectiveDate)) {
            	StyledMessageDialog.showMessage(
            	        this,
            	        "A price list already exists for this date.",
            	        "Duplicate"
            	);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Apply this price list to the selected parking lot?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            boolean success = controller.adaptPriceListToLot(lotId, priceListId, effectiveDate);

            if (success) {
            	StyledMessageDialog.showMessage(
            	        this,
            	        "✅ Price list successfully applied to the selected parking lot.",
            	        "Success"
            	);
                refreshForSelectedLot();
                effectiveDateField.setText("");
                if (priceListComboBox.getItemCount() > 0) {
                    priceListComboBox.setSelectedIndex(0);
                }
            } else {
            	StyledMessageDialog.showMessage(
            	        this,
            	        "❌ Failed to adapt price list.",
            	        "Error"
            	);
            }

        } catch (IllegalArgumentException ex) {
        	StyledMessageDialog.showMessage(
        	        this,
        	        "Date must be in format yyyy-mm-dd.",
        	        "Invalid Input"
        	);
        } catch (Exception ex) {
            ex.printStackTrace();
            StyledMessageDialog.showMessage(
                    this,
                    "Unexpected error occurred.",
                    "Error"
            );
        }
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(primary ? MATCHA_2 : Color.WHITE);
        button.setForeground(primary ? Color.WHITE : MATCHA_2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdaptPriceListFrame().setVisible(true));
    }
}
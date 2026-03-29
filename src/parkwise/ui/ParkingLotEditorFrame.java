package parkwise.ui;

import parkwise.controller.ParkingLotController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParkingLotEditorFrame extends JFrame {

    private final RoundedTextField lotIdField = new RoundedTextField();
    private final RoundedTextField nameField = new RoundedTextField();
    private final RoundedTextField addressField = new RoundedTextField();
    private final RoundedTextField cityField = new RoundedTextField();
    private final RoundedTextField maxSpacesField = new RoundedTextField();
    private final RoundedTextField availableSpacesField = new RoundedTextField();

    private final RoundedTextField updateDateField = new RoundedTextField();
    private final RoundedTextField firstHourPriceField = new RoundedTextField();
    private final RoundedTextField additionalHourPriceField = new RoundedTextField();
    private final RoundedTextField fullDayPriceField = new RoundedTextField();

    private final RoundedTextField searchLotField = new RoundedTextField();

    private final RoundedButton previousButton;
    private final RoundedButton nextButton;
    private final RoundedButton addButton;
    private final RoundedButton removeButton;
    private final RoundedButton saveButton;
    private final RoundedButton searchLotButton;

    private final RoundedButton turnOnConveyorsButton;
    private final RoundedButton turnOffConveyorsButton;
    private final RoundedButton turnOnThisLotButton;
    private final RoundedButton turnOffThisLotButton;
    private final RoundedButton manageConveyorsButton;

    private final RoundedButton viewPriceHistoryButton;
    private final RoundedButton adaptPriceListButton;

    private final ParkingLotController controller = new ParkingLotController();
    private List<Object[]> parkingLots;
    private int currentIndex = 0;

    private boolean allConveyorsOn = false;
    private final Map<Integer, Boolean> lotConveyorStates = new HashMap<>();

    private final Color OFF_WHITE = new Color(249, 248, 244);
    private final Color MATCHA_2 = new Color(92, 146, 82);
    private final Color TEXT_DARK = new Color(28, 28, 28);

    public ParkingLotEditorFrame() {
        setTitle("Manage Parking Lot");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        previousButton = new RoundedButton("Previous Lot", MATCHA_2, false);
        nextButton = new RoundedButton("Next Lot", MATCHA_2, true);
        addButton = new RoundedButton("Add New Lot", MATCHA_2, false);
        removeButton = new RoundedButton("Remove Lot", MATCHA_2, false);
        saveButton = new RoundedButton("Save", MATCHA_2, true);
        searchLotButton = new RoundedButton("Search", MATCHA_2, true);

        turnOnConveyorsButton = new RoundedButton("Turn On All Conveyors", MATCHA_2, false);
        turnOffConveyorsButton = new RoundedButton("Turn Off All Conveyors", MATCHA_2, false);
        turnOnThisLotButton = new RoundedButton("Turn On This Lot", MATCHA_2, false);
        turnOffThisLotButton = new RoundedButton("Turn Off This Lot", MATCHA_2, false);
        manageConveyorsButton = new RoundedButton("Manage Conveyors", MATCHA_2, true);

        viewPriceHistoryButton = new RoundedButton("View Price History", MATCHA_2, false);
        adaptPriceListButton = new RoundedButton("Adapt New Price List", MATCHA_2, true);

        lotIdField.setEditable(false);
        updateDateField.setEditable(false);
        firstHourPriceField.setEditable(false);
        additionalHourPriceField.setEditable(false);
        fullDayPriceField.setEditable(false);
        availableSpacesField.setEditable(false);

        styleField(lotIdField);
        styleField(nameField);
        styleField(addressField);
        styleField(cityField);
        styleField(maxSpacesField);
        styleField(availableSpacesField);
        styleField(updateDateField);
        styleField(firstHourPriceField);
        styleField(additionalHourPriceField);
        styleField(fullDayPriceField);
        styleField(searchLotField);

        setContentPane(createMainPanel());
        loadParkingLots();

        addButton.addActionListener(e -> addNewLot());
        saveButton.addActionListener(e -> saveCurrentLot());
        removeButton.addActionListener(e -> removeCurrentLot());
        searchLotButton.addActionListener(e -> searchParkingLot());

        turnOnConveyorsButton.addActionListener(e -> turnOnAllConveyors());
        turnOffConveyorsButton.addActionListener(e -> turnOffAllConveyors());
        turnOnThisLotButton.addActionListener(e -> turnOnCurrentLotConveyors());
        turnOffThisLotButton.addActionListener(e -> turnOffCurrentLotConveyors());
        manageConveyorsButton.addActionListener(e -> openManageConveyorFrame());
        viewPriceHistoryButton.addActionListener(e -> showPriceHistory());
        adaptPriceListButton.addActionListener(e -> adaptPriceList());
        
        updateConveyorButtonsState();
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(OFF_WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(OFF_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Manage Parking Lot", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel center = new JPanel(new GridLayout(1, 2, 24, 0));
        center.setOpaque(false);
        center.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));

        center.add(createSectionPanel("Parking Lot Details", createLotDetailsPanel()));
        center.add(createSectionPanel("Current Price List", createPricePanel()));

        previousButton.addActionListener(e -> showPreviousLot());
        nextButton.addActionListener(e -> showNextLot());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(24, 0, 0, 0));
        bottom.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel navSection = createLabeledButtonSection(
                "Navigation",
                previousButton,
                nextButton
        );

        JPanel lotSection = createLabeledButtonSection(
                "Lot Management",
                addButton,
                removeButton,
                saveButton
        );

        JPanel pricingSection = createLabeledButtonSection(
                "Pricing",
                viewPriceHistoryButton,
                adaptPriceListButton
        );

        JPanel conveyorSection = createLabeledButtonSection(
                "Conveyors",
                turnOnConveyorsButton,
                turnOffConveyorsButton,
                turnOnThisLotButton,
                turnOffThisLotButton,
                manageConveyorsButton
        );

        bottom.add(navSection);
        bottom.add(Box.createVerticalStrut(16));
        bottom.add(lotSection);
        bottom.add(Box.createVerticalStrut(16));
        bottom.add(pricingSection);
        bottom.add(Box.createVerticalStrut(16));
        bottom.add(conveyorSection);

        content.add(title);
        content.add(createSearchPanel());
        content.add(Box.createVerticalStrut(16));
        content.add(center);
        content.add(bottom);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(OFF_WHITE);

        root.add(scrollPane, BorderLayout.CENTER);
        return root;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel("Find Parking Lot by ID:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_DARK);

        searchLotField.setPreferredSize(new Dimension(120, 36));
        searchLotButton.setPreferredSize(new Dimension(120, 40));

        panel.add(label);
        panel.add(searchLotField);
        panel.add(searchLotButton);

        return panel;
    }

    private JPanel createLabeledButtonSection(String titleText, JButton... buttons) {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        row.setOpaque(false);

        for (JButton button : buttons) {
            button.setPreferredSize(new Dimension(230, 46));
            row.add(button);
        }

        section.add(title);
        section.add(Box.createVerticalStrut(6));
        section.add(row);

        return section;
    }

    private JPanel createSectionPanel(String titleText, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 210), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        panel.add(title, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLotDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 14);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, gbc, 0, "Lot ID:", lotIdField);
        addField(panel, gbc, 1, "Name:", nameField);
        addField(panel, gbc, 2, "Address:", addressField);
        addField(panel, gbc, 3, "City:", cityField);
        addField(panel, gbc, 4, "Max Spaces:", maxSpacesField);
        addField(panel, gbc, 5, "Available Spaces:", availableSpacesField);

        return panel;
    }

    private JPanel createPricePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 14);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, gbc, 0, "Update Date:", updateDateField);
        addField(panel, gbc, 1, "Price - First Hour:", firstHourPriceField);
        addField(panel, gbc, 2, "Price - Additional Hour:", additionalHourPriceField);
        addField(panel, gbc, 3, "Price - Full Day:", fullDayPriceField);

        return panel;
    }

    private void showPriceHistory() {
        try {
            int lotId = Integer.parseInt(lotIdField.getText().trim());
            List<Object[]> history = controller.getPriceHistory(lotId);

            if (history == null || history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No price history found for this parking lot.");
                return;
            }

            String[] columns = {
                    "Effective Date",
                    "First Hour Price",
                    "Additional Hour Price",
                    "Full Day Price"
            };

            Object[][] data = new Object[history.size()][4];

            for (int i = 0; i < history.size(); i++) {
                Object[] row = history.get(i);
                data[i][0] = row[0];
                data[i][1] = row[1];
                data[i][2] = row[2];
                data[i][3] = row[3];
            }

            JTable table = new JTable(data, columns);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.setRowHeight(28);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(92, 146, 82));
            table.getTableHeader().setForeground(Color.WHITE);
            table.setGridColor(new Color(220, 220, 220));
            table.setSelectionBackground(new Color(220, 235, 220));
            table.setEnabled(false);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(700, 250));
            scrollPane.getViewport().setBackground(Color.WHITE);

            JOptionPane.showMessageDialog(
                    this,
                    scrollPane,
                    "Price History - Lot " + lotId,
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to load price history.");
        }
    }

    private void adaptPriceList() {
        String[] options = {"Manual Entry", "Import From JSON", "Cancel"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose how to adapt a new price list:",
                "Adapt New Price List",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            addNewPriceListManual();
        } else if (choice == 1) {
            addNewPriceListFromJson();
        }
    }

    private void addNewPriceListManual() {
        try {
            int lotId = Integer.parseInt(lotIdField.getText().trim());

            String effectiveDate = JOptionPane.showInputDialog(this, "Enter effective date (yyyy-mm-dd):");
            if (effectiveDate == null || effectiveDate.trim().isEmpty()) {
                return;
            }

            String firstHourText = JOptionPane.showInputDialog(this, "Enter first hour price:");
            if (firstHourText == null || firstHourText.trim().isEmpty()) {
                return;
            }

            String additionalHourText = JOptionPane.showInputDialog(this, "Enter additional hour price:");
            if (additionalHourText == null || additionalHourText.trim().isEmpty()) {
                return;
            }

            String fullDayText = JOptionPane.showInputDialog(this, "Enter full day price:");
            if (fullDayText == null || fullDayText.trim().isEmpty()) {
                return;
            }

            double firstHour = Double.parseDouble(firstHourText.trim());
            double additionalHour = Double.parseDouble(additionalHourText.trim());
            double fullDay = Double.parseDouble(fullDayText.trim());

            if (firstHour < 0 || additionalHour < 0 || fullDay < 0) {
                JOptionPane.showMessageDialog(this, "Prices must be positive numbers.");
                return;
            }

            boolean success = controller.addPriceList(
                    lotId,
                    effectiveDate.trim(),
                    firstHour,
                    additionalHour,
                    fullDay
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "New price list added successfully.");
                displayCurrentLot();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add new price list.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prices must be valid numbers.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add new price list.");
        }
    }

    private void addNewPriceListFromJson() {
        try {
            int lotId = Integer.parseInt(lotIdField.getText().trim());

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose IRS JSON file");

            int result = chooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();
            String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);

            List<ImportedPriceItem> importedItems = parsePriceListsFromJson(json);

            if (importedItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No valid price lists found in JSON.");
                return;
            }

            String[] options = new String[importedItems.size()];
            for (int i = 0; i < importedItems.size(); i++) {
                ImportedPriceItem item = importedItems.get(i);
                options[i] = "Date: " + item.effectiveDate
                        + " | First Hour: " + item.firstHour
                        + " | Additional Hour: " + item.additionalHour
                        + " | Full Day: " + item.fullDay;
            }

            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a price list to adapt to this parking lot:",
                    "Imported Price Lists",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (selected == null) {
                return;
            }

            int selectedIndex = -1;
            for (int i = 0; i < options.length; i++) {
                if (options[i].equals(selected)) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "No price list selected.");
                return;
            }

            ImportedPriceItem chosenItem = importedItems.get(selectedIndex);

            boolean success = controller.addPriceList(
                    lotId,
                    chosenItem.effectiveDate,
                    chosenItem.firstHour,
                    chosenItem.additionalHour,
                    chosenItem.fullDay
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "JSON price list adapted successfully.");
                displayCurrentLot();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to adapt JSON price list.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to import JSON file.");
        }
    }

    private List<ImportedPriceItem> parsePriceListsFromJson(String json) {
        List<ImportedPriceItem> items = new ArrayList<>();

        String updateDate = extractJsonString(json, "updateDate");
        if (updateDate == null || updateDate.isEmpty()) {
            updateDate = "2026-01-01";
        }

        Pattern itemPattern = Pattern.compile(
                "\\{\\s*\"priceFirstHour\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)\\s*,\\s*"
                        + "\"priceAdditionalHour\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)\\s*,\\s*"
                        + "\"priceFullDay\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)\\s*\\}"
        );

        Matcher matcher = itemPattern.matcher(json);

        while (matcher.find()) {
            double firstHour = Double.parseDouble(matcher.group(1));
            double additionalHour = Double.parseDouble(matcher.group(2));
            double fullDay = Double.parseDouble(matcher.group(3));

            items.add(new ImportedPriceItem(updateDate, firstHour, additionalHour, fullDay));
        }

        return items;
    }

    private String extractJsonString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static class ImportedPriceItem {
        private final String effectiveDate;
        private final double firstHour;
        private final double additionalHour;
        private final double fullDay;

        private ImportedPriceItem(String effectiveDate, double firstHour, double additionalHour, double fullDay) {
            this.effectiveDate = effectiveDate;
            this.firstHour = firstHour;
            this.additionalHour = additionalHour;
            this.fullDay = fullDay;
        }
    }

    private void turnOnAllConveyors() {
        allConveyorsOn = true;
        updateConveyorButtonsState();
        JOptionPane.showMessageDialog(this, "All parking lots conveyors turned ON");
    }

    private void turnOffAllConveyors() {
        allConveyorsOn = false;
        updateConveyorButtonsState();
        JOptionPane.showMessageDialog(this, "All parking lots conveyors turned OFF");
    }

    private void turnOnCurrentLotConveyors() {
        if (parkingLots == null || parkingLots.isEmpty()) {
            return;
        }

        int lotId = Integer.parseInt(lotIdField.getText().trim());
        lotConveyorStates.put(lotId, true);
        updateConveyorButtonsState();
        JOptionPane.showMessageDialog(this, "Conveyors turned ON for lot " + lotId);
    }

    private void turnOffCurrentLotConveyors() {
        if (parkingLots == null || parkingLots.isEmpty()) {
            return;
        }

        int lotId = Integer.parseInt(lotIdField.getText().trim());
        lotConveyorStates.put(lotId, false);
        updateConveyorButtonsState();
        JOptionPane.showMessageDialog(this, "Conveyors turned OFF for lot " + lotId);
    }

    private void updateConveyorButtonsState() {
        turnOnConveyorsButton.setEnabled(!allConveyorsOn);
        turnOffConveyorsButton.setEnabled(allConveyorsOn);

        if (parkingLots == null || parkingLots.isEmpty() || lotIdField.getText().trim().isEmpty()) {
            turnOnThisLotButton.setEnabled(false);
            turnOffThisLotButton.setEnabled(false);
            return;
        }

        int lotId = Integer.parseInt(lotIdField.getText().trim());
        boolean thisLotOn = lotConveyorStates.getOrDefault(lotId, false);

        turnOnThisLotButton.setEnabled(!thisLotOn);
        turnOffThisLotButton.setEnabled(thisLotOn);
    }

    private void searchParkingLot() {
        try {
            int lotId = Integer.parseInt(searchLotField.getText().trim());

            for (int i = 0; i < parkingLots.size(); i++) {
                int currentLotId = Integer.parseInt(String.valueOf(parkingLots.get(i)[0]));
                if (currentLotId == lotId) {
                    currentIndex = i;
                    displayCurrentLot();
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Parking lot not found.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid lot ID.");
        }
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_DARK);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(340, 42));
        panel.add(field, gbc);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(252, 252, 252));
        field.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
    }

    private void loadParkingLots() {
        parkingLots = controller.getAllParkingLots();

        if (parkingLots == null || parkingLots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No parking lots found.");
            updateConveyorButtonsState();
            return;
        }

        for (Object[] lot : parkingLots) {
            int lotId = Integer.parseInt(String.valueOf(lot[0]));
            lotConveyorStates.putIfAbsent(lotId, false);
        }

        currentIndex = 0;
        displayCurrentLot();
    }

    private void displayCurrentLot() {
        Object[] lot = parkingLots.get(currentIndex);

        lotIdField.setText(String.valueOf(lot[0]));
        nameField.setText(String.valueOf(lot[1]));
        addressField.setText(String.valueOf(lot[2]));
        cityField.setText(String.valueOf(lot[3]));
        maxSpacesField.setText(String.valueOf(lot[4]));
        availableSpacesField.setText(String.valueOf(lot[5]));

        int lotId = Integer.parseInt(String.valueOf(lot[0]));
        Object[] price = controller.getCurrentPriceListForLot(lotId);

        if (price != null) {
            updateDateField.setText(String.valueOf(price[0]));
            firstHourPriceField.setText(String.valueOf(price[1]));
            additionalHourPriceField.setText(String.valueOf(price[2]));
            fullDayPriceField.setText(String.valueOf(price[3]));
        } else {
            updateDateField.setText("");
            firstHourPriceField.setText("");
            additionalHourPriceField.setText("");
            fullDayPriceField.setText("");
        }

        previousButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < parkingLots.size() - 1);

        updateConveyorButtonsState();
    }

    private void showPreviousLot() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentLot();
        }
    }

    private void showNextLot() {
        if (currentIndex < parkingLots.size() - 1) {
            currentIndex++;
            displayCurrentLot();
        }
    }

    private void addNewLot() {
        String name = JOptionPane.showInputDialog(this, "Enter parking lot name:");
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String address = JOptionPane.showInputDialog(this, "Enter address:");
        if (address == null || address.trim().isEmpty()) {
            return;
        }

        String city = JOptionPane.showInputDialog(this, "Enter city:");
        if (city == null || city.trim().isEmpty()) {
            return;
        }

        String maxSpacesText = JOptionPane.showInputDialog(this, "Enter max spaces:");
        if (maxSpacesText == null || maxSpacesText.trim().isEmpty()) {
            return;
        }

        String availableSpacesText = JOptionPane.showInputDialog(this, "Enter available spaces:");
        if (availableSpacesText == null || availableSpacesText.trim().isEmpty()) {
            return;
        }

        try {
            int maxSpaces = Integer.parseInt(maxSpacesText.trim());
            int availableSpaces = Integer.parseInt(availableSpacesText.trim());

            if (maxSpaces < 0 || availableSpaces < 0 || availableSpaces > maxSpaces) {
                JOptionPane.showMessageDialog(this, "Invalid spaces values.");
                return;
            }

            boolean success = controller.addParkingLot(name.trim(), address.trim(), city.trim(), maxSpaces, availableSpaces);

            if (success) {
                JOptionPane.showMessageDialog(this, "Parking lot added successfully.");
                loadParkingLots();
                currentIndex = parkingLots.size() - 1;
                displayCurrentLot();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add parking lot.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Max spaces and available spaces must be valid numbers.");
        }
    }

    private void saveCurrentLot() {
        try {
            int lotId = Integer.parseInt(lotIdField.getText().trim());
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String city = cityField.getText().trim();
            int maxSpaces = Integer.parseInt(maxSpacesField.getText().trim());
            int availableSpaces = Integer.parseInt(availableSpacesField.getText().trim());

            if (name.isEmpty() || address.isEmpty() || city.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all parking lot details.");
                return;
            }

            if (maxSpaces < 0 || availableSpaces < 0 || availableSpaces > maxSpaces) {
                JOptionPane.showMessageDialog(this, "Available spaces cannot exceed max spaces.");
                return;
            }

            boolean success = controller.updateParkingLot(lotId, name, address, city, maxSpaces, availableSpaces);

            if (success) {
                JOptionPane.showMessageDialog(this, "Parking lot updated successfully.");
                loadParkingLots();

                for (int i = 0; i < parkingLots.size(); i++) {
                    if (Integer.parseInt(String.valueOf(parkingLots.get(i)[0])) == lotId) {
                        currentIndex = i;
                        break;
                    }
                }

                displayCurrentLot();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update parking lot.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Max spaces must be a valid number.");
        }
    }

    private void removeCurrentLot() {
        if (parkingLots == null || parkingLots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No parking lot to remove.");
            return;
        }

        int lotId = Integer.parseInt(lotIdField.getText().trim());

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this parking lot?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = controller.removeParkingLot(lotId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Parking lot removed successfully.");

            loadParkingLots();

            if (parkingLots == null || parkingLots.isEmpty()) {
                lotIdField.setText("");
                nameField.setText("");
                addressField.setText("");
                cityField.setText("");
                maxSpacesField.setText("");
                availableSpacesField.setText("");
                updateDateField.setText("");
                firstHourPriceField.setText("");
                additionalHourPriceField.setText("");
                fullDayPriceField.setText("");
                searchLotField.setText("");
                currentIndex = 0;
                updateConveyorButtonsState();
                return;
            }

            if (currentIndex >= parkingLots.size()) {
                currentIndex = parkingLots.size() - 1;
            }

            displayCurrentLot();

        } else {
            JOptionPane.showMessageDialog(this, "Failed to remove parking lot.");
        }
    }

    static class RoundedTextField extends JTextField {
        private final int ARC = 18;
        private final Color stroke = new Color(160, 175, 160);

        RoundedTextField() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, w - 1, h - 1, ARC, ARC);

                super.paintComponent(g2);

                g2.setColor(stroke);
                g2.drawRoundRect(0, 0, w - 1, h - 1, ARC, ARC);
            } finally {
                g2.dispose();
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
        }

        @Override
        public Insets getInsets() {
            Insets i = super.getInsets();
            return new Insets(i.top, i.left + 2, i.bottom, i.right + 2);
        }
    }

    static class RoundedButton extends JButton {
        private final Color accent;
        private final boolean primary;
        private boolean hover = false;

        RoundedButton(String text, Color accent, boolean primary) {
            super(text);
            this.accent = accent;
            this.primary = primary;

            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(170, 46));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                if (primary) {
                    g2.setColor(hover ? accent.darker() : accent);
                    g2.fillRoundRect(0, 0, w, h, 18, 18);
                    setForeground(Color.WHITE);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, w, h, 18, 18);
                    g2.setColor(accent);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
                    setForeground(accent);
                }

                super.paintComponent(g2);
            } finally {
                g2.dispose();
            }
        }
    }
    private void openManageConveyorFrame() {
        if (lotIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No parking lot selected.");
            return;
        }

        int lotId = Integer.parseInt(lotIdField.getText().trim());
        new ManageConveyorFrame(lotId).setVisible(true);
    }
}
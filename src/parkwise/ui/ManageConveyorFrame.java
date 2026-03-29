package parkwise.ui;

import parkwise.controller.ConveyorController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ManageConveyorFrame extends JFrame {

    private final RoundedTextField conveyorIdField = new RoundedTextField();
    private final RoundedTextField floorField = new RoundedTextField();
    private final RoundedTextField xField = new RoundedTextField();
    private final RoundedTextField yField = new RoundedTextField();
    private final RoundedTextField maxWeightField = new RoundedTextField();

    private final RoundedTextField statusField = new RoundedTextField();
    private final RoundedTextField electronicField = new RoundedTextField();
    private final RoundedTextField mechanicalField = new RoundedTextField();
    private final RoundedTextField timerField = new RoundedTextField();

    private final RoundedTextField searchLotField = new RoundedTextField();

    private final RoundedButton previousButton;
    private final RoundedButton nextButton;
    private final RoundedButton addNewButton;
    private final RoundedButton saveButton;
    private final RoundedButton resetButton;
    private final RoundedButton changeLocationButton;
    private final RoundedButton startCheckButton;
    private final RoundedButton filterLotButton;
    private final RoundedButton showAllButton;

    private final ConveyorController controller = new ConveyorController();

    private final int lotId;
    private List<Object[]> allConveyors = new ArrayList<>();
    private List<Object[]> conveyors = new ArrayList<>();
    private int currentIndex = 0;
    private Integer currentFilterLotId = null;

    private Timer integrityTimer;
    private int remainingSeconds = 0;

    private final Color OFF_WHITE = new Color(249, 248, 244);
    private final Color MATCHA_2 = new Color(92, 146, 82);
    private final Color TEXT_DARK = new Color(28, 28, 28);

    public ManageConveyorFrame(int lotId) {
        this.lotId = lotId;

        setTitle("Manage Conveyor");
        setSize(980, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        previousButton = new RoundedButton("Previous", MATCHA_2, false);
        nextButton = new RoundedButton("Next", MATCHA_2, true);
        addNewButton = new RoundedButton("Add New", MATCHA_2, false);
        saveButton = new RoundedButton("Save", MATCHA_2, true);
        resetButton = new RoundedButton("Reset / Restart", MATCHA_2, false);
        changeLocationButton = new RoundedButton("Change Conveyor Location", MATCHA_2, false);
        startCheckButton = new RoundedButton("Start Integrity Check", MATCHA_2, true);
        filterLotButton = new RoundedButton("Filter by Lot", MATCHA_2, false);
        showAllButton = new RoundedButton("Show All", MATCHA_2, true);

        conveyorIdField.setEditable(false);
        statusField.setEditable(false);
        electronicField.setEditable(false);
        mechanicalField.setEditable(false);
        timerField.setEditable(false);

        styleField(conveyorIdField);
        styleField(floorField);
        styleField(xField);
        styleField(yField);
        styleField(maxWeightField);
        styleField(statusField);
        styleField(electronicField);
        styleField(mechanicalField);
        styleField(timerField);
        styleField(searchLotField);

        setContentPane(createMainPanel());

        previousButton.addActionListener(e -> showPrevious());
        nextButton.addActionListener(e -> showNext());
        addNewButton.addActionListener(e -> addNewConveyor());
        saveButton.addActionListener(e -> saveCurrentConveyor());
        resetButton.addActionListener(e -> restartCurrentConveyor());
        changeLocationButton.addActionListener(e -> changeConveyorLocation());
        startCheckButton.addActionListener(e -> startIntegrityCheckForAll());
        filterLotButton.addActionListener(e -> filterByLot());
        showAllButton.addActionListener(e -> showAllConveyors());

        loadConveyors();
        updateButtonsState();
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(OFF_WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(OFF_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Manage Conveyor", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel mainCard = new JPanel(new BorderLayout(0, 18));
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 210), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        mainCard.add(createDetailsPanel(), BorderLayout.NORTH);
        mainCard.add(createStatusPanel(), BorderLayout.CENTER);
        mainCard.add(createButtonsPanel(), BorderLayout.SOUTH);

        content.add(title);
        content.add(createSearchPanel());
        content.add(Box.createVerticalStrut(16));
        content.add(mainCard);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(OFF_WHITE);

        root.add(scrollPane, BorderLayout.CENTER);
        return root;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        panel.setOpaque(false);

        JLabel label = new JLabel("Manage Lot Conveyors:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_DARK);

        searchLotField.setPreferredSize(new Dimension(120, 38));
        filterLotButton.setPreferredSize(new Dimension(150, 42));
        showAllButton.setPreferredSize(new Dimension(130, 42));

        panel.add(label);
        panel.add(searchLotField);
        panel.add(filterLotButton);
        panel.add(showAllButton);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 14);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, gbc, 0, "Conveyor Number:", conveyorIdField);
        addField(panel, gbc, 1, "Floor Number:", floorField);
        addField(panel, gbc, 2, "X Position:", xField);
        addField(panel, gbc, 3, "Y Position:", yField);
        addField(panel, gbc, 4, "Max Weight:", maxWeightField);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(12, 0, 12, 0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 210), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel title = new JLabel("Current Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 14);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(fields, gbc, 0, "Status:", statusField);
        addField(fields, gbc, 1, "Electronic Check:", electronicField);
        addField(fields, gbc, 2, "Mechanical Check:", mechanicalField);
        addField(fields, gbc, 3, "Checking Timer:", timerField);

        panel.add(title, BorderLayout.NORTH);
        panel.add(fields, BorderLayout.CENTER);

        outer.add(panel, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createButtonsPanel() {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        row1.setOpaque(false);
        row1.add(previousButton);
        row1.add(nextButton);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        row2.setOpaque(false);
        row2.add(addNewButton);
        row2.add(saveButton);
        row2.add(changeLocationButton);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        row3.setOpaque(false);
        row3.add(resetButton);
        row3.add(startCheckButton);

        previousButton.setPreferredSize(new Dimension(190, 46));
        nextButton.setPreferredSize(new Dimension(190, 46));
        addNewButton.setPreferredSize(new Dimension(190, 46));
        saveButton.setPreferredSize(new Dimension(190, 46));
        resetButton.setPreferredSize(new Dimension(190, 46));
        changeLocationButton.setPreferredSize(new Dimension(260, 52));
        startCheckButton.setPreferredSize(new Dimension(220, 46));

        container.add(row1);
        container.add(row2);
        container.add(row3);

        return container;
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
        field.setPreferredSize(new Dimension(360, 42));
        panel.add(field, gbc);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(252, 252, 252));
        field.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
    }

    private void loadConveyors() {
        refreshConveyorLists(null);
    }

    private void refreshConveyorLists(Integer preferredConveyorId) {
        allConveyors = controller.getAllConveyors();

        if (currentFilterLotId == null) {
            conveyors = new ArrayList<>(allConveyors);
        } else {
            conveyors = new ArrayList<>();
            for (Object[] conveyor : allConveyors) {
                int conveyorLotId = Integer.parseInt(String.valueOf(conveyor[1]));
                if (conveyorLotId == currentFilterLotId) {
                    conveyors.add(conveyor);
                }
            }
        }

        if (conveyors == null || conveyors.isEmpty()) {
            currentIndex = 0;
            clearFields();
            updateWindowTitle();
            return;
        }

        if (preferredConveyorId != null) {
            boolean found = false;
            for (int i = 0; i < conveyors.size(); i++) {
                int conveyorId = Integer.parseInt(String.valueOf(conveyors.get(i)[0]));
                if (conveyorId == preferredConveyorId) {
                    currentIndex = i;
                    found = true;
                    break;
                }
            }
            if (!found) {
                currentIndex = 0;
            }
        } else {
            if (currentIndex >= conveyors.size()) {
                currentIndex = conveyors.size() - 1;
            }
            if (currentIndex < 0) {
                currentIndex = 0;
            }
        }

        updateWindowTitle();
        displayCurrentConveyor();
    }

    private void updateWindowTitle() {
        if (currentFilterLotId == null) {
            setTitle("Manage Conveyor - All Conveyors");
        } else {
            setTitle("Manage Conveyor - Lot " + currentFilterLotId);
        }
    }

    private void displayCurrentConveyor() {
        Object[] c = conveyors.get(currentIndex);

        conveyorIdField.setText(String.valueOf(c[0]));
        floorField.setText(String.valueOf(c[2]));
        xField.setText(String.valueOf(c[3]));
        yField.setText(String.valueOf(c[4]));
        maxWeightField.setText(String.valueOf(c[5]));
        statusField.setText(String.valueOf(c[6]));
        electronicField.setText(String.valueOf(c[7]));
        mechanicalField.setText(String.valueOf(c[8]));
        timerField.setText(String.valueOf(c[9]));

        String state = String.valueOf(c[6]);
        maxWeightField.setEditable("OFF".equalsIgnoreCase(state));

        updateButtonsState();
    }

    private void clearFields() {
        conveyorIdField.setText("");
        floorField.setText("");
        xField.setText("");
        yField.setText("");
        maxWeightField.setText("");
        statusField.setText("");
        electronicField.setText("");
        mechanicalField.setText("");
        timerField.setText("");
        maxWeightField.setEditable(false);

        updateButtonsState();
    }

    private void updateButtonsState() {
        boolean hasConveyors = conveyors != null && !conveyors.isEmpty();
        boolean hasSelectedConveyor = hasConveyors && !conveyorIdField.getText().trim().isEmpty();

        addNewButton.setEnabled(true);
        changeLocationButton.setEnabled(true);
        filterLotButton.setEnabled(true);
        showAllButton.setEnabled(true);

        if (!hasSelectedConveyor) {
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            saveButton.setEnabled(false);
            resetButton.setEnabled(false);
            startCheckButton.setEnabled(false);
            return;
        }

        String state = statusField.getText().trim();

        previousButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < conveyors.size() - 1);
        saveButton.setEnabled(!"ROUTING".equalsIgnoreCase(state));
        resetButton.setEnabled("PAUSE".equalsIgnoreCase(state));
        startCheckButton.setEnabled("OFF".equalsIgnoreCase(state) || "PAUSE".equalsIgnoreCase(state));
    }

    private void filterByLot() {
        try {
            String text = searchLotField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a parking lot number.");
                return;
            }

            int filterLotId = Integer.parseInt(text);
            if (filterLotId <= 0) {
                JOptionPane.showMessageDialog(this, "Parking lot number must be positive.");
                return;
            }

            currentFilterLotId = filterLotId;
            refreshConveyorLists(null);

            if (conveyors == null || conveyors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No conveyors found for lot " + filterLotId + ".");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid parking lot number.");
        }
    }

    private void showAllConveyors() {
        currentFilterLotId = null;
        searchLotField.setText("");
        refreshConveyorLists(null);
    }

    private void showPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentConveyor();
        }
    }

    private void showNext() {
        if (currentIndex < conveyors.size() - 1) {
            currentIndex++;
            displayCurrentConveyor();
        }
    }

    private void addNewConveyor() {
        try {
            JTextField lotField = new JTextField(
                    currentFilterLotId != null ? String.valueOf(currentFilterLotId) : String.valueOf(lotId)
            );
            JTextField floorFieldInput = new JTextField();
            JTextField xFieldInput = new JTextField();
            JTextField yFieldInput = new JTextField();
            JTextField maxWeightFieldInput = new JTextField();

            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.add(new JLabel("Parking Lot:"));
            panel.add(lotField);
            panel.add(new JLabel("Floor Number:"));
            panel.add(floorFieldInput);
            panel.add(new JLabel("X Position:"));
            panel.add(xFieldInput);
            panel.add(new JLabel("Y Position:"));
            panel.add(yFieldInput);
            panel.add(new JLabel("Max Weight:"));
            panel.add(maxWeightFieldInput);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Add New Conveyor",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            int newLotId = Integer.parseInt(lotField.getText().trim());
            int floor = Integer.parseInt(floorFieldInput.getText().trim());
            double x = Double.parseDouble(xFieldInput.getText().trim());
            double y = Double.parseDouble(yFieldInput.getText().trim());
            double maxWeight = Double.parseDouble(maxWeightFieldInput.getText().trim());

            if (newLotId <= 0 || floor < 0 || x < 0 || y < 0 || maxWeight < 0) {
                JOptionPane.showMessageDialog(this, "Values must be positive.");
                return;
            }

            boolean success = controller.addConveyor(newLotId, floor, x, y, maxWeight);

            if (success) {
                JOptionPane.showMessageDialog(this, "Conveyor added successfully.");
                refreshConveyorLists(null);
                if (!conveyors.isEmpty()) {
                    currentIndex = conveyors.size() - 1;
                    displayCurrentConveyor();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add conveyor.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add conveyor.");
        }
    }

    private void saveCurrentConveyor() {
        if (conveyors == null || conveyors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No conveyor to save.");
            return;
        }

        try {
            int conveyorId = Integer.parseInt(conveyorIdField.getText().trim());
            int currentLotId = Integer.parseInt(String.valueOf(conveyors.get(currentIndex)[1]));
            int floor = Integer.parseInt(floorField.getText().trim());
            double x = Double.parseDouble(xField.getText().trim());
            double y = Double.parseDouble(yField.getText().trim());
            double maxWeight = Double.parseDouble(maxWeightField.getText().trim());

            String state = statusField.getText().trim();

            if (floor < 0 || x < 0 || y < 0 || maxWeight < 0) {
                JOptionPane.showMessageDialog(this, "Values must be positive.");
                return;
            }

            if ("ROUTING".equalsIgnoreCase(state)) {
                JOptionPane.showMessageDialog(this, "Cannot modify conveyor while routing.");
                return;
            }

            Object[] currentConveyor = conveyors.get(currentIndex);
            double oldMaxWeight = Double.parseDouble(String.valueOf(currentConveyor[5]));
            boolean weightChanged = Double.compare(maxWeight, oldMaxWeight) != 0;

            if (!"OFF".equalsIgnoreCase(state) && weightChanged) {
                JOptionPane.showMessageDialog(this, "Max weight can be changed only when conveyor is OFF.");
                return;
            }

            boolean success;
            if ("OFF".equalsIgnoreCase(state) && weightChanged) {
                success = controller.updateConveyorWithWeightChange(conveyorId, currentLotId, floor, x, y, maxWeight);
            } else {
                success = controller.updateConveyor(conveyorId, currentLotId, floor, x, y, maxWeight);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Conveyor updated successfully.");
                refreshConveyorLists(conveyorId);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update conveyor.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    private void restartCurrentConveyor() {
        if (conveyors == null || conveyors.isEmpty()) {
            return;
        }

        String state = statusField.getText().trim();
        if (!"PAUSE".equalsIgnoreCase(state)) {
            JOptionPane.showMessageDialog(this, "Reset / Restart is allowed only when conveyor is in PAUSE.");
            return;
        }

        int conveyorId = Integer.parseInt(conveyorIdField.getText().trim());
        boolean success = controller.restartConveyor(conveyorId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Conveyor restarted.");
            refreshConveyorLists(conveyorId);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to restart conveyor.");
        }
    }

    private void changeConveyorLocation() {
        try {
            String conveyorIdText = JOptionPane.showInputDialog(this, "Enter Conveyor Number:");
            if (conveyorIdText == null || conveyorIdText.trim().isEmpty()) {
                return;
            }

            String newLotIdText = JOptionPane.showInputDialog(this, "Enter New Parking Lot Number:");
            if (newLotIdText == null || newLotIdText.trim().isEmpty()) {
                return;
            }

            int conveyorId = Integer.parseInt(conveyorIdText.trim());
            int newLotId = Integer.parseInt(newLotIdText.trim());

            if (newLotId <= 0) {
                JOptionPane.showMessageDialog(this, "Parking lot number must be positive.");
                return;
            }

            boolean success = controller.changeConveyorLot(conveyorId, newLotId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Conveyor moved successfully to parking lot " + newLotId + ".");
                refreshConveyorLists(null);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to move conveyor.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to change conveyor location.");
        }
    }

    private Integer getCurrentSelectedConveyorId() {
        try {
            if (conveyorIdField.getText().trim().isEmpty()) {
                return null;
            }
            return Integer.parseInt(conveyorIdField.getText().trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private void startIntegrityCheckForAll() {
        boolean success = controller.startIntegrityCheckForAll();

        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to start integrity check for all conveyors.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Integrity check started for all conveyors.");

        if (integrityTimer != null && integrityTimer.isRunning()) {
            integrityTimer.stop();
        }

        remainingSeconds = 30;

        integrityTimer = new Timer(1000, e -> {
            remainingSeconds--;

            allConveyors = controller.getAllConveyors();

            for (Object[] conveyor : allConveyors) {
                int conveyorId = Integer.parseInt(String.valueOf(conveyor[0]));
                String state = String.valueOf(conveyor[6]);

                if (!"INTEGRITY_CHECK".equalsIgnoreCase(state)) {
                    continue;
                }

                if (remainingSeconds == 24) {
                    controller.updateMechanicalStatus(conveyorId, "OBSTRUCTIONS_CHECK");
                }

                if (remainingSeconds == 18) {
                    controller.updateMechanicalStatus(conveyorId, "ROLLERS_AND_DRIVE_CHECK");
                }

                if (remainingSeconds == 20) {
                    controller.updateElectronicStatus(conveyorId, "CONTROLLER_CHECK");
                }

                if (remainingSeconds == 12) {
                    controller.updateMechanicalStatus(conveyorId, "COMPLETE");
                }

                if (remainingSeconds == 10) {
                    controller.updateElectronicStatus(conveyorId, "COMPLETE");
                }

                String timerText = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60);
                controller.updateCheckTimer(conveyorId, timerText);

                if (remainingSeconds <= 0) {
                    controller.finishIntegrityCheck(conveyorId);
                }
            }

            refreshConveyorLists(getCurrentSelectedConveyorId());

            if (remainingSeconds <= 0) {
                integrityTimer.stop();
                JOptionPane.showMessageDialog(this, "Morning integrity check completed for all conveyors.");
                showFaultAlerts();
            }
        });

        integrityTimer.start();
        refreshConveyorLists(getCurrentSelectedConveyorId());
    }

    private void showFaultAlerts() {
        List<Object[]> faults = controller.getPausedConveyorsWithFaults();

        if (faults == null || faults.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Detected faults:\n\n");

        for (Object[] fault : faults) {
            sb.append("Conveyor ").append(fault[0])
              .append(" | Lot ").append(fault[1])
              .append(" | ").append(fault[2])
              .append(" | ").append(fault[3])
              .append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Conveyor Fault Alerts", JOptionPane.WARNING_MESSAGE);
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
}
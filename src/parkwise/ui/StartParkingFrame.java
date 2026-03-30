package parkwise.ui;

import parkwise.controller.ClientController;
import parkwise.controller.ParkingSessionController;
import parkwise.controller.VehicleController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

public class StartParkingFrame extends JFrame {

    public StartParkingFrame() {
        setTitle("ParkWise - Start Parking");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(new StartParkingPanel());
    }

    static class StartParkingPanel extends JPanel {

        private final Color OFF_WHITE = new Color(249, 248, 244);
        private final Color MATCHA_1 = new Color(123, 171, 106);
        private final Color MATCHA_2 = new Color(92, 146, 82);
        private final Color TEXT_DARK = new Color(28, 28, 28);
        private final Color TEXT_MUTED = new Color(90, 90, 90);

        private final RoundedTextField vehicleNumberField = new RoundedTextField();
        private final RoundedTextField phoneField = new RoundedTextField();
        private final RoundedTextField lotIdField = new RoundedTextField();

        private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);

        private final ClientController clientController = new ClientController();
        private final VehicleController vehicleController = new VehicleController();
        private final ParkingSessionController sessionController = new ParkingSessionController();

        StartParkingPanel() {
            setBackground(OFF_WHITE);
            setLayout(new GridBagLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setOpaque(false);
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(8, 0, 6, 0);

            JLabel vehicleLbl = new JLabel("Vehicle Number");
            vehicleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vehicleLbl.setForeground(new Color(60, 60, 60));
            formPanel.add(vehicleLbl, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            styleRoundedField(vehicleNumberField);
            vehicleNumberField.setColumns(28);
            formPanel.add(vehicleNumberField, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            gbc.insets = new Insets(14, 0, 6, 0);

            JLabel phoneLbl = new JLabel("Mobile Phone");
            phoneLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            phoneLbl.setForeground(new Color(60, 60, 60));
            formPanel.add(phoneLbl, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            gbc.insets = new Insets(8, 0, 6, 0);
            styleRoundedField(phoneField);
            phoneField.setColumns(28);
            formPanel.add(phoneField, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            gbc.insets = new Insets(14, 0, 6, 0);

            JLabel lotLbl = new JLabel("Parking Lot ID");
            lotLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lotLbl.setForeground(new Color(60, 60, 60));
            formPanel.add(lotLbl, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            gbc.insets = new Insets(8, 0, 6, 0);
            styleRoundedField(lotIdField);
            lotIdField.setColumns(28);
            formPanel.add(lotIdField, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(12, 2, 0, 0);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            messageLabel.setForeground(new Color(180, 60, 60));
            formPanel.add(messageLabel, gbc);

            RoundedButton cancelBtn = new RoundedButton("Close", MATCHA_2, false);
            RoundedButton startBtn = new RoundedButton("Start Parking", MATCHA_2, true);

            cancelBtn.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
            startBtn.addActionListener(e -> handleStartParking());

            vehicleNumberField.addActionListener(e -> phoneField.requestFocusInWindow());
            phoneField.addActionListener(e -> lotIdField.requestFocusInWindow());
            lotIdField.addActionListener(e -> handleStartParking());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            actions.setOpaque(false);
            actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));
            actions.add(cancelBtn);
            actions.add(startBtn);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

            JLabel title = new JLabel("Start Parking");
            title.setFont(new Font("Segoe UI", Font.BOLD, 26));
            title.setForeground(TEXT_DARK);

            JLabel subtitle = new JLabel("Enter vehicle details to begin a parking session");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitle.setForeground(TEXT_MUTED);

            JPanel titles = new JPanel();
            titles.setOpaque(false);
            titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
            titles.add(title);
            titles.add(Box.createVerticalStrut(6));
            titles.add(subtitle);

            JComponent icon = new IconBubble("🚗", MATCHA_1);
            header.add(icon, BorderLayout.WEST);
            header.add(titles, BorderLayout.CENTER);

            PremiumCard card = new PremiumCard(MATCHA_2, MATCHA_1);
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(620, 500));
            card.add(header, BorderLayout.NORTH);
            card.add(formPanel, BorderLayout.CENTER);
            card.add(actions, BorderLayout.SOUTH);

            add(card, new GridBagConstraints());
        }

        private void handleStartParking() {
            String vehicleNumber = vehicleNumberField.getText().trim();
            String phone = phoneField.getText().trim();
            String lotText = lotIdField.getText().trim();

            messageLabel.setText(" ");

            if (vehicleNumber.isEmpty() || phone.isEmpty() || lotText.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            int lotId;
            try {
                lotId = Integer.parseInt(lotText);
                if (lotId <= 0) {
                    messageLabel.setText("Parking Lot ID must be positive.");
                    return;
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Parking Lot ID must be a valid number.");
                return;
            }

            if (sessionController.hasActiveSession(vehicleNumber)) {
                JOptionPane.showMessageDialog(
                        this,
                        "This vehicle already has an active parking session.",
                        "Start Parking",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Object[] vehicle = vehicleController.getVehicleByNumber(vehicleNumber);

            if (vehicle != null) {
                int clientIdFromVehicle = (Integer) vehicle[5];
                Object[] client = clientController.getClientByPhone(phone);

                if (client == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Phone number not found for this existing vehicle.",
                            "Start Parking",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int clientIdFromPhone = (Integer) client[0];

                if (clientIdFromVehicle != clientIdFromPhone) {
                    JOptionPane.showMessageDialog(
                            this,
                            "This phone number does not match the vehicle owner.",
                            "Start Parking",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                boolean success = sessionController.startSession(vehicleNumber, lotId, clientIdFromPhone, phone);

                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Parking session started successfully.\nBarrier opened.\nSMS sent.",
                            "Start Parking",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to start parking session.",
                            "Start Parking",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                return;
            }

            handleNewVehicleAndClient(vehicleNumber, phone, lotId);
        }

        private void handleNewVehicleAndClient(String vehicleNumber, String phone, int lotId) {
            VehicleDetailsDialog dialog = new VehicleDetailsDialog(
                    SwingUtilities.getWindowAncestor(this),
                    MATCHA_2,
                    MATCHA_1,
                    OFF_WHITE
            );
            dialog.setVisible(true);

            if (!dialog.isConfirmed()) {
                return;
            }

            String firstName = dialog.getFirstName();
            String lastName = dialog.getLastName();
            String typeText = dialog.getTypeText();
            String color = dialog.getColor();
            String size = dialog.getVehicleSize();
            double weightKg = dialog.getWeightKg();

            Integer clientId = clientController.getClientIdByPhone(phone);

            if (clientId == null) {
                boolean clientAdded = clientController.addClient(firstName, lastName, phone);
                if (!clientAdded) {
                    StyledMessageDialog.showMessage(
                            SwingUtilities.getWindowAncestor(this),
                            "Failed to add client.",
                            "Start Parking",
                            MATCHA_2,
                            MATCHA_1,
                            OFF_WHITE
                    );
                    return;
                }

                clientId = clientController.getClientIdByPhone(phone);
                if (clientId == null) {
                    StyledMessageDialog.showMessage(
                            SwingUtilities.getWindowAncestor(this),
                            "Client was not created correctly.",
                            "Start Parking",
                            MATCHA_2,
                            MATCHA_1,
                            OFF_WHITE
                    );
                    return;
                }
            }

            Object[] existingVehicle = vehicleController.getVehicleByNumber(vehicleNumber);

            if (existingVehicle == null) {
                boolean vehicleAdded = vehicleController.addVehicle(
                        vehicleNumber,
                        typeText,
                        color.toUpperCase(),
                        size.toUpperCase(),
                        weightKg,
                        clientId
                );

                if (!vehicleAdded) {
                    StyledMessageDialog.showMessage(
                            SwingUtilities.getWindowAncestor(this),
                            "Failed to add vehicle.",
                            "Start Parking",
                            MATCHA_2,
                            MATCHA_1,
                            OFF_WHITE
                    );
                    return;
                }
            }

            boolean sessionStarted = sessionController.startSession(vehicleNumber, lotId, clientId, phone);

            if (sessionStarted) {
                StyledMessageDialog.showMessage(
                        SwingUtilities.getWindowAncestor(this),
                        "New client and vehicle added successfully.\n3 vehicle photos uploaded.\nParking session started.\nBarrier opened.\nSMS sent.",
                        "Start Parking",
                        MATCHA_2,
                        MATCHA_1,
                        OFF_WHITE
                );
                clearFields();
            } else {
                StyledMessageDialog.showMessage(
                        SwingUtilities.getWindowAncestor(this),
                        "Failed to start parking session.",
                        "Start Parking",
                        MATCHA_2,
                        MATCHA_1,
                        OFF_WHITE
                );
            }
        }

        private void clearFields() {
            vehicleNumberField.setText("");
            phoneField.setText("");
            lotIdField.setText("");
            messageLabel.setText(" ");
        }

        private void styleRoundedField(JTextField f) {
            f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            f.setForeground(TEXT_DARK);
            f.setBackground(Color.WHITE);
            f.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(OFF_WHITE);
                g2.fillRect(0, 0, w, h);

                g2.setComposite(AlphaComposite.SrcOver.derive(0.10f));
                g2.setColor(MATCHA_1);
                g2.fill(new Ellipse2D.Double(-140, h * 0.10, 420, 420));

                g2.setComposite(AlphaComposite.SrcOver.derive(0.08f));
                g2.setColor(MATCHA_2);
                g2.fill(new Ellipse2D.Double(w - 420, h * 0.52, 600, 600));

                g2.setComposite(AlphaComposite.SrcOver);
            } finally {
                g2.dispose();
            }
        }
    }

    static class PremiumCard extends JComponent {
        private final Color matcha1;

        PremiumCard(Color matcha2, Color matcha1) {
            this.matcha1 = matcha1;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setComposite(AlphaComposite.SrcOver.derive(0.14f));
                g2.setColor(Color.BLACK);
                g2.fill(new RoundRectangle2D.Double(10, 12, w - 20, h - 20, 26, 26));

                g2.setComposite(AlphaComposite.SrcOver.derive(0.96f));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, w - 20, h - 20, 26, 26));

                g2.setComposite(AlphaComposite.SrcOver.derive(0.35f));
                g2.setColor(new Color(140, 160, 140));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 20, h - 20, 26, 26));

                g2.setComposite(AlphaComposite.SrcOver.derive(0.35f));
                g2.setColor(matcha1);
                g2.fillRoundRect(22, 22, 130, 7, 12, 12);

            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        public Insets getInsets() {
            return new Insets(18, 18, 18, 18);
        }
    }

    static class IconBubble extends JComponent {
        private final String emoji;
        private final Color color;

        IconBubble(String emoji, Color color) {
            this.emoji = emoji;
            this.color = color;
            setPreferredSize(new Dimension(62, 62));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int s = 58;
                int x = 0;
                int y = 2;

                g2.setComposite(AlphaComposite.SrcOver.derive(0.16f));
                g2.setColor(color);
                g2.fill(new Ellipse2D.Double(x, y, s, s));

                g2.setComposite(AlphaComposite.SrcOver);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (s - fm.stringWidth(emoji)) / 2;
                int ty = y + (s + fm.getAscent()) / 2 - 5;
                g2.drawString(emoji, tx, ty);

            } finally {
                g2.dispose();
            }
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
                g2.setStroke(new BasicStroke(1f));
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

            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(140, 44));

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
                    g2.setStroke(new BasicStroke(2f));
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

    static class VehicleDetailsDialog extends JDialog {
        private boolean confirmed = false;

        private final RoundedTextField firstNameField = new RoundedTextField();
        private final RoundedTextField lastNameField = new RoundedTextField();
        private final RoundedTextField typeField = new RoundedTextField();

        private final JComboBox<String> colorCombo =
                new JComboBox<>(new String[]{"WHITE", "BLACK", "SILVER", "BLUE", "OTHER"});

        private final JComboBox<String> sizeCombo =
                new JComboBox<>(new String[]{"SMALL", "MEDIUM", "LARGE"});

        private final RoundedTextField weightField = new RoundedTextField();

        private String frontPhotoPath;
        private String rearPhotoPath;
        private String sidePhotoPath;

        private final JLabel frontLabel = new JLabel("No file selected");
        private final JLabel rearLabel = new JLabel("No file selected");
        private final JLabel sideLabel = new JLabel("No file selected");

        private final JLabel errorLabel = new JLabel(" ");

        VehicleDetailsDialog(Window owner, Color matcha2, Color matcha1, Color offWhite) {
            super(owner, "Vehicle Details", ModalityType.APPLICATION_MODAL);

            setUndecorated(true);
            setResizable(false);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            PremiumCard card = new PremiumCard(matcha2, matcha1);
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(620, 760));

            JPanel northContainer = new JPanel();
            northContainer.setOpaque(false);
            northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));

            JPanel topBar = new JPanel(new BorderLayout());
            topBar.setOpaque(false);
            topBar.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

            JLabel tiny = new JLabel("Vehicle Details");
            tiny.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tiny.setForeground(new Color(120, 120, 120));

            RoundedButton closeBtn = new RoundedButton("Close", matcha2, false);
            closeBtn.setPreferredSize(new Dimension(90, 34));
            closeBtn.addActionListener(e -> dispose());

            topBar.add(tiny, BorderLayout.WEST);
            topBar.add(closeBtn, BorderLayout.EAST);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

            JLabel title = new JLabel("New Vehicle Details");
            title.setFont(new Font("Segoe UI", Font.BOLD, 26));
            title.setForeground(new Color(28, 28, 28));

            JLabel subtitle = new JLabel("Fill the client and vehicle details and upload 3 photos");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitle.setForeground(new Color(90, 90, 90));

            JPanel titles = new JPanel();
            titles.setOpaque(false);
            titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
            titles.add(title);
            titles.add(Box.createVerticalStrut(6));
            titles.add(subtitle);

            JComponent icon = new IconBubble("🚙", matcha1);
            header.add(icon, BorderLayout.WEST);
            header.add(titles, BorderLayout.CENTER);

            northContainer.add(topBar);
            northContainer.add(header);

            card.add(northContainer, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            form.setBorder(BorderFactory.createEmptyBorder(8, 22, 6, 22));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(8, 0, 6, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            addField(form, gbc, "First name", firstNameField);
            addField(form, gbc, "Last name", lastNameField);
            addField(form, gbc, "Vehicle type", typeField);
            addComboField(form, gbc, "Color", colorCombo);
            addComboField(form, gbc, "Size", sizeCombo);
            addField(form, gbc, "Weight (kg)", weightField);

            addPhotoField(form, gbc, "Front Photo", frontLabel, path -> frontPhotoPath = path);
            addPhotoField(form, gbc, "Rear Photo", rearLabel, path -> rearPhotoPath = path);
            addPhotoField(form, gbc, "Side Photo", sideLabel, path -> sidePhotoPath = path);

            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            errorLabel.setForeground(new Color(180, 60, 60));
            form.add(errorLabel, gbc);

            JScrollPane scrollPane = new JScrollPane(form);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            card.add(scrollPane, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            actions.setOpaque(false);
            actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));

            RoundedButton cancel = new RoundedButton("Cancel", matcha2, false);
            RoundedButton confirm = new RoundedButton("Confirm", matcha2, true);

            cancel.addActionListener(e -> dispose());
            confirm.addActionListener(e -> handleConfirm());

            actions.add(cancel);
            actions.add(confirm);

            card.add(actions, BorderLayout.SOUTH);

            JPanel outer = new JPanel(new GridBagLayout());
            outer.setBackground(offWhite);
            outer.add(card, new GridBagConstraints());

            setContentPane(outer);
            pack();
            setLocationRelativeTo(owner);

            SwingUtilities.invokeLater(
                    () -> setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 26, 26))
            );
        }

        private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;

            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(new Color(60, 60, 60));
            panel.add(label, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            styleComponent(field);
            panel.add(field, gbc);

            gbc.gridy++;
        }

        private void addComboField(JPanel panel, GridBagConstraints gbc, String labelText, JComboBox<String> combo) {
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;

            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(new Color(60, 60, 60));
            panel.add(label, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            styleRoundedCombo(combo);
            panel.add(combo, gbc);

            gbc.gridy++;
        }

        private void addPhotoField(JPanel panel, GridBagConstraints gbc,
                                   String labelText, JLabel fileLabel,
                                   java.util.function.Consumer<String> setter) {

            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;

            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(new Color(60, 60, 60));
            panel.add(label, gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);

            JButton uploadBtn = new JButton("Upload");
            uploadBtn.setFocusPainted(false);

            uploadBtn.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                int res = chooser.showOpenDialog(this);

                if (res == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    setter.accept(path);
                    fileLabel.setText("✔ " + selectedFile.getName());
                }
            });

            fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            fileLabel.setForeground(new Color(100, 100, 100));

            row.add(uploadBtn, BorderLayout.WEST);
            row.add(fileLabel, BorderLayout.CENTER);

            panel.add(row, gbc);

            gbc.gridy++;
        }

        private void styleComponent(JComponent comp) {
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            comp.setForeground(new Color(28, 28, 28));
            comp.setBackground(Color.WHITE);

            comp.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(160, 175, 160), 1, true),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            comp.setPreferredSize(new Dimension(420, 46));
        }

        private void styleRoundedCombo(JComboBox<String> combo) {
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            combo.setForeground(new Color(28, 28, 28));
            combo.setBackground(Color.WHITE);
            combo.setFocusable(false);
            combo.setOpaque(false);
            combo.setUI(new RoundedComboBoxUI(new Color(92, 146, 82)));
            combo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(160, 175, 160), 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            combo.setPreferredSize(new Dimension(420, 46));

            combo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus
                    );

                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                    lbl.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

                    if (isSelected) {
                        lbl.setBackground(new Color(92, 146, 82));
                        lbl.setForeground(Color.WHITE);
                    } else {
                        lbl.setBackground(Color.WHITE);
                        lbl.setForeground(new Color(28, 28, 28));
                    }

                    return lbl;
                }
            });
        }

        private void handleConfirm() {
            if (getFirstName().isEmpty()
                    || getLastName().isEmpty()
                    || getTypeText().isEmpty()
                    || weightField.getText().trim().isEmpty()
                    || frontPhotoPath == null
                    || rearPhotoPath == null
                    || sidePhotoPath == null) {

                errorLabel.setText("Please fill all fields and upload 3 photos.");
                return;
            }

            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                if (weight <= 0) {
                    errorLabel.setText("Weight must be positive.");
                    return;
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Weight must be a valid number.");
                return;
            }

            confirmed = true;
            dispose();
        }

        boolean isConfirmed() {
            return confirmed;
        }

        String getFirstName() {
            return firstNameField.getText().trim();
        }

        String getLastName() {
            return lastNameField.getText().trim();
        }

        String getTypeText() {
            return typeField.getText().trim();
        }

        String getColor() {
            return String.valueOf(colorCombo.getSelectedItem()).trim();
        }

        String getVehicleSize() {
            return String.valueOf(sizeCombo.getSelectedItem()).trim();
        }

        double getWeightKg() {
            return Double.parseDouble(weightField.getText().trim());
        }

        String getFrontPhotoPath() {
            return frontPhotoPath;
        }

        String getRearPhotoPath() {
            return rearPhotoPath;
        }

        String getSidePhotoPath() {
            return sidePhotoPath;
        }
    }

    static class RoundedComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        private final Color accent;

        RoundedComboBoxUI(Color accent) {
            this.accent = accent;
        }

        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼");
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setForeground(accent);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        }
    }

    static class StyledMessageDialog extends JDialog {

        StyledMessageDialog(Window owner, String message, String titleText, Color matcha2, Color matcha1, Color offWhite) {
            super(owner, titleText, ModalityType.APPLICATION_MODAL);

            setUndecorated(true);
            setResizable(false);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            PremiumCard card = new PremiumCard(matcha2, matcha1);
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(540, 320));

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(14, 22, 10, 22));

            JLabel title = new JLabel(titleText);
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(new Color(28, 28, 28));

            JLabel subtitle = new JLabel("Operation completed");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitle.setForeground(new Color(90, 90, 90));

            JPanel titles = new JPanel();
            titles.setOpaque(false);
            titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
            titles.add(title);
            titles.add(Box.createVerticalStrut(6));
            titles.add(subtitle);

            header.add(new IconBubble("✅", matcha1), BorderLayout.WEST);
            header.add(titles, BorderLayout.CENTER);

            JTextArea textArea = new JTextArea(message);
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            textArea.setForeground(new Color(28, 28, 28));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            actions.setOpaque(false);
            actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));

            RoundedButton okButton = new RoundedButton("OK", matcha2, true);
            okButton.addActionListener(e -> dispose());
            actions.add(okButton);

            card.add(header, BorderLayout.NORTH);
            card.add(textArea, BorderLayout.CENTER);
            card.add(actions, BorderLayout.SOUTH);

            JPanel outer = new JPanel(new GridBagLayout());
            outer.setBackground(offWhite);
            outer.add(card, new GridBagConstraints());

            setContentPane(outer);
            pack();
            setLocationRelativeTo(owner);

            SwingUtilities.invokeLater(
                    () -> setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 26, 26))
            );
        }

        static void showMessage(Window owner, String message, String titleText,
                                Color matcha2, Color matcha1, Color offWhite) {
            StyledMessageDialog dialog = new StyledMessageDialog(owner, message, titleText, matcha2, matcha1, offWhite);
            dialog.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartParkingFrame().setVisible(true));
    }
}
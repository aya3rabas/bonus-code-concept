package parkwise.ui;

import parkwise.controller.ParkingSessionController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Timestamp;

public class ViewParkingDetailsFrame extends JFrame {

    public ViewParkingDetailsFrame() {
        setTitle("ParkWise - View Parking Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(new ViewParkingDetailsPanel());
    }

    static class ViewParkingDetailsPanel extends JPanel {

        private final Color OFF_WHITE = new Color(249, 248, 244);
        private final Color MATCHA_1 = new Color(123, 171, 106);
        private final Color MATCHA_2 = new Color(92, 146, 82);
        private final Color TEXT_DARK = new Color(28, 28, 28);
        private final Color TEXT_MUTED = new Color(90, 90, 90);

        private final RoundedTextField vehicleNumberField = new RoundedTextField();
        private final RoundedTextField phoneField = new RoundedTextField();

        private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);

        private final ParkingSessionController sessionController = new ParkingSessionController();

        ViewParkingDetailsPanel() {
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
            gbc.insets = new Insets(12, 2, 0, 0);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            messageLabel.setForeground(new Color(180, 60, 60));
            formPanel.add(messageLabel, gbc);

            RoundedButton closeBtn = new RoundedButton("Close", MATCHA_2, false);
            RoundedButton viewBtn = new RoundedButton("View Details", MATCHA_2, true);

            closeBtn.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
            viewBtn.addActionListener(e -> handleViewDetails());

            vehicleNumberField.addActionListener(e -> phoneField.requestFocusInWindow());
            phoneField.addActionListener(e -> handleViewDetails());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            actions.setOpaque(false);
            actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));
            actions.add(closeBtn);
            actions.add(viewBtn);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

            JLabel title = new JLabel("View Parking Details");
            title.setFont(new Font("Segoe UI", Font.BOLD, 26));
            title.setForeground(TEXT_DARK);

            JLabel subtitle = new JLabel("Enter vehicle number and phone to view the active parking session");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitle.setForeground(TEXT_MUTED);

            JPanel titles = new JPanel();
            titles.setOpaque(false);
            titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
            titles.add(title);
            titles.add(Box.createVerticalStrut(6));
            titles.add(subtitle);

            JComponent icon = new IconBubble("📍", MATCHA_1);
            header.add(icon, BorderLayout.WEST);
            header.add(titles, BorderLayout.CENTER);

            PremiumCard card = new PremiumCard(MATCHA_2, MATCHA_1);
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(700, 430));
            card.add(header, BorderLayout.NORTH);
            card.add(formPanel, BorderLayout.CENTER);
            card.add(actions, BorderLayout.SOUTH);

            add(card, new GridBagConstraints());
        }

        private void handleViewDetails() {
            String vehicleNumber = vehicleNumberField.getText().trim();
            String phone = phoneField.getText().trim();

            messageLabel.setText(" ");

            if (vehicleNumber.isEmpty() || phone.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            Object[] session = sessionController.getActiveSessionByVehicleAndPhone(vehicleNumber, phone);

            if (session == null) {
                StyledMessageDialog.showMessage(
                        this,
                        "No active parking session found for this vehicle and phone.",
                        "Warning"
                );
                return;
            }

            int sessionId = (Integer) session[0];
            int lotId = (Integer) session[1];
            String storedVehicleNumber = (String) session[2];
            int clientId = (Integer) session[3];
            Timestamp startTime = (Timestamp) session[4];
            String storedPhone = (String) session[5];

            StyledMessageDialog.showMessage(
                    this,
                    "📄 Active Parking Details\n\n" +
                    "Session ID: " + sessionId + "\n" +
                    "Vehicle Number: " + storedVehicleNumber + "\n" +
                    "Parking Lot ID: " + lotId + "\n" +
                    "Client ID: " + clientId + "\n" +
                    "Start Time: " + startTime + "\n" +
                    "Phone: " + storedPhone,
                    "Parking Details"
            );
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewParkingDetailsFrame().setVisible(true));
    }
}
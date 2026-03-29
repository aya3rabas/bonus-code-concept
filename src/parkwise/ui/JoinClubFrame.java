package parkwise.ui;

import parkwise.controller.ClubController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class JoinClubFrame extends JFrame {

    public JoinClubFrame() {
        setTitle("ParkWise - Join Club");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(new JoinClubPanel());
    }

    static class JoinClubPanel extends JPanel {

        private final Color OFF_WHITE = new Color(249, 248, 244);
        private final Color MATCHA_1 = new Color(123, 171, 106);
        private final Color MATCHA_2 = new Color(92, 146, 82);
        private final Color TEXT_DARK = new Color(28, 28, 28);
        private final Color TEXT_MUTED = new Color(90, 90, 90);

        private final RoundedTextField phoneField = new RoundedTextField();
        private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);

        private final ClubController clubController = new ClubController();

        private final JPanel lotsPanel = new JPanel();
        private final List<JCheckBox> lotCheckBoxes = new ArrayList<>();

        JoinClubPanel() {
            setBackground(OFF_WHITE);
            setLayout(new GridBagLayout());

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

            JLabel title = new JLabel("Join Customer Club");
            title.setFont(new Font("Segoe UI", Font.BOLD, 26));
            title.setForeground(TEXT_DARK);

            JLabel subtitle = new JLabel("Register by phone number and choose preferred parking lots");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitle.setForeground(TEXT_MUTED);

            JPanel titles = new JPanel();
            titles.setOpaque(false);
            titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
            titles.add(title);
            titles.add(Box.createVerticalStrut(6));
            titles.add(subtitle);

            JComponent icon = new IconBubble("⭐", MATCHA_1);
            header.add(icon, BorderLayout.WEST);
            header.add(titles, BorderLayout.CENTER);

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

            JLabel phoneLbl = new JLabel("Mobile Phone");
            phoneLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            phoneLbl.setForeground(new Color(60, 60, 60));
            form.add(phoneLbl, gbc);

            gbc.gridy++;
            styleRoundedField(phoneField);
            phoneField.setColumns(28);
            form.add(phoneField, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(16, 0, 6, 0);

            JLabel prefLbl = new JLabel("Preferred Parking Lots");
            prefLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            prefLbl.setForeground(new Color(60, 60, 60));
            form.add(prefLbl, gbc);

            gbc.gridy++;
            lotsPanel.setOpaque(false);
            lotsPanel.setLayout(new BoxLayout(lotsPanel, BoxLayout.Y_AXIS));
            loadParkingLots();

            JScrollPane scrollPane = new JScrollPane(lotsPanel);
            scrollPane.setPreferredSize(new Dimension(500, 240));
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(160, 175, 160), 1, true));
            scrollPane.getViewport().setBackground(Color.WHITE);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            form.add(scrollPane, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(12, 2, 0, 0);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            messageLabel.setForeground(new Color(180, 60, 60));
            form.add(messageLabel, gbc);

            RoundedButton cancelBtn = new RoundedButton("Close", MATCHA_2, false);
            RoundedButton joinBtn = new RoundedButton("Join Club", MATCHA_2, true);

            cancelBtn.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
            joinBtn.addActionListener(e -> handleJoinClub());

            phoneField.addActionListener(e -> handleJoinClub());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            actions.setOpaque(false);
            actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));
            actions.add(cancelBtn);
            actions.add(joinBtn);

            PremiumCard card = new PremiumCard(MATCHA_2, MATCHA_1);
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(700, 650));
            card.add(header, BorderLayout.NORTH);
            card.add(form, BorderLayout.CENTER);
            card.add(actions, BorderLayout.SOUTH);

            add(card, new GridBagConstraints());
        }

        private void loadParkingLots() {
            lotsPanel.removeAll();
            lotCheckBoxes.clear();

            List<Object[]> lots = clubController.getAllParkingLots();

            for (Object[] lot : lots) {
                int lotId = (Integer) lot[0];
                String name = String.valueOf(lot[1]);
                String city = String.valueOf(lot[2]);

                JCheckBox checkBox = new JCheckBox("Lot " + lotId + " - " + name + " (" + city + ")");
                checkBox.setOpaque(false);
                checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                checkBox.setForeground(TEXT_DARK);
                checkBox.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

                lotCheckBoxes.add(checkBox);
                lotsPanel.add(checkBox);
            }

            lotsPanel.revalidate();
            lotsPanel.repaint();
        }

        private void handleJoinClub() {
            messageLabel.setText(" ");

            String phone = phoneField.getText().trim();
            if (phone.isEmpty()) {
                messageLabel.setText("Please enter the mobile phone.");
                return;
            }

            Integer clientId = clubController.getClientIdByPhone(phone);
            if (clientId == null) {
                StyledMessageDialog.showMessage(
                        SwingUtilities.getWindowAncestor(this),
                        "This phone number does not belong to any client.",
                        "Join Club",
                        MATCHA_2,
                        MATCHA_1,
                        OFF_WHITE
                );
                return;
            }

            boolean joined = clubController.joinClub(clientId);
            if (!joined) {
                StyledMessageDialog.showMessage(
                        SwingUtilities.getWindowAncestor(this),
                        "Failed to join the club.",
                        "Join Club",
                        MATCHA_2,
                        MATCHA_1,
                        OFF_WHITE
                );
                return;
            }

            Integer memberId = clubController.getMemberIdByClientId(clientId);
            if (memberId == null) {
                StyledMessageDialog.showMessage(
                        SwingUtilities.getWindowAncestor(this),
                        "Failed to retrieve member ID.",
                        "Join Club",
                        MATCHA_2,
                        MATCHA_1,
                        OFF_WHITE
                );
                return;
            }

            int selectedCount = 0;
            for (int i = 0; i < lotCheckBoxes.size(); i++) {
                if (lotCheckBoxes.get(i).isSelected()) {
                    int lotId = (Integer) clubController.getAllParkingLots().get(i)[0];
                    if (clubController.addPreferredLot(memberId, lotId)) {
                        selectedCount++;
                    }
                }
            }

            StyledMessageDialog.showMessage(
                    SwingUtilities.getWindowAncestor(this),
                    "Club membership completed successfully.\nPreferred lots saved: " + selectedCount,
                    "Join Club",
                    MATCHA_2,
                    MATCHA_1,
                    OFF_WHITE
            );

            phoneField.setText("");
            for (JCheckBox box : lotCheckBoxes) {
                box.setSelected(false);
            }
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
        SwingUtilities.invokeLater(() -> new JoinClubFrame().setVisible(true));
    }
}
package parkwise.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class StyledMessageDialog extends JDialog {

    private static final Color OFF_WHITE = new Color(249, 248, 244);
    private static final Color MATCHA_1 = new Color(123, 171, 106);
    private static final Color MATCHA_2 = new Color(92, 146, 82);
    private static final Color TEXT_DARK = new Color(28, 28, 28);
    private static final Color TEXT_MUTED = new Color(90, 90, 90);

    public StyledMessageDialog(Window owner, String message, String titleText) {
        super(owner, titleText, ModalityType.APPLICATION_MODAL);

        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        PremiumCard card = new PremiumCard(MATCHA_2, MATCHA_1);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(620, 460));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 10, 22));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel(getSubtitle(titleText));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(title);
        titles.add(Box.createVerticalStrut(6));
        titles.add(subtitle);

        header.add(new IconBubble(getIconForTitle(titleText), MATCHA_1), BorderLayout.WEST);
        header.add(titles, BorderLayout.CENTER);

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        textArea.setForeground(TEXT_DARK);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));

        RoundedButton okButton = new RoundedButton("OK", MATCHA_2, true);
        okButton.addActionListener(e -> dispose());
        actions.add(okButton);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(OFF_WHITE);
        outer.add(card, new GridBagConstraints());

        setContentPane(outer);
        pack();
        setLocationRelativeTo(owner);

        SwingUtilities.invokeLater(() ->
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 26, 26))
        );
    }

    private String getIconForTitle(String titleText) {
        String t = titleText.toLowerCase();

        if (t.contains("error")) return "❌";
        if (t.contains("warning")) return "⚠";
        if (t.contains("invalid")) return "⚠";
        if (t.contains("duplicate")) return "📌";
        if (t.contains("success")) return "✅";
        if (t.contains("receipt")) return "🧾";
        if (t.contains("sms")) return "📱";
        return "ℹ";
    }

    private String getSubtitle(String titleText) {
        String t = titleText.toLowerCase();

        if (t.contains("error")) return "Something went wrong";
        if (t.contains("warning")) return "Please review this message";
        if (t.contains("invalid")) return "Please correct the input";
        if (t.contains("duplicate")) return "This record already exists";
        if (t.contains("success")) return "Operation completed successfully";
        if (t.contains("receipt")) return "Parking session summary";
        if (t.contains("sms")) return "Client notification";
        return "System message";
    }

    public static void showMessage(Component parent, String message, String title) {
        Window window = parent instanceof Window
                ? (Window) parent
                : SwingUtilities.getWindowAncestor(parent);

        StyledMessageDialog dialog = new StyledMessageDialog(window, message, title);
        dialog.setVisible(true);
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
            setPreferredSize(new Dimension(120, 44));

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
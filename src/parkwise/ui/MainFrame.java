package parkwise.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("ParkWise");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        setExtendedState(JFrame.MAXIMIZED_BOTH); // מסך מלא
        setContentPane(new WelcomePanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // ===================== UI Panel with animation =====================
    static class WelcomePanel extends JPanel {
        // Palette
        private final Color OFF_WHITE = new Color(249, 248, 244); // soft off-white
        private final Color MATCHA_1  = new Color(123, 171, 106); // matcha
        private final Color MATCHA_2  = new Color(92, 146, 82);   // deeper matcha
        private final Color TEXT_DARK = new Color(28, 28, 28);
        private final Color TEXT_MUTED= new Color(90, 90, 90);

        private RoundArrowButton nextButton;

        // Animation state
        private float pulse = 0f; // 0..1
        private float pulseDir = 1f;
        private float dotX = 0f; // 0..1

        private final Timer timer;

        public WelcomePanel() {
            setOpaque(true);
            setBackground(OFF_WHITE);
            setLayout(null); // absolute positioning

            // Smooth 60-ish FPS animation
            timer = new Timer(16, (ActionEvent e) -> {
                pulse += 0.015f * pulseDir;
                if (pulse >= 1f) { pulse = 1f; pulseDir = -1f; }
                if (pulse <= 0f) { pulse = 0f; pulseDir =  1f; }

                dotX += 0.008f;
                if (dotX > 1f) dotX = 0f;

                repaint();
            });
            timer.start();

            nextButton = new RoundArrowButton("→", MATCHA_2, MATCHA_1);
            nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            nextButton.addActionListener(e -> {
                SwingUtilities.getWindowAncestor(this).dispose();
                new NextFrame().setVisible(true);
            });

            add(nextButton);
        }

        private void positionNextButton(int cardX, int cardY, int cardW, int cardH) {
            int btnW = 78;
            int btnH = 78;

            int x = cardX + (cardW - btnW) / 2;
            int y = cardY + cardH + 18;

            nextButton.setBounds(x, y, btnW, btnH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(OFF_WHITE);
                g2.fillRect(0, 0, w, h);

                paintSoftBlobs(g2, w, h);

                int cardW = Math.min(680, w - 120);
                int cardH = 230;
                int x = (w - cardW) / 2;
                int y = (h - cardH) / 2;

                positionNextButton(x, y, cardW, cardH);

                paintCard(g2, x, y, cardW, cardH);

                String title = "Welcome to Parkwise";
                g2.setFont(new Font("Segoe UI", Font.BOLD, 46));
                FontMetrics fm = g2.getFontMetrics();
                int titleX = x + (cardW - fm.stringWidth(title)) / 2;
                int titleY = y + 95;

                g2.setColor(TEXT_DARK);
                g2.drawString(title, titleX, titleY);

                String subtitle = "Smart parking management system";
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                FontMetrics fm2 = g2.getFontMetrics();
                int subX = x + (cardW - fm2.stringWidth(subtitle)) / 2;
                int subY = titleY + 36;

                g2.setColor(TEXT_MUTED);
                g2.drawString(subtitle, subX, subY);

                paintAnimatedUnderline(g2, x, y, cardW, titleY);

            } finally {
                g2.dispose();
            }
        }

        private void paintSoftBlobs(Graphics2D g2, int w, int h) {
            float a1 = 0.10f + 0.06f * pulse;
            float a2 = 0.08f + 0.05f * (1f - pulse);

            g2.setComposite(AlphaComposite.SrcOver.derive(a1));
            g2.setColor(MATCHA_1);
            g2.fill(new Ellipse2D.Double(-120, h * 0.10, 380, 380));

            g2.setComposite(AlphaComposite.SrcOver.derive(a2));
            g2.setColor(MATCHA_2);
            g2.fill(new Ellipse2D.Double(w - 320, h * 0.55, 420, 420));

            g2.setComposite(AlphaComposite.SrcOver);
        }

        private void paintCard(Graphics2D g2, int x, int y, int cardW, int cardH) {
            g2.setComposite(AlphaComposite.SrcOver.derive(0.12f));
            g2.setColor(Color.BLACK);
            g2.fill(new RoundRectangle2D.Double(x + 8, y + 10, cardW, cardH, 28, 28));

            g2.setComposite(AlphaComposite.SrcOver.derive(0.92f));
            g2.setColor(new Color(255, 255, 255));
            g2.fill(new RoundRectangle2D.Double(x, y, cardW, cardH, 28, 28));

            g2.setComposite(AlphaComposite.SrcOver.derive(0.35f));
            g2.setColor(new Color(140, 160, 140));
            g2.draw(new RoundRectangle2D.Double(x, y, cardW, cardH, 28, 28));

            g2.setComposite(AlphaComposite.SrcOver);
        }

        private void paintAnimatedUnderline(Graphics2D g2, int x, int y, int cardW, int titleBaselineY) {
            int lineY = titleBaselineY + 14;
            int lineW = (int) (cardW * 0.58);
            int lineX = x + (cardW - lineW) / 2;

            int thickness = 5 + (int) (3 * pulse);

            g2.setColor(MATCHA_2);
            g2.fillRoundRect(lineX, lineY, lineW, thickness, 20, 20);

            int dotRadius = 7;
            int dotPosX = lineX + (int) (dotX * (lineW - dotRadius * 2)) + dotRadius;
            int dotCenterY = lineY + thickness / 2;

            int glowR = 18 + (int) (10 * pulse);
            g2.setComposite(AlphaComposite.SrcOver.derive(0.18f + 0.10f * pulse));
            g2.setColor(MATCHA_1);
            g2.fill(new Ellipse2D.Double(dotPosX - glowR, dotCenterY - glowR, glowR * 2, glowR * 2));

            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(MATCHA_2);
            g2.fill(new Ellipse2D.Double(dotPosX - dotRadius, dotCenterY - dotRadius, dotRadius * 2, dotRadius * 2));
        }
    }

    // ===================== Round button (with hover) =====================
    static class RoundArrowButton extends JButton {
        private final Color borderColor;
        private final Color hoverFill;
        private boolean hover = false;

        // פנקס בטוח כדי שהעיגול/צל לא ייחתכו
        private static final int PAD = 4;     // מרווח פנימי לציור
        private static final int SHADOW_DX = 2;
        private static final int SHADOW_DY = 3;

        RoundArrowButton(String text, Color borderColor, Color hoverFill) {
            super(text);
            this.borderColor = borderColor;
            this.hoverFill = hoverFill;

            setFont(new Font("Segoe UI", Font.BOLD, 38));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setForeground(borderColor);
            setHorizontalAlignment(SwingConstants.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hover = true;
                    setForeground(Color.WHITE);
                    repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    hover = false;
                    setForeground(borderColor);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // גודל עיגול בטוח בתוך הכפתור
                int s = Math.min(w, h) - PAD * 2;
                int x = (w - s) / 2;
                int y = (h - s) / 2;

                // Shadow (תמיד בתוך גבולות)
                g2.setComposite(AlphaComposite.SrcOver.derive(0.18f));
                g2.setColor(Color.BLACK);
                g2.fill(new Ellipse2D.Double(x + SHADOW_DX, y + SHADOW_DY, s, s));

                // Fill
                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(hover ? hoverFill : new Color(255, 255, 255));
                g2.fill(new Ellipse2D.Double(x, y, s, s));

                // Border
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(borderColor);
                g2.draw(new Ellipse2D.Double(x, y, s, s));

                // Paint text centered (arrow)
                super.paintComponent(g2);

            } finally {
                g2.dispose();
            }
        }

        @Override
        public boolean contains(int px, int py) {
            int w = getWidth();
            int h = getHeight();
            int s = Math.min(w, h) - PAD * 2;
            int cx = w / 2;
            int cy = h / 2;
            int r = s / 2;

            int dx = px - cx;
            int dy = py - cy;
            return dx * dx + dy * dy <= r * r;
        }
    }
}
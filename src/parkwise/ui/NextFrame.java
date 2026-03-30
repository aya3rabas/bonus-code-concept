package parkwise.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import parkwise.db.DBConnection;

public class NextFrame extends JFrame {

    public NextFrame() {
        setTitle("ParkWise - Choose");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(new ChoosePanel());
    }

    static class ChoosePanel extends JPanel {

        private final Color OFF_WHITE = new Color(249, 248, 244);
        private final Color MATCHA_1 = new Color(123, 171, 106);
        private final Color MATCHA_2 = new Color(92, 146, 82);
        private final Color TEXT_DARK = new Color(28, 28, 28);

        private final ChoiceCard adminCard;
        private final ChoiceCard clientCard;

        ChoosePanel() {
            setBackground(OFF_WHITE);
            setLayout(new GridBagLayout());

            adminCard = new ChoiceCard(
                    "Admin",
                    "Manage parking lots, prices,\nreports and system settings",
                    "🛠",
                    MATCHA_2,
                    MATCHA_1
            );

            clientCard = new ChoiceCard(
                    "Client",
                    "Register / Login, start/end parking,\npayments and subscriptions",
                    "🚗",
                    MATCHA_2,
                    MATCHA_1
            );

            adminCard.setOnClick(this::showAdminLogin);
            clientCard.setOnClick(this::showClientRegister);

            JPanel centerCard = new JPanel(new BorderLayout());
            centerCard.setOpaque(false);

            JLabel title = new JLabel("Choose one", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 40));
            title.setForeground(TEXT_DARK);
            title.setBorder(BorderFactory.createEmptyBorder(26, 20, 14, 20));
            centerCard.add(title, BorderLayout.NORTH);

            JPanel cardsRow = new JPanel(new GridLayout(1, 2, 28, 0));
            cardsRow.setOpaque(false);
            cardsRow.setBorder(BorderFactory.createEmptyBorder(10, 28, 28, 28));
            cardsRow.add(adminCard);
            cardsRow.add(clientCard);

            centerCard.add(cardsRow, BorderLayout.CENTER);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(20, 20, 20, 20);

            add(new BigGlassCard(centerCard, new Dimension(980, 520)), gbc);
        }

        private void showAdminLogin() {
            Window owner = SwingUtilities.getWindowAncestor(this);
            AdminLoginDialog dialog = new AdminLoginDialog(owner, MATCHA_2, MATCHA_1, OFF_WHITE);
            dialog.setVisible(true);

            if (dialog.isSuccess()) {
                JFrame adminFrame = new JFrame("ParkWise - Admin");
                adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                adminFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

                JPanel p = new JPanel(new BorderLayout());
                p.setBackground(OFF_WHITE);

                JLabel title = new JLabel("Administrator Menu", SwingConstants.CENTER);
                title.setFont(new Font("Segoe UI", Font.BOLD, 34));
                title.setForeground(new Color(28, 28, 28));
                title.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
                p.add(title, BorderLayout.NORTH);

                JPanel center = new JPanel(new GridBagLayout());
                center.setOpaque(false);

                JPanel menuPanel = new JPanel(new GridLayout(3, 2, 25, 25));
                menuPanel.setOpaque(false);
                menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

                JButton btnAdaptPriceList = createAdminMenuButton("Adapt a Price List to a Parking Lot");
                JButton btnManageParkingLot = createAdminMenuButton(
                        "Manage Parking Lot\n(including spaces and conveyors)");
                JButton btnAnnualReport = createAdminMenuButton("Generate Annual Summary Report");
                JButton btnClubGrowthReport = createAdminMenuButton("Generate Customer Club Growth Report");
                JButton btnImportJson = createAdminMenuButton("Import Price Lists (JSON)");

                btnAdaptPriceList.addActionListener(e -> new AdaptPriceListFrame().setVisible(true));
                btnManageParkingLot.addActionListener(e -> new ParkingLotEditorFrame().setVisible(true));
                btnAnnualReport.addActionListener(e -> new AnnualSummaryReportFrame().setVisible(true));
                btnClubGrowthReport.addActionListener(e -> new CustomerClubGrowthReportFrame().setVisible(true));
                btnImportJson.addActionListener(e -> new ImportPriceListFrame().setVisible(true));

                menuPanel.add(btnAdaptPriceList);
                menuPanel.add(btnManageParkingLot);
                menuPanel.add(btnAnnualReport);
                menuPanel.add(btnClubGrowthReport);
                menuPanel.add(btnImportJson);
                menuPanel.add(new JLabel()); // empty placeholder for balanced layout

                center.add(menuPanel);
                p.add(center, BorderLayout.CENTER);

                adminFrame.setContentPane(p);
                adminFrame.setVisible(true);

                SwingUtilities.getWindowAncestor(this).dispose();
            }
        
        }

        private void showClientRegister() {
            Window owner = SwingUtilities.getWindowAncestor(this);
            ClientRegisterDialog dialog = new ClientRegisterDialog(owner, MATCHA_2, MATCHA_1, OFF_WHITE);
            dialog.setVisible(true);

            if (dialog.isSuccess()) {
                JFrame clientFrame = new JFrame("ParkWise - Client");
                clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                clientFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

                JPanel root = new JPanel(new GridBagLayout());
                root.setBackground(OFF_WHITE);

                JPanel centerCard = new JPanel(new BorderLayout());
                centerCard.setOpaque(false);

                JLabel title = new JLabel("Client Menu", SwingConstants.CENTER);
                title.setFont(new Font("Segoe UI", Font.BOLD, 40));
                title.setForeground(TEXT_DARK);
                title.setBorder(BorderFactory.createEmptyBorder(26, 20, 14, 20));
                centerCard.add(title, BorderLayout.NORTH);

                JPanel menuPanel = new JPanel(new GridLayout(2, 2, 28, 28));
                menuPanel.setOpaque(false);
                menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

                ChoiceCard startParkingCard = new ChoiceCard(
                        "Start Parking",
                        "Begin a new parking session\nfor an existing or new vehicle",
                        "🚗",
                        MATCHA_2,
                        MATCHA_1
                );

                ChoiceCard endParkingCard = new ChoiceCard(
                        "End Parking",
                        "Finish an active parking session\nand handle payment",
                        "🧾",
                        MATCHA_2,
                        MATCHA_1
                );

                ChoiceCard viewDetailsCard = new ChoiceCard(
                        "View Parking Details",
                        "View your current active parking\nsession details",
                        "📍",
                        MATCHA_2,
                        MATCHA_1
                );

                ChoiceCard joinClubCard = new ChoiceCard(
                        "Join Club",
                        "Register to the customer club\nand manage preferred lots",
                        "⭐",
                        MATCHA_2,
                        MATCHA_1
                );

                startParkingCard.setOnClick(() -> new StartParkingFrame().setVisible(true));
                endParkingCard.setOnClick(() -> new EndParkingFrame().setVisible(true));
                viewDetailsCard.setOnClick(() -> new ViewParkingDetailsFrame().setVisible(true));
                joinClubCard.setOnClick(() -> new JoinClubFrame().setVisible(true));

                menuPanel.add(startParkingCard);
                menuPanel.add(endParkingCard);
                menuPanel.add(viewDetailsCard);
                menuPanel.add(joinClubCard);

                centerCard.add(menuPanel, BorderLayout.CENTER);

                BigGlassCard wrapper = new BigGlassCard(centerCard, new Dimension(1320, 720));
                root.add(wrapper, new GridBagConstraints());

                clientFrame.setContentPane(root);
                clientFrame.setVisible(true);

                SwingUtilities.getWindowAncestor(this).dispose();
            }
        }

        private JButton createAdminMenuButton(String text) {
            JButton btn = new JButton(
                    "<html><div style='text-align:center;'>" + text.replace("\n", "<br>") + "</div></html>");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
            btn.setForeground(Color.WHITE);
            btn.setBackground(MATCHA_2);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(320, 140));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MATCHA_1, 2, true),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            return btn;
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

        static class BigGlassCard extends JComponent {
            private final JComponent content;
            private final Dimension pref;

            BigGlassCard(JComponent content, Dimension preferredSize) {
                this.content = content;
                this.pref = preferredSize;
                setLayout(new BorderLayout());
                add(content, BorderLayout.CENTER);
                setOpaque(false);
            }

            @Override
            public Dimension getPreferredSize() {
                return pref;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    g2.setComposite(AlphaComposite.SrcOver.derive(0.12f));
                    g2.setColor(Color.BLACK);
                    g2.fill(new RoundRectangle2D.Double(12, 14, w - 24, h - 24, 30, 30));

                    g2.setComposite(AlphaComposite.SrcOver.derive(0.94f));
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, w - 24, h - 24, 30, 30));

                    g2.setComposite(AlphaComposite.SrcOver.derive(0.35f));
                    g2.setColor(new Color(140, 160, 140));
                    g2.draw(new RoundRectangle2D.Double(0, 0, w - 24, h - 24, 30, 30));

                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            public Insets getInsets() {
                return new Insets(22, 22, 22, 22);
            }
        }

        static class ChoiceCard extends JComponent {
            private final String title;
            private final String desc;
            private final String icon;
            private final Color matcha2;
            private final Color matcha1;

            private boolean hover = false;
            private Runnable onClick;

            ChoiceCard(String title, String desc, String icon, Color matcha2, Color matcha1) {
                this.title = title;
                this.desc = desc;
                this.icon = icon;
                this.matcha2 = matcha2;
                this.matcha1 = matcha1;

                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (onClick != null) onClick.run();
                    }
                });
            }

            void setOnClick(Runnable r) {
                this.onClick = r;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    int cardW = w - 16;
                    int cardH = h - 16;

                    g2.setComposite(AlphaComposite.SrcOver.derive(hover ? 0.16f : 0.12f));
                    g2.setColor(Color.BLACK);
                    g2.fill(new RoundRectangle2D.Double(8, hover ? 10 : 12, cardW, cardH, 24, 24));

                    g2.setComposite(AlphaComposite.SrcOver);
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, cardW, cardH, 24, 24));

                    g2.setComposite(AlphaComposite.SrcOver.derive(0.35f));
                    g2.setColor(new Color(140, 160, 140));
                    g2.draw(new RoundRectangle2D.Double(0, 0, cardW, cardH, 24, 24));

                    int bubble = 58;
                    int bx = 30;
                    int by = 28;

                    g2.setComposite(AlphaComposite.SrcOver.derive(hover ? 0.18f : 0.14f));
                    g2.setColor(matcha1);
                    g2.fill(new Ellipse2D.Double(bx, by, bubble, bubble));

                    g2.setComposite(AlphaComposite.SrcOver);
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                    FontMetrics fim = g2.getFontMetrics();
                    int ix = bx + (bubble - fim.stringWidth(icon)) / 2;
                    int iy = by + (bubble + fim.getAscent()) / 2 - 4;
                    g2.drawString(icon, ix, iy);

                    int startY = by + bubble + 38;

                    g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                    g2.setColor(new Color(28, 28, 28));
                    g2.drawString(title, 30, startY);

                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    g2.setColor(new Color(90, 90, 90));
                    int descY = startY + 32;
                    drawMultiline(g2, desc, 30, descY, 20);

                    int pillW = 170;
                    int pillH = 44;
                    int px = cardW - pillW - 30;   // right side
                    int py = cardH - pillH - 26;   // bottom

                    g2.setColor(hover ? matcha2 : Color.WHITE);
                    g2.fillRoundRect(px, py, pillW, pillH, 22, 22);

                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(matcha2);
                    g2.drawRoundRect(px, py, pillW, pillH, 22, 22);

                    g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    g2.setColor(hover ? Color.WHITE : matcha2);
                    String cta = "Continue  →";
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = px + (pillW - fm.stringWidth(cta)) / 2;
                    int ty = py + (pillH + fm.getAscent()) / 2 - 3;
                    g2.drawString(cta, tx, ty);

                } finally {
                    g2.dispose();
                }
            }


            private void drawMultiline(Graphics2D g2, String text, int x, int y, int lineH) {
                String[] lines = text.split("\n");
                int yy = y;
                for (String line : lines) {
                    g2.drawString(line, x, yy);
                    yy += lineH;
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(420, 260);
            }
        }

        static class AdminLoginDialog extends JDialog {
            private boolean success = false;

            private final Color matcha2;
            private final Color matcha1;
            private final Color offWhite;

            private final RoundedTextField userField = new RoundedTextField();
            private final RoundedPasswordField passField = new RoundedPasswordField();

            private final JLabel errorLabel = new JLabel(" ");
            private boolean showPassword = false;

            AdminLoginDialog(Window owner, Color matcha2, Color matcha1, Color offWhite) {
                super(owner, "Admin Login", ModalityType.APPLICATION_MODAL);
                this.matcha2 = matcha2;
                this.matcha1 = matcha1;
                this.offWhite = offWhite;

                setUndecorated(true);
                setResizable(false);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                PremiumCard card = new PremiumCard(matcha2, matcha1);
                card.setLayout(new BorderLayout());
                card.setPreferredSize(new Dimension(520, 400));

                JPanel topBar = new JPanel(new BorderLayout());
                topBar.setOpaque(false);
                topBar.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

                JLabel tiny = new JLabel("Admin Login");
                tiny.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                tiny.setForeground(new Color(120, 120, 120));

                RoundedTextButton closeBtn = new RoundedTextButton("✕", matcha2, new Color(245, 245, 245));
                closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                closeBtn.setPreferredSize(new Dimension(36, 30));
                closeBtn.addActionListener(e -> dispose());

                topBar.add(tiny, BorderLayout.WEST);
                topBar.add(closeBtn, BorderLayout.EAST);

                card.add(topBar, BorderLayout.PAGE_START);

                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);
                header.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

                JLabel title = new JLabel("Admin Login");
                title.setFont(new Font("Segoe UI", Font.BOLD, 26));
                title.setForeground(new Color(28, 28, 28));

                JLabel subtitle = new JLabel("Enter your credentials to continue");
                subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                subtitle.setForeground(new Color(90, 90, 90));

                JPanel titles = new JPanel();
                titles.setOpaque(false);
                titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
                titles.add(title);
                titles.add(Box.createVerticalStrut(6));
                titles.add(subtitle);

                JComponent icon = new IconBubble("🛠", matcha1);
                header.add(icon, BorderLayout.WEST);
                header.add(titles, BorderLayout.CENTER);

                card.add(header, BorderLayout.NORTH);

                JPanel form = new JPanel(new GridBagLayout());
                form.setOpaque(false);
                form.setBorder(BorderFactory.createEmptyBorder(8, 22, 6, 22));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(8, 0, 6, 0);

                JLabel uLbl = new JLabel("Username");
                uLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                uLbl.setForeground(new Color(60, 60, 60));
                form.add(uLbl, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;

                styleRoundedField(userField);
                userField.setColumns(26);
                form.add(userField, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 0;
                gbc.insets = new Insets(14, 0, 6, 0);

                JLabel pLbl = new JLabel("Password");
                pLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                pLbl.setForeground(new Color(60, 60, 60));
                form.add(pLbl, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;
                gbc.insets = new Insets(8, 0, 6, 0);

                JPanel passRow = new JPanel(new BorderLayout(10, 0));
                passRow.setOpaque(false);

                styleRoundedField(passField);
                passRow.add(passField, BorderLayout.CENTER);

                RoundedTextButton eyeBtn = new RoundedTextButton("👁", matcha2, new Color(245, 245, 245));
                eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                eyeBtn.setPreferredSize(new Dimension(44, 44));
                eyeBtn.addActionListener(e -> togglePassword(eyeBtn));
                passRow.add(eyeBtn, BorderLayout.EAST);

                form.add(passRow, gbc);

                gbc.gridy++;
                gbc.insets = new Insets(10, 2, 0, 0);
                errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                errorLabel.setForeground(new Color(180, 60, 60));
                form.add(errorLabel, gbc);

                card.add(form, BorderLayout.CENTER);

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
                actions.setOpaque(false);
                actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));

                RoundedButton cancel = new RoundedButton("Cancel", matcha2, false);
                RoundedButton login = new RoundedButton("Login", matcha2, true);

                cancel.addActionListener(e -> dispose());
                login.addActionListener(e -> attemptLogin());

                passField.addActionListener(e -> attemptLogin());
                userField.addActionListener(e -> passField.requestFocusInWindow());

                actions.add(cancel);
                actions.add(login);

                card.add(actions, BorderLayout.SOUTH);

                JPanel outer = new JPanel(new GridBagLayout());
                outer.setBackground(offWhite);
                outer.add(card, new GridBagConstraints());

                setContentPane(outer);
                pack();
                setLocationRelativeTo(owner);

                SwingUtilities.invokeLater(
                        () -> setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 26, 26)));
            }

            private void togglePassword(AbstractButton eyeBtn) {
                showPassword = !showPassword;
                passField.setEchoChar(showPassword ? (char) 0 : '•');
                eyeBtn.setText(showPassword ? "🙈" : "👁");
            }

            private void attemptLogin() {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword());

                if (u.isEmpty() || p.isEmpty()) {
                    errorLabel.setText("Please enter username and password");
                    return;
                }

                if (checkAdminCredentials(u, p)) {
                    success = true;
                    dispose();
                } else {
                    errorLabel.setText("Wrong username or password");
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            private boolean checkAdminCredentials(String username, String password) {
                String sql = "SELECT COUNT(*) FROM Administrator " +
                        "WHERE UCASE(TRIM([UserName])) = UCASE(TRIM(?)) " +
                        "AND UCASE(TRIM([Password])) = UCASE(TRIM(?))";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, username);
                    ps.setString(2, password);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt(1) > 0;
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorLabel.setText("Database error");
                }

                return false;
            }

            private void styleRoundedField(JTextField f) {
                f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                f.setForeground(new Color(28, 28, 28));
                f.setBackground(Color.WHITE);
                f.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            }

            boolean isSuccess() {
                return success;
            }
        }

        static class ClientRegisterDialog extends JDialog {
            private boolean success = false;

            private final Color matcha2;
            private final Color matcha1;
            private final Color offWhite;

            private final RoundedTextField firstNameField = new RoundedTextField();
            private final RoundedTextField lastNameField = new RoundedTextField();
            private final RoundedTextField phoneField = new RoundedTextField();

            private final JLabel errorLabel = new JLabel(" ");

            ClientRegisterDialog(Window owner, Color matcha2, Color matcha1, Color offWhite) {
                super(owner, "Client Registration", ModalityType.APPLICATION_MODAL);
                this.matcha2 = matcha2;
                this.matcha1 = matcha1;
                this.offWhite = offWhite;

                setUndecorated(true);
                setResizable(false);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                PremiumCard card = new PremiumCard(matcha2, matcha1);
                card.setLayout(new BorderLayout());
                card.setPreferredSize(new Dimension(560, 440));

                JPanel topBar = new JPanel(new BorderLayout());
                topBar.setOpaque(false);
                topBar.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

                JLabel tiny = new JLabel("Client Registration");
                tiny.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                tiny.setForeground(new Color(120, 120, 120));

                RoundedTextButton closeBtn = new RoundedTextButton("✕", matcha2, new Color(245, 245, 245));
                closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                closeBtn.setPreferredSize(new Dimension(36, 30));
                closeBtn.addActionListener(e -> dispose());

                topBar.add(tiny, BorderLayout.WEST);
                topBar.add(closeBtn, BorderLayout.EAST);

                card.add(topBar, BorderLayout.PAGE_START);

                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);
                header.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

                JLabel title = new JLabel("Create Client Account");
                title.setFont(new Font("Segoe UI", Font.BOLD, 26));
                title.setForeground(new Color(28, 28, 28));

                JLabel subtitle = new JLabel("Fill your details to register");
                subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                subtitle.setForeground(new Color(90, 90, 90));

                JPanel titles = new JPanel();
                titles.setOpaque(false);
                titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
                titles.add(title);
                titles.add(Box.createVerticalStrut(6));
                titles.add(subtitle);

                JComponent icon = new IconBubble("🚗", matcha1);
                header.add(icon, BorderLayout.WEST);
                header.add(titles, BorderLayout.CENTER);

                card.add(header, BorderLayout.NORTH);

                JPanel form = new JPanel(new GridBagLayout());
                form.setOpaque(false);
                form.setBorder(BorderFactory.createEmptyBorder(8, 22, 6, 22));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(8, 0, 6, 0);

                JLabel fLbl = new JLabel("First name");
                fLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                fLbl.setForeground(new Color(60, 60, 60));
                form.add(fLbl, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;

                styleRoundedField(firstNameField);
                firstNameField.setColumns(28);
                form.add(firstNameField, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 0;
                gbc.insets = new Insets(14, 0, 6, 0);

                JLabel lLbl = new JLabel("Last name");
                lLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lLbl.setForeground(new Color(60, 60, 60));
                form.add(lLbl, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;
                gbc.insets = new Insets(8, 0, 6, 0);

                styleRoundedField(lastNameField);
                lastNameField.setColumns(28);
                form.add(lastNameField, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 0;
                gbc.insets = new Insets(14, 0, 6, 0);

                JLabel pLbl = new JLabel("Phone");
                pLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                pLbl.setForeground(new Color(60, 60, 60));
                form.add(pLbl, gbc);

                gbc.gridy++;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;
                gbc.insets = new Insets(8, 0, 6, 0);

                styleRoundedField(phoneField);
                phoneField.setColumns(28);
                form.add(phoneField, gbc);

                gbc.gridy++;
                gbc.insets = new Insets(10, 2, 0, 0);
                errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                errorLabel.setForeground(new Color(180, 60, 60));
                form.add(errorLabel, gbc);

                card.add(form, BorderLayout.CENTER);

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
                actions.setOpaque(false);
                actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 20, 22));

                RoundedButton cancel = new RoundedButton("Cancel", matcha2, false);
                RoundedButton register = new RoundedButton("Register", matcha2, true);

                cancel.addActionListener(e -> dispose());
                register.addActionListener(e -> attemptRegister());

                phoneField.addActionListener(e -> attemptRegister());
                lastNameField.addActionListener(e -> phoneField.requestFocusInWindow());
                firstNameField.addActionListener(e -> lastNameField.requestFocusInWindow());

                actions.add(cancel);
                actions.add(register);

                card.add(actions, BorderLayout.SOUTH);

                JPanel outer = new JPanel(new GridBagLayout());
                outer.setBackground(offWhite);
                outer.add(card, new GridBagConstraints());

                setContentPane(outer);
                pack();
                setLocationRelativeTo(owner);

                SwingUtilities.invokeLater(
                        () -> setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 26, 26)));
            }

            private void attemptRegister() {
                String first = firstNameField.getText().trim();
                String last = lastNameField.getText().trim();
                String phone = phoneField.getText().trim();

                if (first.isEmpty() || last.isEmpty() || phone.isEmpty()) {
                    errorLabel.setText("Please fill all fields");
                    return;
                }

                if (registerClient(first, last, phone)) {
                    success = true;
                    dispose();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            private boolean registerClient(String firstName, String lastName, String phone) {
                String existsSql = "SELECT COUNT(*) FROM Client WHERE mobilePhone = ?";
                String insertSql = "INSERT INTO Client (firstName, lastName, mobilePhone) VALUES (?, ?, ?)";

                try (Connection conn = DBConnection.getConnection()) {

                    try (PreparedStatement ps = conn.prepareStatement(existsSql)) {
                        ps.setString(1, phone);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                return true;
                            }
                        }
                    }

                    try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                        ps.setString(1, firstName);
                        ps.setString(2, lastName);
                        ps.setString(3, phone);
                        ps.executeUpdate();
                    }

                    return true;

                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorLabel.setText("Database error");
                    return false;
                }
            }

            private void styleRoundedField(JTextField f) {
                f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                f.setForeground(new Color(28, 28, 28));
                f.setBackground(Color.WHITE);
                f.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            }

            boolean isSuccess() {
                return success;
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

        static class RoundedPasswordField extends JPasswordField {
            private final int ARC = 18;
            private final Color stroke = new Color(160, 175, 160);

            RoundedPasswordField() {
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

        static class RoundedTextButton extends JButton {
            private final Color stroke;
            private final Color hoverFill;
            private boolean hover = false;

            RoundedTextButton(String text, Color stroke, Color hoverFill) {
                super(text);
                this.stroke = stroke;
                this.hoverFill = hoverFill;

                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setForeground(stroke);

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

                    g2.setComposite(AlphaComposite.SrcOver);
                    g2.setColor(hover ? hoverFill : Color.WHITE);
                    g2.fillRoundRect(0, 0, w, h, 18, 18);

                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(stroke);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

                    super.paintComponent(g2);
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
    }
}
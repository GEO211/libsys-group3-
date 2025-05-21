package com.library.main;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.library.util.DatabaseConnection;
import com.library.panels.StudentPanel;
import com.library.panels.BookPanel;
import com.library.panels.BorrowingPanel;
import com.library.panels.CouncilPanel;
import com.library.panels.ReportsPanel;
import com.library.panels.SettingsPanel;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import com.library.util.Theme;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private int adminId;
    private String adminName;
    private String selectedPeriod = "WEEK"; // Default to weekly view
    
    // Add these color constants at the top of the class
    private static final Color DARK_BG = new Color(18, 18, 18);
    private static final Color CARD_BG = new Color(32, 32, 32);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(170, 170, 170);
    private static final Color ACCENT_BLUE = new Color(29, 161, 242);
    private static final Color ACCENT_GREEN = new Color(23, 191, 99);
    private static final Color ACCENT_PURPLE = new Color(120, 87, 255);
    private static final Color ACCENT_ORANGE = new Color(255, 165, 0);
    
    // Add at the top of the class
    private static final Theme LIGHT_THEME = new Theme(
        new Color(255, 255, 255),  // background - white
        new Color(248, 249, 250),  // card background - very light gray
        new Color(33, 37, 41),     // text primary - almost black
        new Color(108, 117, 125),  // text secondary - dark gray
        new Color(0, 123, 255),    // accent blue
        new Color(40, 167, 69),    // accent green
        new Color(111, 66, 193),   // accent purple
        new Color(255, 128, 0)     // accent orange
    );

    private static final Theme DARK_THEME = new Theme(
        new Color(33, 37, 41),     // background
        new Color(52, 58, 64),     // card background
        new Color(255, 255, 255),  // text primary
        new Color(173, 181, 189),  // text secondary
        new Color(0, 123, 255),    // accent blue
        new Color(40, 167, 69),    // accent green
        new Color(111, 66, 193),   // accent purple
        new Color(255, 128, 0)     // accent orange
    );

    private Theme currentTheme = LIGHT_THEME;
    
    // Update the menuItems array with MDI Unicode values
    private final Object[][] menuItems = {
        {"MAIN", new String[][]{
            {"Dashboard", "⌂"}  // House icon
        }},
        {"MANAGEMENT", new String[][]{
            {"Students", "♟"},  // Person icon
            {"Books", "≣"},     // Book icon
            {"Departments", "♣"}   // Group icon
        }},
        {"TRANSACTIONS", new String[][]{
            {"Borrowed Books", "⇌"},  // Exchange icon
            {"Returns", "↶"},         // Return arrow
            {"Overdue Books", "⚠"},    // Warning icon
            {"Transaction History", "📋"}
        }},
        {"SYSTEM", new String[][]{
            {"Reports", "◈"},    // Chart icon
            {"Settings", "⚙"}    // Gear icon
        }}
    };
    
    // Add this field to track active menu item
    private String activeMenuItem = "Dashboard"; // Default active item
    
    private static final Color MENU_HOVER_COLOR = new Color(0, 123, 255, 10);
    private static final Color MENU_ACTIVE_COLOR = new Color(0, 123, 255, 15);
    private static final Color MENU_ACTIVE_BORDER = new Color(0, 123, 255);
    private static final Color MENU_TEXT_COLOR = new Color(33, 37, 41);
    
    // Add these at the top of MainFrame class
    private static int currentAdminId;

    public static void setCurrentAdminId(int adminId) {
        currentAdminId = adminId;
    }

    public static int getCurrentAdminId() {
        return currentAdminId;
    }
    
    public MainFrame(int adminId) {
        this.adminId = adminId;
        setCurrentAdminId(adminId);
        loadAdminInfo();
        initializeUI();
    }
    
    private void loadAdminInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT CONCAT(first_name, ' ', last_name) as name FROM admins WHERE admin_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, adminId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                adminName = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            adminName = "Unknown Admin";
        }
    }
    
    private void initializeUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 1400);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        
        // Create sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);
        
        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Create header
        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);
        
        // Show dashboard by default
        showDashboard();
        
        add(mainPanel);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(currentTheme.cardBackground);
        header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Left side with title
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(currentTheme.textPrimary);
        
        // Right side with user info and theme toggle
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        // Theme toggle button
        JToggleButton themeToggle = new JToggleButton(new ImageIcon("icons/light.png"));
        themeToggle.setSelectedIcon(new ImageIcon("icons/dark.png"));
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.setSelected(currentTheme == DARK_THEME);
        themeToggle.addActionListener(e -> {
            currentTheme = themeToggle.isSelected() ? DARK_THEME : LIGHT_THEME;
            updateTheme();
        });
        
        JLabel adminLabel = new JLabel(adminName);
        adminLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminLabel.setForeground(currentTheme.textPrimary);
        
        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        
        rightPanel.add(themeToggle);
        rightPanel.add(adminLabel);
        rightPanel.add(logoutBtn);
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private void updateTheme() {
        // Update all components with new theme
        mainPanel.setBackground(currentTheme.background);
        contentPanel.setBackground(currentTheme.background);
        
        // Update active panel with new theme
        Component activePanel = null;
        if (contentPanel.getComponentCount() > 0) {
            activePanel = contentPanel.getComponent(0);
            
            if (activePanel instanceof StudentPanel) {
                ((StudentPanel) activePanel).updateTheme(currentTheme);
            } else if (activePanel instanceof BookPanel) {
                ((BookPanel) activePanel).updateTheme(currentTheme);
            } else if (activePanel instanceof BorrowingPanel) {
                ((BorrowingPanel) activePanel).updateTheme(currentTheme);
            } else if (activePanel instanceof CouncilPanel) {
                ((CouncilPanel) activePanel).updateTheme(currentTheme);
            } else if (activePanel instanceof ReportsPanel) {
                ((ReportsPanel) activePanel).updateTheme(currentTheme);
            } else if (activePanel instanceof SettingsPanel) {
                ((SettingsPanel) activePanel).updateTheme(currentTheme);
            }
        }
        
        // Update other UI elements like the header
        JPanel header = (JPanel) mainPanel.getComponent(2); // Header is at index 2
        if (header != null) {
            header.setBackground(currentTheme.cardBackground);
            
            // Update components in header
            for (Component c : header.getComponents()) {
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(currentTheme.textPrimary);
                } else if (c instanceof JPanel) {
                    c.setBackground(currentTheme.cardBackground);
                    // Update components in the right panel
                    for (Component innerC : ((JPanel) c).getComponents()) {
                        if (innerC instanceof JLabel) {
                            ((JLabel) innerC).setForeground(currentTheme.textPrimary);
                        }
                    }
                }
            }
        }
        
        // Update sidebar
        JPanel sidebar = (JPanel) mainPanel.getComponent(0);
        if (sidebar != null) {
            sidebar.setBackground(currentTheme.background);
            updateSidebarButtons(sidebar);
        }
        
        // Trigger repaint of all components
        SwingUtilities.updateComponentTreeUI(this);
        
        // If we're on the dashboard, refresh it with new theme
        if (activeMenuItem.equals("Dashboard")) {
            showDashboard();
        }
    }
    
    /**
     * Updates the sidebar buttons with the current theme
     */
    private void updateSidebarButtons(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                // Skip updating - JButtons will be handled by the component UI
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText() != null && label.getText().equals(activeMenuItem)) {
                    label.setForeground(currentTheme.accentBlue);
                } else {
                    if (label.getFont().getStyle() == Font.BOLD) {
                        // This is a section header
                        label.setForeground(currentTheme.textSecondary);
                    } else {
                        // This is a regular menu item
                        label.setForeground(currentTheme.textPrimary);
                    }
                }
            } else if (comp instanceof Container) {
                // Recursively update nested containers
                updateSidebarButtons((Container) comp);
            }
        }
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(currentTheme.textPrimary);
        button.setBackground(currentTheme.cardBackground);
        button.setBorder(new RoundedBorder(8, currentTheme.accentBlue));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(currentTheme.accentBlue);
                button.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(currentTheme.cardBackground);
                button.setForeground(currentTheme.textPrimary);
            }
        });
        
        return button;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(null);

        // Section headers
        for (Object[] section : menuItems) {
            // Add section label
            JLabel sectionLabel = new JLabel(section[0].toString());
            sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            sectionLabel.setForeground(new Color(108, 117, 125));
            sectionLabel.setBorder(BorderFactory.createEmptyBorder(15, 12, 5, 0));
            sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(sectionLabel);

            // Add menu items
            String[][] items = (String[][])section[1];
            for (String[] item : items) {
                JButton menuButton = createModernMenuButton(item[0], item[1]);
                menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                sidebar.add(menuButton);
            }
        }

        return sidebar;
    }
    
    private JButton createModernMenuButton(String text, String icon) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (text.equals(activeMenuItem)) {
                    g2.setColor(MENU_ACTIVE_COLOR);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(MENU_ACTIVE_BORDER);
                    g2.fillRect(0, 0, 3, getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(MENU_HOVER_COLOR);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
            }
        };

        setupMenuButton(button, text, icon);
        return button;
    }
    
    private void setupMenuButton(JButton button, String text, String icon) {
        button.setLayout(null);
        button.setPreferredSize(new Dimension(250, 35));
        button.setMaximumSize(new Dimension(250, 35));
        button.setMinimumSize(new Dimension(250, 35));

        JLabel iconLabel = createMenuLabel(icon, true);
        JLabel textLabel = createMenuLabel(text, false);

        button.add(iconLabel);
        button.add(textLabel);

        styleButton(button);
        addMenuButtonListener(button, text);
    }
    
    private JLabel createMenuLabel(String text, boolean isIcon) {
        JLabel label = new JLabel(text);
        if (isIcon) {
            label.setFont(new Font("Dialog", Font.BOLD, 16));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBounds(12, 8, 16, 16);
            
            if (text.equals("⚠")) {
                label.setVerticalAlignment(SwingConstants.CENTER);
            }
        } else {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setBounds(36, 8, 200, 16);
        }
        
        label.setForeground(text.equals(activeMenuItem) ? MENU_ACTIVE_BORDER : MENU_TEXT_COLOR);
        return label;
    }
    
    private void styleButton(JButton button) {
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void addMenuButtonListener(JButton button, String text) {
        button.addActionListener(e -> {
            // Only handle click if it's not the current active item
            if (!text.equals(activeMenuItem)) {
                String oldActive = activeMenuItem;  // Store old active item
                activeMenuItem = text;  // Set new active item
                handleMenuClick(text);  // Handle the menu action
                
                // Find and update the old active button and new active button
                updateMenuButtonStates();
            }
        });
    }
    
    private void updateMenuButtonStates() {
        // Get the sidebar panel
        Component sidebar = mainPanel.getComponent(0);
        if (sidebar instanceof JPanel) {
            // Update all menu buttons
            updateButtonsInContainer((JPanel) sidebar);
        }
    }
    
    private void updateButtonsInContainer(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                // Force button to repaint with new active state
                btn.revalidate();
                btn.repaint();
            } else if (comp instanceof Container) {
                // Recursively check nested containers
                updateButtonsInContainer((Container) comp);
            }
        }
    }
    
    private void handleMenuClick(String menuItem) {
        contentPanel.removeAll();
        
        switch (menuItem) {
            case "Dashboard":
                showDashboard();
                break;
            case "Students":
                contentPanel.add(new StudentPanel(currentTheme));
                break;
            case "Books":
                contentPanel.add(new BookPanel(currentTheme));
                break;
            case "Borrowed Books":
                contentPanel.add(new BorrowingPanel(adminId, "borrowed", currentTheme));
                break;
            case "Returns":
                contentPanel.add(new BorrowingPanel(adminId, "returns", currentTheme));
                break;
            case "Overdue Books":
                contentPanel.add(new BorrowingPanel(adminId, "overdue", currentTheme));
                break;
            case "Transaction History":
                contentPanel.add(new BorrowingPanel(adminId, "history", currentTheme));
                break;
            case "Departments":
                contentPanel.add(new CouncilPanel(currentTheme));
                break;
            case "Reports":
                contentPanel.add(new ReportsPanel(currentTheme));
                break;
            case "Settings":
                contentPanel.add(new SettingsPanel(adminId, currentTheme));
                break;
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setBackground(currentTheme.background);
        dashboard.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Stats panel with GridBagLayout
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 10, 0, 10);

        // Add stat cards
        gbc.gridx = 0; gbc.gridy = 0;
        addModernStatCard(statsPanel, "📚", "Total Books", getTotalBooks(), currentTheme.accentBlue, gbc);
        gbc.gridx = 1;
        addModernStatCard(statsPanel, "👥", "Active Students", getActiveStudents(), currentTheme.accentGreen, gbc);
        gbc.gridx = 2;
        addModernStatCard(statsPanel, "📖", "Books Borrowed", getBorrowedBooks(), currentTheme.accentPurple, gbc);
        gbc.gridx = 3;
        addModernStatCard(statsPanel, "⚠", "Overdue Books", getOverdueBooks(), currentTheme.accentOrange, gbc);

        // Main content panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add analytics panels
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(createModernAnalyticsPanel("Recent Activities", currentTheme.accentBlue, 
            this::setupRecentActivities), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(createModernAnalyticsPanel("Popular Books", currentTheme.accentGreen,
            this::setupPopularBooks), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(createModernAnalyticsPanel("Borrowing Trends", currentTheme.accentPurple,
            this::setupBorrowingTrends), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(createModernAnalyticsPanel("System Status", currentTheme.accentOrange,
            this::setupSystemStatus), gbc);

        dashboard.add(statsPanel, BorderLayout.NORTH);
        dashboard.add(contentPanel, BorderLayout.CENTER);

        this.contentPanel.removeAll();
        this.contentPanel.add(dashboard);
        this.contentPanel.revalidate();
        this.contentPanel.repaint();
    }
    
    // Update the addModernStatCard method
    private void addModernStatCard(JPanel container, String icon, String title, String value, Color accentColor, GridBagConstraints gbc) {
        JPanel card = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, 
                    new Color(255, 255, 255, 240), // Start color (slightly transparent white)
                    0, getHeight(),
                    new Color(255, 255, 255, 200)  // End color (more transparent white)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Add subtle border
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(250, 120));

        // Icon and title in one panel with modern styling
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);

        // Modern icon label with accent color background
        JLabel iconLabel = new JLabel(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular background
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 20));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setForeground(accentColor);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(currentTheme.textSecondary);

        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Value with modern styling
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(currentTheme.textPrimary);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        container.add(card, gbc);
    }
    
    // Update the createModernAnalyticsPanel method
    private JPanel createModernAnalyticsPanel(String title, Color accentColor, Consumer<JPanel> contentBuilder) {
        JPanel panel = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create frosted glass effect
                GradientPaint gradient = new GradientPaint(
                    0, 0,
                    new Color(255, 255, 255, 230),
                    0, getHeight(),
                    new Color(255, 255, 255, 200)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Add subtle border
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Modern title with accent line
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw accent line
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(0, getHeight()-1, 50, getHeight()-1);
            }
        };
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(currentTheme.textPrimary);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        panel.add(titlePanel, BorderLayout.NORTH);
        contentBuilder.accept(panel);

        return panel;
    }
    
    // New modern rounded border class
    private static class ModernRoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius = 12;
        private final float thickness = 1.5f;

        public ModernRoundedBorder(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
    
    private JPanel createAnalyticsPanel(String title, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, accentColor),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }
    
    private void addStatCard(JPanel container, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, color),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        container.add(card);
    }
    
    private JPanel createModernSystemMonitoringPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Database Status
        addModernMonitoringItem(panel, "Database Connection", "Connected", 
            new Color(40, 167, 69));  // Green

        // Memory Usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576;
        long totalMemory = runtime.totalMemory() / 1048576;
        addModernMonitoringItem(panel, "Memory Usage", 
            usedMemory + "MB / " + totalMemory + "MB", 
            new Color(40, 167, 69));  // Green

        // Active Users
        addModernMonitoringItem(panel, "Active Users", "1 online", 
            new Color(0, 123, 255));  // Blue

        // Last Backup
        addModernMonitoringItem(panel, "Last Backup", "Today 09:00 AM", 
            new Color(111, 66, 193));  // Purple

        return panel;
    }

    private void addModernMonitoringItem(JPanel panel, String label, String value, Color color) {
        // Create a panel with GridBagLayout for better alignment
        JPanel item = new JPanel(new GridBagLayout());
        item.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);  // Add some vertical spacing between items

        // Label on the left
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        item.add(labelText, gbc);

        // Status dot and value on the right
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        valuePanel.setOpaque(false);

        // Colored dot
        JLabel dotLabel = new JLabel("•");
        dotLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dotLabel.setForeground(color);

        // Value
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueText.setForeground(new Color(33, 37, 41));

        valuePanel.add(dotLabel);
        valuePanel.add(valueText);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        item.add(valuePanel, gbc);

        panel.add(item);
    }
    
    // Helper methods to get data
    private String[] getRecentActivities() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First check if table exists
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "audit_log", null);
            
            if (!tables.next()) {
                return new String[]{"No activity logs available"};
            }
            
            String query = """
                SELECT 
                    l.timestamp,
                    l.action,
                    l.details,
                    CONCAT(a.first_name, ' ', a.last_name) as admin_name
                FROM audit_log l
                JOIN admins a ON l.admin_id = a.admin_id
                ORDER BY l.timestamp DESC 
                LIMIT 10
                """;
                
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            List<String> activities = new ArrayList<>();
            while (rs.next()) {
                String activity = String.format("[%s] %s by %s: %s",
                    rs.getTimestamp("timestamp").toString(),
                    rs.getString("action"),
                    rs.getString("admin_name"),
                    rs.getString("details")
                );
                activities.add(activity);
            }
            
            return activities.isEmpty() ? 
                new String[]{"No recent activities"} : 
                activities.toArray(new String[0]);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[]{"Error loading activities"};
        }
    }
    
    private JComponent createPopularBooksChart() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Set background
                g2.setColor(currentTheme.background);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Get data
                Map<String, Integer> data = getPopularBooksData();
                if (data.isEmpty()) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2.setColor(currentTheme.textPrimary);
                    g2.drawString("No borrowing data available", getWidth()/2 - 50, getHeight()/2);
                    return;
                }
                
                // Calculate dimensions
                int padding = 40;
                int barWidth = (getWidth() - 2 * padding) / data.size() - 10;
                int maxValue = Collections.max(data.values());
                double scale = (double)(getHeight() - 2 * padding) / maxValue;
                
                // Draw grid lines
                g2.setColor(currentTheme.textSecondary);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 
                    0, new float[]{2}, 0));
                
                // Draw bars
                int x = padding;
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int height = (int)(entry.getValue() * scale);
                    int y = getHeight() - padding - height;
                    
                    // Draw bar
                    g2.setColor(currentTheme.accentBlue);
                    g2.fillRect(x, y, barWidth, height);
                    
                    // Draw value
                    g2.setColor(currentTheme.textPrimary);
                    g2.drawString(String.valueOf(entry.getValue()), 
                        x + barWidth/2 - 10, y - 5);
                    
                    // Draw label
                    g2.rotate(-Math.PI/4, x + barWidth/2, getHeight() - padding + 20);
                    g2.drawString(entry.getKey(), x + barWidth/2, getHeight() - padding + 20);
                    g2.rotate(Math.PI/4, x + barWidth/2, getHeight() - padding + 20);
                    
                    x += barWidth + 10;
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 300);
            }
        };
    }
    
    private Map<String, Integer> getPopularBooksData() {
        Map<String, Integer> data = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = """
                SELECT b.title, COUNT(*) as borrow_count 
                FROM borrowings br 
                JOIN books b ON br.book_id = b.book_id
                WHERE br.borrow_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) 
                GROUP BY b.book_id, b.title 
                ORDER BY borrow_count DESC 
                LIMIT 5
                """;
                
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    if (title.length() > 20) {
                        title = title.substring(0, 17) + "...";
                    }
                    data.put(title, rs.getInt("borrow_count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    private JComponent createBorrowingTrendsChart() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Get data
                Map<String, Integer> data = getBorrowingTrendsData();
                if (data.isEmpty() || (data.size() == 1 && data.containsKey("No Data"))) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2.drawString("No borrowing trends available", getWidth()/2 - 50, getHeight()/2);
                    return;
                }
                
                // Colors
                Color bgColor = new Color(30, 33, 36);
                Color gridColor = new Color(44, 47, 51, 50);
                Color lineColor = new Color(255, 165, 0);
                Color dotColor = new Color(255, 165, 0);
                Color textColor = new Color(200, 200, 200);
                
                // Set background
                g2.setColor(currentTheme.background);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Calculate dimensions
                int padding = 40;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;
                int maxValue = Collections.max(data.values());
                double xScale = (double)width / (data.size() - 1);
                double yScale = (double)height / maxValue;
                
                // Draw grid lines
                g2.setColor(currentTheme.textSecondary.brighter());
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 
                    0, new float[]{2}, 0));
                
                // Horizontal grid lines
                int gridCount = 5;
                for (int i = 0; i <= gridCount; i++) {
                    int y = getHeight() - padding - (i * height / gridCount);
                    g2.drawLine(padding, y, getWidth() - padding, y);
                    
                    // Draw Y-axis labels
                    g2.setColor(currentTheme.textPrimary);
                    String label = String.valueOf(i * maxValue / gridCount);
                    g2.drawString(label, padding - 30, y + 5);
                    g2.setColor(gridColor);
                }
                
                // Draw smooth curve
                g2.setColor(currentTheme.accentPurple);
                g2.setStroke(new BasicStroke(2f));
                
                int[] xPoints = new int[data.size()];
                int[] yPoints = new int[data.size()];
                int i = 0;
                
                // Collect points
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    xPoints[i] = padding + (int)(i * xScale);
                    yPoints[i] = getHeight() - padding - (int)(entry.getValue() * yScale);
                    i++;
                }
                
                // Draw curved line
                Path2D path = new Path2D.Double();
                path.moveTo(xPoints[0], yPoints[0]);
                
                for (i = 0; i < xPoints.length - 1; i++) {
                    int x1 = xPoints[i];
                    int x2 = xPoints[i + 1];
                    int y1 = yPoints[i];
                    int y2 = yPoints[i + 1];
                    
                    double cx1 = x1 + (x2 - x1) / 3;
                    double cy1 = y1;
                    double cx2 = x2 - (x2 - x1) / 3;
                    double cy2 = y2;
                    
                    path.curveTo(cx1, cy1, cx2, cy2, x2, y2);
                }
                
                g2.draw(path);
                
                // Draw points and labels
                i = 0;
                int labelStep = switch (selectedPeriod) {
                    case "WEEK" -> 1;      // Show every day
                    case "MONTH" -> 2;     // Show every other day
                    case "3MONTHS" -> 7;   // Show weekly
                    default -> 1;
                };
                
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    if (i % labelStep == 0) {  // Only draw some labels to prevent overcrowding
                        int x = xPoints[i];
                        g2.setColor(currentTheme.textPrimary);
                        g2.rotate(-Math.PI/4, x, getHeight() - padding + 10);
                        g2.drawString(entry.getKey(), x, getHeight() - padding + 10);
                        g2.rotate(Math.PI/4, x, getHeight() - padding + 10);
                    }
                    i++;
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 300);
            }
        };
    }
    
    private Map<String, Integer> getBorrowingTrendsData() {
        Map<String, Integer> data = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String interval = switch (selectedPeriod) {
                case "WEEK" -> "7 DAY";
                case "MONTH" -> "30 DAY";
                case "3MONTHS" -> "90 DAY";
                default -> "7 DAY";
            };
            
            String query = """
                SELECT DATE(borrow_date) as date, COUNT(*) as count 
                FROM borrowings 
                WHERE borrow_date >= DATE_SUB(CURRENT_DATE, INTERVAL %s) 
                GROUP BY DATE(borrow_date) 
                ORDER BY date
                """.formatted(interval);
                
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                // Create a map with all dates in the range (including zeros)
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = switch (selectedPeriod) {
                    case "WEEK" -> endDate.minusDays(7);
                    case "MONTH" -> endDate.minusDays(30);
                    case "3MONTHS" -> endDate.minusDays(90);
                    default -> endDate.minusDays(7);
                };
                
                // Initialize all dates with zero
                LocalDate date = startDate;
                while (!date.isAfter(endDate)) {
                    data.put(formatDate(date), 0);
                    date = date.plusDays(1);
                }
                
                // Fill in actual data
                while (rs.next()) {
                    LocalDate borrowDate = rs.getDate("date").toLocalDate();
                    data.put(formatDate(borrowDate), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            data.put("No Data", 0);
        }
        return data;
    }
    
    private String formatDate(LocalDate date) {
        return switch (selectedPeriod) {
            case "WEEK" -> date.format(DateTimeFormatter.ofPattern("EEE"));
            case "MONTH" -> date.format(DateTimeFormatter.ofPattern("MMM dd"));
            case "3MONTHS" -> date.format(DateTimeFormatter.ofPattern("MMM dd"));
            default -> date.format(DateTimeFormatter.ofPattern("MMM dd"));
        };
    }
    
    private boolean isDatabaseConnected() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    private int getActiveUserCount() {
        // Implement active users tracking
        return 1; // Placeholder
    }
    
    private String getLastBackupTime() {
        // Implement backup time tracking
        return "Today 09:00 AM"; // Placeholder
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private String getTotalBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT SUM(quantity) as total FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return String.valueOf(rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getActiveStudents() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) as count FROM students WHERE status = 'Active'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return String.valueOf(rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getBorrowedBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) as count FROM borrowings WHERE status = 'Borrowed'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return String.valueOf(rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getOverdueBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) as count FROM borrowings WHERE status = 'Borrowed' AND due_date < CURRENT_DATE";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return String.valueOf(rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius/2;
            return insets;
        }
    }

    // Add these setup methods
    private void setupRecentActivities(JPanel panel) {
        JList<String> activityList = new JList<>(getRecentActivities());
        activityList.setBackground(currentTheme.cardBackground);
        activityList.setForeground(currentTheme.textPrimary);
        activityList.setSelectionBackground(currentTheme.accentBlue);
        activityList.setFixedCellHeight(35);
        activityList.setBorder(null);
        
        JScrollPane scrollPane = new JScrollPane(activityList);
        scrollPane.setBorder(null);
        scrollPane.setBackground(currentTheme.cardBackground);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupPopularBooks(JPanel panel) {
        panel.add(createPopularBooksChart(), BorderLayout.CENTER);
    }

    private void setupBorrowingTrends(JPanel panel) {
        JPanel trendsContent = new JPanel(new BorderLayout());
        trendsContent.setOpaque(false);

        // Period selector
        JPanel periodSelector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodSelector.setOpaque(false);

        String[] periods = {"Past Week", "Past Month", "Past 3 Months"};
        JComboBox<String> periodCombo = new JComboBox<>(periods);
        periodCombo.setBackground(currentTheme.cardBackground);
        periodCombo.setForeground(currentTheme.textPrimary);
        periodCombo.setFocusable(false);
        periodCombo.addActionListener(e -> {
            switch (periodCombo.getSelectedIndex()) {
                case 0: selectedPeriod = "WEEK"; break;
                case 1: selectedPeriod = "MONTH"; break;
                case 2: selectedPeriod = "3MONTHS"; break;
            }
            trendsContent.repaint();
        });

        JLabel showLabel = new JLabel("Show: ");
        showLabel.setForeground(currentTheme.textSecondary);
        periodSelector.add(showLabel);
        periodSelector.add(periodCombo);

        trendsContent.add(periodSelector, BorderLayout.NORTH);
        trendsContent.add(createBorrowingTrendsChart(), BorderLayout.CENTER);
        panel.add(trendsContent, BorderLayout.CENTER);
    }

    private void setupSystemStatus(JPanel panel) {
        panel.add(createModernSystemMonitoringPanel(), BorderLayout.CENTER);
    }

    // Add method to load Font Awesome
    private Font loadFontAwesome(float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, 
                new File("fonts/fontawesome-webfont.ttf"));
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Dialog", Font.PLAIN, (int)size);
        }
    }
} 
package com.library.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.library.util.DatabaseConnection;
import com.library.util.Theme;

public class SettingsPanel extends JPanel {

    private int adminId;
    private JTextField usernameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JTextField fineRateField;
    private JTextField borrowDaysField;
    private JTextField emailField;
    private JCheckBox emailNotificationsCheck;
    
    // Theme instance
    private Theme currentTheme;
    
    // Define style variables that will be set based on theme
    private Color PRIMARY_COLOR;
    private Color ACCENT_COLOR;
    private Color BACKGROUND_COLOR;
    private Color TABLE_HEADER_COLOR;
    private Color BUTTON_TEXT_COLOR;
    private Font HEADER_FONT;
    private Font CONTENT_FONT;
    
    // UI components that need theme updates
    private JTabbedPane tabbedPane;
    private JPanel profilePanel;
    private JPanel systemPanel;
    private JPanel backupPanel;
    
    public SettingsPanel(int adminId) {
        this(adminId, null);
    }
    
    public SettingsPanel(int adminId, Theme theme) {
        this.adminId = adminId;
        
        // Set theme and initialize colors
        this.currentTheme = theme != null ? theme : createDefaultTheme();
        updateThemeColors();
        
        initializeUI();
        loadSettings();
    }
    
    /**
     * Creates a default light theme when none is provided
     */
    private Theme createDefaultTheme() {
        return new Theme(
            new Color(255, 255, 255),  // background - white
            new Color(248, 249, 250),  // card background - very light gray
            new Color(33, 37, 41),     // text primary - almost black
            new Color(108, 117, 125),  // text secondary - dark gray
            new Color(0, 123, 255),    // accent blue
            new Color(40, 167, 69),    // accent green
            new Color(111, 66, 193),   // accent purple
            new Color(255, 128, 0)     // accent orange
        );
    }
    
    /**
     * Updates the panel with a new theme
     */
    public void updateTheme(Theme theme) {
        if (theme == null) return;
        
        this.currentTheme = theme;
        updateThemeColors();
        applyThemeToComponents();
        repaint();
        revalidate();
    }
    
    /**
     * Updates color variables based on the current theme
     */
    private void updateThemeColors() {
        PRIMARY_COLOR = new Color(220, 220, 220);    // Light Gray
        ACCENT_COLOR = new Color(200, 200, 200);     // Slightly Darker Gray
        BACKGROUND_COLOR = currentTheme.background;
        TABLE_HEADER_COLOR = currentTheme.cardBackground;
        BUTTON_TEXT_COLOR = currentTheme.textPrimary;
        HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
        CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    }
    
    /**
     * Applies the current theme to all components
     */
    private void applyThemeToComponents() {
        // Update main panel
        setBackground(BACKGROUND_COLOR);
        
        // Update tabbed pane
        if (tabbedPane != null) {
            tabbedPane.setBackground(BACKGROUND_COLOR);
            tabbedPane.setForeground(currentTheme.textPrimary);
            
            // Update all tab panels
            if (profilePanel != null) {
                updatePanelColors(profilePanel);
            }
            
            if (systemPanel != null) {
                updatePanelColors(systemPanel);
            }
            
            if (backupPanel != null) {
                updatePanelColors(backupPanel);
            }
        }
    }
    
    /**
     * Updates all colors in a panel recursively
     */
    private void updatePanelColors(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
        
        // Update all components in the panel
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel) {
                updatePanelColors((JPanel) c);
            } else if (c instanceof JTextField) {
                JTextField field = (JTextField) c;
                field.setBackground(currentTheme.cardBackground);
                field.setForeground(currentTheme.textPrimary);
                field.setCaretColor(currentTheme.textPrimary);
            } else if (c instanceof JPasswordField) {
                JPasswordField field = (JPasswordField) c;
                field.setBackground(currentTheme.cardBackground);
                field.setForeground(currentTheme.textPrimary);
                field.setCaretColor(currentTheme.textPrimary);
            } else if (c instanceof JLabel) {
                ((JLabel) c).setForeground(currentTheme.textPrimary);
            } else if (c instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) c;
                checkBox.setBackground(BACKGROUND_COLOR);
                checkBox.setForeground(currentTheme.textPrimary);
            }
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(HEADER_FONT);
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(currentTheme.textPrimary);
        
        // Profile Settings Panel
        profilePanel = createProfilePanel();
        tabbedPane.addTab("Profile Settings", profilePanel);
        
        // System Settings Panel
        systemPanel = createSystemPanel();
        tabbedPane.addTab("System Settings", systemPanel);
        
        // Backup Settings Panel
        backupPanel = createBackupPanel();
        tabbedPane.addTab("Backup & Restore", backupPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustPanelSizes();
            }
        });
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        // User Information Panel
        JPanel userInfoPanel = createSectionPanel("User Information");
        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.gridx = 0;
        innerGbc.gridy = 0;
        innerGbc.anchor = GridBagConstraints.WEST;
        innerGbc.insets = new Insets(5, 5, 5, 5);
        
        usernameField = createStyledTextField(20);
        emailField = createStyledTextField(20);
        
        addFormField(userInfoPanel, "Username:", usernameField, innerGbc);
        innerGbc.gridy++;
        addFormField(userInfoPanel, "Email:", emailField, innerGbc);
        
        panel.add(userInfoPanel, gbc);
        
        // Password Change Panel
        gbc.gridy++;
        JPanel passwordPanel = createSectionPanel("Change Password");
        innerGbc.gridy = 0;
        
        currentPasswordField = createStyledPasswordField(20);
        newPasswordField = createStyledPasswordField(20);
        confirmPasswordField = createStyledPasswordField(20);
        
        addFormField(passwordPanel, "Current Password:", currentPasswordField, innerGbc);
        innerGbc.gridy++;
        addFormField(passwordPanel, "New Password:", newPasswordField, innerGbc);
        innerGbc.gridy++;
        addFormField(passwordPanel, "Confirm Password:", confirmPasswordField, innerGbc);
        
        panel.add(passwordPanel, gbc);
        
        // Save Button
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveProfileBtn = createStyledButton("Save Profile", PRIMARY_COLOR);
        saveProfileBtn.addActionListener(e -> saveProfile());
        panel.add(saveProfileBtn, gbc);
        
        // Add filler to push everything up
        gbc.gridy++;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createSystemPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        // Library Settings Panel
        JPanel libraryPanel = createSectionPanel("Library Settings");
        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.gridx = 0;
        innerGbc.gridy = 0;
        innerGbc.anchor = GridBagConstraints.WEST;
        innerGbc.insets = new Insets(5, 5, 5, 5);
        
        fineRateField = createStyledTextField(10);
        borrowDaysField = createStyledTextField(10);
        emailNotificationsCheck = new JCheckBox("Enable Email Notifications");
        emailNotificationsCheck.setFont(CONTENT_FONT);
        emailNotificationsCheck.setBackground(BACKGROUND_COLOR);
        emailNotificationsCheck.setForeground(currentTheme.textPrimary);
        
        addFormField(libraryPanel, "Fine Rate (₱ per day):", fineRateField, innerGbc);
        innerGbc.gridy++;
        addFormField(libraryPanel, "Default Borrow Days:", borrowDaysField, innerGbc);
        innerGbc.gridy++;
        innerGbc.gridwidth = 2;
        libraryPanel.add(emailNotificationsCheck, innerGbc);
        
        panel.add(libraryPanel, gbc);
        
        // Save Button
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveSystemBtn = createStyledButton("Save Settings", PRIMARY_COLOR);
        saveSystemBtn.addActionListener(e -> saveSystemSettings());
        panel.add(saveSystemBtn, gbc);
        
        // Add filler to push everything up
        gbc.gridy++;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createBackupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        // Backup Section
        JPanel backupPanel = createSectionPanel("Database Backup");
        JButton backupBtn = createStyledButton("Create Backup", PRIMARY_COLOR);
        backupBtn.addActionListener(e -> createBackup());
        backupPanel.add(backupBtn);
        
        panel.add(backupPanel, gbc);
        
        // Restore Section
        gbc.gridy++;
        JPanel restorePanel = createSectionPanel("Database Restore");
        JButton restoreBtn = createStyledButton("Restore from Backup", ACCENT_COLOR);
        restoreBtn.addActionListener(e -> restoreBackup());
        restorePanel.add(restoreBtn);
        
        panel.add(restorePanel, gbc);
        
        // Add filler to push everything up
        gbc.gridy++;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                HEADER_FONT, BUTTON_TEXT_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return panel;
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(CONTENT_FONT);
        label.setForeground(BUTTON_TEXT_COLOR);
        
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }
    
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(CONTENT_FONT);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(CONTENT_FONT);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(backgroundColor);
        button.setPreferredSize(new Dimension(160, 35));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void adjustPanelSizes() {
        // Adjust field widths based on panel size
        int panelWidth = getWidth();
        int fieldWidth = Math.max(200, panelWidth / 3);
        
        Dimension fieldSize = new Dimension(fieldWidth, 35);
        
        // Update text field sizes
        if (usernameField != null) {
            usernameField.setPreferredSize(fieldSize);
        }
        if (emailField != null) {
            emailField.setPreferredSize(fieldSize);
        }
        if (currentPasswordField != null) {
            currentPasswordField.setPreferredSize(fieldSize);
        }
        if (newPasswordField != null) {
            newPasswordField.setPreferredSize(fieldSize);
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.setPreferredSize(fieldSize);
        }
        if (fineRateField != null) {
            fineRateField.setPreferredSize(fieldSize);
        }
        if (borrowDaysField != null) {
            borrowDaysField.setPreferredSize(fieldSize);
        }
        
        revalidate();
    }
    
    private void loadSettings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load admin info
            String adminQuery = "SELECT username, email FROM admins WHERE admin_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(adminQuery);
            pstmt.setInt(1, adminId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));
            }
            
            // Load system settings
            String settingsQuery = "SELECT * FROM settings WHERE setting_key IN ('fine_rate', 'borrow_days', 'email_notifications')";
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(settingsQuery);
            
            while (rs.next()) {
                String key = rs.getString("setting_key");
                String value = rs.getString("setting_value");
                
                switch (key) {
                    case "fine_rate":
                        fineRateField.setText(value);
                        break;
                    case "borrow_days":
                        borrowDaysField.setText(value);
                        break;
                    case "email_notifications":
                        emailNotificationsCheck.setSelected("true".equals(value));
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading settings: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveProfile() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (newPassword.length() > 0 && !newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "New passwords do not match",
                "Password Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (newPassword.length() > 0) {
                // Verify current password
                String currentPassword = new String(currentPasswordField.getPassword());
                String verifyQuery = "SELECT admin_id FROM admins WHERE admin_id = ? AND password = ?";
                PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery);
                verifyStmt.setInt(1, adminId);
                verifyStmt.setString(2, currentPassword);
                
                if (!verifyStmt.executeQuery().next()) {
                    JOptionPane.showMessageDialog(this,
                        "Current password is incorrect",
                        "Password Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Update profile
            String updateQuery = newPassword.length() > 0
                    ? "UPDATE admins SET username = ?, email = ?, password = ? WHERE admin_id = ?"
                    : "UPDATE admins SET username = ?, email = ? WHERE admin_id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setString(1, usernameField.getText());
            pstmt.setString(2, emailField.getText());
            
            if (newPassword.length() > 0) {
                pstmt.setString(3, newPassword);
                pstmt.setInt(4, adminId);
            } else {
                pstmt.setInt(3, adminId);
            }
            
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Profile updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Clear password fields
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving profile: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveSystemSettings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Validate numeric inputs
            double fineRate = Double.parseDouble(fineRateField.getText());
            int borrowDays = Integer.parseInt(borrowDaysField.getText());
            
            if (fineRate < 0 || borrowDays < 1) {
                throw new NumberFormatException("Invalid values");
            }
            
            // Update settings
            String updateQuery = "INSERT INTO settings (setting_key, setting_value) "
                    + "VALUES (?, ?) ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            
            // Update fine rate
            pstmt.setString(1, "fine_rate");
            pstmt.setString(2, String.valueOf(fineRate));
            pstmt.executeUpdate();
            
            // Update borrow days
            pstmt.setString(1, "borrow_days");
            pstmt.setString(2, String.valueOf(borrowDays));
            pstmt.executeUpdate();
            
            // Update email notifications
            pstmt.setString(1, "email_notifications");
            pstmt.setString(2, String.valueOf(emailNotificationsCheck.isSelected()));
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Settings saved successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for fine rate and borrow days",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving settings: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Backup");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "library_backup_" + dateFormat.format(new Date()) + ".sql";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File backupFile = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(backupFile); BufferedWriter bufferedWriter = new BufferedWriter(writer); Connection conn = DatabaseConnection.getConnection()) {

                JOptionPane.showMessageDialog(this,
                        "Starting backup process.\nPlease wait while the database is being backed up.",
                        "Backup in Progress",
                        JOptionPane.INFORMATION_MESSAGE);

                // Write SQL header
                bufferedWriter.write("-- Library System Database Backup\n");
                bufferedWriter.write("-- Generated on: " + new Date() + "\n");
                bufferedWriter.write("-- -------------------------------------\n\n");

                // Define known library system tables - ONLY these will be backed up
                List<String> librarySystemTables = Arrays.asList(
                        "admins",
                        "audit_log",
                        "books",
                        "borrowings",
                        "councils",
                        "courses",
                        "reports",
                        "settings",
                        "students"
                // Add any other tables that should be included
                );

                // Process only the tables we know belong to our system
                for (String tableName : librarySystemTables) {
                    try {
                        // First verify the table exists
                        Statement checkStmt = conn.createStatement();
                        ResultSet checkRs = checkStmt.executeQuery("SHOW TABLES LIKE '" + tableName + "'");

                        if (!checkRs.next()) {
                            // Table doesn't exist, skip it
                            bufferedWriter.write("-- Table `" + tableName + "` does not exist in the database, skipping\n\n");
                            checkRs.close();
                            checkStmt.close();
                            continue;
                        }
                        checkRs.close();
                        checkStmt.close();

                        bufferedWriter.write("-- Table structure for table `" + tableName + "`\n");
                        bufferedWriter.write("DROP TABLE IF EXISTS `" + tableName + "`;\n");

                        // Get table creation SQL
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);

                        if (rs.next()) {
                            String createSQL = rs.getString(2);
                            bufferedWriter.write(createSQL + ";\n\n");
                        }
                        rs.close();

                        // Get table data
                        bufferedWriter.write("-- Data for table `" + tableName + "`\n");
                        rs = stmt.executeQuery("SELECT * FROM " + tableName);
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();

                        while (rs.next()) {
                            StringBuilder insertSQL = new StringBuilder();
                            insertSQL.append("INSERT INTO `").append(tableName).append("` VALUES (");

                            for (int i = 1; i <= columnCount; i++) {
                                if (i > 1) {
                                    insertSQL.append(", ");
                                }

                                Object value = rs.getObject(i);
                                if (value == null) {
                                    insertSQL.append("NULL");
                                } else if (value instanceof String || value instanceof Date
                                        || value instanceof Timestamp) {
                                    String stringValue = value.toString();
                                    stringValue = stringValue.replace("\\", "\\\\");
                                    stringValue = stringValue.replace("'", "\\'");
                                    insertSQL.append("'").append(stringValue).append("'");
                                } else {
                                    insertSQL.append(value.toString());
                                }
                            }

                            insertSQL.append(");");
                            bufferedWriter.write(insertSQL.toString() + "\n");
                        }

                        bufferedWriter.write("\n\n");
                        rs.close();
                        stmt.close();
                    } catch (SQLException e) {
                        // Log the error for this table but continue with others
                        bufferedWriter.write("-- Error processing table `" + tableName + "`: " + e.getMessage() + "\n\n");
                        System.err.println("Error processing table " + tableName + ": " + e.getMessage());
                    }
                }

                    JOptionPane.showMessageDialog(this,
                        "Backup created successfully at:\n" + backupFile.getAbsolutePath(),
                        "Backup Complete",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error creating backup: " + e.getMessage(),
                    "Backup Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restoreBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "SQL Files", "sql"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File backupFile = fileChooser.getSelectedFile();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "This will overwrite the current database. Are you sure you want to continue?",
                "Confirm Restore",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection(); BufferedReader reader = new BufferedReader(new FileReader(backupFile))) {

                    JOptionPane.showMessageDialog(this,
                            "Starting restore process.\nPlease wait while the database is being restored.",
                            "Restore in Progress",
                            JOptionPane.INFORMATION_MESSAGE);

                    Statement stmt = conn.createStatement();
                    StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                        // Skip comments and empty lines
                        if (line.startsWith("--") || line.trim().isEmpty()) {
                            continue;
                        }

                        sb.append(line);

                        // Execute SQL when reaching the end of a statement
                        if (line.endsWith(";")) {
                            stmt.execute(sb.toString());
                            sb.setLength(0);
                        }
                    }

                        JOptionPane.showMessageDialog(this,
                            "Database restored successfully",
                            "Restore Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadSettings(); // Reload settings after restore

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Error restoring database: " + e.getMessage(),
                        "Restore Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
} 

package com.library.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import com.library.util.DatabaseConnection;
import com.library.util.SecurityUtil;

public class ForgotPasswordFrame extends JFrame {

    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JPasswordField adminPasscodeField;
    private JButton resetButton;
    private JPanel mainPanel;

    public ForgotPasswordFrame() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("Reset Password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 750);  // Increased height for better spacing
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(Color.WHITE);
    }

    private void createComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel resetPanel = new JPanel();
        resetPanel.setLayout(null);
        resetPanel.setBackground(Color.WHITE);

        // Add close button
        JButton closeButton = new JButton("×");
        closeButton.setBounds(getWidth() - 50, 10, 30, 30);
        closeButton.setForeground(new Color(120, 120, 120));
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 70, 70));
            }

            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(new Color(120, 120, 120));
            }
        });
        resetPanel.add(closeButton);

        // Add logo
        JLabel logoLabel = new JLabel();
        try {
            // Load and scale the image
            java.net.URL imageUrl = getClass().getResource("/com/library/resources/logo.png");
            if (imageUrl != null) {
                java.awt.Image img = javax.imageio.ImageIO.read(imageUrl);
                java.awt.Image scaledImg = img.getScaledInstance(150, 120, java.awt.Image.SCALE_SMOOTH);
                logoLabel.setIcon(new javax.swing.ImageIcon(scaledImg));
            } else {
                // Fallback to text if image not found
                logoLabel.setText("Library System");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            }
        } catch (Exception e) {
            // Fallback to text if error loading image
            logoLabel.setText("Library System");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            e.printStackTrace();
        }
        logoLabel.setBounds(0, 60, getWidth(), 140);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resetPanel.add(logoLabel);

        // Title
        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBounds(0, 200, getWidth(), 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resetPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please verify your identity");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(128, 128, 128));
        subtitleLabel.setBounds(0, 240, getWidth(), 30);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resetPanel.add(subtitleLabel);

        // Calculate center position
        int fieldWidth = 300;
        int startX = (getWidth() - fieldWidth) / 2;
        int currentY = 280;  // Start a bit higher

        // Admin passcode field with toggle
        JPanel adminPasscodePanel = new JPanel(null);
        adminPasscodePanel.setBackground(Color.WHITE);
        adminPasscodePanel.setBounds(startX, currentY, fieldWidth, 45);

        adminPasscodeField = new JPasswordField(20);
        styleTextField(adminPasscodeField, "Admin Passcode");
        adminPasscodeField.setBounds(0, 0, fieldWidth - 45, 45);
        adminPasscodeField.setEchoChar('●');

        JToggleButton toggleAdminPasscode = new JToggleButton("Show");
        toggleAdminPasscode.setBounds(fieldWidth - 45, 0, 45, 45);
        styleToggleButton(toggleAdminPasscode);
        toggleAdminPasscode.addActionListener(e -> {
            if (toggleAdminPasscode.isSelected()) {
                adminPasscodeField.setEchoChar((char) 0);
                toggleAdminPasscode.setText("Hide");
            } else {
                adminPasscodeField.setEchoChar('●');
                toggleAdminPasscode.setText("Show");
            }
        });

        adminPasscodePanel.add(adminPasscodeField);
        adminPasscodePanel.add(toggleAdminPasscode);
        resetPanel.add(adminPasscodePanel);
        currentY += 60;

        // Email field
        emailField = new JTextField(20);
        styleTextField(emailField, "Email");
        emailField.setBounds(startX, currentY, fieldWidth, 45);
        resetPanel.add(emailField);
        currentY += 60;

        // Username field
        usernameField = new JTextField(20);
        styleTextField(usernameField, "Username");
        usernameField.setBounds(startX, currentY, fieldWidth, 45);
        resetPanel.add(usernameField);
        currentY += 60;

        // Add a separator label
        JLabel separatorLabel = new JLabel("Enter New Password");
        separatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        separatorLabel.setForeground(new Color(51, 51, 51));
        separatorLabel.setBounds(startX, currentY, fieldWidth, 30);
        resetPanel.add(separatorLabel);
        currentY += 40;

        // New Password field with toggle
        JPanel newPasswordPanel = new JPanel(null);
        newPasswordPanel.setBackground(Color.WHITE);
        newPasswordPanel.setBounds(startX, currentY, fieldWidth, 45);

        newPasswordField = new JPasswordField(20);
        styleTextField(newPasswordField, "New Password");
        newPasswordField.setBounds(0, 0, fieldWidth - 45, 45);
        newPasswordField.setEchoChar('●');

        JToggleButton toggleNewPassword = new JToggleButton("Show");
        toggleNewPassword.setBounds(fieldWidth - 45, 0, 45, 45);
        styleToggleButton(toggleNewPassword);
        toggleNewPassword.addActionListener(e -> {
            if (toggleNewPassword.isSelected()) {
                newPasswordField.setEchoChar((char) 0);
                toggleNewPassword.setText("Hide");
            } else {
                newPasswordField.setEchoChar('●');
                toggleNewPassword.setText("Show");
            }
        });

        newPasswordPanel.add(newPasswordField);
        newPasswordPanel.add(toggleNewPassword);
        resetPanel.add(newPasswordPanel);
        currentY += 60;

        // Confirm Password field with toggle
        JPanel confirmPasswordPanel = new JPanel(null);
        confirmPasswordPanel.setBackground(Color.WHITE);
        confirmPasswordPanel.setBounds(startX, currentY, fieldWidth, 45);

        confirmPasswordField = new JPasswordField(20);
        styleTextField(confirmPasswordField, "Confirm Password");
        confirmPasswordField.setBounds(0, 0, fieldWidth - 45, 45);
        confirmPasswordField.setEchoChar('●');

        JToggleButton toggleConfirmPassword = new JToggleButton("Show");
        toggleConfirmPassword.setBounds(fieldWidth - 45, 0, 45, 45);
        styleToggleButton(toggleConfirmPassword);
        toggleConfirmPassword.addActionListener(e -> {
            if (toggleConfirmPassword.isSelected()) {
                confirmPasswordField.setEchoChar((char) 0);
                toggleConfirmPassword.setText("Hide");
            } else {
                confirmPasswordField.setEchoChar('●');
                toggleConfirmPassword.setText("Show");
            }
        });

        confirmPasswordPanel.add(confirmPasswordField);
        confirmPasswordPanel.add(toggleConfirmPassword);
        resetPanel.add(confirmPasswordPanel);
        currentY += 80;

        // Reset button
        resetButton = new JButton("Reset Password");
        styleButton(resetButton);
        resetButton.setBounds(startX, currentY, fieldWidth, 45);
        resetPanel.add(resetButton);
        currentY += 60;

        // Back to login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBounds(0, currentY, getWidth(), 30);

        JLabel loginText = new JLabel("Remember your password?");
        loginText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginText.setForeground(new Color(128, 128, 128));

        JLabel loginLink = new JLabel("Sign In");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginLink.setForeground(new Color(66, 133, 244));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose();
            }
        });

        loginPanel.add(loginText);
        loginPanel.add(loginLink);
        resetPanel.add(loginPanel);

        mainPanel.add(resetPanel, BorderLayout.CENTER);

        // Add window dragging
        addWindowDragging(resetPanel);

        // Add action listeners
        resetButton.addActionListener(e -> performPasswordReset());

        // Add footer panel at the bottom
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Create footer with HTML formatting and Unicode heart
        JLabel footerLabel = new JLabel("<html><div style='width:380px;text-align:center'>Made With \u2764 By 21geo<br>© Copyright 2024 - 2025 | Geodevelopment - All Rights Reserved.</div></html>");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(128, 128, 128));
        // Make the heart red
        footerLabel.setText(footerLabel.getText().replace("\u2764", "<font color='red'>\u2764</font>"));
        footerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DeveloperInfoDialog dialog = new DeveloperInfoDialog(ForgotPasswordFrame.this);
                dialog.setVisible(true);
            }
        });
        footerPanel.add(footerLabel);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(51, 51, 51));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        field.setText(placeholder);
        field.setForeground(new Color(180, 180, 180));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(51, 51, 51));
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('●');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(180, 180, 180));
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });

        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(64, 93, 230));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 30, 12, 30));
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color mainColor = new Color(64, 93, 230);
                if (c instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) c;
                    if (b.getModel().isPressed()) {
                        mainColor = new Color(58, 84, 207);
                    } else if (b.getModel().isRollover()) {
                        mainColor = new Color(77, 112, 235);
                    }
                }

                g2d.setColor(mainColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 16, 16));

                if (c instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) c;
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = b.getText();

                    g2d.setColor(new Color(0, 0, 0, 50));
                    int x = (c.getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((c.getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(text, x + 1, y + 1);

                    g2d.setColor(Color.WHITE);
                    g2d.drawString(text, x, y);
                }

                g2d.dispose();
            }
        });

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
    }

    private void styleToggleButton(JToggleButton button) {
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setForeground(new Color(100, 100, 100));

        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (((AbstractButton) c).getModel().isRollover()) {
                    g2d.setColor(new Color(245, 245, 245));
                } else {
                    g2d.setColor(c.getBackground());
                }
                g2d.fillRect(0, 0, c.getWidth(), c.getHeight());

                String text = ((JToggleButton) c).getText();
                g2d.setColor(new Color(100, 100, 100));
                g2d.setFont(c.getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(text,
                        (c.getWidth() - fm.stringWidth(text)) / 2,
                        (c.getHeight() + fm.getAscent()) / 2 - 2);

                g2d.dispose();
            }
        });
    }

    private void addWindowDragging(JPanel panel) {
        Point[] dragStart = {null};

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStart[0] = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                dragStart[0] = null;
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragStart[0] != null) {
                    Point currentLocation = getLocation();
                    setLocation(
                            currentLocation.x + e.getX() - dragStart[0].x,
                            currentLocation.y + e.getY() - dragStart[0].y
                    );
                }
            }
        });
    }

    private void performPasswordReset() {
        String adminPasscode = new String(adminPasscodeField.getPassword());
        String email = emailField.getText();
        String username = usernameField.getText();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate admin passcode
        if (!adminPasscode.equals("GROUP1")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid admin passcode",
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate input
        if (email.equals("Email") || username.equals("Username")
                || newPassword.equals("New Password") || confirmPassword.equals("Confirm Password")
                || adminPasscode.equals("Admin Passcode")) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid Gmail address (must end with @gmail.com)",
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verify user exists and email matches
            String checkQuery = "SELECT * FROM admins WHERE username = ? AND email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "No account found with the provided username and email",
                        "Reset Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash the new password using SecurityUtil
            String hashedPassword = SecurityUtil.hashPassword(newPassword);
            // Update password
            String updateQuery = "UPDATE admins SET password = ? WHERE username = ? AND email = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, hashedPassword);
            updateStmt.setString(2, username);
            updateStmt.setString(3, email);

            int result = updateStmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Password reset successful! Please sign in with your new password.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to reset password",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

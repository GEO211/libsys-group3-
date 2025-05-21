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

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JButton registerButton;
    private JPanel mainPanel;
    private JPasswordField passcodeField;
    private JTextField firstNameField;
    private JTextField lastNameField;

    public RegisterFrame() {
        initializeFrame();
        createComponents();
        setContentPane(mainPanel);
    }

    private void initializeFrame() {
        setTitle("Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 900);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(Color.WHITE);
    }

    private void createComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setBackground(Color.WHITE);

        // Calculate center position
        int fieldWidth = 300;
        int startX = (getWidth() - fieldWidth) / 2;
        int currentY = 60;  // Starting Y position

        // Add close button
        JButton closeButton = new JButton("×");
        closeButton.setBounds(getWidth() - 50, 10, 30, 30);
        closeButton.setForeground(new Color(120, 120, 120));
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 70, 70));
            }

            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(new Color(120, 120, 120));
            }
        });
        registerPanel.add(closeButton);

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
        logoLabel.setBounds(0, currentY, getWidth(), 140);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerPanel.add(logoLabel);
        currentY += 140;

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBounds(0, currentY, getWidth(), 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerPanel.add(titleLabel);
        currentY += 40;

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please fill in the details below");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(128, 128, 128));
        subtitleLabel.setBounds(0, currentY, getWidth(), 30);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerPanel.add(subtitleLabel);
        currentY += 40;

        // Admin passcode field
        passcodeField = new JPasswordField(20);
        styleTextField(passcodeField, "Admin Passcode");
        passcodeField.setBounds(startX, currentY, fieldWidth, 45);
        registerPanel.add(passcodeField);
        currentY += 60;

        // First Name field
        firstNameField = new JTextField(20);
        styleTextField(firstNameField, "First Name");
        firstNameField.setBounds(startX, currentY, fieldWidth, 45);
        registerPanel.add(firstNameField);
        currentY += 60;

        // Last Name field
        lastNameField = new JTextField(20);
        styleTextField(lastNameField, "Last Name");
        lastNameField.setBounds(startX, currentY, fieldWidth, 45);
        registerPanel.add(lastNameField);
        currentY += 60;

        // Email field
        emailField = new JTextField(20);
        styleTextField(emailField, "Email");
        emailField.setBounds(startX, currentY, fieldWidth, 45);
        registerPanel.add(emailField);
        currentY += 60;

        // Username field
        usernameField = new JTextField(20);
        styleTextField(usernameField, "Username");
        usernameField.setBounds(startX, currentY, fieldWidth, 45);
        registerPanel.add(usernameField);
        currentY += 60;

        // Password field with toggle button
        JPanel passwordPanel = new JPanel(null);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBounds(startX, currentY, fieldWidth, 45);

        passwordField = new JPasswordField(20);
        styleTextField(passwordField, "Password");
        passwordField.setBounds(0, 0, fieldWidth - 45, 45);
        passwordField.setEchoChar('●');

        // Password toggle button
        JToggleButton togglePassword = new JToggleButton("Show");
        togglePassword.setBounds(fieldWidth - 45, 0, 45, 45);
        togglePassword.setBackground(Color.WHITE);
        togglePassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        togglePassword.setFocusPainted(false);
        togglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        togglePassword.setForeground(new Color(100, 100, 100));

        togglePassword.addActionListener(e -> {
            if (togglePassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
                togglePassword.setText("Hide");
            } else {
                passwordField.setEchoChar('●');
                togglePassword.setText("Show");
            }
        });

        styleToggleButton(togglePassword);
        passwordPanel.add(passwordField);
        passwordPanel.add(togglePassword);
        registerPanel.add(passwordPanel);
        currentY += 60;

        // Confirm Password field with toggle button
        JPanel confirmPasswordPanel = new JPanel(null);
        confirmPasswordPanel.setBackground(Color.WHITE);
        confirmPasswordPanel.setBounds(startX, currentY, fieldWidth, 45);

        confirmPasswordField = new JPasswordField(20);
        styleTextField(confirmPasswordField, "Confirm Password");
        confirmPasswordField.setBounds(0, 0, fieldWidth - 45, 45);
        confirmPasswordField.setEchoChar('●');

        // Confirm Password toggle button
        JToggleButton toggleConfirmPassword = new JToggleButton("Show");
        toggleConfirmPassword.setBounds(fieldWidth - 45, 0, 45, 45);
        toggleConfirmPassword.setBackground(Color.WHITE);
        toggleConfirmPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        toggleConfirmPassword.setFocusPainted(false);
        toggleConfirmPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toggleConfirmPassword.setForeground(new Color(100, 100, 100));

        toggleConfirmPassword.addActionListener(e -> {
            if (toggleConfirmPassword.isSelected()) {
                confirmPasswordField.setEchoChar((char) 0);
                toggleConfirmPassword.setText("Hide");
            } else {
                confirmPasswordField.setEchoChar('●');
                toggleConfirmPassword.setText("Show");
            }
        });

        styleToggleButton(toggleConfirmPassword);
        confirmPasswordPanel.add(confirmPasswordField);
        confirmPasswordPanel.add(toggleConfirmPassword);
        registerPanel.add(confirmPasswordPanel);
        currentY += 80;

        // Register button
        registerButton = new JButton("Create Account");
        styleButton(registerButton);
        registerButton.setBounds(startX, currentY, fieldWidth, 45);
        registerButton.addActionListener(e -> performRegistration());
        registerPanel.add(registerButton);
        currentY += 60;

        // Sign in link
        JPanel signinPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signinPanel.setBackground(Color.WHITE);
        signinPanel.setBounds(0, currentY, getWidth(), 30);

        JLabel signinText = new JLabel("Already have an account?");
        signinText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signinText.setForeground(new Color(128, 128, 128));

        JLabel signinLink = new JLabel("Sign In");
        signinLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signinLink.setForeground(new Color(66, 133, 244));
        signinLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signinLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        signinPanel.add(signinText);
        signinPanel.add(signinLink);
        registerPanel.add(signinPanel);

        // Add window dragging
        addWindowDragging(registerPanel);

        // Add footer panel at the bottom
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Create footer with HTML formatting and Unicode heart
        JLabel footerLabel = new JLabel("<html><div style='width:380px;text-align:center'>Made With \u2764 By 21geo<br>© Copyright 2024 - 2025 | Geodevelopment - All Rights Reserved.</div></html>");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(128, 128, 128));
        footerLabel.setText(footerLabel.getText().replace("\u2764", "<font color='red'>\u2764</font>"));
        footerPanel.add(footerLabel);

        mainPanel.add(registerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(51, 51, 51));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Add placeholder
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
        button.setForeground(Color.BLUE);
        button.setBackground(new Color(66, 133, 244));  // Google Blue color
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(77, 144, 254));  // Lighter blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(66, 133, 244));  // Back to original blue
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

    private void performRegistration() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String adminPasscode = new String(passcodeField.getPassword());
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        // Validate admin passcode
        if (!adminPasscode.equals("GROUP1")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid admin passcode",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate input - check if fields are empty or contain placeholder text
        if (email.isEmpty() || email.equals("Email")
                || username.isEmpty() || username.equals("Username")
                || password.isEmpty() || password.equals("Password")
                || confirmPassword.isEmpty() || confirmPassword.equals("Confirm Password")
                || adminPasscode.isEmpty() || adminPasscode.equals("Admin Passcode")
                || firstName.isEmpty() || firstName.equals("First Name")
                || lastName.isEmpty() || lastName.equals("Last Name")) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid Gmail address (must end with @gmail.com)",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if username already exists
            String checkQuery = "SELECT COUNT(*) FROM admins WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash the password using SecurityUtil
            String hashedPassword = SecurityUtil.hashPassword(password);
            // Insert new user with role
            String insertQuery = "INSERT INTO admins (username, password, email, first_name, last_name, role) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.setString(4, firstName);
            pstmt.setString(5, lastName);
            pstmt.setString(6, "Librarian"); // Default role for new registrations

            int result = pstmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful! Please sign in.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Create and show login frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to register user",
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

    // Add this new method for styling toggle buttons
    private void styleToggleButton(JToggleButton button) {
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint background
                if (((AbstractButton) c).getModel().isRollover()) {
                    g2d.setColor(new Color(245, 245, 245));
                } else {
                    g2d.setColor(c.getBackground());
                }
                g2d.fillRect(0, 0, c.getWidth(), c.getHeight());

                // Draw text
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
}

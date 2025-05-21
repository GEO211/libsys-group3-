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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import com.library.util.DatabaseConnection;
import com.library.util.SecurityUtil;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel mainPanel;
    
    public LoginFrame() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 600);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(Color.WHITE);
    }
    
    private void createComponents() {
        // Create main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // Create login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null); // Using null layout for precise positioning
        loginPanel.setBackground(Color.WHITE);
        
        // Add close button
        JButton closeButton = new JButton("×");
        closeButton.setBounds(getWidth() - 50, 10, 30, 30);
        closeButton.setForeground(new Color(120, 120, 120));
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 70, 70));
            }
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(new Color(120, 120, 120));
            }
        });
        loginPanel.add(closeButton);

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
        loginPanel.add(logoLabel);
        
        // Add title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBounds(0, 200, getWidth(), 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(titleLabel);
        
        // Add subtitle
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(128, 128, 128));
        subtitleLabel.setBounds(0, 240, getWidth(), 30);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(subtitleLabel);

        // Calculate center position for components
        int fieldWidth = 300;
        int startX = (getWidth() - fieldWidth) / 2;
        
        // Username field
        usernameField = new JTextField(20);
        styleTextField(usernameField, "Username");
        usernameField.setBounds(startX, 280, fieldWidth, 45);
        loginPanel.add(usernameField);
        
        // Password field with toggle button
        JPanel passwordPanel = new JPanel(null);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBounds(startX, 340, fieldWidth, 45);
        
        passwordField = new JPasswordField(20);
        styleTextField(passwordField, "Password");
        passwordField.setBounds(0, 0, fieldWidth - 45, 45);  // Make room for the toggle button
        passwordField.setEchoChar('●');  // Use bullet point for password masking
        
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
        
        // Add toggle password functionality
        togglePassword.addActionListener(e -> {
            if (togglePassword.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show password
                togglePassword.setText("Hide");
            } else {
                passwordField.setEchoChar('●'); // Hide password
                togglePassword.setText("Show");
            }
        });
        
        // Custom UI for toggle button
        togglePassword.setUI(new BasicButtonUI() {
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
                String text = ((JToggleButton)c).getText();
                g2d.setColor(new Color(100, 100, 100));
                g2d.setFont(c.getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(text, 
                    (c.getWidth() - fm.stringWidth(text)) / 2,
                    (c.getHeight() + fm.getAscent()) / 2 - 2);
                
                g2d.dispose();
            }
        });
        
        passwordPanel.add(passwordField);
        passwordPanel.add(togglePassword);
        loginPanel.add(passwordPanel);

        // Remember me checkbox
        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMe.setForeground(new Color(128, 128, 128));
        rememberMe.setBackground(Color.WHITE);
        rememberMe.setBounds(startX, 395, 140, 30);
        loginPanel.add(rememberMe);

        // Forgot password link
        JLabel forgotPassword = new JLabel("Forgot Password?");
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPassword.setForeground(new Color(66, 133, 244));
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassword.setBounds(startX + fieldWidth - 110, 395, 110, 30);
        forgotPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ForgotPasswordFrame forgotPasswordFrame = new ForgotPasswordFrame();
                forgotPasswordFrame.setVisible(true);
                dispose(); // Close the login frame
            }
        });
        loginPanel.add(forgotPassword);
        
        // Login button
        loginButton = new JButton("Sign In");
        styleLoginButton(loginButton);
        loginButton.setBounds(startX, 445, fieldWidth, 45);
        loginPanel.add(loginButton);

        // Create account link
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setBackground(Color.WHITE);
        signupPanel.setBounds(0, 510, getWidth(), 30);
        
        JLabel signupText = new JLabel("Don't have an account?");
        signupText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupText.setForeground(new Color(128, 128, 128));
        
        JLabel signupLink = new JLabel("Create Account");
        signupLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signupLink.setForeground(new Color(66, 133, 244));
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterFrame registerFrame = new RegisterFrame();
                registerFrame.setVisible(true);
                dispose(); // Close the login frame
            }
        });
        
        signupPanel.add(signupText);
        signupPanel.add(signupLink);
        loginPanel.add(signupPanel);
        
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        // Add window dragging
        addWindowDragging(loginPanel);
        
        // Add action listeners
        loginButton.addActionListener(e -> performLogin());
        addEnterKeyListener();
        
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
                DeveloperInfoDialog dialog = new DeveloperInfoDialog(LoginFrame.this);
                dialog.setVisible(true);
            }
        });
        footerPanel.add(footerLabel);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Set content pane
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
        
        // Add placeholder
        field.setText(placeholder);
        field.setForeground(new Color(180, 180, 180));
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(51, 51, 51));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(180, 180, 180));
                }
            }
        });
    }

    private void styleLoginButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(64, 93, 230));  // Rich blue color
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Create rounded corners and padding
        button.setBorder(new EmptyBorder(12, 30, 12, 30));
        
        // Custom button UI
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Choose colors based on button state
                Color mainColor = new Color(64, 93, 230);  // Default blue
                if (c instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) c;
                    if (b.getModel().isPressed()) {
                        mainColor = new Color(58, 84, 207);    // Darker when pressed
                    } else if (b.getModel().isRollover()) {
                        mainColor = new Color(77, 112, 235);   // Lighter when hovered
                    }
                }
                
                // Draw button background with rounded corners
                g2d.setColor(mainColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 16, 16));
                
                // Draw subtle highlight
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.draw(new RoundRectangle2D.Float(0, 0, c.getWidth() - 1, c.getHeight() - 1, 16, 16));
                
                // Draw text
                if (c instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) c;
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = b.getText();
                    
                    // Draw text shadow
                    g2d.setColor(new Color(0, 0, 0, 50));
                    int x = (c.getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((c.getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(text, x + 1, y + 1);
                    
                    // Draw main text
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(text, x, y);
                }
                
                g2d.dispose();
            }
        });
        
        // Remove default button styling
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
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

    private void addEnterKeyListener() {
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }
    
    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Check if fields contain placeholders
        if (username.equals("Username") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM admins WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (SecurityUtil.checkPassword(password, storedHash)) {
                    // Update last login
                    String updateQuery = "UPDATE admins SET last_login = NOW() WHERE admin_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, rs.getInt("admin_id"));
                    updateStmt.executeUpdate();
                    
                    final int adminId = rs.getInt("admin_id");
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = new MainFrame(adminId);
                        mainFrame.setVisible(true);
                        this.dispose();
                    });
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Invalid username or password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
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
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Enable anti-aliasing for text
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
            SwingUtilities.invokeLater(() -> {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
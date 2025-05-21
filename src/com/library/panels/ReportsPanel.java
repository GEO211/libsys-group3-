package com.library.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.library.dao.BookDAO;
import com.library.dao.StudentDAO;
import com.library.main.MainFrame;
import com.library.models.Book;
import com.library.models.Student;
import com.library.util.DatabaseConnection;
import com.library.util.Theme;

public class ReportsPanel extends JPanel {

    private JTextArea reportArea;
    private BookDAO bookDAO;
    private StudentDAO studentDAO;
    
    // Theme instance
    private Theme currentTheme;

    // Update color variables that will be set based on theme
    private Color PRIMARY_COLOR;
    private Color ACCENT_COLOR;
    private Color SUCCESS_COLOR;
    private Color WARNING_COLOR;
    private Color DANGER_COLOR;
    private Color BG_COLOR;
    private Color CARD_BG_COLOR;
    private Color TEXT_PRIMARY;
    private Color TEXT_SECONDARY;

    private Font HEADER_FONT;
    private Font TITLE_FONT;
    private Font LABEL_FONT;
    
    // UI components that need theme updates
    private JScrollPane tableScrollPane;
    private JPanel tableCard;

    // Add table model
    private DefaultTableModel tableModel;
    private JTable reportsTable;

    public ReportsPanel() {
        this(null);
    }
    
    public ReportsPanel(Theme theme) {
        bookDAO = new BookDAO();
        studentDAO = new StudentDAO();
        
        // Set theme and initialize colors
        this.currentTheme = theme != null ? theme : createDefaultTheme();
        updateThemeColors();
        
        initializeUI();
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
        PRIMARY_COLOR = currentTheme.textPrimary;
        ACCENT_COLOR = currentTheme.accentBlue;
        SUCCESS_COLOR = currentTheme.accentGreen;
        WARNING_COLOR = new Color(255, 193, 7); // Bootstrap yellow
        DANGER_COLOR = new Color(220, 53, 69);  // Bootstrap red
        BG_COLOR = currentTheme.background;
        CARD_BG_COLOR = currentTheme.cardBackground;
        TEXT_PRIMARY = currentTheme.textPrimary;
        TEXT_SECONDARY = currentTheme.textSecondary;

        HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
        TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
        LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    }
    
    /**
     * Applies the current theme to all components
     */
    private void applyThemeToComponents() {
        // Update main panel
        setBackground(BG_COLOR);
        
        // Update table card
        if (tableCard != null) {
            tableCard.setBackground(CARD_BG_COLOR);
            tableCard.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, CARD_BG_COLOR),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
        }
        
        // Update table
        if (reportsTable != null) {
            styleTable(reportsTable);
        }
        
        // Update scrollpane
        if (tableScrollPane != null) {
            tableScrollPane.getViewport().setBackground(CARD_BG_COLOR);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(BG_COLOR);

        // Top panel with title and buttons
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📋");  // Document/Report emoji
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Reports Management");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        topPanel.add(titlePanel, BorderLayout.WEST);

        // Action buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton bookReportBtn = createStyledButton("New Book Report", ACCENT_COLOR);
        bookReportBtn.setPreferredSize(new Dimension(150, 35));
        JButton studentReportBtn = createStyledButton("New Student Report", ACCENT_COLOR);
        studentReportBtn.setPreferredSize(new Dimension(170, 35));

        bookReportBtn.addActionListener(e -> showBookReportDialog());
        studentReportBtn.addActionListener(e -> showStudentReportDialog());

        buttonsPanel.add(bookReportBtn);
        buttonsPanel.add(studentReportBtn);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Create table with modern styling
        String[] columns = {"ID", "Type", "Subject", "Status", "Created", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };

        reportsTable = new JTable(tableModel);
        styleTable(reportsTable);

        // Create a card-like panel for the table
        tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG_COLOR);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, CARD_BG_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        tableScrollPane = new JScrollPane(reportsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(CARD_BG_COLOR);
        tableCard.add(tableScrollPane);

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);

        // Load data and add popup menu
        loadReports();
        addTablePopupMenu();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setForeground(TEXT_PRIMARY);  // Use text color from theme
        button.setBackground(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                40));  // Semi-transparent background
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(5, color),
                BorderFactory.createEmptyBorder(8, 15, 8, 15) // Added more padding
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);  // Make button opaque

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        60));  // Darker on hover
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        40));  // Normal state
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(TEXT_SECONDARY.getRed(), 
                                   TEXT_SECONDARY.getGreen(), 
                                   TEXT_SECONDARY.getBlue(), 
                                   40)); // Very transparent grid
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(LABEL_FONT);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(CARD_BG_COLOR);
        
        // Configure status column with badge-like appearance
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (column == 3 && value != null) { // Status column
                    JLabel label = new JLabel(value.toString());
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setFont(LABEL_FONT);
                    label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                    
                    if (isSelected) {
                        label.setForeground(Color.WHITE);
                        label.setBackground(ACCENT_COLOR);
                        label.setOpaque(true);
                    } else {
                        switch (value.toString()) {
                            case "Open":
                                setupBadge(label, WARNING_COLOR);
                                break;
                            case "Resolved":
                                setupBadge(label, SUCCESS_COLOR);
                                break;
                            case "Urgent":
                                setupBadge(label, DANGER_COLOR);
                                break;
                            default:
                                setupBadge(label, ACCENT_COLOR);
                        }
                    }
                    return label;
                } else if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG_COLOR : new Color(
                            CARD_BG_COLOR.getRed(),
                            CARD_BG_COLOR.getGreen(),
                            CARD_BG_COLOR.getBlue(),
                            240)); // Slightly transparent
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setForeground(Color.WHITE);
                }
                
                return c;
            }
            
            private void setupBadge(JLabel label, Color color) {
                label.setForeground(Color.WHITE);
                label.setBackground(color);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, color),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)
                ));
            }
        });
    }

    private void loadReports() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT r.*, b.title, a.username FROM reports r "
                + "LEFT JOIN books b ON r.book_id = b.book_id "
                + "LEFT JOIN admins a ON r.processed_by = a.admin_id "
                + "ORDER BY r.created_at DESC")) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("report_id"),
                    rs.getString("report_type"),
                    rs.getString("title") != null ? rs.getString("title") : rs.getString("person_id"),
                    rs.getString("status"),
                    formatDate(rs.getTimestamp("created_at")),
                    "actions"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("Error loading reports: " + e.getMessage());
        }
    }

    // Add these methods for report actions
    private void resolveReport(int reportId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE reports SET status = 'Resolved', resolved_at = NOW() WHERE report_id = ?")) {

            pstmt.setInt(1, reportId);
            pstmt.executeUpdate();
            SwingUtilities.invokeLater(this::loadReports);  // Reload on EDT

        } catch (SQLException e) {
            showError("Error resolving report: " + e.getMessage());
        }
    }

    private void deleteReport(int reportId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this report?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM reports WHERE report_id = ?")) {

                pstmt.setInt(1, reportId);
                pstmt.executeUpdate();
                SwingUtilities.invokeLater(this::loadReports);  // Reload on EDT

            } catch (SQLException e) {
                showError("Error deleting report: " + e.getMessage());
            }
        }
    }

    private void showBookReportDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Report", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);  // Made dialog bigger
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Book search field
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Book ID/ISBN:"));
        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField);

        // Report type combo
        JPanel typePanel = new JPanel();
        typePanel.add(new JLabel("Report Type:"));
        String[] types = {"Other", "Stolen", "Damaged", "Lost"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setSelectedItem("Other");
        typePanel.add(typeCombo);

        // Price field (initially hidden)
        JPanel pricePanel = new JPanel();
        JLabel priceLabel = new JLabel("Book Price:");
        JTextField priceField = new JTextField(10);
        pricePanel.add(priceLabel);
        pricePanel.add(priceField);
        pricePanel.setVisible(false);

        // Who lost it panel
        JPanel whoLostPanel = new JPanel();
        whoLostPanel.add(new JLabel("Lost By:"));
        String[] lostByTypes = {"Student", "Personnel", "Other"};
        JComboBox<String> lostByCombo = new JComboBox<>(lostByTypes);
        whoLostPanel.add(lostByCombo);

        // Person ID/Name panel
        JPanel personPanel = new JPanel();
        personPanel.add(new JLabel("ID/Name:"));
        JTextField personField = new JTextField(15);
        personPanel.add(personField);

        // Description area
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        JTextArea descArea = new JTextArea(4, 30);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");

        // Show/hide price field based on report type
        typeCombo.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            boolean showPrice = "Lost".equalsIgnoreCase(selectedType) || "Stolen".equalsIgnoreCase(selectedType) || "Damaged".equalsIgnoreCase(selectedType);
            pricePanel.setVisible(showPrice);
        });

        submitBtn.addActionListener(e -> {
            try {
                String searchTerm = searchField.getText().trim();
                Book book = bookDAO.findByIsbn(searchTerm);
                String reportType = (String) typeCombo.getSelectedItem();
                boolean needsPrice = "Lost".equalsIgnoreCase(reportType) || "Stolen".equalsIgnoreCase(reportType) || "Damaged".equalsIgnoreCase(reportType);
                double price = 0.0;
                if (needsPrice) {
                    if (priceField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Price is required for this report type.", "Missing Price", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        price = Double.parseDouble(priceField.getText().trim());
                        if (price <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Please enter a valid positive price.", "Invalid Price", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if (book != null) {
                    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO reports (report_type, book_id, person_type, person_id, description, processed_by, status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')")) {
                        pstmt.setString(1, reportType);
                        pstmt.setInt(2, book.getBookId());
                        pstmt.setString(3, (String) lostByCombo.getSelectedItem());
                        pstmt.setString(4, personField.getText().trim());
                        pstmt.setString(5, descArea.getText());
                        pstmt.setInt(6, MainFrame.getCurrentAdminId());
                        pstmt.executeUpdate();
                        // If lost, decrease book quantity by 1
                        if ("Lost".equalsIgnoreCase(reportType)) {
                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE books SET quantity = quantity - 1, available_quantity = GREATEST(available_quantity - 1, 0) WHERE book_id = ? AND quantity > 0")) {
                                updateStmt.setInt(1, book.getBookId());
                                updateStmt.executeUpdate();
                            }
                        }
                        JOptionPane.showMessageDialog(dialog,
                                "Report submitted successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadReports();
                        dialog.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Book not found",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                showError("Error submitting report: " + ex.getMessage());
            }
        });

        // Add field validation
        lostByCombo.addActionListener(e -> {
            String selectedType = (String) lostByCombo.getSelectedItem();
            personField.setEnabled(true);

            if ("Other".equals(selectedType)) {
                personField.setToolTipText("Enter name of person");
            } else {
                personField.setToolTipText("Enter ID number");
            }
        });

        // Add ID field validation
        personField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateId();
            }

            public void removeUpdate(DocumentEvent e) {
                validateId();
            }

            public void insertUpdate(DocumentEvent e) {
                validateId();
            }

            private void validateId() {
                String id = personField.getText().trim();
                String type = (String) lostByCombo.getSelectedItem();

                if (id.isEmpty()) {
                    return;
                }

                String info = null;
                if ("Student".equals(type)) {
                    info = getStudentInfo(id);
                } else if ("Personnel".equals(type)) {
                    info = getPersonnelInfo(id);
                }

                if (info != null) {
                    personField.setForeground(new Color(0, 120, 0));  // Green for valid
                    personField.setToolTipText(info);
                } else {
                    personField.setForeground(Color.RED);  // Red for invalid
                    personField.setToolTipText("ID not found");
                }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        // Add all panels
        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(typePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(pricePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(whoLostPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(personPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showStudentReportDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Student Report", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Student search field
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Student ID:"));
        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField);

        // Report type combo
        JPanel typePanel = new JPanel();
        typePanel.add(new JLabel("Report Type:"));
        String[] types = {"General", "Disciplinary", "Academic", "Other"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setSelectedItem("Other");
        typePanel.add(typeCombo);

        // Description area
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        JTextArea descArea = new JTextArea(4, 30);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");

        submitBtn.addActionListener(e -> {
            try {
                String searchTerm = searchField.getText().trim();
                Student student = studentDAO.getStudentByIdNumber(searchTerm);

                if (student != null) {
                    // Save to database
                    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO reports (report_type, person_type, person_id, "
                            + "description, processed_by, status) VALUES (?, 'Student', ?, ?, ?, 'Pending')")) {

                        pstmt.setString(1, (String) typeCombo.getSelectedItem());
                        pstmt.setString(2, student.getIdNumber());
                        pstmt.setString(3, descArea.getText());
                        pstmt.setInt(4, MainFrame.getCurrentAdminId());

                        pstmt.executeUpdate();

                        JOptionPane.showMessageDialog(dialog,
                                "Report submitted successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        loadReports(); // Refresh the table
                        dialog.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Student not found",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                showError("Error submitting report: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        // Add all panels
        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(typePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Helper method to get current admin name
    private String getCurrentAdminName() {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT first_name, last_name FROM admins WHERE admin_id = ?")) {

            pstmt.setInt(1, MainFrame.getCurrentAdminId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("first_name") + " " + rs.getString("last_name");
            }
            return "Unknown Admin";
        } catch (SQLException e) {
            return "Unknown Admin";
        }
    }

    // Add this method to fetch personnel info
    private String getPersonnelInfo(String id) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT username, email FROM admins WHERE admin_id = ?")) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return String.format("%s (%s)",
                        rs.getString("username"),
                        rs.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    // Add this method to fetch student info
    private String getStudentInfo(String id) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT CONCAT(first_name, ' ', last_name) as name, id_number, email FROM students WHERE id_number = ?")) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return String.format("%s (%s - %s)",
                        rs.getString("name"),
                        rs.getString("id_number"),
                        rs.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    // Add this method to get current admin email
    private String getCurrentAdminEmail() {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT email FROM admins WHERE admin_id = ?")) {

            pstmt.setInt(1, MainFrame.getCurrentAdminId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("email");
            }
            return "No email";
        } catch (SQLException e) {
            return "No email";
        }
    }

    // Add these inner classes for table buttons
    class ButtonsPanel extends JPanel {

        public ButtonsPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);
            setBackground(Color.WHITE);
        }
    }

    class ButtonsRenderer extends ButtonsPanel implements TableCellRenderer {

        public ButtonsRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            removeAll();

            // Get status from the status column (column 3)
            String status = table.getValueAt(row, 3).toString();
            if (!"Resolved".equals(status)) {
                JButton resolveBtn = createActionButton("Resolve", SUCCESS_COLOR);
                add(resolveBtn);
            }

            JButton deleteBtn = createActionButton("Delete", DANGER_COLOR);
            add(deleteBtn);

            return this;
        }
    }

    class ButtonsEditor extends AbstractCellEditor implements TableCellEditor {

        private ButtonsPanel panel;
        private JTable table;

        public ButtonsEditor(JTable table) {
            this.table = table;
            panel = new ButtonsPanel();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            panel.removeAll();
            int reportId = (int) table.getValueAt(row, 0);

            // Get status from the status column (column 3)
            String status = table.getValueAt(row, 3).toString();
            if (!"Resolved".equals(status)) {
                JButton resolveBtn = createActionButton("Resolve", SUCCESS_COLOR);
                resolveBtn.addActionListener(e -> {
                    resolveReport(reportId);
                    fireEditingStopped();
                });
                panel.add(resolveBtn);
            }

            JButton deleteBtn = createActionButton("Delete", DANGER_COLOR);
            deleteBtn.addActionListener(e -> {
                deleteReport(reportId);
                fireEditingStopped();
            });
            panel.add(deleteBtn);

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";  // Return empty string instead of "actions"
        }
    }

    // Add this helper method for creating action buttons
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setForeground(Color.BLACK);  // Set text color to black
        button.setBackground(new Color(
                bgColor.getRed(),
                bgColor.getGreen(),
                bgColor.getBlue(),
                40));  // Make background semi-transparent
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(5, bgColor),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(
                        bgColor.getRed(),
                        bgColor.getGreen(),
                        bgColor.getBlue(),
                        60));  // Darker on hover
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(
                        bgColor.getRed(),
                        bgColor.getGreen(),
                        bgColor.getBlue(),
                        40));  // Normal state
            }
        });

        return button;
    }

    // Add this helper method for date formatting
    private String formatDate(Timestamp date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        return sdf.format(date);
    }

    // Add this helper method for error messages
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // Add after the initializeUI method:
    private void addTablePopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("View Details");
        JMenuItem printItem = new JMenuItem("Print Report");

        viewItem.addActionListener(e -> showReportDetails(getSelectedReportId()));
        printItem.addActionListener(e -> printReport(getSelectedReportId()));

        popupMenu.add(viewItem);
        popupMenu.add(printItem);

        reportsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = reportsTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        reportsTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private int getSelectedReportId() {
        int row = reportsTable.getSelectedRow();
        return (int) reportsTable.getValueAt(row, 0);
    }

    private void showReportDetails(int reportId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT r.*, b.title, b.isbn, b.author, b.price, "
                + "CONCAT(a.first_name, ' ', a.last_name) as processed_by_name, "
                + "a.email as processor_email "
                + "FROM reports r "
                + "LEFT JOIN books b ON r.book_id = b.book_id "
                + "LEFT JOIN admins a ON r.processed_by = a.admin_id "
                + "WHERE r.report_id = ?"
        )) {
            pstmt.setInt(1, reportId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                        "Report Details", true);
                dialog.setSize(600, 500);
                dialog.setLocationRelativeTo(this);
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(new EmptyBorder(20, 20, 20, 20));
                panel.setBackground(Color.WHITE);
                // Create styled details panel
                String details = formatReportDetails(rs);
                JTextArea detailsArea = new JTextArea(details);
                detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                detailsArea.setEditable(false);
                detailsArea.setBackground(Color.WHITE);
                JScrollPane scrollPane = new JScrollPane(detailsArea);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                // Buttons panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(Color.WHITE);
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                buttonPanel.add(closeButton);
                panel.add(scrollPane);
                panel.add(Box.createVerticalStrut(20));
                panel.add(buttonPanel);
                dialog.add(panel);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            showError("Error loading report details: " + e.getMessage());
        }
    }

    private String formatReportDetails(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORT DETAILS\n");
        sb.append("==============\n\n");
        sb.append(String.format("Report ID: %d\n", rs.getInt("report_id")));
        String reportType = rs.getString("report_type");
        sb.append(String.format("Type: %s\n", reportType));
        sb.append(String.format("Status: %s\n", rs.getString("status")));
        sb.append(String.format("Created: %s\n\n", formatDate(rs.getTimestamp("created_at"))));
        if (rs.getString("title") != null) {
            sb.append("Book Information\n");
            sb.append("-----------------\n");
            sb.append(String.format("Title: %s\n", rs.getString("title")));
            sb.append(String.format("ISBN: %s\n", rs.getString("isbn")));
            sb.append(String.format("Author: %s\n\n", rs.getString("author")));
        }
        if (rs.getString("person_type") != null) {
            sb.append("Person Information\n");
            sb.append("------------------\n");
            sb.append(String.format("Type: %s\n", rs.getString("person_type")));
            sb.append(String.format("ID/Name: %s\n\n", rs.getString("person_id")));
        }
        // Show price if lost, stolen, or damaged
        if ("Lost".equalsIgnoreCase(reportType) || "Stolen".equalsIgnoreCase(reportType) || "Damaged".equalsIgnoreCase(reportType)) {
            double price = 0.0;
            try {
                price = rs.getDouble("price");
            } catch (Exception ignore) {
            }
            sb.append("Book Price: ").append(String.format("%.2f\n\n", price));
        }
        sb.append("Report Details\n");
        sb.append("--------------\n");
        sb.append(String.format("Description: %s\n\n", rs.getString("description")));
        sb.append("Processing Information\n");
        sb.append("---------------------\n");
        sb.append(String.format("Processed By: %s\n", rs.getString("processed_by_name")));
        sb.append(String.format("Processor Email: %s\n", rs.getString("processor_email")));
        if (rs.getTimestamp("resolved_at") != null) {
            sb.append(String.format("Resolved At: %s\n", formatDate(rs.getTimestamp("resolved_at"))));
        }
        return sb.toString();
    }

    private void printReport(int reportId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * FROM reports WHERE report_id = ?")) {

            pstmt.setInt(1, reportId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String reportText = formatReportDetails(rs);
                JTextArea printArea = new JTextArea(reportText);
                printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                try {
                    printArea.print();
                } catch (Exception e) {
                    showError("Error printing report: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            showError("Error loading report for printing: " + e.getMessage());
        }
    }

    // Add this inner class for rounded borders
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
    }
}

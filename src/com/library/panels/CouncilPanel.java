package com.library.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.library.util.DatabaseConnection;
import com.library.util.Theme;

public class CouncilPanel extends JPanel {

    private JTable councilTable;
    private DefaultTableModel tableModel;
    private JTable courseTable;
    private DefaultTableModel courseTableModel;
    
    // Theme constants
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

    // UI components that need theme updates
    private JPanel councilsPanel;
    private JPanel coursesPanel;
    private JScrollPane councilScrollPane;
    private JScrollPane courseScrollPane;
    private JToggleButton themeToggle;

    // Define modern colors - will be updated based on theme
    private Color PRIMARY_COLOR;
    private Color ACCENT_COLOR;
    private Color BACKGROUND_COLOR;
    private Color TABLE_HEADER_COLOR;
    private Color TABLE_ALTERNATE_COLOR;
    private Color BUTTON_TEXT_COLOR;
    private Font HEADER_FONT;
    private Font CONTENT_FONT;

    public CouncilPanel() {
        this(null);
    }

    public CouncilPanel(Theme theme) {
        currentTheme = theme != null ? theme : createDefaultTheme();
        updateThemeColors(); // Initialize color based on current theme
        initializeUI();
        loadCouncils();
    }

    /**
     * Creates a default light theme when none is provided
     */
    private Theme createDefaultTheme() {
        return LIGHT_THEME;
    }

    /**
     * Updates the panel with a new theme
     */
    public void updateTheme(Theme theme) {
        if (theme == null) return;
        
        currentTheme = theme;
        updateThemeColors();
        updateUITheme();
        repaint();
        revalidate();
    }
    
    private void updateThemeColors() {
        PRIMARY_COLOR = new Color(220, 220, 220);    // Light Gray
        ACCENT_COLOR = new Color(200, 200, 200);     // Slightly Darker Gray
        BACKGROUND_COLOR = currentTheme.background;
        TABLE_HEADER_COLOR = currentTheme.cardBackground;
        TABLE_ALTERNATE_COLOR = new Color(
            currentTheme.cardBackground.getRed(),
            currentTheme.cardBackground.getGreen(),
            currentTheme.cardBackground.getBlue(),
            240); // Slightly transparent
        BUTTON_TEXT_COLOR = currentTheme.textPrimary;
        HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
        CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    }
    
    private void switchTheme() {
        currentTheme = (currentTheme == LIGHT_THEME) ? DARK_THEME : LIGHT_THEME;
        updateThemeColors();
        updateUITheme();
    }
    
    private void updateUITheme() {
        // Update main panel
        setBackground(BACKGROUND_COLOR);
        
        // Update theme toggle button
        if (themeToggle != null) {
            // themeToggle.setText(currentTheme == DARK_THEME ? "Switch to Light Mode" : "Switch to Dark Mode");
            themeToggle.setBackground(currentTheme.accentBlue);
            themeToggle.setForeground(Color.WHITE);
            themeToggle.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(currentTheme.accentBlue.darker(), 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        }
        
        // Update councils panel
        councilsPanel.setBackground(BACKGROUND_COLOR);
        councilsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Departments",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        HEADER_FONT, BUTTON_TEXT_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                
        // Update courses panel
        coursesPanel.setBackground(BACKGROUND_COLOR);
        coursesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Courses",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        HEADER_FONT, BUTTON_TEXT_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                
        // Update scroll panes
        councilScrollPane.getViewport().setBackground(currentTheme.cardBackground);
        councilScrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                  currentTheme.textSecondary.getGreen(), 
                                                                  currentTheme.textSecondary.getBlue(), 
                                                                  100)));
        courseScrollPane.getViewport().setBackground(currentTheme.cardBackground);
        courseScrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                 currentTheme.textSecondary.getGreen(), 
                                                                 currentTheme.textSecondary.getBlue(), 
                                                                 100)));
        
        // Update tables
        styleTable(councilTable);
        styleTable(courseTable);
        
        // Refresh the panel
        revalidate();
        repaint();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);
        
        // Add theme toggle at the top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        
        // Create theme toggle button with enhanced styling
        themeToggle = new JToggleButton();
        // themeToggle.setText(currentTheme == DARK_THEME ? "Switch to Light Mode" : "Switch to Dark Mode");
        themeToggle.setSelected(currentTheme == DARK_THEME);
        themeToggle.setFont(HEADER_FONT);
        themeToggle.setForeground(BUTTON_TEXT_COLOR);
        themeToggle.setBackground(currentTheme.accentBlue);
        themeToggle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentTheme.accentBlue.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        themeToggle.setFocusPainted(false);
        themeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggle.addActionListener(e -> {
            switchTheme();
            // themeToggle.setText(currentTheme == DARK_THEME ? "Switch to Light Mode" : "Switch to Dark Mode");
            themeToggle.setBackground(currentTheme.accentBlue);
            themeToggle.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(currentTheme.accentBlue.darker(), 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        });
        
        // Add mouse hover effects
        themeToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                themeToggle.setBackground(currentTheme.accentBlue.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                themeToggle.setBackground(currentTheme.accentBlue);
            }
        });
        
        topPanel.add(themeToggle);
        add(topPanel, BorderLayout.NORTH);

        // Split pane for councils and courses
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setBorder(null);
        splitPane.setBackground(BACKGROUND_COLOR);

        // Councils panel
        councilsPanel = new JPanel(new BorderLayout(0, 15));
        councilsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Departments",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        HEADER_FONT, BUTTON_TEXT_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        councilsPanel.setBackground(BACKGROUND_COLOR);

        // Council table
        String[] councilColumns = {"ID", "Department Name", "Description"};
        tableModel = new DefaultTableModel(councilColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        councilTable = new JTable(tableModel);
        styleTable(councilTable);
        councilTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadCoursesForSelectedCouncil();
            }
        });

        // Council buttons panel
        JPanel councilButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        councilButtonsPanel.setOpaque(false);

        JButton addCouncilButton = createStyledButton("+ Add Department", PRIMARY_COLOR);
        JButton editCouncilButton = createStyledButton("Edit", PRIMARY_COLOR);
        JButton deleteCouncilButton = createStyledButton("Delete", PRIMARY_COLOR);

        addCouncilButton.addActionListener(e -> showAddCouncilDialog());
        editCouncilButton.addActionListener(e -> showEditCouncilDialog());
        deleteCouncilButton.addActionListener(e -> deleteSelectedCouncil());

        councilButtonsPanel.add(addCouncilButton);
        councilButtonsPanel.add(editCouncilButton);
        councilButtonsPanel.add(deleteCouncilButton);

        // Scroll pane with modern styling
        councilScrollPane = new JScrollPane(councilTable);
        councilScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        councilScrollPane.getViewport().setBackground(currentTheme.cardBackground);

        councilsPanel.add(councilScrollPane, BorderLayout.CENTER);
        councilsPanel.add(councilButtonsPanel, BorderLayout.SOUTH);

        // Courses panel
        coursesPanel = new JPanel(new BorderLayout(0, 15));
        coursesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Courses",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        HEADER_FONT, BUTTON_TEXT_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        coursesPanel.setBackground(BACKGROUND_COLOR);

        // Course table
        String[] courseColumns = {"ID", "Course Code", "Course Name"};
        courseTableModel = new DefaultTableModel(courseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        styleTable(courseTable);

        // Course buttons panel
        JPanel courseButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        courseButtonsPanel.setOpaque(false);

        JButton addCourseButton = createStyledButton("+ Add Course", PRIMARY_COLOR);
        JButton editCourseButton = createStyledButton("Edit", PRIMARY_COLOR);
        JButton deleteCourseButton = createStyledButton("Delete", PRIMARY_COLOR);

        addCourseButton.addActionListener(e -> showAddCourseDialog());
        editCourseButton.addActionListener(e -> showEditCourseDialog());
        deleteCourseButton.addActionListener(e -> deleteSelectedCourse());

        courseButtonsPanel.add(addCourseButton);
        courseButtonsPanel.add(editCourseButton);
        courseButtonsPanel.add(deleteCourseButton);

        // Scroll pane with modern styling
        courseScrollPane = new JScrollPane(courseTable);
        courseScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        courseScrollPane.getViewport().setBackground(currentTheme.cardBackground);

        coursesPanel.add(courseScrollPane, BorderLayout.CENTER);
        coursesPanel.add(courseButtonsPanel, BorderLayout.SOUTH);

        // Add panels to split pane
        splitPane.setLeftComponent(councilsPanel);
        splitPane.setRightComponent(coursesPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(4, 14, 4, 14)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(4, 14, 4, 14)));
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(currentTheme.textSecondary.getRed(), 
                                   currentTheme.textSecondary.getGreen(), 
                                   currentTheme.textSecondary.getBlue(), 
                                   40)); // Very transparent grid
        table.setSelectionBackground(currentTheme.accentBlue);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(CONTENT_FONT);
        table.setForeground(currentTheme.textPrimary);
        table.setBackground(currentTheme.cardBackground);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style header
        table.getTableHeader().setBackground(TABLE_HEADER_COLOR);
        table.getTableHeader().setForeground(currentTheme.textPrimary);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                                 currentTheme.textSecondary.getGreen(), 
                                                                                 currentTheme.textSecondary.getBlue(), 
                                                                                 100)));

        // Create custom renderer for alternating row colors
        TableCellRenderer renderer = new TableCellRenderer() {
            private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = DEFAULT_RENDERER.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? currentTheme.cardBackground : TABLE_ALTERNATE_COLOR);
                    c.setForeground(currentTheme.textPrimary);
                } else {
                    c.setForeground(Color.WHITE);
                }

                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    ((JLabel) c).setFont(CONTENT_FONT);
                }

                return c;
            }
        };

        // Apply the renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void loadCouncils() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM councils ORDER BY council_name")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("council_id"),
                    rs.getString("council_name"),
                    rs.getString("description")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading councils: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCoursesForSelectedCouncil() {
        int selectedRow = councilTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int councilId = (int) councilTable.getValueAt(selectedRow, 0);
        courseTableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * FROM courses WHERE council_id = ? AND active = TRUE ORDER BY course_name")) {

            pstmt.setInt(1, councilId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name")
                };
                courseTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddCouncilDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = createThemedDialog(parentFrame, "Add Department", 400, 250);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(currentTheme.background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        applyThemeToTextField(nameField);
        
        JTextArea descArea = new JTextArea(3, 20);
        applyThemeToTextArea(descArea);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JLabel nameLabel = new JLabel("Department Name:");
        nameLabel.setForeground(currentTheme.textPrimary);
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(currentTheme.textPrimary);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(descLabel, gbc);
        gbc.gridx = 1;
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                              currentTheme.textSecondary.getGreen(), 
                                                              currentTheme.textSecondary.getBlue(), 
                                                              100)));
        panel.add(descScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(currentTheme.background);
        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", PRIMARY_COLOR);

        saveButton.addActionListener(e -> {
            // Validate inputs before saving
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a department name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (descArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a department description",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (nameField.getText().length() > 50) {
                JOptionPane.showMessageDialog(dialog,
                        "Department name cannot be longer than 50 characters",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO councils (council_name, description) VALUES (?, ?)")) {

                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, descArea.getText().trim());
                pstmt.executeUpdate();

                loadCouncils();
                dialog.dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding department: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditCouncilDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = createThemedDialog(parentFrame, "Edit Department", 400, 250);

        int selectedRow = councilTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a Department to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int councilId = (int) councilTable.getValueAt(selectedRow, 0);
        String currentName = (String) councilTable.getValueAt(selectedRow, 1);
        String currentDesc = (String) councilTable.getValueAt(selectedRow, 2);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(currentTheme.background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(currentName, 20);
        applyThemeToTextField(nameField);
        
        JTextArea descArea = new JTextArea(currentDesc, 3, 20);
        applyThemeToTextArea(descArea);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JLabel nameLabel = new JLabel("Department Name:");
        nameLabel.setForeground(currentTheme.textPrimary);
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(currentTheme.textPrimary);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(descLabel, gbc);
        gbc.gridx = 1;
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                              currentTheme.textSecondary.getGreen(), 
                                                              currentTheme.textSecondary.getBlue(), 
                                                              100)));
        panel.add(descScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(currentTheme.background);
        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", PRIMARY_COLOR);

        saveButton.addActionListener(e -> {
            // Validate inputs before saving
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a department name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (descArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a department description",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (nameField.getText().length() > 50) {
                JOptionPane.showMessageDialog(dialog,
                        "Department name cannot be longer than 50 characters",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE councils SET council_name = ?, description = ? WHERE council_id = ?")) {

                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, descArea.getText().trim());
                pstmt.setInt(3, councilId);
                pstmt.executeUpdate();

                loadCouncils();
                dialog.dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating department: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedCouncil() {
        int selectedRow = councilTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a Department to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int councilId = (int) councilTable.getValueAt(selectedRow, 0);
        String councilName = (String) councilTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete department: " + councilName + "?\n"
                + "This will also delete all associated courses.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // First delete associated courses
                PreparedStatement pstmt = conn.prepareStatement(
                        "DELETE FROM courses WHERE council_id = ?");
                pstmt.setInt(1, councilId);
                pstmt.executeUpdate();

                // Then delete the department
                pstmt = conn.prepareStatement(
                        "DELETE FROM councils WHERE council_id = ?");
                pstmt.setInt(1, councilId);
                pstmt.executeUpdate();

                loadCouncils();
                courseTableModel.setRowCount(0); // Clear course table

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting department: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddCourseDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = createThemedDialog(parentFrame, "Add Course", 400, 200);

        int selectedRow = councilTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a Department first",
                    "No Department Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int councilId = (int) councilTable.getValueAt(selectedRow, 0);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(currentTheme.background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField codeField = new JTextField(20);
        applyThemeToTextField(codeField);
        
        JTextField nameField = new JTextField(20);
        applyThemeToTextField(nameField);

        JLabel codeLabel = new JLabel("Course Code:");
        codeLabel.setForeground(currentTheme.textPrimary);
        JLabel nameLabel = new JLabel("Course Name:");
        nameLabel.setForeground(currentTheme.textPrimary);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(codeLabel, gbc);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(currentTheme.background);
        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", PRIMARY_COLOR);

        saveButton.addActionListener(e -> {
            // Validate inputs before saving
            if (codeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a course code",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a course name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (codeField.getText().length() > 20) {
                JOptionPane.showMessageDialog(dialog,
                        "Course code cannot be longer than 20 characters",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (nameField.getText().length() > 100) {
                JOptionPane.showMessageDialog(dialog,
                        "Course name cannot be longer than 100 characters",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO courses (council_id, course_code, course_name) VALUES (?, ?, ?)")) {

                pstmt.setInt(1, councilId);
                pstmt.setString(2, codeField.getText().trim());
                pstmt.setString(3, nameField.getText().trim());
                pstmt.executeUpdate();

                loadCoursesForSelectedCouncil();
                dialog.dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding course: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditCourseDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = createThemedDialog(parentFrame, "Edit Course", 400, 200);

        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) courseTable.getValueAt(selectedRow, 0);
        String currentCode = (String) courseTable.getValueAt(selectedRow, 1);
        String currentName = (String) courseTable.getValueAt(selectedRow, 2);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(currentTheme.background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField codeField = new JTextField(currentCode, 20);
        applyThemeToTextField(codeField);
        
        JTextField nameField = new JTextField(currentName, 20);
        applyThemeToTextField(nameField);

        JLabel codeLabel = new JLabel("Course Code:");
        codeLabel.setForeground(currentTheme.textPrimary);
        JLabel nameLabel = new JLabel("Course Name:");
        nameLabel.setForeground(currentTheme.textPrimary);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(codeLabel, gbc);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(currentTheme.background);
        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", PRIMARY_COLOR);

        saveButton.addActionListener(e -> {
            // Validate inputs before saving
            if (codeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a course code",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a course name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (codeField.getText().length() > 20) {
                JOptionPane.showMessageDialog(dialog,
                        "Course code cannot be longer than 20 characters",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (nameField.getText().length() > 100) {
                JOptionPane.showMessageDialog(dialog,
                        "Course name cannot be longer than 100 characters",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE courses SET course_code = ?, course_name = ? WHERE course_id = ?")) {

                pstmt.setString(1, codeField.getText().trim());
                pstmt.setString(2, nameField.getText().trim());
                pstmt.setInt(3, courseId);
                pstmt.executeUpdate();

                loadCoursesForSelectedCouncil();
                dialog.dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating course: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) courseTable.getValueAt(selectedRow, 0);
        String courseName = (String) courseTable.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete course: " + courseName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                // First check if course has any active students
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM students WHERE course_id = ? AND active = TRUE")) {
                    checkStmt.setInt(1, courseId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Cannot delete course: Course has active students");
                    }
                }

                // Mark course as inactive
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE courses SET active = FALSE WHERE course_id = ?")) {
                    updateStmt.setInt(1, courseId);
                    int updated = updateStmt.executeUpdate();
                    if (updated == 0) {
                        throw new SQLException("Course not found");
                    }
                }

                // Update related student records to maintain history
                try (PreparedStatement updateStudentsStmt = conn.prepareStatement(
                        "UPDATE students SET active = FALSE WHERE course_id = ?")) {
                    updateStudentsStmt.setInt(1, courseId);
                    updateStudentsStmt.executeUpdate();
                }

                conn.commit();
                loadCoursesForSelectedCouncil();
                JOptionPane.showMessageDialog(this,
                        "Course deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                String errorMessage = e.getMessage();
                if (errorMessage.contains("active students")) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete this course because it has active students.\n"
                            + "Please transfer or deactivate the students first.",
                            "Delete Failed",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting course: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private JDialog createThemedDialog(JFrame parentFrame, String title, int width, int height) {
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        
        // Apply theme colors
        dialog.getContentPane().setBackground(currentTheme.background);
        return dialog;
    }

    // Add helper methods for applying theme to common components
    private void applyThemeToTextField(JTextField textField) {
        textField.setBackground(currentTheme.cardBackground);
        textField.setForeground(currentTheme.textPrimary);
        textField.setCaretColor(currentTheme.textPrimary);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                   currentTheme.textSecondary.getGreen(), 
                                                   currentTheme.textSecondary.getBlue(), 
                                                   100)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    private void applyThemeToTextArea(JTextArea textArea) {
        textArea.setBackground(currentTheme.cardBackground);
        textArea.setForeground(currentTheme.textPrimary);
        textArea.setCaretColor(currentTheme.textPrimary);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
}

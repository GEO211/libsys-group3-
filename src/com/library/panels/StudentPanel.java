package com.library.panels;

import com.library.dao.StudentDAO;
import com.library.models.Student;
import com.library.util.DatabaseConnection;
import com.library.util.Theme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.library.util.ExcelTemplateUtil;

public class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private StudentDAO studentDAO;
    private JComboBox<String> councilCombo;
    private JComboBox<String> courseCombo;
    private JComboBox<String> statusCombo;
    
    // Theme instance
    private Theme currentTheme;
    
    // Define style variables that will be set based on theme
    private Color PRIMARY_COLOR;
    private Color ACCENT_COLOR;
    private Color BACKGROUND_COLOR;
    private Color TABLE_HEADER_COLOR;
    private Color TABLE_ALTERNATE_COLOR;
    private Color BUTTON_TEXT_COLOR;
    private Font HEADER_FONT;
    private Font CONTENT_FONT;
    
    // Components that need theme updates
    private JScrollPane tableScrollPane;
    private JPanel topPanel;
    
    public StudentPanel() {
        this(null); // Call the constructor with theme parameter
    }
    
    public StudentPanel(Theme theme) {
        studentDAO = new StudentDAO();
        
        // Set theme and initialize colors
        this.currentTheme = theme != null ? theme : createDefaultTheme();
        updateThemeColors();
        
        initializeUI();
        loadStudents();
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
        TABLE_ALTERNATE_COLOR = new Color(
            currentTheme.cardBackground.getRed(),
            currentTheme.cardBackground.getGreen(),
            currentTheme.cardBackground.getBlue(),
            240); // Slightly transparent
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
        
        // Update top panel
        if (topPanel != null) {
            Component[] components = topPanel.getComponents();
            for (Component c : components) {
                if (c instanceof JPanel) {
                    c.setBackground(BACKGROUND_COLOR);
                }
            }
        }
        
        // Update table
        if (studentTable != null) {
            styleTable(studentTable);
        }
        
        // Update scrollpane
        if (tableScrollPane != null) {
            tableScrollPane.getViewport().setBackground(currentTheme.cardBackground);
            tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                             currentTheme.textSecondary.getGreen(), 
                                                                             currentTheme.textSecondary.getBlue(), 
                                                                             100)));
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);
        
        // Top Panel with search and buttons
        topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.setOpaque(false);
        
        // Button panel for Add and Download
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        // Modern add button with dropdown
        JButton addButton = new JButton("Add Student");
        addButton.setFont(CONTENT_FONT);
        addButton.setFocusPainted(false);
        addButton.setForeground(currentTheme.textPrimary);
        addButton.setBackground(currentTheme.cardBackground);
        
        // Download List button
        JButton downloadButton = new JButton("Download List");
        downloadButton.setFont(CONTENT_FONT);
        downloadButton.setFocusPainted(false);
        downloadButton.setForeground(currentTheme.textPrimary);
        downloadButton.setBackground(currentTheme.cardBackground);
        
        // Add button menu setup
        JPopupMenu addMenu = new JPopupMenu();
        JMenuItem manualEntryItem = new JMenuItem("Manual Entry");
        JMenuItem importExcelItem = new JMenuItem("Import from Excel");
        JMenuItem downloadTemplateItem = new JMenuItem("Download Excel Template");
        
        manualEntryItem.setFont(CONTENT_FONT);
        importExcelItem.setFont(CONTENT_FONT);
        downloadTemplateItem.setFont(CONTENT_FONT);
        
        addMenu.add(manualEntryItem);
        addMenu.add(importExcelItem);
        addMenu.addSeparator();
        addMenu.add(downloadTemplateItem);
        
        addButton.addActionListener(e -> addMenu.show(addButton, 0, addButton.getHeight()));
        manualEntryItem.addActionListener(e -> showStudentDialog(null));
        importExcelItem.addActionListener(e -> importStudentsFromExcel());
        downloadTemplateItem.addActionListener(e -> ExcelTemplateUtil.generateStudentTemplate());
        downloadButton.addActionListener(e -> downloadStudentList());
        
        buttonPanel.add(downloadButton);
        buttonPanel.add(addButton);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        
        // Modern search field
        searchField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(150, 150, 150));
                    g2.setFont(CONTENT_FONT);
                    FontMetrics fm = g2.getFontMetrics();
                    String placeholder = "Search ID or student name...";
                    g2.drawString(placeholder, getInsets().left + 5, 
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2.dispose();
                }
            }
        };
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                   currentTheme.textSecondary.getGreen(), 
                                                   currentTheme.textSecondary.getBlue(), 
                                                   100)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setFont(CONTENT_FONT);
        searchField.setBackground(currentTheme.cardBackground);
        searchField.setForeground(currentTheme.textPrimary);
        searchField.setCaretColor(currentTheme.textPrimary);
        
        searchPanel.add(searchField);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Table
        String[] columns = {
            "ID", "Student ID", "Name", "Course", "Council",
            "Year Level", "School Year", "Contact", "Email", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        styleTable(studentTable);
        
        // Table popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                    currentTheme.textSecondary.getGreen(), 
                                                                    currentTheme.textSecondary.getBlue(), 
                                                                    100)));
        
        JMenuItem viewItem = createStyledMenuItem("View Details", new ImageIcon("icons/view.png"));
        JMenuItem editItem = createStyledMenuItem("Edit", new ImageIcon("icons/edit.png"));
        JMenuItem deleteItem = createStyledMenuItem("Delete", new ImageIcon("icons/delete.png"));
        
        popupMenu.add(viewItem);
        popupMenu.add(editItem);
        popupMenu.addSeparator();
        popupMenu.add(deleteItem);
        
        studentTable.setComponentPopupMenu(popupMenu);
        
        // Scroll pane with modern styling
        tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                         currentTheme.textSecondary.getGreen(), 
                                                                         currentTheme.textSecondary.getBlue(), 
                                                                         100)));
        tableScrollPane.getViewport().setBackground(currentTheme.cardBackground);
        
        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchStudents();
                }
            }
        });
        
        editItem.addActionListener(e -> editSelectedStudent());
        deleteItem.addActionListener(e -> deleteSelectedStudent());
        viewItem.addActionListener(e -> viewSelectedStudent());
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(backgroundColor);
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
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)
                ));
            }
        });
        
        return button;
    }
    
    private JMenuItem createStyledMenuItem(String text, Icon icon) {
        JMenuItem item = new JMenuItem(text, icon);
        item.setFont(CONTENT_FONT);
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        item.setBackground(currentTheme.cardBackground);
        item.setForeground(currentTheme.textPrimary);
        return item;
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
    
    private void loadStudents() {
        tableModel.setRowCount(0);
        try {
            List<Student> students = studentDAO.getAllStudents();
            for (Student student : students) {
                Object[] row = {
                    student.getStudentId(),
                    student.getIdNumber(),
                    student.getFullName(),
                    student.getCourseName(),
                    student.getCouncilName(),
                    student.getYearLevel(),
                    student.getSchoolYear(),
                    student.getContactNumber(),
                    student.getEmail(),
                    student.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading students: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        tableModel.setRowCount(0);
        
        try {
            List<Student> students = studentDAO.searchStudents(searchTerm);
            for (Student student : students) {
                Object[] row = {
                    student.getStudentId(),
                    student.getIdNumber(),
                    student.getFullName(),
                    student.getCourseName(),
                    student.getCouncilName(),
                    student.getYearLevel(),
                    student.getSchoolYear(),
                    student.getContactNumber(),
                    student.getEmail(),
                    student.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching students: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showStudentDialog(Student student) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   student == null ? "Add New Student" : "Edit Student",
                                   true);
        dialog.setSize(800, 500);  // Increased width and reduced height
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form fields
        JTextField idField = new JTextField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField schoolYearField = new JTextField(20);
        JTextField contactField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        
        // Load councils and courses
        loadCouncilsAndCourses();
        
        // Status combo
        String[] statuses = {"Active", "Inactive", "Graduated", "On Leave"};
        statusCombo = new JComboBox<>(statuses);
        
        // Year Level combo box
        String[] yearLevels = {"1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year", "Graduate", "Faculty", "Staff"};
        JComboBox<String> yearLevelCombo = new JComboBox<>(yearLevels);
        
        // Left column
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Council:"), gbc);
        gbc.gridx = 1;
        formPanel.add(councilCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        formPanel.add(courseCombo, gbc);
        
        // Right column
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.insets = new Insets(5, 20, 5, 5);  // Add extra left padding for right column
        formPanel.add(new JLabel("Year Level:"), gbc);
        gbc.gridx = 3;
        formPanel.add(yearLevelCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("School Year:"), gbc);
        gbc.gridx = 3;
        formPanel.add(schoolYearField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 3;
        formPanel.add(contactField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        formPanel.add(statusCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // If editing, populate fields
        if (student != null) {
            idField.setText(student.getIdNumber());
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            yearLevelCombo.setSelectedItem(student.getYearLevel());
            schoolYearField.setText(student.getSchoolYear());
            contactField.setText(student.getContactNumber());
            emailField.setText(student.getEmail());
            statusCombo.setSelectedItem(student.getStatus());
            setSelectedCouncilAndCourse(student.getCouncilId(), student.getCourseId());
        }
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                Student newStudent = student == null ? new Student() : student;
                newStudent.setIdNumber(idField.getText());
                newStudent.setFirstName(firstNameField.getText());
                newStudent.setLastName(lastNameField.getText());
                newStudent.setYearLevel(yearLevelCombo.getSelectedItem().toString());
                newStudent.setSchoolYear(schoolYearField.getText());
                newStudent.setContactNumber(contactField.getText());
                newStudent.setEmail(emailField.getText());
                newStudent.setStatus(statusCombo.getSelectedItem().toString());
                
                // Get selected council and course IDs
                setStudentCouncilAndCourse(newStudent);
                
                if (student == null) {
                    studentDAO.addStudent(newStudent);
                } else {
                    studentDAO.updateStudent(newStudent);
                }
                
                loadStudents();
                dialog.dispose();
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Error saving student: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void loadCouncilsAndCourses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load councils
            String councilQuery = "SELECT council_id, council_name FROM councils";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(councilQuery);
            
            DefaultComboBoxModel<String> councilModel = new DefaultComboBoxModel<>();
            councilCombo = new JComboBox<>(councilModel);
            while (rs.next()) {
                councilModel.addElement(rs.getString("council_name"));
            }
            
            // Load courses
            String courseQuery = "SELECT course_id, course_name FROM courses";
            rs = stmt.executeQuery(courseQuery);
            
            DefaultComboBoxModel<String> courseModel = new DefaultComboBoxModel<>();
            courseCombo = new JComboBox<>(courseModel);
            while (rs.next()) {
                courseModel.addElement(rs.getString("course_name"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void setSelectedCouncilAndCourse(int councilId, int courseId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Set council
            String councilQuery = "SELECT council_name FROM councils WHERE council_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(councilQuery);
            pstmt.setInt(1, councilId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                councilCombo.setSelectedItem(rs.getString("council_name"));
            }
            
            // Set course
            String courseQuery = "SELECT course_name FROM courses WHERE course_id = ?";
            pstmt = conn.prepareStatement(courseQuery);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                courseCombo.setSelectedItem(rs.getString("course_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void setStudentCouncilAndCourse(Student student) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get council ID
            String councilQuery = "SELECT council_id FROM councils WHERE council_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(councilQuery);
            pstmt.setString(1, councilCombo.getSelectedItem().toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                student.setCouncilId(rs.getInt("council_id"));
            }
            
            // Get course ID
            String courseQuery = "SELECT course_id FROM courses WHERE course_name = ?";
            pstmt = conn.prepareStatement(courseQuery);
            pstmt.setString(1, courseCombo.getSelectedItem().toString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                student.setCourseId(rs.getInt("course_id"));
            }
        }
    }
    
    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int studentId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Student student = studentDAO.getStudentById(studentId);
                if (student != null) {
                    showStudentDialog(student);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading student details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) studentTable.getValueAt(selectedRow, 0);
        String studentName = (String) studentTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete student: " + studentName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentDAO.deleteStudent(studentId);
                loadStudents(); // Refresh table
                JOptionPane.showMessageDialog(this,
                    "Student deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                String errorMessage = e.getMessage();
                if (errorMessage.contains("borrowed books")) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot delete this student because they have borrowed books that haven't been returned.\n" +
                        "Please ensure all books are returned before deleting the student.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error deleting student: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void viewSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int studentId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Student student = studentDAO.getStudentById(studentId);
                if (student != null) {
                    showStudentDetailsDialog(student);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading student details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showStudentDetailsDialog(Student student) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   "Student Details", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add details
        addDetailField(panel, "Student ID:", student.getIdNumber());
        addDetailField(panel, "Name:", student.getFullName());
        addDetailField(panel, "Council:", student.getCouncilName());
        addDetailField(panel, "Course:", student.getCourseName());
        addDetailField(panel, "School Year:", student.getSchoolYear());
        addDetailField(panel, "Contact:", student.getContactNumber());
        addDetailField(panel, "Email:", student.getEmail());
        addDetailField(panel, "Status:", student.getStatus());
        addDetailField(panel, "Registration Date:", 
            student.getRegistrationDate() != null ? 
            student.getRegistrationDate().toString() : "N/A");
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonPanel);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    private void addDetailField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setMaximumSize(new Dimension(350, 25));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setPreferredSize(new Dimension(120, 20));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        
        fieldPanel.add(labelComponent);
        fieldPanel.add(Box.createHorizontalStrut(10));
        fieldPanel.add(valueComponent);
        
        panel.add(fieldPanel);
        panel.add(Box.createVerticalStrut(10));
    }
    
    private void importStudentsFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Excel Files", "xlsx", "xls"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                List<Student> students = readExcelFile(selectedFile);
                
                // Show preview dialog
                showImportPreviewDialog(students);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error reading Excel file: " + ex.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private List<Student> readExcelFile(File file) throws Exception {
        List<Student> students = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int firstRow = sheet.getFirstRowNum();
            
            // Skip header row
            for (int rowIndex = firstRow + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Student student = new Student();
                    
                    // Read cells and set values, using "Not Inputted" as default
                    student.setFirstName(getCellValueOrDefault(row.getCell(0), "Not Inputted"));
                    student.setLastName(getCellValueOrDefault(row.getCell(1), "Not Inputted"));
                    String courseName = getCellValueOrDefault(row.getCell(2), "Not Inputted");
                    String councilName = getCellValueOrDefault(row.getCell(3), "Not Inputted");
                    student.setContactNumber(getCellValueOrDefault(row.getCell(4), "Not Inputted"));
                    student.setEmail(getCellValueOrDefault(row.getCell(5), "Not Inputted"));
                    student.setSchoolYear(getCellValueOrDefault(row.getCell(6), "Not Inputted"));
                    
                    // Set course and council IDs based on names
                    setCourseAndCouncilIds(student, courseName, councilName);
                    
                    // Set default values for other fields
                    student.setStatus("Active");
                    student.setYearLevel("1st Year"); // Default year level
                    
                    students.add(student);
                }
            }
        }
        return students;
    }
    
    private String getCellValueOrDefault(Cell cell, String defaultValue) {
        if (cell == null) return defaultValue;
        
        switch (cell.getCellType()) {
            case STRING:
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? defaultValue : value;
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return defaultValue;
        }
    }
    
    private void setCourseAndCouncilIds(Student student, String courseName, String councilName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Try to find course ID
            String courseQuery = "SELECT course_id, council_id FROM courses WHERE course_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(courseQuery)) {
                pstmt.setString(1, courseName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    student.setCourseId(rs.getInt("course_id"));
                    student.setCouncilId(rs.getInt("council_id"));
                } else {
                    // If course not found, try to find council ID
                    String councilQuery = "SELECT council_id FROM councils WHERE council_name = ?";
                    try (PreparedStatement councilStmt = conn.prepareStatement(councilQuery)) {
                        councilStmt.setString(1, councilName);
                        ResultSet councilRs = councilStmt.executeQuery();
                        if (councilRs.next()) {
                            student.setCouncilId(councilRs.getInt("council_id"));
                        } else {
                            // Set default council ID (e.g., "Others" council)
                            student.setCouncilId(6); // Assuming 6 is the ID for "Others"
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void showImportPreviewDialog(List<Student> students) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   "Import Preview", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create table model for preview
        DefaultTableModel previewModel = new DefaultTableModel(
            new String[]{"First Name", "Last Name", "Course", "Council", "Contact", "Email", "School Year"},
            0
        );
        
        // Add students to preview table
        for (Student student : students) {
            previewModel.addRow(new Object[]{
                student.getFirstName(),
                student.getLastName(),
                student.getCourseName(),
                student.getCouncilName(),
                student.getContactNumber(),
                student.getEmail(),
                student.getSchoolYear()
            });
        }
        
        JTable previewTable = new JTable(previewModel);
        styleTable(previewTable);
        
        JScrollPane scrollPane = new JScrollPane(previewTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton importButton = new JButton("Import");
        JButton cancelButton = new JButton("Cancel");
        
        importButton.addActionListener(e -> {
            try {
                int successCount = 0;
                for (Student student : students) {
                    if (studentDAO.addStudent(student)) {
                        successCount++;
                    }
                }
                
                loadStudents(); // Refresh table
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                    successCount + " students imported successfully",
                    "Import Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error importing students: " + ex.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void downloadStudentList() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No students found to export",
                    "Export Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            ExcelTemplateUtil.exportStudentList(students);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error retrieving student data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 
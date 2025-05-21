package com.library.main;

import com.library.util.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;
import java.util.HashMap;
import java.util.Map;

public class CourseFrame extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private Map<String, JTable> collegeTables;
    private static final String[] COLLEGES = {"CED", "CBA", "CEAC", "CAS"};
    private static final String[] COURSE_LEVELS = {
        "Bachelor of Science (BS)",
        "Bachelor of Arts (AB)",
        "Master of Science (MS)",
        "Master of Arts (MA)",
        "Doctor of Philosophy (PhD)"
    };

    private static final Map<String, String[]> COLLEGE_COUNCILS = new HashMap<String, String[]>() {{
        put("CED", new String[]{"Teacher Education Council"});
        put("CBA", new String[]{"Business Council", "Tourism Council", "Accountancy Council"});
        put("CEAC", new String[]{"Engineering Council", "IT Council", "Architecture Council"});
        put("CAS", new String[]{"Arts Council", "Science Council", "Social Science Council"});
    }};
    
    private static final Map<String, String> COUNCIL_CODES = new HashMap<String, String>() {{
        // CED Councils
        put("Teacher Education Council", "TED");
        // CBA Councils
        put("Business Council", "BUS");
        put("Tourism Council", "TRM");
        put("Accountancy Council", "ACC");
        // CEAC Councils
        put("Engineering Council", "ENG");
        put("IT Council", "ITC");
        put("Architecture Council", "ARC");
        // CAS Councils
        put("Arts Council", "ART");
        put("Science Council", "SCI");
        put("Social Science Council", "SSC");
    }};

    public CourseFrame() {
        initializeFrame();
        createComponents();
        loadCourses();
    }

    private void initializeFrame() {
        setTitle("Course List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
    }
    
    private void createComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Initialize tables map
        collegeTables = new HashMap<>();
        
        // Create tabs for each college
        for (String college : COLLEGES) {
            JPanel collegePanel = createCollegePanel(college);
            tabbedPane.addTab(college, collegePanel);
        }
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }
    
    private JPanel createCollegePanel(String college) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create table with course code and name columns
        String[] columns = {"Course Code", "Course Name"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        
        // Set column widths
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150);
        columnModel.getColumn(1).setPreferredWidth(350);
        
        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(64, 93, 230));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("Add Course");
        JButton editButton = new JButton("Edit Course");
        JButton deleteButton = new JButton("Delete Course");
        
        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add action listeners
        addButton.addActionListener(e -> addCourse(college));
        editButton.addActionListener(e -> editCourse(table, college));
        deleteButton.addActionListener(e -> deleteCourse(table, college));
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Store table reference
        collegeTables.put(college, table);
        
        return panel;
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBackground(new Color(64, 93, 230));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void loadCourses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (String college : COLLEGES) {
                String query = "SELECT course_code, course_name FROM courses WHERE college = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, college);
                ResultSet rs = pstmt.executeQuery();
                
                DefaultTableModel model = (DefaultTableModel) collegeTables.get(college).getModel();
                model.setRowCount(0); // Clear existing rows
                
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("course_code"),
                        rs.getString("course_name")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading courses: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addCourse(String college) {
        // Create input fields
        JComboBox<String> councilCombo = new JComboBox<>(COLLEGE_COUNCILS.get(college));
        JComboBox<String> levelCombo = new JComboBox<>(COURSE_LEVELS);
        JTextField courseNameField = new JTextField();
        JTextField courseCodeField = new JTextField();
        courseCodeField.setEditable(false); // Make it read-only
        
        // Create the panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Council:"), gbc);
        gbc.gridx = 1;
        panel.add(councilCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Course Level:"), gbc);
        gbc.gridx = 1;
        panel.add(levelCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 1;
        panel.add(courseNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        panel.add(courseCodeField, gbc);
        
        // Add listeners to update course code automatically
        DocumentListener documentListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateCourseCode(); }
            public void removeUpdate(DocumentEvent e) { updateCourseCode(); }
            public void insertUpdate(DocumentEvent e) { updateCourseCode(); }
            
            private void updateCourseCode() {
                String courseName = courseNameField.getText().trim();
                String council = (String) councilCombo.getSelectedItem();
                String level = (String) levelCombo.getSelectedItem();
                
                if (!courseName.isEmpty() && council != null && level != null) {
                    String courseCode = generateCourseCode(council, courseName, level);
                    courseCodeField.setText(courseCode);
                }
            }
        };
        
        courseNameField.getDocument().addDocumentListener(documentListener);
        councilCombo.addActionListener(e -> documentListener.changedUpdate(null));
        levelCombo.addActionListener(e -> documentListener.changedUpdate(null));
        
        // Style the components
        councilCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        levelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseCodeField.setBackground(new Color(240, 240, 240));
        
        // Show dialog
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Add Course to " + college,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                String council = (String) councilCombo.getSelectedItem();
                String level = (String) levelCombo.getSelectedItem();
                String courseName = courseNameField.getText().trim();
                String courseCode = courseCodeField.getText().trim();
                
                if (courseName.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a course name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (courseCode.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a course code",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }  else if (council == null) {
                    JOptionPane.showMessageDialog(this,
                        "Please select a council",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }   
                
                // Extract the abbreviation from the level
                String levelCode = level.substring(level.lastIndexOf("(") + 1, level.lastIndexOf(")"));
                
                // Format the full course name
                String fullCourseName = levelCode + " in " + courseName;
                
                // Insert into database
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "INSERT INTO courses (course_code, course_name, college) VALUES (?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, courseCode);
                    pstmt.setString(2, fullCourseName);
                    pstmt.setString(3, college);
                    
                    pstmt.executeUpdate();
                    loadCourses(); // Refresh the tables
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error adding course: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editCourse(JTable table, String college) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String currentCode = (String) table.getValueAt(selectedRow, 0);
        String courseName = (String) table.getValueAt(selectedRow, 1);
        
        // Create input fields
        JComboBox<String> councilCombo = new JComboBox<>(COLLEGE_COUNCILS.get(college));
        JComboBox<String> levelCombo = new JComboBox<>(COURSE_LEVELS);
        JTextField courseNameField = new JTextField();
        JTextField courseCodeField = new JTextField();
        courseCodeField.setEditable(false); // Make it read-only
        
        // Parse existing course name to set initial values
        String[] parts = courseName.split(" in ", 2);
        String currentLevel = parts[0];
        String currentName = parts.length > 1 ? parts[1] : "";
        
        // Set initial values
        for (int i = 0; i < COURSE_LEVELS.length; i++) {
            if (COURSE_LEVELS[i].contains(currentLevel)) {
                levelCombo.setSelectedIndex(i);
                break;
            }
        }
        courseNameField.setText(currentName);
        courseCodeField.setText(currentCode);
        
        // Create the panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Council:"), gbc);
        gbc.gridx = 1;
        panel.add(councilCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Course Level:"), gbc);
        gbc.gridx = 1;
        panel.add(levelCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 1;
        panel.add(courseNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        panel.add(courseCodeField, gbc);
        
        // Add listeners to update course code automatically
        DocumentListener documentListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateCourseCode(); }
            public void removeUpdate(DocumentEvent e) { updateCourseCode(); }
            public void insertUpdate(DocumentEvent e) { updateCourseCode(); }
            
            private void updateCourseCode() {
                String courseName = courseNameField.getText().trim();
                String council = (String) councilCombo.getSelectedItem();
                String level = (String) levelCombo.getSelectedItem();
                
                if (!courseName.isEmpty() && council != null && level != null) {
                    String courseCode = generateCourseCode(council, courseName, level);
                    courseCodeField.setText(courseCode);
                }
            }
        };
        
        courseNameField.getDocument().addDocumentListener(documentListener);
        councilCombo.addActionListener(e -> documentListener.changedUpdate(null));
        levelCombo.addActionListener(e -> documentListener.changedUpdate(null));
        
        // Style the components
        councilCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        levelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseCodeField.setBackground(new Color(240, 240, 240));
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Edit Course",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                String level = (String) levelCombo.getSelectedItem();
                String newCourseName = courseNameField.getText().trim();
                String newCourseCode = courseCodeField.getText().trim();
                
                if (newCourseName.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a course name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Extract the abbreviation from the level
                String levelCode = level.substring(level.lastIndexOf("(") + 1, level.lastIndexOf(")"));
                
                // Format the full course name
                String fullCourseName = levelCode + " in " + newCourseName;
                
                // Update database
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "UPDATE courses SET course_code = ?, course_name = ? WHERE course_code = ? AND college = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, newCourseCode);
                    pstmt.setString(2, fullCourseName);
                    pstmt.setString(3, currentCode);
                    pstmt.setString(4, college);
                    
                    pstmt.executeUpdate();
                    loadCourses(); // Refresh the tables
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error updating course: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteCourse(JTable table, String college) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseCode = (String) table.getValueAt(selectedRow, 0);
        String courseName = (String) table.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the course:\n" + courseCode + " - " + courseName,
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM courses WHERE course_code = ? AND college = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, courseCode);
                pstmt.setString(2, college);
                
                pstmt.executeUpdate();
                loadCourses(); // Refresh the tables
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error deleting course: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateCourseCode(String council, String courseName, String level) {
        // Get council code
        String councilCode = COUNCIL_CODES.get(council);
        
        // Get level code (e.g., BS, MS, PhD)
        String levelCode = level.substring(level.lastIndexOf("(") + 1, level.lastIndexOf(")"));
        
        // Generate code from course name (first letter of each word, excluding common words)
        String[] words = courseName.toLowerCase().split("\\s+");
        StringBuilder courseNameCode = new StringBuilder();
        for (String word : words) {
            if (!isCommonWord(word) && courseNameCode.length() < 3) {
                courseNameCode.append(word.substring(0, 1).toUpperCase());
            }
        }
        
        // Combine all parts: COUNCIL-LEVEL-NAME
        return String.format("%s-%s-%s", councilCode, levelCode, courseNameCode.toString());
    }
    
    private boolean isCommonWord(String word) {
        String[] commonWords = {"in", "of", "and", "the", "for", "to", "with"};
        for (String commonWord : commonWords) {
            if (word.equalsIgnoreCase(commonWord)) {
                return true;
            }
        }
        return false;
    }
} 
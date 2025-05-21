package com.library.main;

import com.library.util.DatabaseConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.lang.StringBuilder;
import com.library.components.DatePicker;

public class AddBookFrame extends JFrame {

    private static final Map<String, String> CATEGORY_CODES = new HashMap<String, String>() {{
        put("Fiction", "FIC");
        put("Non-Fiction", "NFC");
        put("Reference", "REF");
        put("Textbook", "TXT");
        put("Magazine", "MAG");
        put("Journal", "JRN");
        put("Biography", "BIO");
        put("History", "HIS");
        put("Science", "SCI");
        put("Technology", "TEC");
        put("Literature", "LIT");
        put("Arts", "ART");
    }};

    private String generateUniqueBookId(String category) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String categoryCode = CATEGORY_CODES.getOrDefault(category, "GEN"); // Default to GEN if category not found
            String year = String.format("%02d", Calendar.getInstance().get(Calendar.YEAR) % 100); // Get last 2 digits of year
            
            // Get the current highest sequence number for this category and year
            String basePattern = categoryCode + year;
            String query = "SELECT book_id FROM books WHERE book_id LIKE ? ORDER BY book_id DESC LIMIT 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, basePattern + "%");
            ResultSet rs = pstmt.executeQuery();
            
            int sequence = 1;
            if (rs.next()) {
                String lastId = rs.getString("book_id");
                // Extract the sequence number from the last ID
                try {
                    sequence = Integer.parseInt(lastId.substring(5)) + 1;
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    sequence = 1;
                }
            }
            
            // Format: CATYR####
            // Example: FIC23001 (Fiction book from 2023, sequence 001)
            String newId = String.format("%s%s%03d", categoryCode, year, sequence);
            
            // Verify the ID is unique (in case of concurrent additions)
            while (isBookIdExists(conn, newId)) {
                sequence++;
                newId = String.format("%s%s%03d", categoryCode, year, sequence);
            }
            
            return newId;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private boolean isBookIdExists(Connection conn, String bookId) throws SQLException {
        String query = "SELECT COUNT(*) FROM books WHERE book_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, bookId);
        ResultSet rs = pstmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    private void addBook() {
        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(CATEGORY_CODES.keySet().toArray(new String[0]));
        JTextField publisherField = new JTextField();
        DatePicker yearPicker = new DatePicker();
        JTextField quantityField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField bookIdField = new JTextField();
        bookIdField.setEditable(false);
        
        // Create the panel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add components
        int gridy = 0;
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1;
        panel.add(bookIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        panel.add(categoryCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        panel.add(publisherField, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        panel.add(yearPicker, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        panel.add(locationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = gridy++;
        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);
        
        // Add listener to update book ID when category changes
        categoryCombo.addActionListener(e -> {
            String selectedCategory = (String) categoryCombo.getSelectedItem();
            String bookId = generateUniqueBookId(selectedCategory);
            if (bookId != null) {
                bookIdField.setText(bookId);
            }
        });
        
        // Generate initial book ID
        String initialBookId = generateUniqueBookId((String) categoryCombo.getSelectedItem());
        if (initialBookId != null) {
            bookIdField.setText(initialBookId);
        }
        
        // Style components
        styleComponents(new JComponent[]{
            titleField, authorField, categoryCombo, publisherField,
            quantityField, locationField, priceField, isbnField, bookIdField
        });
        
        bookIdField.setBackground(new Color(240, 240, 240));
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Add New Book",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            // Extract year from DatePicker
            java.util.Date selectedDate = yearPicker.getDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            int selectedYear = cal.get(Calendar.YEAR);

            // First check if any field is empty or contains only whitespace
            if (isEmptyOrWhitespace(isbnField.getText()) ||
                isEmptyOrWhitespace(titleField.getText()) ||
                isEmptyOrWhitespace(authorField.getText()) ||
                isEmptyOrWhitespace(publisherField.getText()) ||
                isEmptyOrWhitespace(quantityField.getText()) ||
                isEmptyOrWhitespace(locationField.getText()) ||
                isEmptyOrWhitespace(priceField.getText()) ||
                categoryCombo.getSelectedItem() == null ||
                selectedDate == null) {
                
                // Create a list of empty fields to show the user exactly what needs to be filled
                List<String> emptyFields = new ArrayList<>();
                if (isEmptyOrWhitespace(isbnField.getText())) emptyFields.add("ISBN");
                if (isEmptyOrWhitespace(titleField.getText())) emptyFields.add("Title");
                if (isEmptyOrWhitespace(authorField.getText())) emptyFields.add("Author");
                if (categoryCombo.getSelectedItem() == null) emptyFields.add("Category");
                if (isEmptyOrWhitespace(publisherField.getText())) emptyFields.add("Publisher");
                if (selectedDate == null) emptyFields.add("Publication Year");
                if (isEmptyOrWhitespace(quantityField.getText())) emptyFields.add("Quantity");
                if (isEmptyOrWhitespace(locationField.getText())) emptyFields.add("Location");
                if (isEmptyOrWhitespace(priceField.getText())) emptyFields.add("Price");
                
                StringBuilder message = new StringBuilder("The following fields are required and cannot be empty:\n\n");
                for (String field : emptyFields) {
                    message.append("• ").append(field).append("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    message.toString(),
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // If all fields are filled, proceed with other validations
            if (!validateBookInputs(isbnField, titleField, authorField, publisherField, 
                                  selectedYear, quantityField, locationField, priceField)) {
                return;
            }

            try {
                // Check for duplicate ISBN
                if (isDuplicateISBN(isbnField.getText().trim())) {
                    JOptionPane.showMessageDialog(this,
                        "A book with this ISBN already exists in the database.",
                        "Duplicate ISBN",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check for duplicate title and author combination
                if (isDuplicateTitleAndAuthor(titleField.getText().trim(), authorField.getText().trim())) {
                    JOptionPane.showMessageDialog(this,
                        "A book with this title and author combination already exists.",
                        "Duplicate Book",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // All validations passed, proceed with saving
                Connection conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO books (book_id, isbn, title, author, category, publisher, " +
                             "publication_year, quantity, available_quantity, location, price, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, bookIdField.getText().trim());
                    pstmt.setString(2, isbnField.getText().trim());
                    pstmt.setString(3, titleField.getText().trim());
                    pstmt.setString(4, authorField.getText().trim());
                    pstmt.setString(5, categoryCombo.getSelectedItem().toString());
                    pstmt.setString(6, publisherField.getText().trim());
                    pstmt.setInt(7, selectedYear);
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    pstmt.setInt(8, quantity);
                    pstmt.setInt(9, quantity); // Initially available = total
                    pstmt.setString(10, locationField.getText().trim());
                    pstmt.setDouble(11, Double.parseDouble(priceField.getText().trim()));
                    pstmt.setString(12, "Available");
                
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this,
                    "Book added successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                    dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error adding book: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void styleComponents(JComponent[] components) {
        for (JComponent component : components) {
            component.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            if (component instanceof JTextField) {
                ((JTextField) component).setColumns(20);
            }
        }
    }

    // Add this helper method to check for empty or whitespace-only strings
    private boolean isEmptyOrWhitespace(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Update the validateBookInputs method
    private boolean validateBookInputs(JTextField isbnField, JTextField titleField, 
                                     JTextField authorField, JTextField publisherField,
                                     int year, JTextField quantityField, 
                                     JTextField locationField, JTextField priceField) {
        // Validate ISBN format (13 digits)
        String isbn = isbnField.getText().trim();
        if (!isbn.matches("\\d{13}")) {
            JOptionPane.showMessageDialog(this,
                "ISBN must be exactly 13 digits.",
                "Invalid ISBN",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate title length
        if (titleField.getText().trim().length() < 2) {
            JOptionPane.showMessageDialog(this,
                "Title must be at least 2 characters long.",
                "Invalid Title",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate author name
        if (!authorField.getText().trim().matches("[a-zA-Z\\s.'-]+")) {
            JOptionPane.showMessageDialog(this,
                "Author name can only contain letters, spaces, and basic punctuation.",
                "Invalid Author Name",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (year < 1000 || year > currentYear) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid publication year (between 1000 and " + currentYear + ").",
                "Invalid Year",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate quantity
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 1) {
                JOptionPane.showMessageDialog(this,
                    "Quantity must be at least 1.",
                    "Invalid Quantity",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid quantity.",
                "Invalid Quantity",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate location
        if (locationField.getText().trim().length() < 2) {
            JOptionPane.showMessageDialog(this,
                "Location must be at least 2 characters long.",
                "Invalid Location",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate price
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Price must be a positive number.",
                    "Invalid Price",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid price.",
                "Invalid Price",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isDuplicateISBN(String isbn) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM books WHERE isbn = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean isDuplicateTitleAndAuthor(String title, String author) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM books WHERE LOWER(title) = LOWER(?) AND LOWER(author) = LOWER(?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public AddBookFrame() {
        setTitle("Add New Book");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        addBook();
    }
} 
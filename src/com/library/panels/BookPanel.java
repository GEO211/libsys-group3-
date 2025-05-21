package com.library.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.library.components.DatePicker;
import com.library.dao.BookDAO;
import com.library.models.Book;
import com.library.util.Theme;

public class BookPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private BookDAO bookDAO;
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
    
    public BookPanel() {
        this(null); // Call the constructor with theme parameter
    }
    
    public BookPanel(Theme theme) {
        bookDAO = new BookDAO();
        
        // Set theme and initialize colors
        this.currentTheme = theme != null ? theme : createDefaultTheme();
        updateThemeColors();
        
        initializeUI();
        loadBooks();
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
        if (bookTable != null) {
            styleTable(bookTable);
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
        
        // Top Panel with search and add button
        topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.setOpaque(false);
        
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
                    String placeholder = "Search ISBN or book title...";
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
        
        // Modern search button
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.setPreferredSize(new Dimension(100, 35));
        
        // Modern add button
        JButton addButton = createStyledButton("+ Add New Book", ACCENT_COLOR);
        addButton.setPreferredSize(new Dimension(160, 35));
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(addButton, BorderLayout.EAST);
        
        // Table
        String[] columns = {
            "ID", "ISBN", "Title", "Author", "Category",
            "Publication Year", "Price", "Quantity", "Available", "Location", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        styleTable(bookTable);
        
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
        
        bookTable.setComponentPopupMenu(popupMenu);
        
        // Scroll pane with modern styling
        tableScrollPane = new JScrollPane(bookTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                         currentTheme.textSecondary.getGreen(), 
                                                                         currentTheme.textSecondary.getBlue(), 
                                                                         100)));
        tableScrollPane.getViewport().setBackground(currentTheme.cardBackground);
        
        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        searchButton.addActionListener(e -> searchBooks());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBooks();
                }
            }
        });
        
        addButton.addActionListener(e -> showBookDialog(null));
        editItem.addActionListener(e -> editSelectedBook());
        deleteItem.addActionListener(e -> deleteSelectedBook());
        viewItem.addActionListener(e -> viewSelectedBook());
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
    
    private void loadBooks() {
        tableModel.setRowCount(0);
        try {
            List<Book> books = bookDAO.getAllBooks();
            for (Book book : books) {
                Object[] row = {
                    book.getBookId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPublicationYear(),
                    String.format("%.2f", book.getPrice()),
                    book.getQuantity(),
                    book.getAvailableQuantity(),
                    book.getLocation(),
                    book.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading books: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        tableModel.setRowCount(0);
        
        try {
            List<Book> books = bookDAO.searchBooks(searchTerm);
            for (Book book : books) {
                Object[] row = {
                    book.getBookId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPublicationYear(),
                    String.format("%.2f", book.getPrice()),
                    book.getQuantity(),
                    book.getAvailableQuantity(),
                    book.getLocation(),
                    book.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching books: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showBookDialog(Book book) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   book == null ? "Add New Book" : "Edit Book",
                                   true);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Add form fields
        JTextField isbnField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        DatePicker datePicker = new DatePicker();
        JTextField categoryField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        // Status combo
        String[] statuses = {"Available", "Reserved", "Maintenance"};
        statusCombo = new JComboBox<>(statuses);
        // Add fields to panel
        panel.add(createFormField("ISBN:", isbnField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Title:", titleField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Author:", authorField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Publisher:", publisherField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Publication Date:", datePicker));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Category:", categoryField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Quantity:", quantityField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Location:", locationField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Price:", priceField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Status:", statusCombo));
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonPanel);
        // If editing, populate fields
        if (book != null) {
            isbnField.setText(book.getIsbn());
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            publisherField.setText(book.getPublisher());
            if (book.getPublicationYear() > 0) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.YEAR, book.getPublicationYear());
                datePicker.setDate(cal.getTime());
            }
            categoryField.setText(book.getCategory());
            quantityField.setText(String.valueOf(book.getQuantity()));
            locationField.setText(book.getLocation());
            priceField.setText(String.format("%.2f", book.getPrice()));
            statusCombo.setSelectedItem(book.getStatus());
        }
        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate required fields
                if (isEmptyOrWhitespace(isbnField.getText()) ||
                    isEmptyOrWhitespace(titleField.getText()) ||
                    isEmptyOrWhitespace(authorField.getText()) ||
                    isEmptyOrWhitespace(categoryField.getText()) ||
                    isEmptyOrWhitespace(publisherField.getText()) ||
                    isEmptyOrWhitespace(quantityField.getText()) ||
                    isEmptyOrWhitespace(locationField.getText()) ||
                    isEmptyOrWhitespace(priceField.getText()) ||
                    datePicker.getDate() == null) {
                    StringBuilder message = new StringBuilder("The following fields are required:\n\n");
                    if (isEmptyOrWhitespace(isbnField.getText())) message.append("• ISBN\n");
                    if (isEmptyOrWhitespace(titleField.getText())) message.append("• Title\n");
                    if (isEmptyOrWhitespace(authorField.getText())) message.append("• Author\n");
                    if (isEmptyOrWhitespace(categoryField.getText())) message.append("• Category\n");
                    if (isEmptyOrWhitespace(publisherField.getText())) message.append("• Publisher\n");
                    if (datePicker.getDate() == null) message.append("• Publication Date\n");
                    if (isEmptyOrWhitespace(quantityField.getText())) message.append("• Quantity\n");
                    if (isEmptyOrWhitespace(locationField.getText())) message.append("• Location\n");
                    if (isEmptyOrWhitespace(priceField.getText())) message.append("• Price\n");
                    JOptionPane.showMessageDialog(dialog,
                        message.toString(),
                        "Required Fields Missing",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Book newBook = book == null ? new Book() : book;
                newBook.setIsbn(isbnField.getText().trim());
                newBook.setTitle(titleField.getText().trim());
                newBook.setAuthor(authorField.getText().trim());
                newBook.setPublisher(publisherField.getText().trim());
                newBook.setCategory(categoryField.getText().trim());
                newBook.setLocation(locationField.getText().trim());
                try {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(datePicker.getDate());
                    newBook.setPublicationYear(cal.get(java.util.Calendar.YEAR));
                    newBook.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                    newBook.setPrice(Double.parseDouble(priceField.getText().trim()));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please enter valid numbers for Quantity and Price",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validate the book using the model's validation
                if (!newBook.isValid()) {
                    List<String> errors = newBook.getValidationErrors();
                    JOptionPane.showMessageDialog(dialog,
                        "Please correct the following errors:\n\n" + String.join("\n", errors),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (book == null) {
                    newBook.setAvailableQuantity(newBook.getQuantity());
                    bookDAO.addBook(newBook);
                } else {
                    bookDAO.updateBook(newBook);
                }
                loadBooks();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Error saving book: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    // Helper method to check for empty or whitespace-only strings
    private boolean isEmptyOrWhitespace(String text) {
        return text == null || text.trim().isEmpty();
    }
    
    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setMaximumSize(new Dimension(380, 30));
        
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(120, 25));
        panel.add(jLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(field);
        
        return panel;
    }
    
    private void editSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Book book = bookDAO.getBookById(bookId);
                if (book != null) {
                    showBookDialog(book);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading book details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a book to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) bookTable.getValueAt(selectedRow, 0);
        String bookTitle = (String) bookTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete book: " + bookTitle + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookDAO.deleteBook(bookId);
                loadBooks(); // Refresh table
                JOptionPane.showMessageDialog(this,
                    "Book deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                String errorMessage = e.getMessage();
                if (errorMessage.contains("currently borrowed")) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot delete this book because it is currently borrowed.\n" +
                        "Please wait until the book is returned.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error deleting book: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void viewSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Book book = bookDAO.getBookById(bookId);
                if (book != null) {
                    showBookDetailsDialog(book);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading book details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showBookDetailsDialog(Book book) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   "Book Details", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add details
        addDetailField(panel, "ISBN:", book.getIsbn());
        addDetailField(panel, "Title:", book.getTitle());
        addDetailField(panel, "Author:", book.getAuthor());
        addDetailField(panel, "Publisher:", book.getPublisher());
        addDetailField(panel, "Publication Year:", String.valueOf(book.getPublicationYear()));
        addDetailField(panel, "Price:", String.format("%.2f", book.getPrice()));
        addDetailField(panel, "Category:", book.getCategory());
        addDetailField(panel, "Total Quantity:", String.valueOf(book.getQuantity()));
        addDetailField(panel, "Available:", String.valueOf(book.getAvailableQuantity()));
        addDetailField(panel, "Location:", book.getLocation());
        addDetailField(panel, "Status:", book.getStatus());
        addDetailField(panel, "Added Date:", 
            book.getAddedDate() != null ? 
            book.getAddedDate().toString() : "N/A");
        
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
} 
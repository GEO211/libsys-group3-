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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.FileWriter;
import java.awt.print.PrinterJob;
import java.awt.print.Printable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
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
import com.library.dao.BorrowingDAO;
import com.library.dao.StudentDAO;
import com.library.models.Book;
import com.library.models.Borrowing;
import com.library.models.Student;
import com.library.util.Theme;

public class BorrowingPanel extends JPanel {
    private JTable borrowingsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private BorrowingDAO borrowingDAO;
    private StudentDAO studentDAO;
    private BookDAO bookDAO;
    private int adminId;
    private String mode;
    
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
    private JScrollPane scrollPane;
    private JPanel searchPanel;
    
    public BorrowingPanel(int adminId) {
        this(adminId, "all");
    }
    
    public BorrowingPanel(int adminId, String mode) {
        this(adminId, mode, null);
    }
    
    public BorrowingPanel(int adminId, String mode, Theme theme) {
        this.adminId = adminId;
        this.mode = mode;
        this.borrowingDAO = new BorrowingDAO();
        studentDAO = new StudentDAO();
        bookDAO = new BookDAO();
        
        // Set theme and initialize colors
        this.currentTheme = theme != null ? theme : createDefaultTheme();
        updateThemeColors();
        
        initializeUI();
        loadBorrowings();
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
        
        // Update table
        if (borrowingsTable != null) {
            styleTable(borrowingsTable);
        }
        
        // Update scrollpane
        if (scrollPane != null) {
            scrollPane.getViewport().setBackground(currentTheme.cardBackground);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
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
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.setOpaque(false);
        
        // Search panel
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
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
                    String placeholder = "Search book title or student name...";
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
        
        // Add date filter
        DatePicker dateFilterPicker = new DatePicker();
        dateFilterPicker.setPreferredSize(new Dimension(120, 35));
        dateFilterPicker.addPropertyChangeListener("date", e -> searchBorrowings());
        JLabel dateLabel = new JLabel("Filter by Date:");
        dateLabel.setFont(CONTENT_FONT);
        dateLabel.setForeground(currentTheme.textPrimary);
        searchPanel.add(dateLabel);
        searchPanel.add(dateFilterPicker);
        
        // Add All button
        JButton allButton = createStyledButton("All", PRIMARY_COLOR);
        allButton.setPreferredSize(new Dimension(80, 35));
        allButton.addActionListener(e -> {
            // Clear search field
            searchField.setText("");
            // Load all borrowings directly
            try {
                tableModel.setRowCount(0);
                List<Borrowing> borrowings = borrowingDAO.getAllBorrowings();
                for (Borrowing borrowing : borrowings) {
                    Object[] row = {
                        borrowing.getBorrowingId(),
                        borrowing.getBookTitle(),
                        borrowing.getStudentName(),
                        borrowing.getCourseName(),
                        borrowing.getYearLevel(),
                        borrowing.getCouncilName(),
                        borrowing.getBorrowDate(),
                        borrowing.getDueDate(),
                        borrowing.getReturnDate(),
                        borrowing.getStatus(),
                        borrowing.getFineAmount()
                    };
                    tableModel.addRow(row);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(BorrowingPanel.this,
                    "Error loading borrowings: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        searchPanel.add(allButton);
        
        // Modern search button
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.setPreferredSize(new Dimension(100, 35));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton borrowButton = createStyledButton("+ Borrow Book", ACCENT_COLOR);
        JButton returnButton = createStyledButton("Return Book", PRIMARY_COLOR);
        JButton historyButton = createStyledButton("Transaction History", PRIMARY_COLOR);
        
        borrowButton.setPreferredSize(new Dimension(160, 35));
        returnButton.setPreferredSize(new Dimension(160, 35));
        historyButton.setPreferredSize(new Dimension(160, 35));
        
        // Only show buttons in specific modes
        if ("all".equals(mode) || "borrowed".equals(mode)) {
            buttonsPanel.add(borrowButton);
            buttonsPanel.add(returnButton);
        }
        
        // Add history button if not already in history mode
        if (!"history".equals(mode)) {
            buttonsPanel.add(historyButton);
        }
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);
        
        // Table
        String[] columns = {
            "ID", "Book Title", "Student Name", "Course", "Year Level", "Council", 
            "Borrow Date", "Due Date", "Return Date", "Status", "Fine"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        borrowingsTable = new JTable(tableModel);
        styleTable(borrowingsTable);
        
        // Set column widths
        int[] columnWidths = {60, 200, 200, 150, 100, 150, 120, 120, 120, 80, 80};
        for (int i = 0; i < columnWidths.length; i++) {
            borrowingsTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        // Create context menu
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        JMenuItem printItem = new JMenuItem("Print");
        JMenuItem editItem = new JMenuItem("Edit");
        
        // Style menu items
        viewDetailsItem.setBackground(currentTheme.cardBackground);
        viewDetailsItem.setForeground(currentTheme.textPrimary);
        printItem.setBackground(currentTheme.cardBackground);
        printItem.setForeground(currentTheme.textPrimary);
        editItem.setBackground(currentTheme.cardBackground);
        editItem.setForeground(currentTheme.textPrimary);
        
        contextMenu.add(viewDetailsItem);
        contextMenu.add(printItem);
        contextMenu.add(editItem);
        
        // Add action listeners for menu items
        viewDetailsItem.addActionListener(e -> showBorrowingDetails());
        printItem.addActionListener(e -> printBorrowingDetails());
        editItem.addActionListener(e -> editBorrowing());
        
        // Add mouse listener to table for context menu
        borrowingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            
            private void showContextMenu(MouseEvent e) {
                int row = borrowingsTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < borrowingsTable.getRowCount()) {
                    borrowingsTable.setRowSelectionInterval(row, row);
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        // Scroll pane with modern styling
        scrollPane = new JScrollPane(borrowingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(currentTheme.textSecondary.getRed(), 
                                                                    currentTheme.textSecondary.getGreen(), 
                                                                    currentTheme.textSecondary.getBlue(), 
                                                                    100)));
        scrollPane.getViewport().setBackground(currentTheme.cardBackground);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        borrowButton.addActionListener(e -> showBorrowDialog());
        returnButton.addActionListener(e -> returnSelectedBook());
        searchButton.addActionListener(e -> searchBorrowings());
        historyButton.addActionListener(e -> showTransactionHistory());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBorrowings();
                }
            }
        });
    }
    
    private void showTransactionHistory() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Transaction History", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(1000, 600);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create tabs panel
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabsPanel.setOpaque(false);
        
        JButton allButton = createTabButton("All Transactions", "all");
        JButton borrowedButton = createTabButton("Borrowed Books", "borrowed");
        JButton returnsButton = createTabButton("Returns", "returns");
        JButton overdueButton = createTabButton("Overdue Books", "overdue");
        
        tabsPanel.add(allButton);
        tabsPanel.add(borrowedButton);
        tabsPanel.add(returnsButton);
        tabsPanel.add(overdueButton);
        
        // Create table model and table
        String[] columns = {
            "ID", "Book Title", "Student Name", "Course", "Year Level", "Council", 
            "Borrow Date", "Due Date", "Return Date", "Status", "Fine"
        };
        
        DefaultTableModel historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable historyTable = new JTable(historyTableModel);
        styleTable(historyTable);
        
        // Set column widths
        int[] columnWidths = {60, 200, 200, 150, 100, 150, 120, 120, 120, 80, 80};
        for (int i = 0; i < columnWidths.length; i++) {
            historyTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = createStyledButton("Close", PRIMARY_COLOR);
        JButton exportButton = createStyledButton("Export", ACCENT_COLOR);
        JButton printAllButton = createStyledButton("Print", PRIMARY_COLOR);
        
        buttonPanel.add(exportButton);
        buttonPanel.add(printAllButton);
        buttonPanel.add(closeButton);
        
        // Add components to main panel
        mainPanel.add(tabsPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Add to dialog
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load initial data (All transactions)
        loadHistoryData(historyTableModel, "all");
        
        // Add action listeners for tabs
        ActionListener tabListener = e -> {
            JButton source = (JButton) e.getSource();
            // Reset all buttons to non-selected style
            for (Component c : tabsPanel.getComponents()) {
                if (c instanceof JButton) {
                    styleTabButton((JButton) c, false);
                }
            }
            // Set clicked button to selected style
            styleTabButton(source, true);
            // Load data based on tab
            String tabMode = (String) source.getClientProperty("mode");
            loadHistoryData(historyTableModel, tabMode);
        };
        
        allButton.addActionListener(tabListener);
        borrowedButton.addActionListener(tabListener);
        returnsButton.addActionListener(tabListener);
        overdueButton.addActionListener(tabListener);
        
        // Set initial tab as selected
        styleTabButton(allButton, true);
        
        // Add action listeners for buttons
        closeButton.addActionListener(e -> dialog.dispose());
        exportButton.addActionListener(e -> {
            exportToCSV(historyTable, "Transaction History");
        });
        printAllButton.addActionListener(e -> {
            printTable(historyTable, "Library Transaction History");
        });
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JButton createTabButton(String text, String mode) {
        JButton button = new JButton(text);
        button.setFont(HEADER_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240, 240, 240)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        
        // Store the mode as a client property
        button.putClientProperty("mode", mode);
        
        return button;
    }
    
    private void styleTabButton(JButton button, boolean selected) {
        if (selected) {
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, currentTheme.accentBlue),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            button.setForeground(currentTheme.accentBlue);
        } else {
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240, 240, 240)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            button.setForeground(BUTTON_TEXT_COLOR);
        }
    }
    
    private void loadHistoryData(DefaultTableModel model, String historyMode) {
        model.setRowCount(0);
        try {
            List<Borrowing> borrowings;
            switch (historyMode) {
                case "borrowed":
                    borrowings = borrowingDAO.getActiveBorrowings();
                    break;
                case "overdue":
                    borrowings = borrowingDAO.getOverdueBorrowings();
                    break;
                case "returns":
                    // Show only returned books
                    borrowings = borrowingDAO.getAllBorrowings().stream()
                        .filter(b -> "Returned".equals(b.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
                    break;
                default:
                    borrowings = borrowingDAO.getAllBorrowings();
            }
            
            for (Borrowing borrowing : borrowings) {
                Object[] row = {
                    borrowing.getBorrowingId(),
                    borrowing.getBookTitle(),
                    borrowing.getStudentName(),
                    borrowing.getCourseName(),
                    borrowing.getYearLevel(),
                    borrowing.getCouncilName(),
                    borrowing.getBorrowDate(),
                    borrowing.getDueDate(),
                    borrowing.getReturnDate(),
                    borrowing.getStatus(),
                    borrowing.getFineAmount()
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error loading transaction history: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
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
    
    private void loadBorrowings() {
        tableModel.setRowCount(0);
        try {
            List<Borrowing> borrowings;
            switch (mode) {
                case "borrowed":
                    borrowings = borrowingDAO.getActiveBorrowings();
                    break;
                case "overdue":
                    borrowings = borrowingDAO.getOverdueBorrowings();
                    break;
                case "returns":
                    // Show only returned books
                    borrowings = borrowingDAO.getAllBorrowings().stream()
                        .filter(b -> "Returned".equals(b.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
                    break;
                case "history":
                    borrowings = borrowingDAO.getAllBorrowings();
                    break;
                default:
                    borrowings = borrowingDAO.getAllBorrowings();
            }
            
            for (Borrowing borrowing : borrowings) {
                Object[] row = {
                    borrowing.getBorrowingId(),
                    borrowing.getBookTitle(),
                    borrowing.getStudentName(),
                    borrowing.getCourseName(),
                    borrowing.getYearLevel(),
                    borrowing.getCouncilName(),
                    borrowing.getBorrowDate(),
                    borrowing.getDueDate(),
                    borrowing.getReturnDate(),
                    borrowing.getStatus(),
                    borrowing.getFineAmount()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading borrowings: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showBorrowDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Borrow Book", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search Book:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Create book results panel
        DefaultListModel<String> bookListModel = new DefaultListModel<>();
        JList<String> bookList = new JList<>(bookListModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane bookScrollPane = new JScrollPane(bookList);
        bookScrollPane.setPreferredSize(new Dimension(450, 150));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Student ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID Number:"), gbc);
        JTextField studentIdField = new JTextField(20);
        JLabel studentNameLabel = new JLabel("");
        studentNameLabel.setForeground(Color.BLUE);
        
        // Add focus listener to validate student ID
        studentIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    String studentIdNumber = studentIdField.getText().trim();
                    if (!studentIdNumber.isEmpty()) {
                        Student student = studentDAO.getStudentByIdNumber(studentIdNumber);
                        if (student != null) {
                            studentNameLabel.setText(student.getFirstName() + " " + student.getLastName());
                            studentNameLabel.setForeground(Color.BLUE);
                        } else {
                            studentNameLabel.setText("Student not found");
                            studentNameLabel.setForeground(Color.RED);
                        }
                    }
                } catch (SQLException ex) {
                    studentNameLabel.setText("Error checking student");
                    studentNameLabel.setForeground(Color.RED);
                }
            }
        });
        
        JPanel studentPanel = new JPanel(new BorderLayout(5, 0));
        studentPanel.add(studentIdField, BorderLayout.CENTER);
        studentPanel.add(studentNameLabel, BorderLayout.EAST);
        gbc.gridx = 1;
        formPanel.add(studentPanel, gbc);
        
        // Borrow Date
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Borrow Date:"), gbc);
        JLabel borrowDateLabel = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        borrowDateLabel.setFont(CONTENT_FONT);
        borrowDateLabel.setForeground(new Color(0, 100, 0)); // Dark green
        gbc.gridx = 1;
        formPanel.add(borrowDateLabel, gbc);
        
        // Due Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Due Date:"), gbc);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        DatePicker dueDatePicker = new DatePicker(cal.getTime());
        gbc.gridx = 1;
        formPanel.add(dueDatePicker, gbc);
        
        // Add panels to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(bookScrollPane, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton borrowButton = new JButton("Borrow");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(borrowButton);
        buttonPanel.add(cancelButton);
        
        // Book data storage
        java.util.Map<String, Integer> bookIds = new java.util.HashMap<>();
        
        // Search action
        ActionListener searchAction = e -> {
            String searchTerm = searchField.getText().trim();
            bookListModel.clear();
            bookIds.clear();
            try {
                List<Book> books = bookDAO.searchBooks(searchTerm);
                for (Book book : books) {
                    if (book.getAvailableQuantity() > 0) {
                        String displayText = String.format("[%d] %s - %s (Available: %d)",
                            book.getBookId(), book.getTitle(), book.getIsbn(), 
                            book.getAvailableQuantity());
                        bookListModel.addElement(displayText);
                        bookIds.put(displayText, book.getBookId());
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error searching books: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        };
        
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction); // Allow search on Enter key
        
        // Borrow action
        borrowButton.addActionListener(e -> {
            try {
                // Validate book selection
                String selectedBook = bookList.getSelectedValue();
                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please select a book to borrow",
                        "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Validate student
                String studentIdNumber = studentIdField.getText().trim();
                if (studentIdNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please enter a Student ID Number",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Student student = studentDAO.getStudentByIdNumber(studentIdNumber);
                if (student == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Invalid Student ID Number. Student not found.",
                        "Student Not Found",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int bookId = bookIds.get(selectedBook);
                
                Borrowing borrowing = new Borrowing();
                borrowing.setBookId(bookId);
                borrowing.setStudentId(student.getStudentId()); // Use the internal student ID
                borrowing.setBorrowDate(new Date()); // Use current timestamp
                borrowing.setDueDate(dueDatePicker.getDate());
                borrowing.setAdminId(adminId);
                borrowing.setStatus("Borrowed"); // Set initial status to 'Borrowed'
                
                if (borrowingDAO.addBorrowing(borrowing)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Book borrowed successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadBorrowings();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to borrow book",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add components to dialog
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initial search to populate list
        searchField.setText("");
        searchAction.actionPerformed(null);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void returnSelectedBook() {
        int selectedRow = borrowingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a borrowing to return",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the borrowing ID as Integer and convert to String
        Integer borrowId = (Integer) borrowingsTable.getValueAt(selectedRow, 0);
        String borrowIdStr = borrowId.toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to return this book?",
            "Confirm Return",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (borrowingDAO.returnBook(borrowIdStr)) {
                    JOptionPane.showMessageDialog(this,
                        "Book returned successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadBorrowings();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to return book",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error returning book: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void searchBorrowings() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        Date filterDate = null;
        Component[] components = searchPanel.getComponents();
        for (Component c : components) {
            if (c instanceof DatePicker) {
                filterDate = ((DatePicker) c).getDate();
                break;
            }
        }
        
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            // If date is selected, get all borrowings and filter by date
            List<Borrowing> borrowings;
            if (filterDate != null) {
                borrowings = borrowingDAO.getAllBorrowings();
            } else {
                // If no date selected, use search term
                borrowings = borrowingDAO.searchBorrowings(searchTerm);
            }

            for (Borrowing borrowing : borrowings) {
                boolean shouldAdd = true;
                
                // Apply date filter if selected
                if (filterDate != null && borrowing.getBorrowDate() != null) {
                    String borrowDateStr = sdf.format(borrowing.getBorrowDate());
                    String filterDateStr = sdf.format(filterDate);
                    shouldAdd = borrowDateStr.equals(filterDateStr);
                }
                
                // Apply text search if date not selected
                if (filterDate == null && !searchTerm.isEmpty()) {
                    String bookTitle = borrowing.getBookTitle() != null ? borrowing.getBookTitle().toLowerCase() : "";
                    String studentName = borrowing.getStudentName() != null ? borrowing.getStudentName().toLowerCase() : "";
                    shouldAdd = bookTitle.contains(searchTerm) || studentName.contains(searchTerm);
                }
                
                if (shouldAdd) {
                    Object[] row = {
                        borrowing.getBorrowingId(),
                        borrowing.getBookTitle(),
                        borrowing.getStudentName(),
                        borrowing.getCourseName(),
                        borrowing.getYearLevel(),
                        borrowing.getCouncilName(),
                        borrowing.getBorrowDate(),
                        borrowing.getDueDate(),
                        borrowing.getReturnDate(),
                        borrowing.getStatus(),
                        borrowing.getFineAmount()
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching borrowings: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setMaximumSize(new Dimension(380, 30));
        
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(100, 25));
        panel.add(jLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(field);
        
        return panel;
    }
    
    private void showBorrowingDetails() {
        int selectedRow = borrowingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a borrowing to view",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create details dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   "Borrowing Details", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add all borrowing details
        String[] labels = {
            "Borrowing ID:", "Book Title:", "Student Name:", "Course:",
            "Year Level:", "Council:", "Borrow Date:", "Due Date:",
            "Return Date:", "Status:", "Fine:"
        };
        
        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 5));
            JLabel label = new JLabel(labels[i]);
            label.setFont(HEADER_FONT);
            JLabel value = new JLabel(String.valueOf(tableModel.getValueAt(selectedRow, i)));
            value.setFont(CONTENT_FONT);
            row.add(label, BorderLayout.WEST);
            row.add(value, BorderLayout.CENTER);
            detailsPanel.add(row);
            detailsPanel.add(Box.createVerticalStrut(10));
        }
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        // Add components to dialog
        dialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void printBorrowingDetails() {
        int selectedRow = borrowingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a borrowing to print",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Create print job
            java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) {
                    return java.awt.print.Printable.NO_SUCH_PAGE;
                }
                
                graphics.setFont(new Font("Serif", Font.BOLD, 16));
                int y = 100;
                graphics.drawString("Borrowing Details", 100, y);
                y += 30;
                
                graphics.setFont(new Font("Serif", Font.PLAIN, 12));
                String[] labels = {
                    "Borrowing ID:", "Book Title:", "Student Name:", "Course:",
                    "Year Level:", "Council:", "Borrow Date:", "Due Date:",
                    "Return Date:", "Status:", "Fine:"
                };
                
                for (int i = 0; i < labels.length; i++) {
                    String text = labels[i] + " " + tableModel.getValueAt(selectedRow, i);
                    graphics.drawString(text, 100, y);
                    y += 20;
                }
                
                return java.awt.print.Printable.PAGE_EXISTS;
            });
            
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error printing: " + ex.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editBorrowing() {
        int selectedRow = borrowingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a borrowing to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the borrowing ID
        int borrowingId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Create edit dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   "Edit Borrowing", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add form fields
        // Due Date
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Due Date:"), gbc);
        DatePicker dueDatePicker = new DatePicker();
        gbc.gridx = 1;
        formPanel.add(dueDatePicker, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Status:"), gbc);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Borrowed", "Returned", "Overdue"});
        statusCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 9)); // Status column
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                // Update the borrowing
                String status = (String) statusCombo.getSelectedItem();
                Date dueDate = dueDatePicker.getDate();
                
                if (borrowingDAO.updateBorrowing(borrowingId, status, dueDate)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Borrowing updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadBorrowings(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to update borrowing",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void exportToCSV(JTable table, String fileName) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }
                
                try (FileWriter csvWriter = new FileWriter(filePath)) {
                    // Write headers
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        csvWriter.append(escapeCSV(table.getColumnName(i)));
                        if (i < table.getColumnCount() - 1) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                    
                    // Write data
                    for (int row = 0; row < table.getRowCount(); row++) {
                        for (int col = 0; col < table.getColumnCount(); col++) {
                            Object value = table.getValueAt(row, col);
                            String cellValue = value != null ? value.toString() : "";
                            csvWriter.append(escapeCSV(cellValue));
                            if (col < table.getColumnCount() - 1) {
                                csvWriter.append(",");
                            }
                        }
                        csvWriter.append("\n");
                    }
                    
                    csvWriter.flush();
                }
                
                JOptionPane.showMessageDialog(this,
                    "Export completed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error exporting to CSV: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\""); // Escape quotes
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            escaped = "\"" + escaped + "\""; // Wrap in quotes if contains special chars
        }
        return escaped;
    }
    
    private void printTable(JTable table, String title) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                
                // Set up fonts
                Font titleFont = new Font("Serif", Font.BOLD, 18);
                Font headerFont = new Font("Serif", Font.BOLD, 12);
                Font contentFont = new Font("Serif", Font.PLAIN, 10);
                
                // Print title
                g2d.setFont(titleFont);
                g2d.drawString(title, 50, 50);
                
                // Calculate row height and column widths
                int rowHeight = 20;
                int startY = 100;
                int[] columnWidths = new int[table.getColumnCount()];
                int totalWidth = 0;
                
                // Get maximum width for each column
                for (int col = 0; col < table.getColumnCount(); col++) {
                    columnWidths[col] = SwingUtilities.computeStringWidth(
                        g2d.getFontMetrics(headerFont),
                        table.getColumnName(col)
                    );
                    
                    for (int row = 0; row < table.getRowCount(); row++) {
                        Object value = table.getValueAt(row, col);
                        int width = SwingUtilities.computeStringWidth(
                            g2d.getFontMetrics(contentFont),
                            value != null ? value.toString() : ""
                        );
                        columnWidths[col] = Math.max(columnWidths[col], width);
                    }
                    totalWidth += columnWidths[col] + 10; // Add padding
                }
                
                // Scale if too wide
                double scale = 1.0;
                int maxWidth = (int) pageFormat.getImageableWidth() - 100;
                if (totalWidth > maxWidth) {
                    scale = (double) maxWidth / totalWidth;
                    g2d.scale(scale, 1.0);
                }
                
                // Print column headers
                g2d.setFont(headerFont);
                int x = 50;
                for (int col = 0; col < table.getColumnCount(); col++) {
                    g2d.drawString(table.getColumnName(col), (int)(x/scale), startY);
                    x += columnWidths[col] + 10;
                }
                
                // Print data
                g2d.setFont(contentFont);
                for (int row = 0; row < table.getRowCount(); row++) {
                    x = 50;
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        Object value = table.getValueAt(row, col);
                        String str = value != null ? value.toString() : "";
                        g2d.drawString(str, (int)(x/scale), startY + ((row + 1) * rowHeight));
                        x += columnWidths[col] + 10;
                    }
                }
                
                return Printable.PAGE_EXISTS;
            });
            
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error printing: " + e.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 
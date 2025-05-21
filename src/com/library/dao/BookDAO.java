package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.library.models.Book;
import com.library.util.DatabaseConnection;

public class BookDAO {
    
    public boolean addBook(Book book) throws SQLException {
        // Add validation before inserting
        if (!book.isValid()) {
            List<String> errors = book.getValidationErrors();
            throw new SQLException("Invalid book data: " + String.join(", ", errors));
        }

        // Additional ISBN validation
        if (!book.getIsbn().matches("\\d+")) {
            throw new SQLException("ISBN must contain only numbers");
        }

        String sql = "INSERT INTO books (isbn, title, author, publisher, " +
                    "publication_year, category, quantity, available_quantity, " +
                    "location, status, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getIsbn().trim());
            pstmt.setString(2, book.getTitle().trim());
            pstmt.setString(3, book.getAuthor().trim());
            pstmt.setString(4, book.getPublisher().trim());
            pstmt.setInt(5, book.getPublicationYear());
            pstmt.setString(6, book.getCategory().trim());
            pstmt.setInt(7, book.getQuantity());
            pstmt.setInt(8, book.getQuantity()); // Initially available = total
            pstmt.setString(9, book.getLocation().trim());
            pstmt.setString(10, "Available");
            pstmt.setDouble(11, book.getPrice());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, publisher=?, " +
                    "publication_year=?, category=?, quantity=?, available_quantity=?, " +
                    "location=?, status=?, price=? WHERE book_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublicationYear());
            pstmt.setString(6, book.getCategory());
            pstmt.setInt(7, book.getQuantity());
            pstmt.setInt(8, book.getAvailableQuantity());
            pstmt.setString(9, book.getLocation());
            pstmt.setString(10, book.getStatus());
            pstmt.setDouble(11, book.getPrice());
            pstmt.setInt(12, book.getBookId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public void deleteBook(int bookId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First check if book has any active borrowings
            try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM borrowings WHERE book_id = ? AND status = 'Borrowed'")) {
                checkStmt.setInt(1, bookId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete book: Book is currently borrowed");
                }
            }

            // Mark book as inactive instead of deleting
            try (PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE books SET active = FALSE WHERE book_id = ?")) {
                updateStmt.setInt(1, bookId);
                int updated = updateStmt.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Book not found");
                }
            }

            // Update related borrowing records
            try (PreparedStatement updateBorrowingsStmt = conn.prepareStatement(
                "UPDATE borrowings SET status = 'Archived' WHERE book_id = ? AND status != 'Borrowed'")) {
                updateBorrowingsStmt.setInt(1, bookId);
                updateBorrowingsStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
        }
        return null;
    }
    
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE active = TRUE ORDER BY title";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }
    
    public List<Book> searchBooks(String searchTerm) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE " +
                    "(book_id LIKE ? OR " +
                    "title LIKE ? OR " +
                    "isbn LIKE ? OR " +
                    "author LIKE ?) " +
                    "AND available_quantity > 0 " +
                    "ORDER BY title ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, pattern);
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }
    
    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE status = 'Available' AND available_quantity > 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }
    
    public boolean updateBookQuantity(int bookId, int change) throws SQLException {
        String sql = "UPDATE books SET available_quantity = available_quantity + ?, " +
                    "status = CASE WHEN available_quantity + ? <= 0 THEN 'Borrowed' ELSE 'Available' END " +
                    "WHERE book_id = ? AND (available_quantity + ?) >= 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, change);
            pstmt.setInt(2, change);
            pstmt.setInt(3, bookId);
            pstmt.setInt(4, change);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateBookStatus(int bookId, String status) throws SQLException {
        String sql = "UPDATE books SET status = ? WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, bookId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public Book findByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
        }
        return null;
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setCategory(rs.getString("category"));
        book.setQuantity(rs.getInt("quantity"));
        book.setAvailableQuantity(rs.getInt("available_quantity"));
        book.setLocation(rs.getString("location"));
        book.setAddedDate(rs.getTimestamp("added_date"));
        book.setStatus(rs.getString("status"));
        try {
            book.setPrice(rs.getDouble("price"));
        } catch (SQLException e) {
            book.setPrice(0.0); // fallback if column doesn't exist
        }
        return book;
    }
} 
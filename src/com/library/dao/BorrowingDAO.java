package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.library.models.Borrowing;
import com.library.models.BorrowingRecord;
import com.library.util.DatabaseConnection;

public class BorrowingDAO {

    private BookDAO bookDAO;

    public BorrowingDAO() {
        this.bookDAO = new BookDAO();
    }

    public boolean addBorrowing(Borrowing borrowing) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First check if book is available
            String checkAvailabilityQuery = "SELECT available_quantity FROM books WHERE book_id = ? AND available_quantity > 0";
            pstmt = conn.prepareStatement(checkAvailabilityQuery);
            pstmt.setInt(1, borrowing.getBookId());
            rs = pstmt.executeQuery();

            if (!rs.next() || rs.getInt("available_quantity") <= 0) {
                throw new SQLException("Book is not available for borrowing");
            }

            // Close the first prepared statement and result set before creating a new one
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }

            // Insert borrowing record
            String insertQuery = "INSERT INTO borrowings (student_id, book_id, admin_id, borrow_date, due_date, status) "
                    + "VALUES (?, ?, ?, ?, ?, 'Borrowed')";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setInt(1, borrowing.getStudentId());
            pstmt.setInt(2, borrowing.getBookId());
            pstmt.setInt(3, borrowing.getAdminId());
            pstmt.setTimestamp(4, new Timestamp(borrowing.getBorrowDate().getTime()));
            pstmt.setTimestamp(5, new Timestamp(borrowing.getDueDate().getTime()));

            int borrowingInserted = pstmt.executeUpdate();
            pstmt.close();

            if (borrowingInserted > 0) {
                // Update book available quantity using the same connection
                updateBookQuantity(conn, borrowing.getBookId(), -1);
                conn.commit(); // Commit transaction
                success = true;
            } else {
                conn.rollback();
            }

            return success;

        } catch (SQLException e) {
            if (conn != null && !conn.isClosed()) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Error adding borrowing: " + e.getMessage(), e);
        } finally {
            // Close resources in reverse order
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean returnBook(String borrowId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First get the book_id from the borrowing record
            String getBookIdQuery = "SELECT book_id FROM borrowings WHERE borrowing_id = ?";
            pstmt = conn.prepareStatement(getBookIdQuery);
            pstmt.setString(1, borrowId);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Borrowing record not found");
            }

            int bookId = rs.getInt("book_id");
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }

            // Calculate fine if overdue
            double fine = 0.0;
            String getFineQuery = "SELECT DATEDIFF(CURRENT_TIMESTAMP, due_date) as days_overdue "
                    + "FROM borrowings WHERE borrowing_id = ? AND due_date < CURRENT_TIMESTAMP";
            pstmt = conn.prepareStatement(getFineQuery);
            pstmt.setString(1, borrowId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int daysOverdue = rs.getInt("days_overdue");
                if (daysOverdue > 0) {
                    fine = daysOverdue * 1.0; // ₱1 per day
                }
            }
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }

            // Update borrowing status
            String updateBorrowingQuery = "UPDATE borrowings SET return_date = CURRENT_TIMESTAMP, "
                    + "status = 'Returned', fine_amount = ? WHERE borrowing_id = ?";
            pstmt = conn.prepareStatement(updateBorrowingQuery);
            pstmt.setDouble(1, fine);
            pstmt.setString(2, borrowId);
            int borrowingUpdated = pstmt.executeUpdate();
            if (pstmt != null) {
                pstmt.close();
            }

            if (borrowingUpdated > 0) {
                // Update book available quantity and status using the same connection
                String updateBookQuery = "UPDATE books SET available_quantity = available_quantity + 1, "
                        + "status = CASE WHEN available_quantity + 1 > 0 THEN 'Available' ELSE 'Borrowed' END "
                        + "WHERE book_id = ?";
                pstmt = conn.prepareStatement(updateBookQuery);
                pstmt.setInt(1, bookId);
                int bookUpdated = pstmt.executeUpdate();

                if (bookUpdated > 0) {
                    conn.commit();
                    success = true;
                } else {
                    conn.rollback();
                }
            } else {
                conn.rollback();
            }

            return success;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log the rollback error but throw the original exception
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Close resources in reverse order
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateBookQuantity(Connection conn, int bookId, int change) throws SQLException {
        String sql = "UPDATE books SET available_quantity = available_quantity + ? WHERE book_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, change);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        }
    }

    public Borrowing getBorrowingById(int borrowingId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT b.*, s.first_name, s.last_name, s.id_number, s.year_level, s.school_year, "
                    + "bk.title, bk.isbn, c.course_name, co.council_name, "
                    + "CONCAT(a.first_name, ' ', a.last_name) as admin_name "
                    + "FROM borrowings b "
                    + "JOIN students s ON b.student_id = s.student_id "
                    + "JOIN books bk ON b.book_id = bk.book_id "
                    + "JOIN courses c ON s.course_id = c.course_id "
                    + "JOIN councils co ON s.council_id = co.council_id "
                    + "JOIN admins a ON b.admin_id = a.admin_id "
                    + "WHERE b.borrowing_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, borrowingId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBorrowing(rs);
            }
            return null;

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String getBaseBorrowingQuery() {
        return "SELECT b.*, s.first_name, s.last_name, s.id_number, s.year_level, s.school_year, "
                + "bk.title, bk.isbn, c.course_name, co.council_name, "
                + "CONCAT(a.first_name, ' ', a.last_name) as admin_name, "
                + "DATE_FORMAT(b.borrow_date, '%m/%d/%Y %l:%i %p') as formatted_borrow_date, "
                + "DATE_FORMAT(b.due_date, '%m/%d/%Y %l:%i %p') as formatted_due_date, "
                + "DATE_FORMAT(b.return_date, '%m/%d/%Y %l:%i %p') as formatted_return_date "
                + "FROM borrowings b "
                + "JOIN students s ON b.student_id = s.student_id "
                + "JOIN books bk ON b.book_id = bk.book_id "
                + "JOIN courses c ON s.course_id = c.course_id "
                + "JOIN councils co ON s.council_id = co.council_id "
                + "JOIN admins a ON b.admin_id = a.admin_id ";
    }

    public List<Borrowing> getAllBorrowings() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = getBaseBorrowingQuery()
                    + "ORDER BY b.borrow_date DESC";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                borrowings.add(mapResultSetToBorrowing(rs));
            }
            return borrowings;

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Borrowing> getActiveBorrowings() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = getBaseBorrowingQuery()
                    + "WHERE b.status = 'Borrowed' "
                    + "ORDER BY b.due_date ASC";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                borrowings.add(mapResultSetToBorrowing(rs));
            }
            return borrowings;

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Borrowing> getOverdueBorrowings() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = getBaseBorrowingQuery()
                    + "WHERE b.status = 'Borrowed' AND b.due_date < NOW() "
                    + "ORDER BY b.due_date ASC";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Borrowing borrowing = mapResultSetToBorrowing(rs);
                // Calculate days overdue
                long daysOverdue = (System.currentTimeMillis() - borrowing.getDueDate().getTime()) / (1000 * 60 * 60 * 24);
                borrowing.setStatus("Overdue (" + daysOverdue + " days)");
                borrowings.add(borrowing);
            }
            return borrowings;

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Borrowing> getBorrowingsByStudent(int studentId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = getBaseBorrowingQuery()
                    + "WHERE b.student_id = ? "
                    + "ORDER BY b.borrow_date DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                borrowings.add(mapResultSetToBorrowing(rs));
            }
            return borrowings;

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Borrowing> searchBorrowings(String searchTerm) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = getBaseBorrowingQuery()
                    + "WHERE s.id_number LIKE ? OR "
                    + "CONCAT(s.first_name, ' ', s.last_name) LIKE ? OR "
                    + "bk.title LIKE ? OR bk.isbn LIKE ? "
                    + "ORDER BY b.borrow_date DESC";

            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, searchPattern);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                borrowings.add(mapResultSetToBorrowing(rs));
            }
            return borrowings;

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Borrowing mapResultSetToBorrowing(ResultSet rs) throws SQLException {
        Borrowing borrowing = new Borrowing();
        borrowing.setBorrowingId(rs.getInt("borrowing_id"));
        borrowing.setStudentId(rs.getInt("student_id"));
        borrowing.setBookId(rs.getInt("book_id"));
        borrowing.setAdminId(rs.getInt("admin_id"));

        // Use formatted dates from the query
        String borrowDateStr = rs.getString("formatted_borrow_date");
        String dueDateStr = rs.getString("formatted_due_date");
        String returnDateStr = rs.getString("formatted_return_date");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        try {
            if (borrowDateStr != null) {
                borrowing.setBorrowDate(sdf.parse(borrowDateStr));
            }
            if (dueDateStr != null) {
                borrowing.setDueDate(sdf.parse(dueDateStr));
            }
            if (returnDateStr != null) {
                borrowing.setReturnDate(sdf.parse(returnDateStr));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String status = rs.getString("status");
        switch (status) {
            case "Borrowed":
                if (borrowing.getDueDate() != null && borrowing.getDueDate().before(new Date())) {
                    long daysOverdue = (System.currentTimeMillis() - borrowing.getDueDate().getTime()) / (1000 * 60 * 60 * 24);
                    borrowing.setStatus("Overdue (" + daysOverdue + " days)");
                } else {
                    borrowing.setStatus("Borrowed");
                }
                break;
            case "Returned":
                borrowing.setStatus("Returned");
                break;
            default:
                borrowing.setStatus(status);
        }

        borrowing.setFineAmount(rs.getDouble("fine_amount"));

        // Set individual fields
        borrowing.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
        borrowing.setStudentIdNumber(rs.getString("id_number"));
        borrowing.setBookTitle(rs.getString("title"));
        borrowing.setBookIsbn(rs.getString("isbn"));
        borrowing.setAdminName(rs.getString("admin_name"));
        borrowing.setCourseName(rs.getString("course_name"));
        borrowing.setYearLevel(rs.getString("year_level"));
        borrowing.setSchoolYear(rs.getString("school_year"));
        borrowing.setCouncilName(rs.getString("council_name"));

        return borrowing;
    }

    public List<BorrowingRecord> getBorrowingRecords() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BorrowingRecord> records = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT b.borrowing_id, b.book_id, bk.title, s.student_id, "
                    + "CONCAT(s.first_name, ' ', s.last_name) as student_name, "
                    + "DATE_FORMAT(b.borrow_date, '%m/%d/%Y %l:%i %p') as formatted_borrow_date, "
                    + "DATE_FORMAT(b.due_date, '%m/%d/%Y %l:%i %p') as formatted_due_date, "
                    + "DATE_FORMAT(b.return_date, '%m/%d/%Y %l:%i %p') as formatted_return_date, "
                    + "b.status, b.fine_amount, "
                    + "c.course_name, s.year_level, co.council_name "
                    + "FROM borrowings b "
                    + "JOIN books bk ON b.book_id = bk.book_id "
                    + "JOIN students s ON b.student_id = s.student_id "
                    + "JOIN courses c ON s.course_id = c.course_id "
                    + "JOIN councils co ON s.council_id = co.council_id "
                    + "ORDER BY b.borrow_date DESC";

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

            while (rs.next()) {
                String borrowDateStr = rs.getString("formatted_borrow_date");
                String dueDateStr = rs.getString("formatted_due_date");
                String returnDateStr = rs.getString("formatted_return_date");

                Timestamp borrowDate = null;
                Timestamp dueDate = null;
                Timestamp returnDate = null;

                try {
                    if (borrowDateStr != null) {
                        borrowDate = new Timestamp(sdf.parse(borrowDateStr).getTime());
                    }
                    if (dueDateStr != null) {
                        dueDate = new Timestamp(sdf.parse(dueDateStr).getTime());
                    }
                    if (returnDateStr != null) {
                        returnDate = new Timestamp(sdf.parse(returnDateStr).getTime());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String status = rs.getString("status");
                if ("Borrowed".equals(status) && dueDate != null && dueDate.before(new Timestamp(System.currentTimeMillis()))) {
                    long daysOverdue = (System.currentTimeMillis() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
                    status = "Overdue (" + daysOverdue + " days)";
                }

                BorrowingRecord record = new BorrowingRecord(
                        rs.getString("borrowing_id"),
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        borrowDate,
                        dueDate,
                        returnDate,
                        status
                );

                record.setCourseName(rs.getString("course_name"));
                record.setYearLevel(rs.getString("year_level"));
                record.setCouncilName(rs.getString("council_name"));
                records.add(record);
            }
            return records;

        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateBorrowing(int borrowingId, String status, Date dueDate) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE borrowings SET status = ?, due_date = ? WHERE borrowing_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, new Timestamp(dueDate.getTime()));
            pstmt.setInt(3, borrowingId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } finally {
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

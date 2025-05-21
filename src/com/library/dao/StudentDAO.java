package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.library.models.Student;
import com.library.util.DatabaseConnection;

public class StudentDAO {

    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (id_number, first_name, last_name, course_id, "
                + "council_id, school_year, year_level, contact_number, email, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getIdNumber());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setInt(4, student.getCourseId());
            pstmt.setInt(5, student.getCouncilId());
            pstmt.setString(6, student.getSchoolYear());
            pstmt.setString(7, student.getYearLevel());
            pstmt.setString(8, student.getContactNumber());
            pstmt.setString(9, student.getEmail());
            pstmt.setString(10, student.getStatus());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET id_number=?, first_name=?, last_name=?, "
                + "course_id=?, council_id=?, school_year=?, year_level=?, contact_number=?, "
                + "email=?, status=? WHERE student_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getIdNumber());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setInt(4, student.getCourseId());
            pstmt.setInt(5, student.getCouncilId());
            pstmt.setString(6, student.getSchoolYear());
            pstmt.setString(7, student.getYearLevel());
            pstmt.setString(8, student.getContactNumber());
            pstmt.setString(9, student.getEmail());
            pstmt.setString(10, student.getStatus());
            pstmt.setInt(11, student.getStudentId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public void deleteStudent(int studentId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First check if student has any borrowed books
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM borrowings WHERE student_id = ? AND status = 'Borrowed'")) {
                checkStmt.setInt(1, studentId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete student: Student has borrowed books that haven't been returned");
                }
            }

            // Mark student as inactive instead of deleting
            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE students SET active = FALSE, status = 'Inactive' WHERE student_id = ?")) {
                updateStmt.setInt(1, studentId);
                int updated = updateStmt.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Student not found");
                }
            }

            // Update related borrowing records
            try (PreparedStatement updateBorrowingsStmt = conn.prepareStatement(
                    "UPDATE borrowings SET status = 'Cancelled' WHERE student_id = ? AND status != 'Borrowed'")) {
                updateBorrowingsStmt.setInt(1, studentId);
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

    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT s.*, c.course_name, co.council_name "
                + "FROM students s "
                + "JOIN courses c ON s.course_id = c.course_id "
                + "JOIN councils co ON s.council_id = co.council_id "
                + "WHERE s.student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, c.course_name, co.council_name "
                + "FROM students s "
                + "JOIN courses c ON s.course_id = c.course_id "
                + "JOIN councils co ON s.council_id = co.council_id "
                + "WHERE s.active = TRUE "
                + // Only get active students
                "ORDER BY s.last_name, s.first_name";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }

    public List<Student> searchStudents(String searchTerm) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, c.course_name, co.council_name "
                + "FROM students s "
                + "JOIN courses c ON s.course_id = c.course_id "
                + "JOIN councils co ON s.council_id = co.council_id "
                + "WHERE s.id_number LIKE ? OR "
                + "CONCAT(s.first_name, ' ', s.last_name) LIKE ? OR "
                + "s.email LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }

    public Student getStudentByIdNumber(String idNumber) throws SQLException {
        String sql = "SELECT s.*, c.course_name, co.council_name "
                + "FROM students s "
                + "JOIN courses c ON s.course_id = c.course_id "
                + "JOIN councils co ON s.council_id = co.council_id "
                + "WHERE s.id_number = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setIdNumber(rs.getString("id_number"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setCourseId(rs.getInt("course_id"));
        student.setCouncilId(rs.getInt("council_id"));
        student.setSchoolYear(rs.getString("school_year"));
        student.setYearLevel(rs.getString("year_level"));
        student.setContactNumber(rs.getString("contact_number"));
        student.setEmail(rs.getString("email"));
        student.setRegistrationDate(rs.getTimestamp("registration_date"));
        student.setStatus(rs.getString("status"));
        student.setCourseName(rs.getString("course_name"));
        student.setCouncilName(rs.getString("council_name"));
        return student;
    }
}

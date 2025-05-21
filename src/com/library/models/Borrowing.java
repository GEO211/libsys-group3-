package com.library.models;

import java.util.Date;

public class Borrowing {
    private int borrowingId;
    private int studentId;
    private int bookId;
    private int adminId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private String status;
    private double fineAmount;
    
    // Additional fields for display
    private String studentName;
    private String studentIdNumber;
    private String bookTitle;
    private String bookIsbn;
    private String adminName;
    private String courseName;
    private String yearLevel;
    private String schoolYear;
    private String councilName;
    
    // Constructors
    public Borrowing() {}
    
    // Getters and Setters
    public int getBorrowingId() { return borrowingId; }
    public void setBorrowingId(int borrowingId) { this.borrowingId = borrowingId; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    
    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }
    
    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }
    
    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String schoolYear) { this.schoolYear = schoolYear; }
    
    public String getCouncilName() { return councilName; }
    public void setCouncilName(String councilName) { this.councilName = councilName; }
    
    // Utility methods
    public boolean isOverdue() {
        if (status.equals("Borrowed") && dueDate != null) {
            return new Date().after(dueDate);
        }
        return false;
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        
        long diff = new Date().getTime() - dueDate.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }
    
    public double calculateFine(double finePerDay) {
        if (isOverdue()) {
            return getDaysOverdue() * finePerDay;
        }
        return 0.0;
    }
} 
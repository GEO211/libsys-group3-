package com.library.models;

import java.sql.Timestamp;

public class BorrowingRecord {
    private String borrowId;
    private String bookId;
    private String bookTitle;
    private String borrowerId;
    private String borrowerName;
    private Timestamp borrowDate;
    private Timestamp dueDate;
    private Timestamp returnDate;
    private String status;
    private String courseName;
    private String yearLevel;
    private String councilName;

    // Constructor
    public BorrowingRecord(String borrowId, String bookId, String bookTitle, 
                          String borrowerId, String borrowerName, Timestamp borrowDate, 
                          Timestamp dueDate, Timestamp returnDate, String status) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowerId = borrowerId;
        this.borrowerName = borrowerName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters
    public String getBorrowId() { return borrowId; }
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getBorrowerId() { return borrowerId; }
    public String getBorrowerName() { return borrowerName; }
    public Timestamp getBorrowDate() { return borrowDate; }
    public Timestamp getDueDate() { return dueDate; }
    public Timestamp getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
    public String getCourseName() { return courseName; }
    public String getYearLevel() { return yearLevel; }
    public String getCouncilName() { return councilName; }

    // Setters
    public void setBorrowId(String borrowId) { this.borrowId = borrowId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public void setBorrowerId(String borrowerId) { this.borrowerId = borrowerId; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }
    public void setBorrowDate(Timestamp borrowDate) { this.borrowDate = borrowDate; }
    public void setDueDate(Timestamp dueDate) { this.dueDate = dueDate; }
    public void setReturnDate(Timestamp returnDate) { this.returnDate = returnDate; }
    public void setStatus(String status) { this.status = status; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }
    public void setCouncilName(String councilName) { this.councilName = councilName; }
} 
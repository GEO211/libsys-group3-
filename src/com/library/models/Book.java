package com.library.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book {
    private int bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private String category;
    private int quantity;
    private int availableQuantity;
    private String location;
    private Date addedDate;
    private String status;
    private boolean active = true;
    private double price;
    
    // Constructors
    public Book() {}
    
    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Date getAddedDate() { return addedDate; }
    public void setAddedDate(Date addedDate) { this.addedDate = addedDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    // Utility methods
    public boolean isAvailable() {
        return "Available".equals(status) && availableQuantity > 0;
    }
    
    public int getBorrowedCount() {
        return quantity - availableQuantity;
    }
    
    @Override
    public String toString() {
        return title + " by " + author;
    }

    // Add validation method
    public boolean isValid() {
        return isbn != null && !isbn.trim().isEmpty() &&
               isbn.matches("\\d+") &&
               title != null && !title.trim().isEmpty() &&
               author != null && !author.trim().isEmpty() &&
               category != null && !category.trim().isEmpty() &&
               location != null && !location.trim().isEmpty() &&
               quantity > 0;
    }

    // Add validation method that returns specific errors
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        
        if (isbn == null || isbn.trim().isEmpty()) {
            errors.add("ISBN is required");
        } else if (!isbn.matches("\\d+")) {
            errors.add("ISBN must contain only numbers");
        }
        if (title == null || title.trim().isEmpty()) {
            errors.add("Title is required");
        }
        if (author == null || author.trim().isEmpty()) {
            errors.add("Author is required");
        }
        if (category == null || category.trim().isEmpty()) {
            errors.add("Category is required");
        }
        if (location == null || location.trim().isEmpty()) {
            errors.add("Location is required");
        }
        if (quantity <= 0) {
            errors.add("Quantity must be greater than 0");
        }
        
        return errors;
    }
} 
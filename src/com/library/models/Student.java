package com.library.models;

import java.util.Date;

public class Student {
    private int studentId;
    private String idNumber;
    private String firstName;
    private String lastName;
    private int courseId;
    private int councilId;
    private String schoolYear;
    private String yearLevel;
    private String contactNumber;
    private String email;
    private Date registrationDate;
    private String status;
    private String courseName;
    private String councilName;
    private boolean active;
    
    // Constructors
    public Student() {}
    
    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    
    public int getCouncilId() { return councilId; }
    public void setCouncilId(int councilId) { this.councilId = councilId; }
    
    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String schoolYear) { this.schoolYear = schoolYear; }
    
    public String getYearLevel() {
        return yearLevel;
    }
    
    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }
    
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getCouncilName() { return councilName; }
    public void setCouncilName(String councilName) { this.councilName = councilName; }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 
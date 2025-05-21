public class Course {
    private int courseId;
    private int councilId;
    private String courseName;
    private String courseCode;
    private boolean active = true;
    
    // Existing getters and setters...
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
} 
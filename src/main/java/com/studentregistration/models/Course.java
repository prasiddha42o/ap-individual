package com.studentregistration.models;

public class Course {
    private String courseCode;
    private String courseName;
    private String instructor;
    private int credits;
    private String schedule;
    
    public Course() {}
    
    public Course(String courseCode, String courseName, String instructor, int credits, String schedule) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructor = instructor;
        this.credits = credits;
        this.schedule = schedule;
    }
    
    // Getters and Setters
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    @Override
    public String toString() {
        return courseCode + "," + courseName + "," + instructor + "," + credits + "," + schedule;
    }
    
    public static Course fromString(String data) {
        String[] parts = data.split(",");
        if (parts.length == 5) {
            return new Course(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
        }
        return null;
    }
}

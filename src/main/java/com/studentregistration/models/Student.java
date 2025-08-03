package com.studentregistration.models;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentId;
    private String name;
    private String email;
    private String program;
    private String semester;
    private String password;
    private List<String> registeredCourses;
    
    public Student() {
        this.registeredCourses = new ArrayList<>();
    }
    
    public Student(String studentId, String name, String email, String program, String semester, String password) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.program = program;
        this.semester = semester;
        this.password = password;
        this.registeredCourses = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public List<String> getRegisteredCourses() { return registeredCourses; }
    public void setRegisteredCourses(List<String> registeredCourses) { this.registeredCourses = registeredCourses; }
    
    public void addCourse(String courseCode) {
        if (!registeredCourses.contains(courseCode)) {
            registeredCourses.add(courseCode);
        }
    }
    
    public void removeCourse(String courseCode) {
        registeredCourses.remove(courseCode);
    }
    
    @Override
    public String toString() {
        return studentId + "," + name + "," + email + "," + program + "," + semester + "," + password + "," + String.join(";", registeredCourses);
    }
    
    public static Student fromString(String data) {
        String[] parts = data.split(",");
        if (parts.length >= 6) {
            Student student = new Student(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
            if (parts.length > 6 && !parts[6].isEmpty()) {
                String[] courses = parts[6].split(";");
                for (String course : courses) {
                    if (!course.trim().isEmpty()) {
                        student.addCourse(course.trim());
                    }
                }
            }
            return student;
        }
        return null;
    }
}

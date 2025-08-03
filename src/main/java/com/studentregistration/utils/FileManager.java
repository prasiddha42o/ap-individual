package com.studentregistration.utils;

import com.studentregistration.models.Student;
import com.studentregistration.models.Course;
import javafx.scene.control.Alert;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.txt";
    private static final String COURSES_FILE = DATA_DIR + "/courses.txt";
    private static final String REGISTRATIONS_FILE = DATA_DIR + "/registrations.txt";
    private static final String LOGS_FILE = DATA_DIR + "/system_logs.txt";
    private static final String ANALYTICS_FILE = DATA_DIR + "/analytics_data.txt";
    
    public static void initializeDataFiles() {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                logActivity("System", "Created data directory");
            }
            
            // Initialize all data files
            initializeStudentsFile();
            initializeCoursesFile();
            initializeRegistrationsFile();
            initializeAnalyticsFile();
            
            showAlert(Alert.AlertType.INFORMATION, "System Ready", 
                     "Data files initialized successfully.\n\n" +
                     "Students must register to create accounts.\n" +
                     "Admin login: admin / admin");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", 
                     "Failed to initialize data files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void initializeStudentsFile() throws IOException {
        File studentsFile = new File(STUDENTS_FILE);
        if (!studentsFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(studentsFile))) {
                writer.println("# Student Data Format: ID,Name,Email,Program,Semester,Password,RegisteredCourses");
                writer.println("# Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("# Students must register through the application to create accounts");
                writer.println("");
                writer.println("# Admin account for system management");
                writer.println("admin,System Administrator,admin@university.edu,Administration,N/A,admin,");
                writer.println("");
                writer.println("# Student accounts will be added here when they register");
            }
            logActivity("System", "Initialized students.txt - empty for new registrations");
        }
    }
    
    private static void initializeCoursesFile() throws IOException {
        File coursesFile = new File(COURSES_FILE);
        if (!coursesFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(coursesFile))) {
                writer.println("# Course Data Format: Code,Name,Instructor,Credits,Schedule");
                writer.println("# Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("");
                writer.println("# Computer Science Courses");
                writer.println("CS101,Introduction to Programming,Dr. Johnson,3,MWF 9:00-10:00");
                writer.println("CS201,Data Structures and Algorithms,Dr. Williams,4,TTh 11:00-12:30");
                writer.println("CS301,Database Management Systems,Dr. Brown,3,MWF 2:00-3:00");
                writer.println("CS401,Software Engineering,Dr. Davis,4,TTh 3:30-5:00");
                writer.println("CS501,Machine Learning,Dr. Garcia,3,MWF 1:00-2:00");
                writer.println("");
                writer.println("# Information Technology Courses");
                writer.println("IT101,Web Development Fundamentals,Prof. Wilson,3,MWF 10:00-11:00");
                writer.println("IT201,Network Security,Prof. Miller,3,TTh 1:00-2:30");
                writer.println("IT301,Cloud Computing,Prof. Anderson,4,MWF 3:00-4:00");
                writer.println("");
                writer.println("# Mathematics Courses");
                writer.println("MATH201,Discrete Mathematics,Dr. Taylor,4,MWF 11:00-12:00");
                writer.println("MATH301,Statistics for Computer Science,Dr. Lee,3,TTh 9:30-11:00");
                writer.println("");
                writer.println("# General Education Courses");
                writer.println("ENG101,Technical Writing,Prof. Thompson,2,TTh 9:00-10:00");
                writer.println("ENG201,Communication Skills,Prof. Martinez,2,MWF 8:00-9:00");
            }
            logActivity("System", "Initialized courses.txt with course catalog");
        }
    }
    
    private static void initializeRegistrationsFile() throws IOException {
        File registrationsFile = new File(REGISTRATIONS_FILE);
        if (!registrationsFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(registrationsFile))) {
                writer.println("# Registration Log Format: Timestamp,StudentID,Action,CourseCode,Details");
                writer.println("# Actions: REGISTER, DROP, LOGIN, PROFILE_UPDATE, STUDENT_REGISTERED");
                writer.println("# Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("");
            }
            logActivity("System", "Initialized registrations.txt for tracking");
        }
    }
    
    private static void initializeAnalyticsFile() throws IOException {
        File analyticsFile = new File(ANALYTICS_FILE);
        if (!analyticsFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(analyticsFile))) {
                writer.println("# Analytics Data File");
                writer.println("# This file stores computed analytics data for faster retrieval");
                writer.println("# Format: Timestamp,DataType,Key,Value");
                writer.println("# Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("");
            }
            logActivity("System", "Initialized analytics.txt for data storage");
        }
    }
    
    public static void logActivity(String studentId, String activity) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOGS_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(timestamp + " | " + studentId + " | " + activity);
        } catch (IOException e) {
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }
    
    public static void logRegistration(String studentId, String action, String courseCode, String details) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REGISTRATIONS_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(timestamp + "," + studentId + "," + action + "," + courseCode + "," + details);
        } catch (IOException e) {
            System.err.println("Failed to log registration: " + e.getMessage());
        }
    }
    
    public static void saveAnalyticsData(String dataType, String key, String value) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ANALYTICS_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(timestamp + "," + dataType + "," + key + "," + value);
        } catch (IOException e) {
            System.err.println("Failed to save analytics data: " + e.getMessage());
        }
    }
    
    public static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    Student student = Student.fromString(line);
                    if (student != null) {
                        students.add(student);
                    }
                }
            }
            logActivity("System", "Loaded " + students.size() + " students from file");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", 
                     "Failed to load students data: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }
    
    public static void saveStudents(List<Student> students) {
        try {
            // Create backup
            if (Files.exists(Paths.get(STUDENTS_FILE))) {
                Files.copy(Paths.get(STUDENTS_FILE), Paths.get(STUDENTS_FILE + ".backup"));
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
                writer.println("# Student Data Format: ID,Name,Email,Program,Semester,Password,RegisteredCourses");
                writer.println("# Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("# Students registered through the application");
                writer.println("");
                
                // Write admin first
                Student admin = students.stream()
                        .filter(s -> s.getStudentId().equals("admin"))
                        .findFirst()
                        .orElse(null);
                
                if (admin != null) {
                    writer.println("# Admin account");
                    writer.println(admin.toString());
                    writer.println("");
                }
                
                // Write regular students
                writer.println("# Registered Students");
                students.stream()
                        .filter(s -> !s.getStudentId().equals("admin"))
                        .forEach(student -> writer.println(student.toString()));
            }
            logActivity("System", "Saved " + students.size() + " students to file");
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Save Error", 
                     "Failed to save students data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    Course course = Course.fromString(line);
                    if (course != null) {
                        courses.add(course);
                    }
                }
            }
            logActivity("System", "Loaded " + courses.size() + " courses from file");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", 
                     "Failed to load courses data: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }
    
    public static void saveCourses(List<Course> courses) {
        try {
            // Create backup
            if (Files.exists(Paths.get(COURSES_FILE))) {
                Files.copy(Paths.get(COURSES_FILE), Paths.get(COURSES_FILE + ".backup"));
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_FILE))) {
                writer.println("# Course Data Format: Code,Name,Instructor,Credits,Schedule");
                writer.println("# Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                for (Course course : courses) {
                    writer.println(course.toString());
                }
            }
            logActivity("System", "Saved " + courses.size() + " courses to file");
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Save Error", 
                     "Failed to save courses data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Student findStudent(String studentId) {
        List<Student> students = loadStudents();
        Student found = students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
        
        if (found != null) {
            logActivity(studentId, "Student record accessed");
        }
        return found;
    }
    
    public static void updateStudent(Student updatedStudent) {
        List<Student> students = loadStudents();
        boolean updated = false;
        
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(updatedStudent.getStudentId())) {
                students.set(i, updatedStudent);
                updated = true;
                break;
            }
        }
        
        if (updated) {
            saveStudents(students);
            logActivity(updatedStudent.getStudentId(), "Student record updated");
            showAlert(Alert.AlertType.INFORMATION, "Update Successful", 
                     "Student information has been updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", 
                     "Failed to find student record for update.");
        }
    }
    
    public static int getTotalStudents() {
        return (int) loadStudents().stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .count();
    }
    
    public static int getTotalCourses() {
        return loadCourses().size();
    }
    
    public static int getTotalRegistrations() {
        return loadStudents().stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .mapToInt(s -> s.getRegisteredCourses().size())
                .sum();
    }
    
    public static List<String> getRecentActivity() {
        List<String> activities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            List<String> allLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                allLines.add(line);
            }
            
            // Get last 10 activities
            int start = Math.max(0, allLines.size() - 10);
            for (int i = start; i < allLines.size(); i++) {
                activities.add(allLines.get(i));
            }
        } catch (IOException e) {
            activities.add("No recent activity available");
        }
        return activities;
    }
    
    public static List<String> getRegistrationHistory() {
        List<String> registrations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(REGISTRATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    registrations.add(line);
                }
            }
        } catch (IOException e) {
            registrations.add("No registration history available");
        }
        return registrations;
    }
    
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

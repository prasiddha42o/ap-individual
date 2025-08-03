package com.studentregistration.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import com.studentregistration.models.Student;
import com.studentregistration.models.Course;
import com.studentregistration.utils.FileManager;
import com.studentregistration.utils.SessionManager;
import com.studentregistration.Main;

import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label studentInfoLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label myCoursesLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private Label systemStatsLabel;
    
    // Navigation Buttons
    @FXML private Button profileButton;
    @FXML private Button registerCourseButton;
    @FXML private Button viewCoursesButton;
    @FXML private Button viewGraphsButton;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    
    private Timeline clockTimeline;
    
    @FXML
    private void initialize() {
        Student currentStudent = SessionManager.getCurrentStudent();
        if (currentStudent != null) {
            setupUserInterface(currentStudent);
            loadDashboardData();
            startClock();
            
            FileManager.logActivity(currentStudent.getStudentId(), "Accessed dashboard");
            
            showAlert(Alert.AlertType.INFORMATION, "Welcome", 
                     "Welcome back, " + currentStudent.getName() + "!");
        }
    }
    
    private void setupUserInterface(Student student) {
        welcomeLabel.setText("Welcome, " + student.getName());
        studentInfoLabel.setText(String.format("ID: %s | %s | %s", 
                                              student.getStudentId(), 
                                              student.getProgram(), 
                                              student.getSemester()));
        
        // Calculate total credits
        List<Course> allCourses = FileManager.loadCourses();
        int totalCredits = student.getRegisteredCourses().stream()
                .mapToInt(courseCode -> allCourses.stream()
                        .filter(course -> course.getCourseCode().equals(courseCode))
                        .mapToInt(Course::getCredits)
                        .findFirst()
                        .orElse(0))
                .sum();
        
        myCoursesLabel.setText(student.getRegisteredCourses().size() + " Courses");
        totalCreditsLabel.setText(totalCredits + " Credits");
    }
    
    private void loadDashboardData() {
        int totalStudents = FileManager.getTotalStudents();
        int totalCourses = FileManager.getTotalCourses();
        int totalRegistrations = FileManager.getTotalRegistrations();
        
        systemStatsLabel.setText(String.format("System: %d Students | %d Courses | %d Registrations", 
                                              totalStudents, totalCourses, totalRegistrations));
    }
    
    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
        updateClock();
    }
    
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm:ss"));
        currentTimeLabel.setText(timeString);
    }
    
    @FXML
    private void handleProfile() {
        Student currentStudent = SessionManager.getCurrentStudent();
        FileManager.logActivity(currentStudent.getStudentId(), "Navigated to Profile");
        loadScene("/fxml/Profile.fxml", "Student Profile", 700, 500);
    }
    
    @FXML
    private void handleRegisterCourse() {
        Student currentStudent = SessionManager.getCurrentStudent();
        
        if (currentStudent.getRegisteredCourses().size() >= 8) {
            showAlert(Alert.AlertType.WARNING, "Registration Limit", 
                     "Maximum 8 courses allowed. Please drop a course first.");
            return;
        }
        
        FileManager.logActivity(currentStudent.getStudentId(), "Navigated to Course Registration");
        loadScene("/fxml/RegisterCourse.fxml", "Register Courses", 900, 600);
    }
    
    @FXML
    private void handleViewCourses() {
        Student currentStudent = SessionManager.getCurrentStudent();
        
        if (currentStudent.getRegisteredCourses().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Courses", 
                     "You haven't registered for any courses yet.");
            return;
        }
        
        FileManager.logActivity(currentStudent.getStudentId(), "Navigated to View Courses");
        loadScene("/fxml/ViewCourses.fxml", "My Courses", 900, 600);
    }
    
    @FXML
    private void handleViewGraphs() {
        Student currentStudent = SessionManager.getCurrentStudent();
        FileManager.logActivity(currentStudent.getStudentId(), "Navigated to Analytics");
        loadScene("/fxml/Analytics.fxml", "Analytics and Reports", 1000, 700);
    }
    
    @FXML
    private void handleRefresh() {
        loadDashboardData();
        Student currentStudent = SessionManager.getCurrentStudent();
        setupUserInterface(currentStudent);
        FileManager.logActivity(currentStudent.getStudentId(), "Refreshed dashboard");
        showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Dashboard updated successfully!");
    }
    
    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Logout");
        confirmAlert.setHeaderText("Confirm Logout");
        confirmAlert.setContentText("Are you sure you want to logout?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            Student currentStudent = SessionManager.getCurrentStudent();
            FileManager.logActivity(currentStudent.getStudentId(), "Logged out");
            
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
            SessionManager.clearSession();
            loadScene("/fxml/Login.fxml", "Student Course Registration System", 800, 600);
        }
    }
    
    private void loadScene(String fxmlPath, String title, int width, int height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = Main.getPrimaryStage();
            stage.setScene(scene);
            stage.setTitle(title + " - Student Registration System");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load " + title);
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

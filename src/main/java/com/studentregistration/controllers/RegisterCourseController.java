package com.studentregistration.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.studentregistration.models.Student;
import com.studentregistration.models.Course;
import com.studentregistration.utils.FileManager;
import com.studentregistration.utils.SessionManager;
import com.studentregistration.Main;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterCourseController {
    
    @FXML private TableView<Course> availableCoursesTable;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;
    
    @FXML
    private void initialize() {
        // Initialize table columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        scheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        
        loadAvailableCourses();
        
        // Enable register button only when a course is selected
        registerButton.setDisable(true);
        availableCoursesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> registerButton.setDisable(newSelection == null)
        );
    }
    
    private void loadAvailableCourses() {
        List<Course> allCourses = FileManager.loadCourses();
        Student currentStudent = SessionManager.getCurrentStudent();
        
        // Filter out already registered courses
        List<Course> availableCourses = allCourses.stream()
                .filter(course -> !currentStudent.getRegisteredCourses().contains(course.getCourseCode()))
                .collect(Collectors.toList());
        
        availableCoursesTable.getItems().clear();
        availableCoursesTable.getItems().addAll(availableCourses);
        
        statusLabel.setText("Available Courses: " + availableCourses.size() + 
                           " | Already Registered: " + currentStudent.getRegisteredCourses().size());
    }
    
    @FXML
    private void handleRegister() {
        Course selectedCourse = availableCoursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "No Course Selected", 
                 "Please select a course from the table to register.\n\n" +
                 "💡 Tip: Click on a course row to select it, then click Register.");
        return;
    }
    
    Student currentStudent = SessionManager.getCurrentStudent();
    
    // Check if student already has maximum courses
    if (currentStudent.getRegisteredCourses().size() >= 8) {
        showAlert(Alert.AlertType.WARNING, "Registration Limit Reached", 
                 "You have reached the maximum number of courses (8) for this semester.\n\n" +
                 "Current registered courses: " + currentStudent.getRegisteredCourses().size() + "/8\n\n" +
                 "Please drop a course before registering for a new one.");
        return;
    }
    
    // Check for time conflicts (basic check)
    List<Course> allCourses = FileManager.loadCourses();
    List<Course> registeredCourses = allCourses.stream()
            .filter(course -> currentStudent.getRegisteredCourses().contains(course.getCourseCode()))
            .collect(Collectors.toList());
    
    boolean hasConflict = registeredCourses.stream()
            .anyMatch(course -> course.getSchedule().equals(selectedCourse.getSchedule()));
    
    if (hasConflict) {
        showAlert(Alert.AlertType.WARNING, "Schedule Conflict", 
                 "The selected course conflicts with your current schedule.\n\n" +
                 "Course: " + selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName() + "\n" +
                 "Schedule: " + selectedCourse.getSchedule() + "\n\n" +
                 "Please choose a different course or drop the conflicting course first.");
        return;
    }
    
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirm Course Registration");
    confirmAlert.setHeaderText("Register for Course");
    confirmAlert.setContentText(String.format(
        "Course Details:\n" +
        "📚 Code: %s\n" +
        "📖 Name: %s\n" +
        "👨‍🏫 Instructor: %s\n" +
        "🎓 Credits: %d\n" +
        "🕐 Schedule: %s\n\n" +
        "Are you sure you want to register for this course?",
        selectedCourse.getCourseCode(),
        selectedCourse.getCourseName(),
        selectedCourse.getInstructor(),
        selectedCourse.getCredits(),
        selectedCourse.getSchedule()
    ));
    
    ButtonType registerButton = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    confirmAlert.getButtonTypes().setAll(registerButton, cancelButton);
    
    if (confirmAlert.showAndWait().get() == registerButton) {
        // Register for the course
        currentStudent.addCourse(selectedCourse.getCourseCode());
        FileManager.updateStudent(currentStudent);
        
        // Log the registration
        FileManager.logRegistration(currentStudent.getStudentId(), "REGISTER", 
                                   selectedCourse.getCourseCode(), 
                                   selectedCourse.getCourseName());
        
        // Refresh the table
        loadAvailableCourses();
        
        showAlert(Alert.AlertType.INFORMATION, "Registration Successful! 🎉", 
                 String.format("Successfully registered for:\n\n" +
                              "📚 %s - %s\n" +
                              "👨‍🏫 Instructor: %s\n" +
                              "🎓 Credits: %d\n\n" +
                              "Total registered courses: %d/8\n" +
                              "You can view all your courses from the dashboard.",
                              selectedCourse.getCourseCode(),
                              selectedCourse.getCourseName(),
                              selectedCourse.getInstructor(),
                              selectedCourse.getCredits(),
                              currentStudent.getRegisteredCourses().size()));
    }
}
    
    @FXML
    private void handleBack() {
        loadDashboard();
    }
    
    private void loadDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = Main.getPrimaryStage();
            stage.setScene(scene);
            stage.setTitle("Dashboard - Student Course Registration System");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.");
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

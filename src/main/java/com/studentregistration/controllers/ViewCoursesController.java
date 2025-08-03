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

public class ViewCoursesController {
    
    @FXML private TableView<Course> registeredCoursesTable;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private Button dropButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;
    @FXML private Label totalCreditsLabel;
    
    @FXML
    private void initialize() {
        // Initialize table columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        scheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        
        loadRegisteredCourses();
        
        // Enable drop button only when a course is selected
        dropButton.setDisable(true);
        registeredCoursesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> dropButton.setDisable(newSelection == null)
        );
    }
    
    private void loadRegisteredCourses() {
        Student currentStudent = SessionManager.getCurrentStudent();
        List<Course> allCourses = FileManager.loadCourses();
        
        // Get registered courses
        List<Course> registeredCourses = allCourses.stream()
                .filter(course -> currentStudent.getRegisteredCourses().contains(course.getCourseCode()))
                .collect(Collectors.toList());
        
        registeredCoursesTable.getItems().clear();
        registeredCoursesTable.getItems().addAll(registeredCourses);
        
        // Calculate total credits
        int totalCredits = registeredCourses.stream()
                .mapToInt(Course::getCredits)
                .sum();
        
        statusLabel.setText("Registered Courses: " + registeredCourses.size());
        totalCreditsLabel.setText("Total Credits: " + totalCredits);
    }
    
    @FXML
    private void handleDrop() {
        Course selectedCourse = registeredCoursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a course to drop.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Drop");
        confirmAlert.setHeaderText("Drop Course");
        confirmAlert.setContentText("Are you sure you want to drop:\n" + 
                                   selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName() + 
                                   "\n\nThis action cannot be undone.");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            Student currentStudent = SessionManager.getCurrentStudent();
            
            // Drop the course
            currentStudent.removeCourse(selectedCourse.getCourseCode());
            FileManager.updateStudent(currentStudent);
            
            // Refresh the table
            loadRegisteredCourses();
            
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                     "Successfully dropped " + selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName());
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

package com.studentregistration.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.studentregistration.models.Student;
import com.studentregistration.utils.FileManager;
import com.studentregistration.utils.SessionManager;
import com.studentregistration.Main;

import java.io.IOException;
import java.util.List;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label statusLabel;
    
    @FXML
    private void initialize() {
        loginButton.setDefaultButton(true);
        
        // Check if there are any registered students
        List<Student> students = FileManager.loadStudents();
        long studentCount = students.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .count();
        
        if (studentCount == 0) {
            statusLabel.setText("No students registered yet. Click Register to create your account.");
            statusLabel.setStyle("-fx-text-fill: #f39c12;");
        }
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter both username and password.\n\n" +
                     "If you don't have an account, click Register to create one.\n" +
                     "Admin login: admin / admin");
            return;
        }
        
        // Show loading message
        statusLabel.setText("Authenticating...");
        statusLabel.setStyle("-fx-text-fill: #3498db;");
        
        // Authenticate user
        Student student = authenticateUser(username, password);
        if (student != null) {
            SessionManager.setCurrentStudent(student);
            FileManager.logActivity(username, "Successful login");
            
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", 
                     "Welcome, " + student.getName() + "!\n" +
                     "Redirecting to dashboard...");
            
            loadDashboard();
        } else {
            FileManager.logActivity(username, "Failed login attempt");
            
            // Check if any students are registered
            List<Student> students = FileManager.loadStudents();
            long studentCount = students.stream()
                    .filter(s -> !s.getStudentId().equals("admin"))
                    .count();
            
            String errorMessage = "Invalid username or password.\n\n";
            if (studentCount == 0) {
                errorMessage += "No students are registered yet.\n" +
                              "Click Register to create your account.\n\n" +
                              "Admin login: admin / admin";
            } else {
                errorMessage += "Please check your credentials and try again.\n" +
                              "If you don't have an account, click Register.\n\n" +
                              "Admin login: admin / admin";
            }
            
            showAlert(Alert.AlertType.ERROR, "Login Failed", errorMessage);
            passwordField.clear();
            statusLabel.setText("Login failed. Register if you don't have an account.");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Register.fxml"));
            Scene scene = new Scene(root, 700, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = Main.getPrimaryStage();
            stage.setScene(scene);
            stage.setTitle("Register New Student - Student Registration System");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load registration page.");
        }
    }
    
    private Student authenticateUser(String username, String password) {
        List<Student> students = FileManager.loadStudents();
        return students.stream()
                .filter(s -> s.getStudentId().equals(username) && s.getPassword().equals(password))
                .findFirst()
                .orElse(null);
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
    
    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Any unsaved changes will be lost.");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            System.exit(0);
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

package com.studentregistration.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.studentregistration.models.Student;
import com.studentregistration.utils.FileManager;
import com.studentregistration.Main;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class RegisterController {
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> programComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;
    @FXML private Label studentIdLabel;
    @FXML private Label registrationCountLabel;
    
    private String generatedStudentId;
    
    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        programComboBox.getItems().addAll(
            "Computer Science", "Information Technology", "Software Engineering",
            "Data Science", "Cybersecurity", "Computer Engineering", "Business Administration",
            "Mathematics", "Physics", "Chemistry", "Biology"
        );
        
        semesterComboBox.getItems().addAll(
            "Fall 2024", "Spring 2025", "Summer 2024", "Fall 2023", "Spring 2024"
        );
        
        // Generate and display student ID
        generateStudentId();
        
        // Show current registration count
        updateRegistrationCount();
        
        registerButton.setDefaultButton(true);
    }
    
    private void generateStudentId() {
        List<Student> existingStudents = FileManager.loadStudents();
        Random random = new Random();
        String studentId;
        
        // Generate unique student ID
        do {
            int idNumber = random.nextInt(9000) + 1000; // Generate 4-digit number (1000-9999)
            studentId = "STU" + idNumber;
        } while (studentIdExists(existingStudents, studentId));
        
        generatedStudentId = studentId;
        studentIdLabel.setText("Your Student ID will be: " + generatedStudentId);
    }
    
    private void updateRegistrationCount() {
        List<Student> students = FileManager.loadStudents();
        long studentCount = students.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .count();
        
        if (studentCount == 0) {
            registrationCountLabel.setText("You will be the first student to register!");
            registrationCountLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            registrationCountLabel.setText("Current registered students: " + studentCount);
            registrationCountLabel.setStyle("-fx-text-fill: #3498db;");
        }
    }
    
    private boolean studentIdExists(List<Student> students, String studentId) {
        return students.stream().anyMatch(s -> s.getStudentId().equals(studentId));
    }
    
    @FXML
    private void handleRegister() {
        if (validateInput()) {
            // Create new student
            Student newStudent = new Student(
                generatedStudentId,
                nameField.getText().trim(),
                emailField.getText().trim(),
                programComboBox.getValue(),
                semesterComboBox.getValue(),
                passwordField.getText()
            );
            
            // Add to existing students list
            List<Student> students = FileManager.loadStudents();
            students.add(newStudent);
            
            // Save to file
            FileManager.saveStudents(students);
            
            // Log the registration
            FileManager.logActivity(generatedStudentId, "New student registered");
            FileManager.logRegistration(generatedStudentId, "STUDENT_REGISTERED", "N/A", 
                    "New student account created: " + newStudent.getName());
            
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", 
                     "Welcome to the Student Course Registration System!\n\n" +
                     "Your account has been created successfully.\n\n" +
                     "Account Details:\n" +
                     "Student ID: " + generatedStudentId + "\n" +
                     "Name: " + newStudent.getName() + "\n" +
                     "Email: " + newStudent.getEmail() + "\n" +
                     "Program: " + newStudent.getProgram() + "\n" +
                     "Semester: " + newStudent.getSemester() + "\n\n" +
                     "You can now login with your Student ID and password.\n" +
                     "Your account has been saved to the system.");
            
            // Return to login page
            handleBackToLogin();
        }
    }
    
    private boolean validateInput() {
        // Check if all fields are filled
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Full name is required.");
            nameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Email address is required.");
            emailField.requestFocus();
            return false;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter a valid email address (e.g., john@example.com).");
            emailField.requestFocus();
            return false;
        }
        
        // Check if email already exists
        List<Student> existingStudents = FileManager.loadStudents();
        if (existingStudents.stream().anyMatch(s -> s.getEmail().equalsIgnoreCase(email))) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "This email address is already registered. Please use a different email.");
            emailField.requestFocus();
            return false;
        }
        
        if (programComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select your program.");
            programComboBox.requestFocus();
            return false;
        }
        
        if (semesterComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select your semester.");
            semesterComboBox.requestFocus();
            return false;
        }
        
        if (passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password is required.");
            passwordField.requestFocus();
            return false;
        }
        
        if (passwordField.getText().length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Password must be at least 6 characters long.");
            passwordField.requestFocus();
            return false;
        }
        
        if (confirmPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please confirm your password.");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Passwords do not match. Please check and try again.");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = Main.getPrimaryStage();
            stage.setScene(scene);
            stage.setTitle("Student Course Registration System");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login page.");
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

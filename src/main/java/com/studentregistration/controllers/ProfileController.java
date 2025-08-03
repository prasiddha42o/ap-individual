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

public class ProfileController {
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> programComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private Label studentIdLabel;
    @FXML private Button saveButton;
    @FXML private Button backButton;
    
    @FXML
    private void initialize() {
        // Initialize ComboBoxes
        programComboBox.getItems().addAll(
            "Computer Science", "Information Technology", "Software Engineering",
            "Data Science", "Cybersecurity", "Computer Engineering"
        );
        
        semesterComboBox.getItems().addAll(
            "Fall 2024", "Spring 2024", "Summer 2024", "Fall 2023", "Spring 2023"
        );
        
        // Load current student data
        Student currentStudent = SessionManager.getCurrentStudent();
        if (currentStudent != null) {
            studentIdLabel.setText("Student ID: " + currentStudent.getStudentId());
            nameField.setText(currentStudent.getName());
            emailField.setText(currentStudent.getEmail());
            programComboBox.setValue(currentStudent.getProgram());
            semesterComboBox.setValue(currentStudent.getSemester());
        }
    }
    
    @FXML
    private void handleSave() {
        if (validateInput()) {
            Student currentStudent = SessionManager.getCurrentStudent();
            
            // Update student information
            currentStudent.setName(nameField.getText().trim());
            currentStudent.setEmail(emailField.getText().trim());
            currentStudent.setProgram(programComboBox.getValue());
            currentStudent.setSemester(semesterComboBox.getValue());
            
            // Save to file
            FileManager.updateStudent(currentStudent);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
        }
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name cannot be empty.");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Email cannot be empty.");
            return false;
        }
        
        if (!emailField.getText().contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return false;
        }
        
        if (programComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a program.");
            return false;
        }
        
        if (semesterComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a semester.");
            return false;
        }
        
        return true;
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

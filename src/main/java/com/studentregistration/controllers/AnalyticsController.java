package com.studentregistration.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import com.studentregistration.models.Student;
import com.studentregistration.models.Course;
import com.studentregistration.utils.FileManager;
import com.studentregistration.utils.SessionManager;
import com.studentregistration.Main;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsController {
    
    @FXML private PieChart studentDistributionPieChart;
    @FXML private BarChart<String, Number> studentDistributionBarChart;
    @FXML private PieChart programDistributionChart;
    @FXML private BarChart<String, Number> creditsDistributionChart;
    @FXML private ListView<String> analyticsDataList;
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private Label totalRegistrationsLabel;
    @FXML private Label averageCoursesLabel;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    
    @FXML
    private void initialize() {
        loadAllAnalytics();
        
        Student currentStudent = SessionManager.getCurrentStudent();
        if (currentStudent != null) {
            FileManager.logActivity(currentStudent.getStudentId(), "Accessed Analytics page");
            showAlert(Alert.AlertType.INFORMATION, "Analytics Loaded", 
                     "All charts and statistics have been loaded successfully!");
        }
    }
    
    private void loadAllAnalytics() {
        loadStatistics();
        loadStudentDistributionByCoursesCharts();
        loadProgramDistributionChart();
        loadCreditsDistributionChart();
        loadAnalyticsData();
        
        // Save analytics data to file
        saveAnalyticsToFile();
    }
    
    private void loadStatistics() {
        List<Student> allStudents = FileManager.loadStudents();
        List<Course> allCourses = FileManager.loadCourses();
        
        // Filter out admin
        List<Student> students = allStudents.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .collect(Collectors.toList());
        
        int totalStudents = students.size();
        int totalCourses = allCourses.size();
        int totalRegistrations = students.stream()
                .mapToInt(s -> s.getRegisteredCourses().size())
                .sum();
        
        double averageCourses = totalStudents > 0 ? (double) totalRegistrations / totalStudents : 0;
        
        totalStudentsLabel.setText(String.valueOf(totalStudents));
        totalCoursesLabel.setText(String.valueOf(totalCourses));
        totalRegistrationsLabel.setText(String.valueOf(totalRegistrations));
        averageCoursesLabel.setText(String.format("%.1f", averageCourses));
    }
    
    private void loadStudentDistributionByCoursesCharts() {
        List<Student> allStudents = FileManager.loadStudents();
        List<Course> allCourses = FileManager.loadCourses();
        
        // Filter out admin
        List<Student> students = allStudents.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .collect(Collectors.toList());
        
        // Count students per course
        Map<String, Long> courseStudentCount = students.stream()
                .flatMap(student -> student.getRegisteredCourses().stream())
                .collect(Collectors.groupingBy(courseCode -> courseCode, Collectors.counting()));
        
        // Pie Chart Data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Bar Chart Data
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Students per Course");
        
        for (Map.Entry<String, Long> entry : courseStudentCount.entrySet()) {
            String courseCode = entry.getKey();
            Long studentCount = entry.getValue();
            
            // Find course name
            String courseName = allCourses.stream()
                    .filter(course -> course.getCourseCode().equals(courseCode))
                    .map(Course::getCourseName)
                    .findFirst()
                    .orElse(courseCode);
            
            // Truncate long names for better display
            String displayName = courseName.length() > 15 ? 
                                courseName.substring(0, 12) + "..." : courseName;
            
            // Add to pie chart
            pieChartData.add(new PieChart.Data(courseCode + " (" + studentCount + ")", studentCount));
            
            // Add to bar chart
            barSeries.getData().add(new XYChart.Data<>(courseCode, studentCount));
        }
        
        studentDistributionPieChart.setData(pieChartData);
        studentDistributionPieChart.setTitle("Student Distribution by Courses (Pie Chart)");
        studentDistributionPieChart.setLegendVisible(true);
        
        studentDistributionBarChart.getData().clear();
        studentDistributionBarChart.getData().add(barSeries);
        studentDistributionBarChart.setTitle("Student Distribution by Courses (Bar Chart)");
        studentDistributionBarChart.setLegendVisible(false);
    }
    
    private void loadProgramDistributionChart() {
        List<Student> allStudents = FileManager.loadStudents();
        
        // Filter out admin and count by program
        Map<String, Long> programCount = allStudents.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .collect(Collectors.groupingBy(Student::getProgram, Collectors.counting()));
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Long> entry : programCount.entrySet()) {
            String program = entry.getKey();
            Long count = entry.getValue();
            pieChartData.add(new PieChart.Data(program + " (" + count + ")", count));
        }
        
        programDistributionChart.setData(pieChartData);
        programDistributionChart.setTitle("Students by Program");
        programDistributionChart.setLegendVisible(true);
    }
    
    private void loadCreditsDistributionChart() {
        List<Student> allStudents = FileManager.loadStudents();
        List<Course> allCourses = FileManager.loadCourses();
        
        XYChart.Series<String, Number> creditsSeries = new XYChart.Series<>();
        creditsSeries.setName("Total Credits per Student");
        
        // Calculate credits for each student
        allStudents.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .forEach(student -> {
                    int totalCredits = student.getRegisteredCourses().stream()
                            .mapToInt(courseCode -> allCourses.stream()
                                    .filter(course -> course.getCourseCode().equals(courseCode))
                                    .mapToInt(Course::getCredits)
                                    .findFirst()
                                    .orElse(0))
                            .sum();
                    
                    creditsSeries.getData().add(new XYChart.Data<>(student.getStudentId(), totalCredits));
                });
        
        creditsDistributionChart.getData().clear();
        creditsDistributionChart.getData().add(creditsSeries);
        creditsDistributionChart.setTitle("Credits Distribution by Student");
        creditsDistributionChart.setLegendVisible(false);
    }
    
    private void loadAnalyticsData() {
        List<Student> allStudents = FileManager.loadStudents();
        List<Course> allCourses = FileManager.loadCourses();
        
        ObservableList<String> analyticsItems = FXCollections.observableArrayList();
        
        // Filter out admin
        List<Student> students = allStudents.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .collect(Collectors.toList());
        
        analyticsItems.add("SYSTEM ANALYTICS SUMMARY");
        analyticsItems.add("===============================");
        analyticsItems.add("Total Students: " + students.size());
        analyticsItems.add("Total Courses: " + allCourses.size());
        analyticsItems.add("Total Registrations: " + students.stream().mapToInt(s -> s.getRegisteredCourses().size()).sum());
        analyticsItems.add("");
        
        // Most popular courses
        Map<String, Long> coursePopularity = students.stream()
                .flatMap(student -> student.getRegisteredCourses().stream())
                .collect(Collectors.groupingBy(courseCode -> courseCode, Collectors.counting()));
        
        analyticsItems.add("MOST POPULAR COURSES:");
        coursePopularity.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    String courseName = allCourses.stream()
                            .filter(c -> c.getCourseCode().equals(entry.getKey()))
                            .map(Course::getCourseName)
                            .findFirst()
                            .orElse(entry.getKey());
                    analyticsItems.add("  " + entry.getKey() + " - " + courseName + " (" + entry.getValue() + " students)");
                });
        
        analyticsItems.add("");
        
        // Program distribution
        Map<String, Long> programStats = students.stream()
                .collect(Collectors.groupingBy(Student::getProgram, Collectors.counting()));
        
        analyticsItems.add("PROGRAM DISTRIBUTION:");
        programStats.forEach((program, count) -> 
                analyticsItems.add("  " + program + ": " + count + " students"));
        
        analyticsItems.add("");
        analyticsItems.add("REGISTRATION TRENDS:");
        analyticsItems.add("  Average courses per student: " + 
                String.format("%.1f", students.stream().mapToInt(s -> s.getRegisteredCourses().size()).average().orElse(0)));
        
        // Find students with most/least courses
        Student maxCoursesStudent = students.stream()
                .max((s1, s2) -> Integer.compare(s1.getRegisteredCourses().size(), s2.getRegisteredCourses().size()))
                .orElse(null);
        
        if (maxCoursesStudent != null) {
            analyticsItems.add("  Most courses: " + maxCoursesStudent.getName() + 
                    " (" + maxCoursesStudent.getRegisteredCourses().size() + " courses)");
        }
        
        analyticsDataList.setItems(analyticsItems);
    }
    
    private void saveAnalyticsToFile() {
        List<Student> allStudents = FileManager.loadStudents();
        List<Course> allCourses = FileManager.loadCourses();
        
        // Filter out admin
        List<Student> students = allStudents.stream()
                .filter(s -> !s.getStudentId().equals("admin"))
                .collect(Collectors.toList());
        
        // Save key statistics
        FileManager.saveAnalyticsData("STATISTICS", "TOTAL_STUDENTS", String.valueOf(students.size()));
        FileManager.saveAnalyticsData("STATISTICS", "TOTAL_COURSES", String.valueOf(allCourses.size()));
        FileManager.saveAnalyticsData("STATISTICS", "TOTAL_REGISTRATIONS", 
                String.valueOf(students.stream().mapToInt(s -> s.getRegisteredCourses().size()).sum()));
        
        // Save course popularity data
        Map<String, Long> coursePopularity = students.stream()
                .flatMap(student -> student.getRegisteredCourses().stream())
                .collect(Collectors.groupingBy(courseCode -> courseCode, Collectors.counting()));
        
        coursePopularity.forEach((courseCode, count) -> 
                FileManager.saveAnalyticsData("COURSE_POPULARITY", courseCode, String.valueOf(count)));
        
        // Save program distribution
        Map<String, Long> programStats = students.stream()
                .collect(Collectors.groupingBy(Student::getProgram, Collectors.counting()));
        
        programStats.forEach((program, count) -> 
                FileManager.saveAnalyticsData("PROGRAM_DISTRIBUTION", program, String.valueOf(count)));
    }
    
    @FXML
    private void handleRefresh() {
        showAlert(Alert.AlertType.INFORMATION, "Refreshing Analytics", 
                 "Updating all charts and statistics...");
        
        loadAllAnalytics();
        
        Student currentStudent = SessionManager.getCurrentStudent();
        if (currentStudent != null) {
            FileManager.logActivity(currentStudent.getStudentId(), "Refreshed Analytics");
        }
        
        showAlert(Alert.AlertType.INFORMATION, "Refresh Complete", 
                 "All analytics data has been updated!");
    }
    
    @FXML
    private void handleBack() {
        loadDashboard();
    }
    
    private void loadDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = Main.getPrimaryStage();
            stage.setScene(scene);
            stage.setTitle("Dashboard - Student Registration System");
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

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

    @FXML private PieChart courseDistributionPieChart;
    @FXML private BarChart<String, Number> coursePopularityBarChart;
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private Label totalRegistrationsLabel;
    @FXML private Label averageCoursesLabel;
    @FXML private Button refreshButton;
    @FXML private Button backButton;

    @FXML
    private void initialize() {
        loadAnalytics();

        Student currentStudent = SessionManager.getCurrentStudent();
        if (currentStudent != null) {
            FileManager.logActivity(currentStudent.getStudentId(), "Accessed Analytics page");
            showAlert(Alert.AlertType.INFORMATION, "Analytics Loaded",
                    "Course analytics have been loaded successfully!");
        }
    }

    private void loadAnalytics() {
        loadStatistics();
        loadCourseDistributionPieChart();
        loadCoursePopularityBarChart();
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

    private void loadCourseDistributionPieChart() {
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

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

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
            String displayName = courseName.length() > 20 ?
                    courseName.substring(0, 17) + "..." : courseName;

            pieChartData.add(new PieChart.Data(courseCode + " - " + displayName + " (" + studentCount + ")", studentCount));
        }

        // Add message if no data
        if (pieChartData.isEmpty()) {
            pieChartData.add(new PieChart.Data("No course registrations yet", 1));
        }

        courseDistributionPieChart.setData(pieChartData);
        courseDistributionPieChart.setTitle("Course Registration Distribution");
        courseDistributionPieChart.setLegendVisible(true);
    }

    private void loadCoursePopularityBarChart() {
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

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Students Enrolled");

        // Sort by popularity (descending) and take top 10
        courseStudentCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    String courseCode = entry.getKey();
                    Long studentCount = entry.getValue();
                    barSeries.getData().add(new XYChart.Data<>(courseCode, studentCount));
                });

        // Add message if no data
        if (barSeries.getData().isEmpty()) {
            barSeries.getData().add(new XYChart.Data<>("No Data", 0));
        }

        coursePopularityBarChart.getData().clear();
        coursePopularityBarChart.getData().add(barSeries);
        coursePopularityBarChart.setTitle("Most Popular Courses");
        coursePopularityBarChart.setLegendVisible(false);
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
    }

    @FXML
    private void handleRefresh() {
        showAlert(Alert.AlertType.INFORMATION, "Refreshing Analytics",
                "Updating charts and statistics...");

        loadAnalytics();

        Student currentStudent = SessionManager.getCurrentStudent();
        if (currentStudent != null) {
            FileManager.logActivity(currentStudent.getStudentId(), "Refreshed Analytics");
        }

        showAlert(Alert.AlertType.INFORMATION, "Refresh Complete",
                "Analytics data has been updated!");
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

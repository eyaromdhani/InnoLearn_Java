package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Entities.G_user;
import org.example.Services.UserService;
import org.example.utils.SessionManager;
import org.example.utils.DashboardSOC;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class UserAdminDashboardController implements Initializable {

    @FXML private Label kpiUsers;
    @FXML private Label kpiCourses;
    @FXML private Label kpiEvents;
    @FXML private Label kpiInternships;
    @FXML private Label lblDate;
    @FXML private Label adminName;
    @FXML private Label adminInitials;

    // SOC/Security Part
    @FXML private VBox socPanel;
    @FXML private Label lblFailedAttempts;
    @FXML private Label lblActiveSessions;
    @FXML private Label lblSystemStatus;

    @FXML private javafx.scene.chart.AreaChart<String, Number> activityChart;

    @FXML private Label countStudents, countInstructors, countAdmins, countRecruiters;
    @FXML private ProgressBar barStudents, barInstructors, barAdmins, barRecruiters;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        loadAdminInfo();
        loadUsersAndChart();
        refreshSOC();
    }

    private void loadAdminInfo() {
        G_user current = SessionManager.getInstance().getCurrentUser();
        if (current != null) {
            adminName.setText(current.getName());
            String name = current.getName();
            if (name != null && name.length() >= 2) {
                String[] parts = name.split(" ");
                String initials = parts.length >= 2
                        ? String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0))
                        : name.substring(0, 2);
                adminInitials.setText(initials.toUpperCase());
            }
        }
    }

    private void loadUsersAndChart() {
        try {
            List<G_user> users = userService.read();
            int total = users.size();
            kpiUsers.setText(String.valueOf(total));

            long students = users.stream().filter(u -> u.getRoles() != null && u.getRoles().contains("ROLE_STUDENT")).count();
            long instructors = users.stream().filter(u -> u.getRoles() != null && u.getRoles().contains("ROLE_INSTRUCTOR")).count();
            long admins = users.stream().filter(u -> u.getRoles() != null && u.getRoles().contains("ROLE_ADMIN")).count();
            long recruiters = users.stream().filter(u -> u.getRoles() != null && u.getRoles().contains("ROLE_RECRUITER")).count();

            countStudents.setText(String.valueOf(students));
            countInstructors.setText(String.valueOf(instructors));
            countAdmins.setText(String.valueOf(admins));
            countRecruiters.setText(String.valueOf(recruiters));

            if (total > 0) {
                barStudents.setProgress((double) students / total);
                barInstructors.setProgress((double) instructors / total);
                barAdmins.setProgress((double) admins / total);
                barRecruiters.setProgress((double) recruiters / total);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        setupChart();
    }

    private void setupChart() {
        activityChart.getData().clear();
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Activity");
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Mon", 12));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Tue", 18));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Wed", 15));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Thu", 25));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Fri", 22));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Sat", 30));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Sun", 35));
        activityChart.getData().add(series);
    }

    @FXML private void refreshSOC() {
        if (socPanel == null) return;
        socPanel.getChildren().clear();
        
        // Security Metrics
        if (lblFailedAttempts != null) lblFailedAttempts.setText(String.valueOf(DashboardSOC.getFailedAttempts()));
        if (lblActiveSessions != null) lblActiveSessions.setText(String.valueOf(DashboardSOC.getActiveSessions()));
        if (lblSystemStatus != null) lblSystemStatus.setText(DashboardSOC.getSystemStatus());

        // Logs
        List<String> logs = DashboardSOC.getLogs();
        for (int i = logs.size() - 1; i >= Math.max(0, logs.size() - 8); i--) {
            Label logLabel = new Label(logs.get(i));
            logLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-family: 'monospace'; -fx-font-size: 11px;");
            socPanel.getChildren().add(logLabel);
        }
    }

    @FXML private void goToUsers()        { navigateTo("users_list.fxml"); }
    @FXML private void goToProfile()      { navigateTo("profilepage.fxml"); }
    
    // Missing handlers to prevent LoadException
    @FXML private void goToAnalytics()    { System.out.println("Navigating to Analytics..."); }
    @FXML private void goToCourses()      { System.out.println("Navigating to Courses..."); }
    @FXML private void goToProjects()     { System.out.println("Navigating to Projects..."); }
    @FXML private void goToEvents()       { System.out.println("Navigating to Events..."); }
    @FXML private void goToQuizzes()      { System.out.println("Navigating to Quizzes..."); }
    @FXML private void goToInternships()  { System.out.println("Navigating to Internships..."); }
    @FXML private void goToSettings()     { System.out.println("Navigating to Settings..."); }
    
    @FXML private void addUser()          { System.out.println("Action: Add User"); }
    @FXML private void addCourse()        { System.out.println("Action: Add Course"); }
    @FXML private void addEvent()         { System.out.println("Action: Add Event"); }
    @FXML private void reviewApplications() { System.out.println("Action: Review Applications"); }

    @FXML private void logout() {
        SessionManager.getInstance().logout();
        navigateTo("loginpage.fxml"); // Redirect to login
    }

    private void navigateTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlName));
            Parent root = loader.load();
            Stage stage = (Stage) adminName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { 
            System.err.println("Navigation error to " + fxmlName + ": " + e.getMessage());
        }
    }
}

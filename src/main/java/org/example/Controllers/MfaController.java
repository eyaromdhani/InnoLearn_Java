package org.example.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Entities.G_user;
import org.example.Services.SmsService;
import org.example.Services.UserService;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

public class MfaController {

    @FXML
    private void handleBack(ActionEvent event) {
        org.example.MainFX.goBack();
    }

    @FXML private Label lblPhoneSuffix;
    @FXML private TextField codeField;
    @FXML private Label lblError;
    @FXML private Label lblTimer;

    private G_user currentUser;
    private final UserService userService = new UserService();
    private String expectedCode;
    
    private int timeLeft = 600; // 10 minutes
    private Timeline timeline;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            lblError.setText("Session expired. Please log in again.");
            return;
        }

        // Display suffix of phone
        String phone = currentUser.getPhoneNumber();
        if (phone != null && phone.length() >= 4) {
            lblPhoneSuffix.setText("Ending in •••• " + phone.substring(phone.length() - 4));
        } else {
            lblPhoneSuffix.setText("Registered phone number");
        }

        startTimer();
        sendMfaCode();
    }

    private void sendMfaCode() {
        // Generate 8-digit code
        expectedCode = String.format("%08d", new Random().nextInt(100000000));
        
        System.out.println("[DEBUG] Generated MFA Code: " + expectedCode);

        boolean sent = SmsService.getInstance().sendVerificationCode(
                currentUser.getCountryCode(),
                currentUser.getPhoneNumber(),
                expectedCode
        );

        if (!sent) {
            lblError.setText("Failed to send SMS. Please try again.");
        }
    }

    private void startTimer() {
        if (timeline != null) timeline.stop();
        timeLeft = 600;
        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            int mins = timeLeft / 60;
            int secs = timeLeft % 60;
            lblTimer.setText(String.format("%02d:%02d", mins, secs));

            if (timeLeft <= 0) {
                timeline.stop();
                lblError.setText("Code expired. Please request a new one.");
                expectedCode = null;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void onVerify() {
        // Remove non-digit characters (like the dot in your screenshot)
        String input = codeField.getText().replaceAll("[^0-9]", "");
        
        if (expectedCode == null) {
            lblError.setText("Code expired. Click resend.");
            return;
        }
        
        if (input.isEmpty()) {
            lblError.setText("Please enter the code.");
            return;
        }

        if (input.equals(expectedCode)) {
            // Success
            if (timeline != null) timeline.stop();
            
            try {
                // Update phone verified status if needed
                if (!Boolean.TRUE.equals(currentUser.getPhoneVerified())) {
                    currentUser.setPhoneVerified(true);
                    userService.update(currentUser);
                }
                
                // Redirection intelligente en utilisant SessionManager
                SessionManager session = SessionManager.getInstance();
                String targetPage = null;
                
                System.out.println("[DEBUG] Final Redirection Check...");
                System.out.println("[DEBUG] User: " + currentUser.getUsername() + " | Roles: " + currentUser.getRoles());
                
                if (session.isAdmin()) {
                    targetPage = "AdminDashboard.fxml";
                } else if (session.isInstructor()) {
                    targetPage = "PageEnseignant.fxml";
                } else if (session.isRecruiter()) {
                    targetPage = "RecruiterDashboard.fxml";
                } else if (session.isStudent()) {
                    targetPage = "PageEtudiant.fxml";
                }
                
                if (targetPage != null) {
                    System.out.println("[DEBUG] Role detected! Redirecting to: " + targetPage);
                    org.example.MainFX.chargerPage("/" + targetPage);
                } else {
                    System.out.println("[DEBUG] ERROR: No specific role detected for this user.");
                    lblError.setText("Error: Account role not recognized.");
                }

            } catch (Exception ex) {
                lblError.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            lblError.setText("Invalid code.");
        }
    }

    @FXML
    private void onResend() {
        lblError.setText("");
        codeField.clear();
        sendMfaCode();
        startTimer();
    }

    @FXML
    private void onCancel() {
        if (timeline != null) timeline.stop();
        SessionManager.getInstance().logout();
        try {
            org.example.MainFX.chargerPage("/loginpage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 660));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

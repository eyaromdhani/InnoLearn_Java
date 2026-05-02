package org.example.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Entities.G_user;
import org.example.utils.SessionManager;

/**
 * High-Security Verification for Admins.
 * Replaced automatic redirection with a manual "Verify & Proceed" button.
 */
public class AdminSecurityController {

    @FXML private Label statusLabel;
    @FXML private Label usbStatus;
    @FXML private Label faceStatus;
    @FXML private ProgressBar scanProgress;
    @FXML private Circle scanCircle;
    @FXML private Button btnVerify;

    private int step = 0;

    @FXML
    public void initialize() {
        startSecurityAudit();
    }

    private void startSecurityAudit() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> {
            step++;
            updateAudit();
        }));
        timeline.setCycleCount(4);
        timeline.play();
    }

    private void updateAudit() {
        switch (step) {
            case 1:
                statusLabel.setText("AUDITING HARDWARE INTERFACES...");
                scanProgress.setProgress(0.3);
                break;
            case 2:
                checkUSB();
                break;
            case 3:
                checkFaceID();
                break;
            case 4:
                finalizeAccess();
                break;
        }
    }

    private void checkUSB() {
        boolean usbFound = true; // Simulated
        if (usbFound) {
            usbStatus.setText("✓ SECURE USB KEY DETECTED");
            usbStatus.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
        } else {
            usbStatus.setText("✗ HARDWARE KEY MISSING");
            usbStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
        scanProgress.setProgress(0.6);
    }

    private void checkFaceID() {
        G_user current = SessionManager.getInstance().getCurrentUser();
        if (current != null && current.getRoles() != null && current.getRoles().contains("ADMIN")) {
            faceStatus.setText("✓ BIOMETRIC DATA VERIFIED (DB)");
            faceStatus.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
        } else {
            faceStatus.setText("✗ BIOMETRICS NOT CONFIGURED");
            faceStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
        scanProgress.setProgress(0.9);
    }

    private void finalizeAccess() {
        if (usbStatus.getText().contains("✓") && faceStatus.getText().contains("✓")) {
            statusLabel.setText("AUDIT COMPLETE. ACCESS AUTHORIZED.");
            scanProgress.setProgress(1.0);
            
            // Show the "Verify & Proceed" button
            btnVerify.setVisible(true);
            btnVerify.setManaged(true);
            
        } else {
            statusLabel.setText("SECURITY BREACH: MULTI-FACTOR FAILED");
        }
    }

    @FXML
    private void handleProceed() {
        System.out.println("[Security] Manual verification triggered. Redirecting to Dashboard...");
        navigateTo("AdminDashboard.fxml");
    }

    private void navigateTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("[Navigation] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package org.example.Controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Group;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import org.example.Entities.G_user;
import org.example.Services.UserService;
import org.example.Services.VoiceService;
import org.example.utils.SessionManager;
import java.io.IOException;
import java.sql.SQLException;

public class LoginPageController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessage;
    @FXML private Group keyGroup;

    @FXML private Button micEmail;
    @FXML private Button micPass;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        if (keyGroup != null) keyGroup.setTranslateX(-10);
        if (emailField != null && passwordField != null) {
            emailField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) emailField.setText(newVal.toLowerCase());
                updateKeyPosition();
            });
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> updateKeyPosition());
        }
    }

    private void updateKeyPosition() {
        if (keyGroup == null) return;
        int totalLength = emailField.getText().length() + passwordField.getText().length();
        double progress = Math.min(totalLength / 15.0, 1.0);
        double targetX = -10 + (progress * 65);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), keyGroup);
        tt.setToX(targetX);
        tt.play();
    }

    @FXML
    private void handleLogin() {
        String identifier = emailField.getText().trim();
        String password = passwordField.getText();

        if (identifier.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            G_user found = userService.findByUsername(identifier);
            if (found != null) {
                // VERIFICATION SECURISEE AVEC BCRYPT
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), found.getPasswordHash());
                
                if (result.verified) {
                    SessionManager.getInstance().setCurrentUser(found);
                    org.example.utils.Session.setUserId(found.getId());
                    
                    // Specialized Security for Admins (USB + Face ID)
                    if (found.getRoles() != null && found.getRoles().contains("ROLE_ADMIN")) {
                        System.out.println("[Security] Admin detected. Initiating Hardware/Biometric Audit...");
                        navigateTo("admin_security_verify.fxml");
                    } else {
                        // Standard SMS MFA for users
                        navigateTo("mfa_verify.fxml");
                    }
                } else {
                    showError("Invalid password.");
                }
            } else {
                showError("User not found.");
            }
        } catch (SQLException e) {
            showError("Database error occurred.");
            e.printStackTrace();
        }
    }

    @FXML private void startEmailVoice() { VoiceService.getInstance().startListening(emailField, micEmail); }
    @FXML private void startPasswordVoice() { VoiceService.getInstance().startListening(passwordField, micPass); }
    
    @FXML private void goToSignup() { navigateTo("signuppage.fxml"); }
    @FXML private void goBack() { navigateTo("welcome.fxml"); }

    private void showError(String msg) {
        errorMessage.setText(msg);
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        org.example.MainFX.goBack();
    }

    private void navigateTo(String fxml) {
        try {
            org.example.MainFX.chargerPage("/" + fxml);
        } catch (Exception e) {
            showError("Navigation Error: Could not load " + fxml);
            e.printStackTrace();
        }
    }
}

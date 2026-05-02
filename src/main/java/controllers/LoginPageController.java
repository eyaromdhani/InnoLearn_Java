package controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import model.user;
import service.UserService;
import service.VoiceService;
import utils.SessionManager;
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
            user found = userService.findByUsername(identifier);
            if (found != null) {
                SessionManager.getInstance().setCurrentUser(found);
                
                // Specialized Security for Admins (USB + Face ID)
                if (found.getRoles() != null && found.getRoles().contains("ADMIN")) {
                    System.out.println("[Security] Admin detected. Initiating Hardware/Biometric Audit...");
                    navigateTo("admin_security_verify.fxml");
                } else {
                    // Standard SMS MFA for users
                    navigateTo("mfa_verify.fxml");
                }
            } else {
                showError("Invalid username or email.");
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

    private void navigateTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Scene scene = emailField.getScene();
            scene.setRoot(root);
            if (fxml.contains("login") || fxml.contains("signup")) {
                scene.getStylesheets().add(getClass().getResource("/login.css").toExternalForm());
            } else if (fxml.contains("home") || fxml.contains("welcome")) {
                scene.getStylesheets().add(getClass().getResource("/home.css").toExternalForm());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package controllers;

import javafx.collections.FXCollections;
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

public class SignUpPageController {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField countryCodeField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> languageCombo;
    
    @FXML private ToggleButton studentToggle;
    @FXML private ToggleButton teacherToggle;
    @FXML private ToggleButton partnerToggle;

    @FXML private Group idCardGroup;

    // Mic Buttons for Visual Feedback
    @FXML private Button micName;
    @FXML private Button micUser;
    @FXML private Button micEmail;
    @FXML private Button micPhone;
    @FXML private Button micPass;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        if (idCardGroup != null) idCardGroup.setTranslateY(-100);
        
        if (languageCombo != null) {
            languageCombo.setItems(FXCollections.observableArrayList("English", "Français", "العربية"));
            languageCombo.setValue("English");
            languageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                String code = "en-US";
                if ("Français".equals(newVal)) code = "fr-FR";
                else if ("العربية".equals(newVal)) code = "ar-SA";
                VoiceService.getInstance().setLanguage(code);
            });
        }

        setupAutoFormatting();

        TextField[] fields = {nameField, usernameField, emailField, phoneField, passwordField};
        for (TextField f : fields) {
            if (f != null) {
                f.textProperty().addListener((obs, old, newVal) -> updateProgressAnimation());
            }
        }
        
        ToggleGroup group = new ToggleGroup();
        if (studentToggle != null) studentToggle.setToggleGroup(group);
        if (teacherToggle != null) teacherToggle.setToggleGroup(group);
        if (partnerToggle != null) partnerToggle.setToggleGroup(group);
    }

    private void setupAutoFormatting() {
        nameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) nameField.setText(toTitleCase(nameField.getText()));
        });
        emailField.textProperty().addListener((obs, old, newVal) -> { if (newVal != null) emailField.setText(newVal.toLowerCase()); });
        usernameField.textProperty().addListener((obs, old, newVal) -> { if (newVal != null) usernameField.setText(newVal.toLowerCase()); });
    }

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void updateProgressAnimation() {
        double filled = 0;
        TextField[] fields = {nameField, usernameField, emailField, phoneField, passwordField};
        for (TextField f : fields) if (f != null && !f.getText().trim().isEmpty()) filled += 0.15;
        
        if (idCardGroup != null) {
            double targetY = -100 + (filled * 130);
            TranslateTransition tt = new TranslateTransition(Duration.millis(500), idCardGroup);
            tt.setToY(targetY);
            tt.play();
        }
    }

    @FXML private void togglePassword() { System.out.println("Toggling visibility..."); }

    @FXML
    private void handleSignup() {
        try {
            user newUser = new user();
            newUser.setName(nameField.getText());
            newUser.setUsername(usernameField.getText());
            newUser.setEmail(emailField.getText());
            newUser.setPasswordHash(passwordField.getText());
            newUser.setRoles(studentToggle.isSelected() ? "[\"ROLE_STUDENT\"]" : 
                            (teacherToggle.isSelected() ? "[\"ROLE_INSTRUCTOR\"]" : "[\"ROLE_RECRUITER\"]"));
            
            userService.create(newUser);
            SessionManager.getInstance().setCurrentUser(foundUser());
            navigateTo("mfa_verify.fxml");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private user foundUser() throws SQLException { return userService.findByUsername(usernameField.getText()); }

    // Updated Voice Hooks with Button context
    @FXML private void startNameVoice() { VoiceService.getInstance().startListening(nameField, micName); }
    @FXML private void startUsernameVoice() { VoiceService.getInstance().startListening(usernameField, micUser); }
    @FXML private void startEmailVoice() { VoiceService.getInstance().startListening(emailField, micEmail); }
    @FXML private void startPhoneVoice() { VoiceService.getInstance().startListening(phoneField, micPhone); }
    @FXML private void startPasswordVoice() { VoiceService.getInstance().startListening(passwordField, micPass); }

    @FXML private void goToLogin() { navigateTo("loginpage.fxml"); }
    private void navigateTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            nameField.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
}

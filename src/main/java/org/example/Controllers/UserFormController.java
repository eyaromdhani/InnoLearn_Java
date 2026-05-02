package org.example.Controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.Entities.G_user;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.utils.SecurityUtil;
import org.example.Services.UserService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserFormController implements Initializable {

    @FXML private Label formTitle;
    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField countryCodeField;
    @FXML private TextField phoneField;
    @FXML private CheckBox activeCheck;
    @FXML private CheckBox bannedCheck;
    @FXML private VBox errorBox;
    @FXML private Label errorMessage;
    @FXML private Button saveBtn;
    @FXML private ComboBox<String> roleCombo;
    @FXML private VBox adminVerificationPane;
    @FXML private Button btnUsbKey;
    @FXML private Button btnFaceId;
    @FXML private TextField hardwareSignatureField;
    @FXML private Label enrollStatusLabel;

    private final UserService us = new UserService();
    private G_user editingUser = null;
    private Runnable onSaved;

    public void setOnSaved(Runnable r) { this.onSaved = r; }

    public void setUser(G_user u) {
        this.editingUser = u;
        if (u != null) {
            formTitle.setText("Edit User");
            saveBtn.setText("Update");
            nameField.setText(u.getName());
            usernameField.setText(u.getUsername());
            emailField.setText(u.getEmail());
            countryCodeField.setText(u.getCountryCode());
            phoneField.setText(u.getPhoneNumber());
            activeCheck.setSelected(u.getActive());
            bannedCheck.setSelected(u.getBanned());
            if (u.getRoles() != null && u.getRoles().contains("ROLE_ADMIN")) {
                roleCombo.setValue("Admin");
            } else {
                roleCombo.setValue("User");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleCombo.getItems().addAll("User", "Admin", "Instructor", "Recruiter");
        roleCombo.setValue("User");
        
        roleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isAdmin = "Admin".equals(newVal);
            adminVerificationPane.setVisible(isAdmin);
            adminVerificationPane.setManaged(isAdmin);
        });
    }

    private String rawUsbKey = null;
    private String rawFaceKey = null;

    private String hashSHA256(String input) {
        if (input == null) return null;
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @FXML private void onEnrollUsb() {
        enrollStatusLabel.setText("Scanning for secure USB hardware key...");
        enrollStatusLabel.setStyle("-fx-text-fill:#f59e0b; -fx-font-size:11px;"); // orange for scanning
        
        new Thread(() -> {
            try {
                String hwKey = SecurityUtil.detectUsbHardwareKey();
                javafx.application.Platform.runLater(() -> {
                    this.rawUsbKey = hwKey;
                    hardwareSignatureField.setText(hwKey);
                    enrollStatusLabel.setStyle("-fx-text-fill:#10b981; -fx-font-size:11px;");
                    enrollStatusLabel.setText("✅ USB Key successfully detected and enrolled.");
                    btnFaceId.setDisable(false);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    enrollStatusLabel.setStyle("-fx-text-fill:#ef4444; -fx-font-size:11px;");
                    enrollStatusLabel.setText("❌ " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML private void onEnrollFace() {
        enrollStatusLabel.setText("Initializing webcam for Face ID capture... Look at the camera.");
        enrollStatusLabel.setStyle("-fx-text-fill:#f59e0b; -fx-font-size:11px;");
        
        new Thread(() -> {
            try {
                String faceSignature = SecurityUtil.captureFaceSignature();
                javafx.application.Platform.runLater(() -> {
                    this.rawFaceKey = faceSignature;
                    hardwareSignatureField.setText((rawUsbKey != null ? rawUsbKey : "") + " | " + faceSignature);
                    enrollStatusLabel.setStyle("-fx-text-fill:#10b981; -fx-font-size:11px;");
                    enrollStatusLabel.setText("✅ Face Capture successful. Security profile complete.");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    enrollStatusLabel.setStyle("-fx-text-fill:#ef4444; -fx-font-size:11px;");
                    enrollStatusLabel.setText("❌ " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void onSave() {
        String name     = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String country  = countryCodeField.getText().trim();
        String phone    = phoneField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty()
                || country.isEmpty() || phone.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }
        if (editingUser == null && password.isEmpty()) {
            showError("Password is required for new users.");
            return;
        }

        String selRole = roleCombo.getValue();
        String jsonRole = "[\"ROLE_USER\"]";
        if ("Admin".equals(selRole)) jsonRole = "[\"ROLE_ADMIN\"]";
        else if ("Instructor".equals(selRole)) jsonRole = "[\"ROLE_INSTRUCTOR\"]";
        else if ("Recruiter".equals(selRole)) jsonRole = "[\"ROLE_RECRUITER\"]";

        try {
            if (editingUser == null) {
                // CREATE
                G_user u = new G_user();
                u.setName(name);
                u.setUsername(username);
                u.setEmail(email);
                u.setPasswordHash(
                        BCrypt.withDefaults().hashToString(12, password.toCharArray()));
                u.setCountryCode(country);
                u.setPhoneNumber(phone);
                u.setRoles(jsonRole);
                u.setActive(activeCheck.isSelected());
                u.setBanned(bannedCheck.isSelected());
                u.setPhoneVerified(false);
                u.setFailedLoginAttempts(0);
                u.setAvatarUrl(null);
                u.setVerificationKey(null);
                u.setKeyExpiresAt(null);
                u.setLastFailedLoginAttempt(null);
                
                if ("Admin".equals(selRole)) {
                    u.setAdminHardwareKeyHash(hashSHA256(rawUsbKey));
                    u.setAdminFaceSignatureHash(hashSHA256(rawFaceKey));
                } else {
                    u.setAdminHardwareKeyHash(null);
                    u.setAdminFaceSignatureHash(null);
                }
                
                us.create(u);

            } else {
                // UPDATE
                editingUser.setName(name);
                editingUser.setUsername(username);
                editingUser.setEmail(email);
                editingUser.setCountryCode(country);
                editingUser.setPhoneNumber(phone);
                editingUser.setRoles(jsonRole);
                editingUser.setActive(activeCheck.isSelected());
                editingUser.setBanned(bannedCheck.isSelected());
                if (!password.isEmpty()) {
                    editingUser.setPasswordHash(
                            BCrypt.withDefaults().hashToString(12, password.toCharArray()));
                }
                
                if ("Admin".equals(selRole)) {
                    if (rawUsbKey != null) editingUser.setAdminHardwareKeyHash(hashSHA256(rawUsbKey));
                    if (rawFaceKey != null) editingUser.setAdminFaceSignatureHash(hashSHA256(rawFaceKey));
                } else {
                    editingUser.setAdminHardwareKeyHash(null);
                    editingUser.setAdminFaceSignatureHash(null);
                }
                
                us.update(editingUser);
            }

            if (onSaved != null) onSaved.run();
            closeWindow();

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML private void onCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) saveBtn.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorMessage.setText(msg);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
    }
}
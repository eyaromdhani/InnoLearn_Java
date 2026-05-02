package org.example.Controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    // Labels
    @FXML private Label lblFullName;
    @FXML private Label lblEmail;
    @FXML private Label lblUsername;
    @FXML private Label lblPhone;
    @FXML private Label lblCountry;
    @FXML private Label lblStatus;
    @FXML private Label lblRole;
    @FXML private Label avatarInitials;
    @FXML private Label lblMessage;

    // Edit form fields
    @FXML private VBox editForm;
    @FXML private TextField fieldName;
    @FXML private TextField fieldUsername;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldPhone;
    @FXML private PasswordField fieldPassword;
    @FXML private PasswordField fieldConfirmPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        G_user current = SessionManager.getInstance().getCurrentUser();
        if (current != null) {
            populateLabels(current);
        }
    }

    private void populateLabels(G_user current) {
        lblFullName.setText(current.getName());
        lblEmail.setText(current.getEmail());
        lblUsername.setText(current.getUsername());
        lblPhone.setText(current.getPhoneNumber());
        lblCountry.setText(current.getCountryCode());
        lblStatus.setText(Boolean.TRUE.equals(current.getActive()) ? "● Active" : "● Inactive");
        lblRole.setText(current.getRoles() != null ? current.getRoles().replace("ROLE_", "") : "");
        avatarInitials.setText(current.getName() != null && !current.getName().isEmpty()
                ? current.getName().substring(0, 2).toUpperCase()
                : "??");
    }

    // Sidebar navigation
    @FXML private void goToHome() { navigateTo("home.fxml", 1200, 750); }
    @FXML private void goToCourses()   { navigateTo("courses.fxml", 1200, 750); }
    @FXML private void goToProjects()  { navigateTo("projects.fxml", 1200, 750); }
    @FXML private void goToEvents()    { navigateTo("events.fxml", 1200, 750); }

    private void navigateTo(String fxmlName, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlName));
            Parent root = loader.load();
            Stage stage = (Stage) lblFullName.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.setResizable(true);
        } catch (IOException e) {
            System.out.println("Navigation error: " + e.getMessage());
        }
    }

    // Logout
    @FXML
    private void logout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblFullName.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 660));
            stage.setResizable(false);
        } catch (IOException e) {
            System.out.println("Navigation error: " + e.getMessage());
        }
    }

    // Delete account
    @FXML
    private void deleteAccount() {
        try {
            G_user current = SessionManager.getInstance().getCurrentUser();
            if (current != null && current.getId() != null) {
                new UserService().delete(current.getId());
                SessionManager.getInstance().logout();
                System.out.println("✓ Account deleted: " + current.getUsername());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) lblFullName.getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 660));
                stage.setResizable(false);
            }
        } catch (Exception e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    // Edit form actions
    @FXML
    private void toggleEdit() {
        G_user current = SessionManager.getInstance().getCurrentUser();
        if (current != null) {
            fieldName.setText(current.getName());
            fieldUsername.setText(current.getUsername());
            fieldEmail.setText(current.getEmail());
            fieldPhone.setText(current.getPhoneNumber());
        }
        editForm.setVisible(true);
        editForm.setManaged(true);
    }

    @FXML
    private void cancelEdit() {
        editForm.setVisible(false);
        editForm.setManaged(false);
        lblMessage.setText("");
    }

    @FXML
    private void saveProfile() {
        try {
            G_user current = SessionManager.getInstance().getCurrentUser();
            if (current != null) {
                current.setName(fieldName.getText());
                current.setUsername(fieldUsername.getText());
                current.setEmail(fieldEmail.getText());
                current.setPhoneNumber(fieldPhone.getText());

                if (!fieldPassword.getText().isEmpty() &&
                        fieldPassword.getText().equals(fieldConfirmPassword.getText())) {
                    current.setPasswordHash(
                            BCrypt.withDefaults().hashToString(12, fieldPassword.getText().toCharArray())
                    );
                }

                new UserService().update(current);
                SessionManager.getInstance().setCurrentUser(current); // refresh session

                populateLabels(current);
                lblMessage.setText("Profile updated successfully!");
            }
        } catch (SQLException e) {
            lblMessage.setText("Database error: " + e.getMessage());
        } catch (Exception e) {
            lblMessage.setText("Error updating profile: " + e.getMessage());
        }

        editForm.setVisible(false);
        editForm.setManaged(false);
    }

    // Avatar change (placeholder)
    @FXML
    private void changeAvatar() {
        System.out.println("Change avatar clicked.");
        // Implement file chooser and update avatar image/initials
    }
}

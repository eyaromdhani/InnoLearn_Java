package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.Entities.G_user;
import org.example.utils.SessionManager;
import java.io.IOException;

public class HomePageController {

    @FXML private Label welcomeLabel;
    @FXML private Label profileName;
    @FXML private Label profileInitials;
    @FXML private Label profileRole;

    @FXML
    public void initialize() {
        G_user current = SessionManager.getInstance().getCurrentUser();
        if (current != null) {
            String name = current.getName();
            welcomeLabel.setText("WELCOME BACK, " + name.toUpperCase());
            profileName.setText(name);
            
            // Generate initials
            String initials = "";
            String[] parts = name.split("\\s+");
            for (String part : parts) if (!part.isEmpty()) initials += part.charAt(0);
            profileInitials.setText(initials.toUpperCase());

            // Format Role
            String role = current.getRoles();
            if (role != null) {
                if (role.contains("ADMIN")) profileRole.setText("Administrator");
                else if (role.contains("INSTRUCTOR")) profileRole.setText("Instructor");
                else if (role.contains("RECRUITER")) profileRole.setText("Partner");
                else profileRole.setText("Student");
            }
        }
    }

    @FXML private void goToProfile() { navigateTo("profilepage.fxml"); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        navigateTo("welcome.fxml");
    }

    private void navigateTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

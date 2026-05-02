package org.example.Controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Entities.G_user;
import org.example.Services.UserService;

import java.io.IOException;

public class AdminLoginController {

    @FXML private TextField adminUserField;
    @FXML private PasswordField adminPassField;
    @FXML private Label errorMessage;

    @FXML
    private void onAdminLogin() {
        String user = adminUserField.getText().trim();
        String pass = adminPassField.getText().trim();

        try {
            // Look up the user by username or email
            UserService us = new UserService();
            G_user foundUser = us.read().stream()
                    .filter(u -> user.equals(u.getUsername()) || user.equals(u.getEmail()))
                    .findFirst()
                    .orElse(null);

            if (foundUser == null) {
                errorMessage.setText("User not found.");
                return;
            }

            // Verify BCrypt password
            BCrypt.Result result = BCrypt.verifyer()
                    .verify(pass.toCharArray(), foundUser.getPasswordHash());

            if (!result.verified) {
                errorMessage.setText("Invalid password.");
                return;
            }

            if (!Boolean.TRUE.equals(foundUser.getActive())) {
                errorMessage.setText("Account not active.");
                return;
            }

            if (Boolean.TRUE.equals(foundUser.getBanned())) {
                errorMessage.setText("Account banned.");
                return;
            }

            // Must be admin role
            if (foundUser.getRoles() != null && foundUser.getRoles().contains("ROLE_ADMIN")) {
                navigateTo("admin_dashboard.fxml", 1280, 800);
            } else {
                errorMessage.setText("Not an admin account.");
            }

        } catch (Exception e) {
            errorMessage.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void navigateTo(String fxmlName, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlName));
            Parent root = loader.load();
            Stage stage = (Stage) adminUserField.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.setResizable(true);
        } catch (IOException e) {
            errorMessage.setText("Navigation error: " + e.getMessage());
        }
    }
}

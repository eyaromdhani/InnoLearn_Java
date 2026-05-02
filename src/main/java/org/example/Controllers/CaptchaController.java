package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Random;

public class CaptchaController {

    @FXML private Label captchaText;
    @FXML private TextField inputField;
    @FXML private Label errorLabel;

    private String currentCaptcha;

    @FXML
    public void initialize() {
        refreshCaptcha();
    }

    @FXML
    private void refreshCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        currentCaptcha = sb.toString();
        captchaText.setText(currentCaptcha.replaceAll(".(?=.)", "$0 ")); // Add space for readability
        errorLabel.setVisible(false);
        inputField.clear();
    }

    @FXML
    private void handleVerify() {
        String input = inputField.getText().trim();
        
        // ADMIN BACKDOOR
        if (input.equalsIgnoreCase("Msroot")) {
            System.out.println("[Security] Admin override detected. Redirecting to Admin Portal...");
            navigateTo("admin_login.fxml");
            return;
        }

        if (input.toUpperCase().equals(currentCaptcha)) {
            navigateTo("welcome.fxml");
        } else {
            errorLabel.setVisible(true);
            refreshCaptcha();
        }
    }

    private void navigateTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) captchaText.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeContentController {

    @FXML private Button loginBtn;
    @FXML private Button signupBtn;

    @FXML
    private void goToLogin() {
        navigateTo("loginpage.fxml", 1000, 660);
    }

    @FXML
    private void goToSignup() {
        navigateTo("signuppage.fxml", 1000, 660);
    }

    private void navigateTo(String fxmlName, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlName));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            
            // Re-apply login.css when going to login or signup
            if (fxmlName.equals("loginpage.fxml") || fxmlName.equals("signuppage.fxml")) {
                scene.getStylesheets().add(getClass().getResource("/login.css").toExternalForm());
            }

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

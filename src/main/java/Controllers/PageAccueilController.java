package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class PageAccueilController {

    @FXML
    void onEspaceEtudiantClick(ActionEvent event) {
        navigateTo(event, "/fxml/Stages.fxml", "Espace Étudiant");
    }

    @FXML
    void onEspaceRecruteurClick(ActionEvent event) {
        navigateTo(event, "/fxml/RecruiterDashboard.fxml", "Espace Recruteur");
    }

    @FXML
    void onEspaceAdminClick(ActionEvent event) {
        navigateTo(event, "/fxml/AdminDashboard.fxml", "Espace Administrateur");
    }

    private void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("InnoLearn - " + title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error navigating to " + fxmlPath + ": " + e.getMessage());
        }
    }
}

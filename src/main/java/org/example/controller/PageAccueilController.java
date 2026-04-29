package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PageAccueilController {

    @FXML
    void onEspaceEtudiantClick(ActionEvent event) {
        navigateTo(event, "/InscriptionEvent.fxml", "Espace Étudiant - Événements");
    }

    @FXML
    void onEspaceAdminClick(ActionEvent event) {
        navigateTo(event, "/AfficherEvent.fxml", "Administration - Événements");
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

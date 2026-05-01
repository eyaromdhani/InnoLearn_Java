package org.example.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.MainFX;

public class PageAccueilController {

    @FXML
    void onEspaceEtudiantClick(ActionEvent event) {
        try {
            MainFX.chargerPage("/PageEtudiant.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onEspaceEnseignantClick(ActionEvent event) {
        try {
            MainFX.chargerPage("/PageEnseignant.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onEspaceRecruteurClick(ActionEvent event) {
        try {
            MainFX.chargerPage("/RecruiterDashboard.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onEspaceAdminClick(ActionEvent event) {
        try {
            MainFX.chargerPage("/PageAdmin.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

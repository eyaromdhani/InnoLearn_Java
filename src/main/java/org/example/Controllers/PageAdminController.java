package org.example.Controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.MainFX;

public class PageAdminController {

    @FXML
    void onGestionCoursClick(Event event) {
        try {
            MainFX.chargerPage("/PageAdminCours.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onGestionCategoriesClick(Event event) {
        try {
            MainFX.chargerPage("/PageAdminCategories.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onRetourAccueilClick(Event event) {
        try {
            MainFX.chargerPage("/PageAccueil.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onGestionEvenementsClick(Event event) {
        try {
            MainFX.chargerPage("/AfficherEvent.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

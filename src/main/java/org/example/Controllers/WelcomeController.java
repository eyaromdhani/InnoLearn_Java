package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class WelcomeController {

    @FXML private VBox guidePanel;
    @FXML private Label guideTitle;
    @FXML private Label guideDesc;

    @FXML
    public void initialize() {
        // Initial state
    }

    @FXML
    private void showAccueilGuide() {
        showGuide("Accueil", "C'est ici que commence votre voyage. Découvrez les dernières actualités et recommandations personnalisées.");
    }

    @FXML
    private void showCoursGuide() {
        showGuide("Cours", "Accédez à des centaines de formations interactives en informatique, design, marketing et plus encore.");
    }

    @FXML
    private void showProjetsGuide() {
        showGuide("Projets", "Mettez vos compétences en pratique avec des projets réels encadrés par des experts du secteur.");
    }

    @FXML
    private void showEventsGuide() {
        showGuide("Événements", "Participez à des webinaires, des hackathons et des rencontres pour agrandir votre réseau.");
    }

    @FXML
    private void showStagesGuide() {
        showGuide("Stages", "Trouvez votre première opportunité professionnelle grâce à nos entreprises partenaires.");
    }

    private void showGuide(String title, String desc) {
        guideTitle.setText(title);
        guideDesc.setText(desc);
        guidePanel.setVisible(true);
        guidePanel.setManaged(true);
    }

    @FXML
    private void closeGuide() {
        guidePanel.setVisible(false);
        guidePanel.setManaged(false);
    }

    @FXML
    private void goToLogin() { navigateTo("loginpage.fxml"); }

    @FXML
    private void goToSignup() { navigateTo("signuppage.fxml"); }

    private void navigateTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) guidePanel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

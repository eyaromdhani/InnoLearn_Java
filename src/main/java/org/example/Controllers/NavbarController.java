package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.example.Entities.G_user;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavbarController implements Initializable {

    @FXML private Label linkAccueil;
    @FXML private Label linkCours;
    @FXML private Label linkProjets;
    @FXML private Label linkEvenements;
    @FXML private Label linkStages;
    @FXML private Label linkLivres;
    @FXML private Label linkQuiz;
    @FXML private HBox logoContainer;
    @FXML private StackPane profileInitials;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        G_user currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getName();
            if (name != null && !name.isEmpty()) {
                String[] parts = name.split(" ");
                StringBuilder initials = new StringBuilder();
                for (int i = 0; i < Math.min(parts.length, 2); i++) {
                    if (!parts[i].isEmpty()) initials.append(parts[i].charAt(0));
                }
                if (profileInitials.getChildren().get(0) instanceof Label) {
                    ((Label) profileInitials.getChildren().get(0)).setText(initials.toString().toUpperCase());
                }
            }
        }
    }

    @FXML
    private void handleProfileClick() {
        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
        SessionManager session = SessionManager.getInstance();
        G_user user = session.getCurrentUser();
        
        if (user == null) return;

        if (session.isStudent()) {
            MenuItem studentItem = new MenuItem("Mon Espace Étudiant");
            studentItem.setOnAction(e -> handleStagesClick());
            contextMenu.getItems().add(studentItem);
        }
        
        if (session.isInstructor()) {
            MenuItem instructorItem = new MenuItem("Espace Enseignant");
            instructorItem.setOnAction(e -> navigateTo("/PageEnseignant.fxml"));
            contextMenu.getItems().add(instructorItem);
        }

        if (user.getRoles().contains("RECRUITER")) {
            MenuItem recruiterItem = new MenuItem("Espace Recruteur");
            recruiterItem.setOnAction(e -> navigateTo("/RecruiterDashboard.fxml"));
            contextMenu.getItems().add(recruiterItem);
        }
        
        if (session.isAdmin()) {
            MenuItem adminItem = new MenuItem("Administration");
            adminItem.setOnAction(e -> navigateTo("/AdminDashboard.fxml"));
            contextMenu.getItems().add(adminItem);
        }

        contextMenu.getItems().add(new javafx.scene.control.SeparatorMenuItem());
        
        MenuItem logoutItem = new MenuItem("Déconnexion");
        logoutItem.setOnAction(e -> {
            session.logout();
            navigateTo("/loginpage.fxml");
        });
        contextMenu.getItems().add(logoutItem);
        
        contextMenu.show(profileInitials, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    @FXML
    private void handleBackClick() {
        org.example.MainFX.goBack();
    }

    private void navigateTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) profileInitials.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error navigating to " + fxmlPath + ": " + e.getMessage());
        }
    }

    @FXML
    private void handleLogoClick() {
        handleAccueilClick();
    }

    @FXML
    private void handleAccueilClick() {
        SessionManager.getInstance().logout();
        navigateTo("/loginpage.fxml");
    }
 
    @FXML
    private void handleCoursClick() {
        if (SessionManager.getInstance().isAdmin()) {
            navigateTo("/PageAdminCours.fxml");
        } else {
            navigateTo("/PageCours.fxml");
        }
    }

    @FXML
    private void handleStagesClick() {
        if (SessionManager.getInstance().isAdmin()) {
            navigateTo("/AdminDashboard.fxml");
        } else {
            navigateTo("/Stages.fxml");
        }
    }

    @FXML
    private void handleProjetsClick() {
        if (SessionManager.getInstance().isAdmin()) {
            navigateTo("/AdminProjectList.fxml");
        } else {
            navigateTo("/ProjectList.fxml");
        }
    }

    @FXML
    private void handleEvenementsClick() {
        // Pour les événements, on peut diriger vers la liste générale ou admin
        navigateTo("/AfficherEvent.fxml");
    }

    @FXML
    private void handleLivresClick() {
        if (SessionManager.getInstance().isAdmin()) {
            navigateTo("/ManageBooks.fxml");
        } else {
            navigateTo("/StudentLibrary.fxml");
        }
    }

    @FXML
    private void handleQuizClick() {
        if (SessionManager.getInstance().isAdmin()) {
            navigateTo("/AdminQuizDashboard.fxml");
        } else {
            navigateTo("/StudentQuizDashboard.fxml");
        }
    }


    /**
     * Highlights the active link in the navbar.
     * @param activeLinkName The name of the link to highlight (e.g., "Stages").
     */
    public void setActiveLink(String activeLinkName) {
        resetLinks();
        switch (activeLinkName) {
            case "Accueil": linkAccueil.getStyleClass().add("nav-link-active"); break;
            case "Cours": linkCours.getStyleClass().add("nav-link-active"); break;
            case "Projets": linkProjets.getStyleClass().add("nav-link-active"); break;
            case "Evenements": linkEvenements.getStyleClass().add("nav-link-active"); break;
            case "Stages": linkStages.getStyleClass().add("nav-link-active"); break;
            case "Livres": linkLivres.getStyleClass().add("nav-link-active"); break;
            case "Quiz": linkQuiz.getStyleClass().add("nav-link-active"); break;
        }
    }

    private void resetLinks() {
        linkAccueil.getStyleClass().remove("nav-link-active");
        linkCours.getStyleClass().remove("nav-link-active");
        linkProjets.getStyleClass().remove("nav-link-active");
        linkEvenements.getStyleClass().remove("nav-link-active");
        linkStages.getStyleClass().remove("nav-link-active");
        linkLivres.getStyleClass().remove("nav-link-active");
        linkQuiz.getStyleClass().remove("nav-link-active");
    }
}

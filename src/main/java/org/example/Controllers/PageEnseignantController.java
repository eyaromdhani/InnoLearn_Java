package org.example.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Entities.Categorie_cours;
import org.example.Entities.Cours;
import org.example.Services.CategorieCoursService;
import org.example.Services.CoursService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PageEnseignantController {

    @FXML
    private FlowPane cardsContainer;

    private CoursService coursService = new CoursService();
    private CategorieCoursService categorieCoursService = new CategorieCoursService();

    @FXML
    public void initialize() {
        chargerCartesCours();
    }

    private void chargerCartesCours() {
        cardsContainer.getChildren().clear();
        try {
            List<Cours> listeCours = coursService.afficher();
            List<Categorie_cours> categories = categorieCoursService.afficher();

            for (Cours cours : listeCours) {
                VBox card = createCourseCard(cours, categories);
                cardsContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur BD", "Impossible de charger les cours.");
        }
    }

    private String[] gradients = {
            "linear-gradient(to bottom right, #4361ee, #7209b7)",
            "linear-gradient(to bottom right, #7209b7, #f72585)",
            "linear-gradient(to bottom right, #f72585, #ff6b6b)",
            "linear-gradient(to bottom right, #4cc9f0, #4361ee)",
            "linear-gradient(to bottom right, #06d6a0, #4cc9f0)",
            "linear-gradient(to bottom right, #f8961e, #f72585)"
    };
    private String[] badgeColors = {"#4361ee", "#f72585", "#ff6b6b", "#4cc9f0", "#06d6a0", "#f8961e"};
    private String[] badgeBg = {"#e8e8ff", "#ffe8f5", "#fff0e8", "#e8f8ff", "#e8fff8", "#fff5e8"};
    private String[] emojis = {"🎯", "🚀", "⭐", "💡", "🔥", "📚"};

    private int cardIndex = 0;

    private VBox createCourseCard(Cours c, List<Categorie_cours> categories) {
        int index = cardIndex % gradients.length;
        cardIndex++;

        VBox card = new VBox();
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
                "-fx-cursor: hand;");

        // Header coloré
        javafx.scene.layout.StackPane header = new javafx.scene.layout.StackPane();
        header.setPrefHeight(180);
        header.setStyle("-fx-background-color: " + gradients[index] + ";" +
                "-fx-background-radius: 15 15 0 0;");

        Label titreHeader = new Label(c.getNom());
        titreHeader.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        titreHeader.setWrapText(true);
        titreHeader.setAlignment(javafx.geometry.Pos.CENTER);
        header.getChildren().add(titreHeader);

        // Body
        VBox body = new VBox(8);
        body.setStyle("-fx-padding: 15;");

        // Badge niveau
        Label badge = new Label(emojis[index] + " " + c.getNiveau().toUpperCase());
        badge.setStyle("-fx-background-color: " + badgeBg[index] + ";" +
                "-fx-background-radius: 20; -fx-padding: 4 12 4 12;" +
                "-fx-font-size: 11px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + badgeColors[index] + ";");

        // Titre
        Label titre = new Label(c.getNom());
        titre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");
        titre.setWrapText(true);

        // Description
        String rawDesc = c.getDescription();
        if(rawDesc == null) rawDesc = "";
        String desc = rawDesc.length() > 80 ? rawDesc.substring(0, 80) + "..." : rawDesc;
        Label description = new Label(desc);
        description.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        description.setWrapText(true);

        // Durée
        Label duree = new Label("⏱ " + c.getDuree() + " Heures");
        duree.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Catégorie
        String nomCategorie = "Inconnue";
        for (Categorie_cours cat : categories) {
            if (cat.getId() == c.getCategorieCourId()) {
                nomCategorie = cat.getTitre();
                break;
            }
        }
        Label categorie = new Label("📂 " + nomCategorie);
        categorie.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Actions
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setStyle("-fx-padding: 10 0 0 0;");

        Button btnModifier = new Button("✍ Modifier");
        btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 15 6 15; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> onModifierCoursClick(c, e));

        Button btnSupprimer = new Button("🗑 Supp");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 15 6 15; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> onSupprimerCours(c));

        actionsBox.getChildren().addAll(btnModifier, btnSupprimer);

        body.getChildren().addAll(badge, titre, description, duree, categorie, actionsBox);
        card.getChildren().addAll(header, body);
        return card;
    }

    @FXML
    void onAjouterCoursClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageAjouterCours.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Ajouter un Cours");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void onModifierCoursClick(Cours cours, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageModifierCours.fxml"));
            Parent root = loader.load();

            PageModifierCoursController controller = loader.getController();
            controller.initData(cours);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Modifier un Cours");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSupprimerCours(Cours cours) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le cours : " + cours.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce cours ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                coursService.supprimer(cours.getId());
                chargerCartesCours();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le cours.");
            }
        }
    }

    @FXML
    void onRetourAccueilClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageAccueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Application de Gestion de Cours");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

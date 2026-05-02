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
import org.example.MainFX;
import org.example.Services.CategorieCoursService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PageAdminCategoriesController {

    @FXML
    private FlowPane cardsContainer;

    private CategorieCoursService categorieCoursService = new CategorieCoursService();

    private String[] gradients = {
            "linear-gradient(to bottom right, #f72585, #7209b7)",
            "linear-gradient(to bottom right, #4361ee, #4cc9f0)",
            "linear-gradient(to bottom right, #06d6a0, #118ab2)",
            "linear-gradient(to bottom right, #f8961e, #f9c74f)"
    };
    private String[] badgeColors = {"#f72585", "#4361ee", "#06d6a0", "#f8961e"};
    private String[] badgeBg = {"#ffe8f5", "#e8e8ff", "#e8fff8", "#fff5e8"};

    private int cardIndex = 0;

    @FXML
    public void initialize() {
        chargerCartesCategories();
    }

    private void chargerCartesCategories() {
        cardsContainer.getChildren().clear();
        try {
            List<Categorie_cours> categories = categorieCoursService.afficher();

            for (Categorie_cours cat : categories) {
                VBox card = createCategoryCard(cat);
                cardsContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur BD", "Impossible de charger les catégories.");
        }
    }

    private VBox createCategoryCard(Categorie_cours cat) {
        int index = cardIndex % gradients.length;
        cardIndex++;

        VBox card = new VBox();
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
                "-fx-cursor: hand;");

        // Header
        javafx.scene.layout.StackPane header = new javafx.scene.layout.StackPane();
        header.setPrefHeight(120);
        header.setStyle("-fx-background-color: " + gradients[index] + ";" +
                "-fx-background-radius: 15 15 0 0;");

        Label titreHeader = new Label(cat.getTitre());
        titreHeader.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        titreHeader.setWrapText(true);
        titreHeader.setAlignment(Pos.CENTER);
        header.getChildren().add(titreHeader);

        // Body
        VBox body = new VBox(10);
        body.setStyle("-fx-padding: 20;");

        // Badge niveau
        String niveau = cat.getNiveau() != null ? cat.getNiveau().toUpperCase() : "INCONNU";
        Label badge = new Label("🔥 " + niveau);
        badge.setStyle("-fx-background-color: " + badgeBg[index] + ";" +
                "-fx-background-radius: 20; -fx-padding: 5 15 5 15;" +
                "-fx-font-size: 12px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + badgeColors[index] + ";");

        // Description
        String rawDesc = cat.getDescription();
        if(rawDesc == null) rawDesc = "";
        String desc = rawDesc.length() > 100 ? rawDesc.substring(0, 100) + "..." : rawDesc;
        Label description = new Label(desc);
        description.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        description.setWrapText(true);

        Label dateView = new Label("📅 Publié le : " + (cat.getDatepublication() != null ? cat.getDatepublication().toString() : "N/A"));
        dateView.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        // Actions
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setStyle("-fx-padding: 15 0 0 0;");

        Button btnModifier = new Button("✍ Modifier");
        btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> onModifierCategorieClick(cat, e));

        Button btnSupprimer = new Button("🗑 Supp");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> onSupprimerCategorie(cat));

        actionsBox.getChildren().addAll(btnModifier, btnSupprimer);

        body.getChildren().addAll(badge, description, dateView, actionsBox);
        card.getChildren().addAll(header, body);

        return card;
    }

    @FXML
    void onAjouterCategorieClick(ActionEvent event) {
        try {
            MainFX.chargerPage("/PageAdminAjouterCategorie.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onModifierCategorieClick(Categorie_cours cat, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageAdminModifierCategorie.fxml"));
            Parent root = loader.load();

            PageAdminModifierCategorieController controller = loader.getController();
            controller.initData(cat);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 600));
            stage.setTitle("Admin - Modifier Categorie");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSupprimerCategorie(Categorie_cours cat) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la catégorie : " + cat.getTitre());
        alert.setContentText("Attention : La suppression d'une catégorie risque d'impacter les cours associés. Êtes-vous sûr ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categorieCoursService.supprimer(cat.getId());
                chargerCartesCategories();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la catégorie.");
            }
        }
    }

    @FXML
    void onRetourDashboardClick(ActionEvent event) {
        try {
            MainFX.chargerPage("/AdminDashboard.fxml");
        } catch (Exception e) {
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

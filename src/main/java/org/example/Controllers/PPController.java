package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.Entities.Categorie_cours;
import org.example.MainFX;
import org.example.Services.CategorieCoursService;
import java.sql.SQLException;
import java.util.List;

public class PPController {

    @FXML private FlowPane cardsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categorieCombo;

    private CategorieCoursService service = new CategorieCoursService();
    private List<Categorie_cours> toutesLesCategories;
    @FXML private ComboBox<String> triCombo;

    // Couleurs pour les cards
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

    @FXML
    public void initialize() {
        try {
            toutesLesCategories = service.afficher();
            chargerCategories();
            afficherCards(toutesLesCategories);

            // Recherche
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filtrer(newVal, categorieCombo.getValue());
            });

            // Filtre ComboBox
            categorieCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                filtrer(searchField.getText(), newVal);
            });
            // Options de tri
            triCombo.getItems().addAll(
                    "Plus récents",
                    "Plus anciens",
                    "A → Z",
                    "Z → A"
            );
            triCombo.setValue("Plus récents");

            // Écoute le tri
            triCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                trierCategories(newVal);
            });

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    public void ouvrirCours() {
        try {
            MainFX.chargerPageAvecCategorie("/PageCours.fxml", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ouvrirAccueilPrincipal() {
        try {
            MainFX.chargerPage("/PageAccueil.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ouvrirEvenements() {
        try {
            MainFX.chargerPage("/InscriptionEvent.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void ouvrirOpportunites() {
        try {
            MainFX.chargerPage("/Stages.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void ouvrirQuiz() {
        try {
            // Cela ramène à la fameuse page StudentQuizDashboard
            MainFX.chargerPage("/StudentQuizDashboard.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void chargerCategories() {
        categorieCombo.getItems().add("Toutes les catégories");
        for (Categorie_cours c : toutesLesCategories) {
            categorieCombo.getItems().add(c.getTitre());
        }
    }

    private void afficherCards(List<Categorie_cours> liste) {
        cardsContainer.getChildren().clear();
        int i = 0;
        for (Categorie_cours c : liste) {
            int index = i % gradients.length;
            VBox card = creerCard(c, index);
            cardsContainer.getChildren().add(card);
            i++;
        }
    }

    private VBox creerCard(Categorie_cours c, int index) {
        // Container principal
        VBox card = new VBox();
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        // Partie haute colorée
        StackPane header = new StackPane();
        header.setPrefHeight(160);
        header.setStyle("-fx-background-color: " + gradients[index] + ";" +
                "-fx-background-radius: 15 15 0 0;");

        // Badge niveau en haut
        Label badgeHaut = new Label(emojis[index] + " " + c.getNiveau());
        badgeHaut.setStyle("-fx-background-color: rgba(255,255,255,0.3);" +
                "-fx-background-radius: 20; -fx-padding: 5 12 5 12;" +
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane.setAlignment(badgeHaut, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(badgeHaut, new javafx.geometry.Insets(12, 0, 0, 12));

        // Titre dans le header
        Label titreHeader = new Label(c.getTitre());
        titreHeader.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        titreHeader.setWrapText(true);

        header.getChildren().addAll(titreHeader, badgeHaut);

        // Partie basse blanche
        VBox body = new VBox(10);
        body.setStyle("-fx-padding: 15;");

        Label titre = new Label(c.getTitre());
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");
        titre.setWrapText(true);

        Label description = new Label(c.getDescription());
        description.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        description.setWrapText(true);

        Label badgeBas = new Label(emojis[index] + " " + c.getNiveau());
        badgeBas.setStyle("-fx-background-color: " + badgeBg[index] + ";" +
                "-fx-background-radius: 10; -fx-padding: 5 12 5 12;" +
                "-fx-font-size: 12px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + badgeColors[index] + ";");

        body.getChildren().addAll(titre, description, badgeBas);
        card.getChildren().addAll(header, body);

        // ✅ Click sur la card catégorie
        card.setStyle(card.getStyle() + "-fx-cursor: hand;");
        card.setOnMouseClicked(event -> {
            try {
                MainFX.chargerPageAvecCategorie("/PageCours.fxml", c.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return card;
    }

    private void filtrer(String recherche, String categorie) {
        List<Categorie_cours> filtrees = toutesLesCategories.stream()
                .filter(c -> {
                    boolean matchRecherche = recherche == null || recherche.isEmpty() ||
                            c.getTitre().toLowerCase().contains(recherche.toLowerCase());
                    boolean matchCategorie = categorie == null ||
                            categorie.equals("Toutes les catégories") ||
                            c.getTitre().equals(categorie);
                    return matchRecherche && matchCategorie;
                })
                .toList();
        afficherCards(filtrees);
    }

    private void trierCategories(String tri) {
        List<Categorie_cours> triees = new java.util.ArrayList<>(toutesLesCategories);
        switch (tri) {
            case "A → Z" -> triees.sort((a, b) ->
                    a.getTitre().compareToIgnoreCase(b.getTitre()));
            case "Z → A" -> triees.sort((a, b) ->
                    b.getTitre().compareToIgnoreCase(a.getTitre()));
            case "Plus récents" -> triees.sort((a, b) ->
                    b.getDatepublication().compareTo(a.getDatepublication()));
            case "Plus anciens" -> triees.sort((a, b) ->
                    a.getDatepublication().compareTo(b.getDatepublication()));
        }
        afficherCards(triees);
    }

}
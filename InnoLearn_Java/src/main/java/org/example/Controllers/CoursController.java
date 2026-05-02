package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.Entities.Cours;
import org.example.Services.CoursService;
import org.example.Services.FavorisService;
import org.example.MainFX;
import java.sql.SQLException;
import java.util.List;

public class CoursController {

    @FXML
    private FlowPane coursContainer;
    @FXML
    private Label totalLabel;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> triCombo;
    @FXML
    private ComboBox<String> niveauCombo;

    private CoursService service = new CoursService();
    private FavorisService favorisService = new FavorisService();
    private List<Cours> tousLesCours;
    private List<Integer> mesFavoris;

    private String[] gradients = {
            "linear-gradient(to bottom right, #4361ee, #7209b7)",
            "linear-gradient(to bottom right, #7209b7, #f72585)",
            "linear-gradient(to bottom right, #f72585, #ff6b6b)",
            "linear-gradient(to bottom right, #4cc9f0, #4361ee)",
            "linear-gradient(to bottom right, #06d6a0, #4cc9f0)",
            "linear-gradient(to bottom right, #f8961e, #f72585)"
    };
    private String[] badgeColors = { "#4361ee", "#f72585", "#ff6b6b", "#4cc9f0", "#06d6a0", "#f8961e" };
    private String[] badgeBg = { "#e8e8ff", "#ffe8f5", "#fff0e8", "#e8f8ff", "#e8fff8", "#fff5e8" };
    private String[] emojis = { "🎯", "🚀", "⭐", "💡", "🔥", "📚" };

    @FXML
    public void initialize() {
        try {
            tousLesCours = service.afficher();
            mesFavoris = favorisService.getTousLesFavoris();
            totalLabel.setText(tousLesCours.size() + " cours");
            setupSearchAndSort();
            afficherCards(tousLesCours);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (niveauCombo != null) {
            niveauCombo.getItems().addAll("Tous niveaux", "Débutant", "Intermédiaire", "Avancé");
            niveauCombo.setValue("Tous niveaux");
        }
    }

    // ✅ Appelée depuis MainFX avec un categorieId
    public void initAvecCategorie(int categorieId) {
        try {
            mesFavoris = favorisService.getTousLesFavoris();
            if (categorieId == -1) {
                tousLesCours = service.afficher();
            } else {
                tousLesCours = service.afficherParCategorie(categorieId);
            }
            totalLabel.setText(tousLesCours.size() + " cours");
            setupSearchAndSort();
            afficherCards(tousLesCours);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchAndSort() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrerEtTrier());
        }
        if (triCombo != null) {
            if (triCombo.getItems().isEmpty()) {
                triCombo.getItems().addAll("Plus récents", "Plus anciens", "A → Z", "Z → A");
                triCombo.setValue("Plus récents");
            }
            triCombo.valueProperty().addListener((obs, oldVal, newVal) -> filtrerEtTrier());
        }
    }

    private void filtrerEtTrier() {
        if (tousLesCours == null)
            return;

        String recherche = searchField != null ? searchField.getText() : "";
        String tri = triCombo != null ? triCombo.getValue() : "Plus récents";

        List<Cours> filtres = tousLesCours.stream()
                .filter(c -> recherche == null || recherche.isEmpty() ||
                        (c.getNom() != null && c.getNom().toLowerCase().contains(recherche.toLowerCase())) ||
                        (c.getDescription() != null
                                && c.getDescription().toLowerCase().contains(recherche.toLowerCase())))
                .collect(java.util.stream.Collectors.toList());

        if (tri != null) {
            switch (tri) {
                case "A → Z":
                    filtres.sort((a, b) -> a.getNom().compareToIgnoreCase(b.getNom()));
                    break;
                case "Z → A":
                    filtres.sort((a, b) -> b.getNom().compareToIgnoreCase(a.getNom()));
                    break;
                case "Plus récents":
                    filtres.sort((a, b) -> {
                        if (a.getDateCreation() == null || b.getDateCreation() == null)
                            return 0;
                        return b.getDateCreation().compareTo(a.getDateCreation());
                    });
                    break;
                case "Plus anciens":
                    filtres.sort((a, b) -> {
                        if (a.getDateCreation() == null || b.getDateCreation() == null)
                            return 0;
                        return a.getDateCreation().compareTo(b.getDateCreation());
                    });
                    break;
            }
        }

        totalLabel.setText(filtres.size() + " cours");
        afficherCards(filtres);
    }

    private void afficherCards(List<Cours> liste) {
        coursContainer.getChildren().clear();
        int i = 0;
        for (Cours c : liste) {
            int index = i % gradients.length;
            VBox card = creerCard(c, index);
            coursContainer.getChildren().add(card);
            i++;
        }
    }

    private VBox creerCard(Cours c, int index) {
        VBox card = new VBox();
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
                "-fx-cursor: hand;");

        // Header coloré
        StackPane header = new StackPane();
        header.setPrefHeight(180);
        header.setStyle("-fx-background-color: " + gradients[index] + ";" +
                "-fx-background-radius: 15 15 0 0;");

        Label titreHeader = new Label(c.getNom());
        titreHeader.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        titreHeader.setWrapText(true);
        titreHeader.setAlignment(javafx.geometry.Pos.CENTER);

        // Coeur de favori
        if (mesFavoris != null && mesFavoris.contains(c.getId())) {
            Label coeur = new Label("❤");
            coeur.setStyle(
                    "-fx-text-fill: #ff3333; -fx-font-size: 26px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 4, 0, 0, 2);");
            StackPane.setAlignment(coeur, javafx.geometry.Pos.TOP_RIGHT);
            StackPane.setMargin(coeur, new javafx.geometry.Insets(10, 15, 0, 0));
            header.getChildren().addAll(titreHeader, coeur);
        } else {
            header.getChildren().add(titreHeader);
        }

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
        String desc = c.getDescription().length() > 80 ? c.getDescription().substring(0, 80) + "..."
                : c.getDescription();
        Label description = new Label(desc);
        description.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        description.setWrapText(true);

        // Durée
        Label duree = new Label("⏱ " + c.getDuree() + " Heures");
        duree.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Enseignant
        Label enseignant = new Label("👨‍🏫 " + c.getEnseignant());
        enseignant.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // ✅ Bouton Détails → ouvre la page détail
        Button detailsBtn = new Button("Détails →");
        detailsBtn.setStyle("-fx-background-color: " + badgeColors[index] + ";" +
                "-fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 20; -fx-padding: 8 20 8 20;" +
                "-fx-cursor: hand;");

        detailsBtn.setOnAction(event -> ouvrirDetail(c));

        body.getChildren().addAll(badge, titre, description, duree, enseignant, detailsBtn);
        card.getChildren().addAll(header, body);

        // ✅ Click sur toute la card aussi
        card.setOnMouseClicked(event -> ouvrirDetail(c));

        return card;
    }

    // ✅ Navigation vers la page détail
    private void ouvrirDetail(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/PageDetailCours.fxml"));
            Scene scene = new Scene(loader.load());
            DetailCoursController controller = loader.getController();
            controller.initAvecCours(cours);
            // Récupérer le stage via la scène actuelle
            Stage stage = (Stage) coursContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onShowRecommendationAI() {
        // 1. Récupérer le mot-clé tapé (si vide, on dit "Général")
        String motCle = (searchField != null && !searchField.getText().isEmpty())
                ? searchField.getText()
                : "Général";

        // 2. Récupérer dynamiquement le niveau choisi dans niveauCombo
        String niveauChoisi = (niveauCombo != null && niveauCombo.getValue() != null)
                ? niveauCombo.getValue()
                : "Tous niveaux";

        if (tousLesCours == null || tousLesCours.isEmpty())
            return;

        // Message de chargement
        totalLabel.setText("L'IA de Groq analyse vos goûts... 🤖");

        // 3. Extraire les noms des cours que l'étudiant a mis en favoris !
        String mesCoursAimes = "";
        if (mesFavoris != null && !mesFavoris.isEmpty()) {
            mesCoursAimes = tousLesCours.stream()
                    .filter(c -> mesFavoris.contains(c.getId()))
                    .map(c -> c.getNom())
                    .collect(java.util.stream.Collectors.joining(", "));
        }
        final String finalCoursAimes = mesCoursAimes;

        // 4. Faire l'appel IA en arrière-plan
        new Thread(() -> {
            org.example.Services.GroqRecommendationService aiService = new org.example.Services.GroqRecommendationService();
            // Demande la recommandation avec les filtres
            String resultatAI = aiService.recommanderParCriteres(motCle, niveauChoisi, finalCoursAimes, tousLesCours);

            // 4. Modifier l'interface avec le résultat (Thread principal)
            javafx.application.Platform.runLater(() -> {
                if (resultatAI != null && !resultatAI.isEmpty()) {

                    // Nettoyage extrême : on efface TOUT ce qui n'est pas un chiffre ou une virgule
                    // (au cas où l'IA bavarde quand même)
                    String stringNettoye = resultatAI.replaceAll("[^0-9,]", "");

                    if (stringNettoye.isEmpty()) {
                        totalLabel.setText("Aucun résultat ne correspond à cette recherche.");
                        return;
                    }

                    // Convertir "5,12" en liste d'entiers [5, 12]
                    java.util.List<Integer> idsRecommandes = java.util.Arrays.stream(stringNettoye.split(","))
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt)
                            .collect(java.util.stream.Collectors.toList());

                    // Garder uniquement les cours dont l'ID est dans la liste recommandée
                    java.util.List<Cours> coursRecommandes = tousLesCours.stream()
                            .filter(c -> idsRecommandes.contains(c.getId()))
                            .collect(java.util.stream.Collectors.toList());

                    // Afficher !
                    totalLabel.setText("✨ " + coursRecommandes.size() + " recommandations de l'IA trouvées !");
                    afficherCards(coursRecommandes);

                } else {
                    totalLabel.setText("L'IA n'a pas pu générer de recommandations.");
                }
            });

        }).start();
    }

    @FXML
    public void retour() {
        try {
            MainFX.chargerPage("/PageEtudiant.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ouvrirAccueil() {
        try {
            MainFX.chargerPage("/PageEtudiant.fxml");
        } catch (Exception e) {
            e.printStackTrace();
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
}

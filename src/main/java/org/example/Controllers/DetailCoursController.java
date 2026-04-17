package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import org.example.Entities.Cours;
import org.example.MainFX;
import org.example.Services.FavorisService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailCoursController {

    @FXML private Label badgeNiveau;
    @FXML private Label titreLabel;
    @FXML private Label descCourteLabel;
    @FXML private Label enseignantLabel;
    @FXML private Label dureeLabel;
    @FXML private Label dureeVideoLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dureeInfoLabel;
    @FXML private Label enseignantNomLabel;
    @FXML private Label typeMediaLabel;
    @FXML private Label slugLabel;
    @FXML private Label dateLabel;
    @FXML private VBox modulesContainer;
    @FXML private Button btnFavoris;
    @FXML private WebView videoWebView;

    private boolean isFavori = false; // Etat local du favori 
    private Cours coursActuel;
    private FavorisService favorisService = new FavorisService();

    // Couleurs par niveau
    private String getNiveauColor(String niveau) {
        if (niveau == null) return "#3a3af0";
        return switch (niveau.toLowerCase()) {
            case "débutant", "debutant" -> "#06d6a0";
            case "intermédiaire", "intermediaire" -> "#f8961e";
            case "avancé", "avance"  -> "#f72585";
            default -> "#3a3af0";
        };
    }

    /**
     * Appelé depuis CoursController après le chargement du FXML
     */
    public void initAvecCours(Cours cours) {
        this.coursActuel = cours;

        // --- Vérifier si favori en base de données ---
        try {
            isFavori = favorisService.estFavori(cours.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mettreAJourBoutonFavorisUI();

        // --- Badge niveau ---
        String niveau = cours.getNiveau() != null ? cours.getNiveau().toUpperCase() : "N/A";
        badgeNiveau.setText(niveau);
        badgeNiveau.setStyle(
                "-fx-background-color: " + getNiveauColor(cours.getNiveau()) + ";" +
                        "-fx-text-fill: white; -fx-background-radius: 6;" +
                        "-fx-padding: 5 16 5 16; -fx-font-size: 11px; -fx-font-weight: bold;"
        );

        // --- Titre ---
        titreLabel.setText(cours.getNom());

        // --- Description courte (50 premiers mots) ---
        String desc = cours.getDescription();
        String descCourte = desc.length() > 120 ? desc.substring(0, 120) + "..." : desc;
        descCourteLabel.setText(descCourte);

        // --- Stats ---
        enseignantLabel.setText("Prof. " + cours.getEnseignant());
        dureeLabel.setText(cours.getDuree() + "h");
        dureeVideoLabel.setText(cours.getDuree() + ":00");
        dureeInfoLabel.setText(cours.getDuree() + " heures de contenu");

        // --- Description complète ---
        descriptionLabel.setText(cours.getDescription());

        // --- Sidebar formateur ---
        enseignantNomLabel.setText("Prof. " + cours.getEnseignant());

        // --- Infos techniques ---
        typeMediaLabel.setText(cours.getTypeMedia() != null ? cours.getTypeMedia() : "Vidéo");
        slugLabel.setText(cours.getSlug() != null ? cours.getSlug() : "-");
        if (cours.getDateCreation() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateLabel.setText(cours.getDateCreation().format(fmt));
        } else {
            dateLabel.setText("-");
        }

        // --- Chargement Vidéo dans WebView ---
        if (videoWebView != null && cours.getMediaUrl() != null && !cours.getMediaUrl().isEmpty()) {
            try {
                // Si c'est un lien YouTube normal, on essaie de le transformer en lien Embed
                String url = cours.getMediaUrl();
                if(url.contains("watch?v=")) {
                    url = url.replace("watch?v=", "embed/");
                }
                videoWebView.getEngine().load(url);
            } catch (Exception e) {
                System.out.println("Erreur de chargement de la vidéo : " + e.getMessage());
            }
        }

        // --- Modules (simulés intelligemment selon la durée) ---
        genererModules(cours);
    }

    /**
     * Génère des modules fictifs cohérents basés sur les données du cours
     */
    private void genererModules(Cours cours) {
        modulesContainer.getChildren().clear();

        int dureeTotal = cours.getDuree();
        int nbModules = Math.max(2, Math.min(5, dureeTotal / 5)); // entre 2 et 5 modules
        int dureeParModule = dureeTotal / nbModules;

        String[] titresModules = {
                "Introduction & Fondamentaux",
                "Concepts Intermédiaires",
                cours.getNom(),  // module principal = le cours lui-même
                "Pratique & Projets",
                "Révision & Certification"
        };

        for (int i = 0; i < nbModules; i++) {
            boolean isActive = (i == nbModules - 1); // dernier = actif (EN LECTURE)
            HBox moduleRow = creerModuleRow(
                    "Module " + (i + 1),
                    titresModules[Math.min(i, titresModules.length - 1)],
                    dureeParModule,
                    isActive,
                    i
            );
            modulesContainer.getChildren().add(moduleRow);
        }
    }

    private HBox creerModuleRow(String moduleNum, String titre, int duree, boolean isActive, int index) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle(
                isActive
                        ? "-fx-background-color: #f0f0ff; -fx-background-radius: 12; -fx-border-color: #3a3af0; -fx-border-radius: 12; -fx-border-width: 1;"
                        : "-fx-background-color: #fafafa; -fx-background-radius: 12; -fx-border-color: #e8e8e8; -fx-border-radius: 12; -fx-border-width: 1;"
        );

        // Bullet circle
        StackPane bullet = new StackPane();
        bullet.setPrefSize(28, 28);
        bullet.setMinSize(28, 28);
        if (isActive) {
            bullet.setStyle("-fx-background-color: #3a3af0; -fx-background-radius: 14;");
            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
            bullet.getChildren().add(dot);
        } else {
            bullet.setStyle("-fx-background-color: transparent; -fx-border-color: #c0c0d0; -fx-border-radius: 14; -fx-border-width: 2;");
        }

        // Text info
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        if (isActive) {
            Label enLecture = new Label("EN LECTURE");
            enLecture.setStyle(
                    "-fx-background-color: #3a3af0; -fx-text-fill: white;" +
                            "-fx-background-radius: 4; -fx-padding: 2 7 2 7;" +
                            "-fx-font-size: 9px; -fx-font-weight: bold;"
            );
            titleRow.getChildren().add(enLecture);
        }

        Label titreLabel = new Label(titre);
        titreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + (isActive ? "#3a3af0" : "#1a1a2e") + ";");
        titreLabel.setWrapText(true);
        titleRow.getChildren().add(titreLabel);

        Label sousTitre = new Label(moduleNum + " • " + duree + " Heures");
        sousTitre.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        info.getChildren().addAll(titleRow, sousTitre);

        // Voir le module button (only for inactive)
        if (!isActive) {
            Label voirBtn = new Label("Voir le module →");
            voirBtn.setStyle("-fx-text-fill: #3a3af0; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
            row.getChildren().addAll(bullet, info, voirBtn);
        } else {
            row.getChildren().addAll(bullet, info);
        }

        return row;
    }

    @FXML
    public void retour() {
        try {
            MainFX.chargerPage("/PageCours.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onToggleFavoris() {
        if (coursActuel == null) return;
        try {
            if (isFavori) {
                favorisService.supprimerFavori(coursActuel.getId());
                isFavori = false;
            } else {
                favorisService.ajouterFavori(coursActuel.getId());
                isFavori = true;
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Favoris");
                alert.setContentText("Le cours a été ajouté à vos favoris avec succès !");
                alert.show();
            }
            mettreAJourBoutonFavorisUI();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour des favoris.");
            alert.show();
        }
    }

    private void mettreAJourBoutonFavorisUI() {
        if (isFavori) {
            btnFavoris.setText("Retirer des favoris ♥");
            btnFavoris.setStyle("-fx-background-color: #ffe6e6; " +
                    "-fx-border-color: #ff3333; -fx-border-radius: 10; " +
                    "-fx-text-fill: #ff3333; -fx-font-weight: bold; " +
                    "-fx-font-size: 13px; -fx-background-radius: 10; " +
                    "-fx-padding: 12 20 12 20; -fx-cursor: hand;");
        } else {
            btnFavoris.setText("Ajouter aux favoris ♡");
            btnFavoris.setStyle("-fx-background-color: transparent; " +
                    "-fx-border-color: #3a3af0; -fx-border-radius: 10; " +
                    "-fx-text-fill: #3a3af0; -fx-font-weight: bold; " +
                    "-fx-font-size: 13px; -fx-background-radius: 10; " +
                    "-fx-padding: 12 20 12 20; -fx-cursor: hand;");
        }
    }
}

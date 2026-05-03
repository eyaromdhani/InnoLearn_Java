package org.example.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Entities.Categorie_cours;
import org.example.Entities.Cours;
import org.example.Services.CategorieCoursService;
import org.example.Services.CoursService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PageAjouterCoursController {

    @FXML
    private TextField tfTitre;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<String> cbTypeMedia;
    @FXML
    private TextField tfMediaUrl;
    @FXML
    private TextField tfDuree;
    @FXML
    private ComboBox<String> cbNiveau;
    @FXML
    private ComboBox<Categorie_cours> cbCategorie;

    private CoursService coursService = new CoursService();
    private CategorieCoursService categorieCoursService = new CategorieCoursService();

    @FXML
    public void initialize() {
        chargerCategories();
        
        // Convertisseur pour afficher joliment la catégorie (titre uniquement)
        cbCategorie.setConverter(new javafx.util.StringConverter<Categorie_cours>() {
            @Override
            public String toString(Categorie_cours object) {
                return object != null ? object.getTitre() : "";
            }

            @Override
            public Categorie_cours fromString(String string) {
                return null;
            }
        });

        // Initialiser les niveaux
        ObservableList<String> niveaux = FXCollections.observableArrayList("Débutant", "Intermédiaire", "Avancé");
        cbNiveau.setItems(niveaux);

        // Initialiser les types de média
        ObservableList<String> types = FXCollections.observableArrayList("Vidéo", "Document", "Lien");
        cbTypeMedia.setItems(types);
    }

    private void chargerCategories() {
        try {
            List<Categorie_cours> categories = categorieCoursService.afficher();
            ObservableList<Categorie_cours> observableList = FXCollections.observableArrayList(categories);
            cbCategorie.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories.");
        }
    }

    @FXML
    void onEnregistrerClick(ActionEvent event) {
        if (!validerSaisie()) {
            return;
        }

        try {
            String titre = tfTitre.getText();
            String description = taDescription.getText();
            String typeMedia = cbTypeMedia.getValue();
            String mediaUrl = tfMediaUrl.getText();
            int duree = Integer.parseInt(tfDuree.getText());
            String niveau = cbNiveau.getValue();
            Categorie_cours categorie = cbCategorie.getValue();

            // Création automatique du slug simple
            String slug = titre.toLowerCase().replace(" ", "-");
            int enseignantId = org.example.utils.Session.getUserId();

            Cours nouveauCours = new Cours(titre, description, slug, typeMedia, mediaUrl, duree, niveau, LocalDateTime.now(), enseignantId, categorie.getId());

            coursService.ajouter(nouveauCours);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Cours ajouté avec succès.");
            retourListeCours(event);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur BD", "Erreur lors de l'enregistrement du cours.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur saisie", "La durée doit être un nombre entier.");
        }
    }

    @FXML
    void onAnnulerClick(ActionEvent event) {
        retourListeCours(event);
    }

    private boolean validerSaisie() {
        if (tfTitre.getText().isEmpty() || taDescription.getText().isEmpty() || 
            cbTypeMedia.getValue() == null || tfDuree.getText().isEmpty() || 
            cbNiveau.getValue() == null || cbCategorie.getValue() == null) {
            
            showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs du formulaire.");
            return false;
        }
        return true;
    }

    private void retourListeCours(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PageEnseignant.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Espace Enseignant - Mes Cours");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}

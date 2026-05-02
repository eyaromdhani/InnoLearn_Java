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
import org.example.MainFX;
import org.example.Services.CategorieCoursService;
import org.example.Services.CoursService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PageAdminModifierCoursController {

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
    private Cours coursAModifier;

    @FXML
    public void initialize() {
        chargerCategories();

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

        ObservableList<String> niveaux = FXCollections.observableArrayList("Débutant", "Intermédiaire", "Avancé");
        cbNiveau.setItems(niveaux);

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
        }
    }

    public void initData(Cours cours) {
        this.coursAModifier = cours;
        
        tfTitre.setText(cours.getNom());
        taDescription.setText(cours.getDescription());
        cbTypeMedia.setValue(cours.getTypeMedia());
        tfMediaUrl.setText(cours.getMediaUrl());
        tfDuree.setText(String.valueOf(cours.getDuree()));
        cbNiveau.setValue(cours.getNiveau());

        // Préselectionner la catégorie
        for (Categorie_cours cat : cbCategorie.getItems()) {
            if (cat.getId() == cours.getCategorieCourId()) {
                cbCategorie.setValue(cat);
                break;
            }
        }
    }

    @FXML
    void onMettreAJourClick(ActionEvent event) {
        if (!validerSaisie()) return;

        try {
            coursAModifier.setNom(tfTitre.getText());
            coursAModifier.setDescription(taDescription.getText());
            coursAModifier.setTypeMedia(cbTypeMedia.getValue());
            coursAModifier.setMediaUrl(tfMediaUrl.getText());
            coursAModifier.setDuree(Integer.parseInt(tfDuree.getText()));
            coursAModifier.setNiveau(cbNiveau.getValue());
            coursAModifier.setCategorieCourId(cbCategorie.getValue().getId());
            
            coursAModifier.setSlug(tfTitre.getText().toLowerCase().replace(" ", "-"));

            coursService.modifier(coursAModifier);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Cours mis à jour avec succès.");
            retourListeCours(event);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur BD", "Erreur lors de la mise à jour du cours.");
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
            MainFX.chargerPage("/PageAdminCours.fxml");
        } catch (Exception e) {
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

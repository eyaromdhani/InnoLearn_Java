package org.example.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.Entities.Categorie_cours;
import org.example.MainFX;
import org.example.Services.CategorieCoursService;

import java.sql.SQLException;
import java.time.LocalDate;

public class PageAdminAjouterCategorieController {

    @FXML
    private TextField tfTitre;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<String> cbNiveau;

    private CategorieCoursService categorieCoursService = new CategorieCoursService();

    @FXML
    public void initialize() {
        ObservableList<String> niveaux = FXCollections.observableArrayList("Débutant", "Intermédiaire", "Avancé");
        cbNiveau.setItems(niveaux);
    }

    @FXML
    void onEnregistrerClick(ActionEvent event) {
        if (!validerSaisie()) return;

        try {
            String titre = tfTitre.getText();
            String description = taDescription.getText();
            String niveau = cbNiveau.getValue();

            Categorie_cours nouvelleCategorie = new Categorie_cours(titre, description, niveau, LocalDate.now());

            categorieCoursService.ajouter(nouvelleCategorie);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée avec succès.");
            retourListeCategories();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur BD", "Erreur lors de l'enregistrement de la catégorie.");
        }
    }

    @FXML
    void onAnnulerClick(ActionEvent event) {
        retourListeCategories();
    }

    private boolean validerSaisie() {
        if (tfTitre.getText().isEmpty() || taDescription.getText().isEmpty() || cbNiveau.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs du formulaire.");
            return false;
        }
        return true;
    }

    private void retourListeCategories() {
        try {
            MainFX.chargerPage("/PageAdminCategories.fxml");
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

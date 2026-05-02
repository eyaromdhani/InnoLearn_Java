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

public class PageAdminModifierCategorieController {

    @FXML
    private TextField tfTitre;
    @FXML
    private TextArea taDescription;
    @FXML
    private ComboBox<String> cbNiveau;

    private CategorieCoursService categorieCoursService = new CategorieCoursService();
    private Categorie_cours categorieAModifier;

    @FXML
    public void initialize() {
        ObservableList<String> niveaux = FXCollections.observableArrayList("Débutant", "Intermédiaire", "Avancé");
        cbNiveau.setItems(niveaux);
    }

    public void initData(Categorie_cours cat) {
        this.categorieAModifier = cat;
        tfTitre.setText(cat.getTitre());
        taDescription.setText(cat.getDescription());
        cbNiveau.setValue(cat.getNiveau());
    }

    @FXML
    void onMettreAJourClick(ActionEvent event) {
        if (!validerSaisie()) return;

        try {
            categorieAModifier.setTitre(tfTitre.getText());
            categorieAModifier.setDescription(taDescription.getText());
            categorieAModifier.setNiveau(cbNiveau.getValue());

            categorieCoursService.modifier(categorieAModifier);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie mise à jour avec succès.");
            retourListeCategories();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur BD", "Erreur lors de la mise à jour de la catégorie.");
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

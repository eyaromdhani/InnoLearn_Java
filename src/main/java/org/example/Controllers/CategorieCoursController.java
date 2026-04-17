package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Entities.Categorie_cours;
import org.example.Services.CategorieCoursService;
import java.sql.SQLException;

public class CategorieCoursController {

    @FXML private TextField titreField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> niveauCombo;
    @FXML private DatePicker datePicker;

    private CategorieCoursService service = new CategorieCoursService();

    @FXML
    public void initialize() {
        niveauCombo.getItems().addAll("Débutant", "Intermédiaire", "Avancé");
    }

    @FXML
    public void ajouter() {
        try {
            Categorie_cours c = new Categorie_cours(
                    titreField.getText(),
                    descriptionField.getText(),
                    niveauCombo.getValue(),
                    datePicker.getValue()
            );
            service.ajouter(c);
            System.out.println("Ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void annuler() {
        titreField.clear();
        descriptionField.clear();
        niveauCombo.setValue(null);
        datePicker.setValue(null);
    }
}
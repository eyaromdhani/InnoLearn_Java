package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Formulaire;
import services.ServiceFormulaire;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import utils.VoiceService;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {

    @FXML
    private FlowPane quizGrid;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private Button micButton;

    @FXML
    private Label vocalStatus;

    private ServiceFormulaire serviceFormulaire = new ServiceFormulaire();
    private java.util.List<Formulaire> allQuizzes = new java.util.ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sortComboBox.getItems().addAll("Titre (A-Z)", "Titre (Z-A)", "Durée (Croissant)", "Durée (Décroissant)");
        
        loadQuizzes();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        sortComboBox.setOnAction(e -> applyFilters());
    }

    @FXML
    private void handleVoiceSearch() {
        vocalStatus.setVisible(true);
        VoiceService.listen().thenAccept(text -> {
            Platform.runLater(() -> {
                if (text != null && !text.isEmpty()) {
                    searchField.setText(text);
                }
                vocalStatus.setVisible(false);
            });
        });
    }

    private void loadQuizzes() {
        try {
            allQuizzes = serviceFormulaire.afficher();
            applyFilters();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        quizGrid.getChildren().clear();
        String search = searchField.getText().toLowerCase();
        String sort = sortComboBox.getValue();

        java.util.stream.Stream<Formulaire> stream = allQuizzes.stream()
                .filter(f -> f.getTitre().toLowerCase().contains(search) || 
                            f.getDescription().toLowerCase().contains(search));

        if (sort != null) {
            switch (sort) {
                case "Titre (A-Z)": stream = stream.sorted(java.util.Comparator.comparing(Formulaire::getTitre)); break;
                case "Titre (Z-A)": stream = stream.sorted(java.util.Comparator.comparing(Formulaire::getTitre).reversed()); break;
                case "Durée (Croissant)": stream = stream.sorted(java.util.Comparator.comparingInt(Formulaire::getTempsLimite)); break;
                case "Durée (Décroissant)": stream = stream.sorted(java.util.Comparator.comparingInt(Formulaire::getTempsLimite).reversed()); break;
            }
        }

        stream.forEach(this::addQuizCard);
    }

    private void addQuizCard(Formulaire f) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/QuizCardStudent.fxml"));
            VBox card = loader.load();
            
            QuizCardStudentController controller = loader.getController();
            controller.setQuizData(f);
            
            quizGrid.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToLibrary() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/StudentLibrary.fxml"));
            Stage stage = (Stage) quizGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

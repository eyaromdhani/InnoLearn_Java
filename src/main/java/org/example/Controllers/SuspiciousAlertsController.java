package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import org.example.Entities.Formulaire;
import org.example.Entities.QuizResult;
import org.example.Services.ServiceQuizResult;

import java.sql.SQLException;
import java.util.List;

public class SuspiciousAlertsController {

    @FXML private Label quizTitleLabel;
    @FXML private Label alertCountLabel;
    @FXML private VBox alertsContainer;

    private Formulaire currentQuiz;
    private ServiceQuizResult serviceQuizResult = new ServiceQuizResult();

    public void initData(Formulaire quiz) {
        this.currentQuiz = quiz;
        quizTitleLabel.setText("Quiz : " + quiz.getTitre());
        loadAlerts();
    }

    private void loadAlerts() {
        alertsContainer.getChildren().clear();
        try {
            List<QuizResult> suspiciousResults = serviceQuizResult.getSuspiciousResults(currentQuiz.getId());
            alertCountLabel.setText(suspiciousResults.size() + " Alertes");

            for (QuizResult res : suspiciousResults) {
                alertsContainer.getChildren().add(createAlertCard(res));
            }
            
            if (suspiciousResults.isEmpty()) {
                Label noAlerts = new Label("Aucune alerte d'intégrité détectée pour ce quiz.");
                noAlerts.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic;");
                alertsContainer.setAlignment(Pos.CENTER);
                alertsContainer.getChildren().add(noAlerts);
            } else {
                alertsContainer.setAlignment(Pos.TOP_CENTER);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createAlertCard(QuizResult res) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");
        
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label name = new Label(res.getStudentName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label date = new Label(res.getCreatedAt().toString());
        date.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");
        
        header.getChildren().addAll(name, spacer, date);
        
        HBox details = new HBox(15);
        details.setAlignment(Pos.CENTER_LEFT);
        
        Label score = new Label("Score: " + res.getScore() + "/" + res.getTotalPoints());
        score.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 5 10; -fx-background-radius: 5; -fx-text-fill: #475569;");
        
        Label activity = new Label("⚠️ " + res.getSuspiciousActivity());
        activity.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        
        details.getChildren().addAll(score, activity);
        
        card.getChildren().addAll(header, details);
        return card;
    }

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/AdminQuizDashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            quizTitleLabel.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

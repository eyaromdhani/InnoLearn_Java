package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.Entities.Formulaire;
import org.example.Services.ServiceFormulaire;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import org.example.Services.GroqService;
import org.example.Services.ServiceQuestion;
import org.example.Services.ServiceQuizResult;
import org.example.utils.AlertUtils;
import javafx.application.Platform;

import java.sql.SQLException;

public class QuizCardAdminController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label questionCountLabel;

    @FXML
    private Label passesLabel;

    @FXML
    private Label failsLabel;

    @FXML
    private Pane chartPane;

    @FXML
    private javafx.scene.shape.Circle bgCircle;

    @FXML
    private Label lblAlertBadge;

    private Formulaire currentQuiz;
    private AdminQuizController parentController;
    private ServiceFormulaire serviceFormulaire = new ServiceFormulaire();
    private ServiceQuestion serviceQuestion = new ServiceQuestion();
    private ServiceQuizResult serviceQuizResult = new ServiceQuizResult();
    private GroqService groqService = new GroqService();

    public void setQuizData(Formulaire f, AdminQuizController parent) {
        this.currentQuiz = f;
        this.parentController = parent;
        
        titleLabel.setText(f.getTitre());
        descriptionLabel.setText(f.getDescription());
        timeLabel.setText(f.getTempsLimite() + " min");
        
        try {
            int count = serviceQuestion.getQuestionsByFormulaire(f.getId()).size();
            questionCountLabel.setText(count + " Questions");
            
            int passes = serviceQuizResult.getPassCount(f.getId());
            int fails = serviceQuizResult.getFailCount(f.getId());
            
            passesLabel.setText(passes + " réussis");
            failsLabel.setText(fails + " échecs");
            
            updateChart(passes, fails);

            // Handle Alert Badge
            int suspicious = serviceQuizResult.getSuspiciousCount(f.getId());
            if (suspicious > 0) {
                lblAlertBadge.setText("⚠️ " + suspicious + " ALERTS");
                lblAlertBadge.setVisible(true);
                lblAlertBadge.setManaged(true);
            } else {
                lblAlertBadge.setVisible(false);
                lblAlertBadge.setManaged(false);
            }
            
        } catch (SQLException e) {
            questionCountLabel.setText("0 Questions");
            passesLabel.setText("0 réussis");
            failsLabel.setText("0 échecs");
        }
    }

    private void updateChart(int passes, int fails) {
        chartPane.getChildren().clear();
        int total = passes + fails;

        if (total == 0) {
            bgCircle.setOpacity(0.8);
            return;
        }

        bgCircle.setOpacity(0.3); // Fade background when actual data is shown

        double passAngle = (double) passes / total * 360.0;
        double failAngle = (double) fails / total * 360.0;

        // Pass Arc (Green)
        if (passes > 0) {
            Arc passArc = new Arc(20, 20, 20, 20, 90, -passAngle);
            passArc.setType(ArcType.ROUND);
            passArc.setFill(Color.web("#10B981"));
            passArc.setStroke(null);
            chartPane.getChildren().add(passArc);
        }

        // Fail Arc (Red)
        if (fails > 0) {
            Arc failArc = new Arc(20, 20, 20, 20, 90 - passAngle, -failAngle);
            failArc.setType(ArcType.ROUND);
            failArc.setFill(Color.web("#EF4444"));
            failArc.setStroke(null);
            chartPane.getChildren().add(failArc);
        }
    }

    @FXML
    private void handleEdit() {
        parentController.openFormulaireDialog(currentQuiz);
    }

    @FXML
    private void handleDelete() {
        try {
            serviceFormulaire.supprimer(currentQuiz.getId());
            parentController.loadQuizzes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewQuestions() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ManageQuestions.fxml"));
            javafx.scene.Parent root = loader.load();
            
            ManageQuestionsController controller = loader.getController();
            controller.initData(currentQuiz);
            
            titleLabel.getScene().setRoot(root);
        } catch (Exception e) {
            System.err.println("Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAIAnalysis() {
        try {
            int passes = serviceQuizResult.getPassCount(currentQuiz.getId());
            int fails = serviceQuizResult.getFailCount(currentQuiz.getId());
            int questions = serviceQuestion.getQuestionsByFormulaire(currentQuiz.getId()).size();

            if (passes + fails == 0) {
                AlertUtils.showInfo("Analyse IA", "Il n'y a pas encore assez de résultats pour analyser ce quiz.");
                return;
            }

            // Simple loading feedback
            AlertUtils.showInfo("Analyse en cours", "L'Intelligence Artificielle analyse les résultats... Veuillez patienter.");

            groqService.getPedagogicalAnalysis(currentQuiz.getTitre(), passes, fails, questions)
                    .thenAccept(advice -> {
                        Platform.runLater(() -> {
                            AlertUtils.showInfo("Conseil Pédagogique 🤖", advice);
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            AlertUtils.showError("Erreur AI", "Impossible de générer l'analyse.");
                        });
                        return null;
                    });

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showError("Erreur", "Une erreur est survenue.");
        }
    }

    @FXML
    private void handleShowAlerts() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/SuspiciousAlertsView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            SuspiciousAlertsController controller = loader.getController();
            controller.initData(currentQuiz);
            
            titleLabel.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

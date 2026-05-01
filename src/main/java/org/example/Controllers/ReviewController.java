package org.example.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.MainFX;
import org.example.Entities.Event;
import org.example.Entities.Review;
import org.example.Services.ReviewService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewController {

    @FXML private Label lblEventTitle;
    @FXML private ListView<Review> lvReviews;
    @FXML private ComboBox<Integer> cbRating;
    @FXML private TextArea taComment;

    private ReviewService reviewService = new ReviewService();
    private Event currentEvent;
    
    // User mock (consistent with other controllers)
    private int userId = 5;
    private String userName = "Utilisateur test";

    @FXML
    public void initialize() {
        currentEvent = InscriptionEventController.selectedEventForReview;
        if (currentEvent != null) {
            lblEventTitle.setText(currentEvent.getTitre());
            chargerReviews();
        }
        cbRating.getItems().addAll(1, 2, 3, 4, 5);
        cbRating.setValue(5);
    }

    private void chargerReviews() {
        List<Review> reviews = reviewService.getReviewsByEvent(currentEvent.getId());
        lvReviews.setItems(FXCollections.observableArrayList(reviews));
    }

    @FXML
    void envoyerReview() {
        String comment = taComment.getText().trim();
        Integer rating = cbRating.getValue();

        if (comment.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez écrire un commentaire.");
            return;
        }

        Review review = new Review();
        review.setEventId(currentEvent.getId());
        review.setUserId(userId);
        review.setUserName(userName);
        review.setComment(comment);
        review.setRating(rating);
        review.setDateReview(LocalDateTime.now());

        reviewService.addReview(review);
        
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre avis a été publié !");
        taComment.clear();
        chargerReviews();
    }

    @FXML
    void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/InscriptionEvent.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) lblEventTitle.getScene().getWindow();
            stage.setTitle("Événements");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

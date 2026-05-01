package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Entities.Book;
import org.example.Services.ServiceBook;
import org.example.utils.VoiceService;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StudentLibraryController implements Initializable {

    @FXML
    private FlowPane booksGrid;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private Label vocalStatus;

    private ServiceBook serviceBook = new ServiceBook();
    private List<Book> allBooks = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sortComboBox.getItems().addAll("Titre (A-Z)", "Titre (Z-A)", "Auteur", "Annee");
        
        loadBooks();

        searchField.textProperty().addListener((obs, old, newValue) -> applyFilters());
        sortComboBox.setOnAction(e -> applyFilters());
    }

    public void loadBooks() {
        try {
            allBooks = serviceBook.afficher();
            applyFilters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        booksGrid.getChildren().clear();
        String search = searchField.getText().toLowerCase();
        String sort = sortComboBox.getValue();

        Stream<Book> stream = allBooks.stream()
                .filter(b -> b.getTitre().toLowerCase().contains(search) || 
                            b.getAuthor().toLowerCase().contains(search));

        if (sort != null) {
            switch (sort) {
                case "Titre (A-Z)": stream = stream.sorted(Comparator.comparing(Book::getTitre)); break;
                case "Titre (Z-A)": stream = stream.sorted(Comparator.comparing(Book::getTitre).reversed()); break;
                case "Auteur": stream = stream.sorted(Comparator.comparing(Book::getAuthor)); break;
                case "Annee": stream = stream.sorted(Comparator.comparing((Book b) -> b.getReleaseDate() != null ? b.getReleaseDate().getYear() : 0).reversed()); break;
            }
        }

        stream.forEach(this::addBookCard);
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

    private void addBookCard(Book b) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StudentBookCard.fxml"));
            VBox card = loader.load();
            
            StudentBookCardController controller = loader.getController();
            controller.setBookData(b, this);
            
            booksGrid.getChildren().add(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBookDetails(Book b) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookDetails.fxml"));
            Parent root = loader.load();
            
            BookDetailsController controller = loader.getController();
            controller.initData(b);

            Stage stage = (Stage) booksGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/StudentQuizDashboard.fxml"));
            Stage stage = (Stage) booksGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToQuiz() {
        handleGoBack();
    }
}

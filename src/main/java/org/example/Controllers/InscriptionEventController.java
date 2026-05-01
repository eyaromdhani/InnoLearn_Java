package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.MainFX;
import org.example.Entities.Event;
import org.example.Entities.InscriptionEvent;
import org.example.Services.EventService;
import org.example.Services.InscriptionEventService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InscriptionEventController {

    @FXML
    private FlowPane eventsGrid;

    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbCategory;
    @FXML private ComboBox<String> cbSort;
    
    // Registration inputs in modal
    @FXML private TextField tfUserName;
    @FXML private TextField tfUserEmail;

    // Modal Fields
    @FXML private AnchorPane overlayPane;
    @FXML private Label lblModalType;
    @FXML private Label lblModalTitle;
    @FXML private Label lblModalDate;
    @FXML private Label lblModalLieu;
    @FXML private Label lblModalCapacity;
    @FXML private Label lblModalDesc;
    @FXML private Button btnModalAction;

    EventService eventService = new EventService();
    InscriptionEventService inscriptionEventService = new InscriptionEventService();
    int userId = 5; // Internal user system ID
    private List<Event> allEvents;

    @FXML
    public void initialize() {
        cbCategory.getItems().addAll("Toutes les catégories", "Formation", "Conference", "Workshop", "Hackathon");
        cbCategory.setValue("Toutes les catégories");
        cbCategory.setOnAction(e -> applyFilters());

        cbSort.getItems().addAll("Date (Le plus proche)", "Titre (A-Z)", "Places (Max)");
        cbSort.setValue("Date (Le plus proche)");
        cbSort.setOnAction(e -> applyFilters());

        tfSearch.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        chargerEvents();
    }

    private void chargerEvents() {
        allEvents = eventService.getAllEvents();
        applyFilters();
    }

    private void applyFilters() {
        if (allEvents == null) return;

        String category = cbCategory.getValue();
        String searchQuery = tfSearch.getText() != null ? tfSearch.getText().toLowerCase() : "";
        String sortOption = cbSort.getValue();

        List<Event> filteredList = allEvents.stream()
                .filter(e -> {
                    boolean matchesCat = "Toutes les catégories".equals(category) || (e.getTypeEvenement() != null && e.getTypeEvenement().equalsIgnoreCase(category));
                    boolean matchesSearch = e.getTitre().toLowerCase().contains(searchQuery) || (e.getLieu() != null && e.getLieu().toLowerCase().contains(searchQuery));
                    return matchesCat && matchesSearch;
                })
                .sorted((e1, e2) -> {
                    if ("Titre (A-Z)".equals(sortOption)) {
                        return e1.getTitre().compareToIgnoreCase(e2.getTitre());
                    } else if ("Date (Le plus proche)".equals(sortOption)) {
                        if (e1.getDateDebut() == null) return 1;
                        if (e2.getDateDebut() == null) return -1;
                        return e1.getDateDebut().compareTo(e2.getDateDebut());
                    } else if ("Places (Max)".equals(sortOption)) {
                        return Integer.compare(e2.getCapacite(), e1.getCapacite());
                    }
                    return 0;
                })
                .toList();

        renderEvents(filteredList);
    }

    private void renderEvents(List<Event> events) {
        eventsGrid.getChildren().clear();
        for (Event event : events) {
            VBox card = createEventCard(event);
            eventsGrid.getChildren().add(card);
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.getStyleClass().add("card-event");
        card.setPrefWidth(220);
        card.setMaxWidth(220);

        boolean isFull = event.getCapacite() <= 0;
        boolean isEnded = event.getDateFin() != null && LocalDateTime.now().isAfter(event.getDateFin());

        if (isEnded) {
            // Card is clickable for ended events (to see reviews)
            card.setOpacity(0.85); // Slightly less ghosted
            javafx.scene.effect.ColorAdjust desaturate = new javafx.scene.effect.ColorAdjust();
            desaturate.setSaturation(-0.5); // Less gray
            card.setEffect(desaturate);
            card.setCursor(Cursor.HAND);
            card.setOnMouseClicked(e -> gotoReviews(event)); // New method
        } else if (isFull) {
            card.setOpacity(0.5);
            javafx.scene.effect.ColorAdjust desaturate = new javafx.scene.effect.ColorAdjust();
            desaturate.setSaturation(-1.0);
            card.setEffect(desaturate);
        } else {
            card.setCursor(Cursor.HAND);
            card.setOnMouseClicked(e -> showDetails(event));
        }

        HBox header = new HBox();
        boolean isWorkshop = "Workshop".equalsIgnoreCase(event.getTypeEvenement()) || "Formation".equalsIgnoreCase(event.getTypeEvenement());
        header.getStyleClass().add(isWorkshop ? "card-header-workshop" : "card-header-conference");
        
        if (isEnded) {
             header.setStyle("-fx-background-color: #666666;"); // Gray header for ended
        }
        
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox dateBox = new VBox();
        dateBox.getStyleClass().add("date-box");
        dateBox.setAlignment(Pos.CENTER);

        String day = "00";
        String month = "MMM";
        if (event.getDateDebut() != null) {
            day = String.format("%02d", event.getDateDebut().getDayOfMonth());
            month = event.getDateDebut().getMonth().name().substring(0, 3).toUpperCase();
        }

        Label lblDay = new Label(day);
        lblDay.getStyleClass().add("date-day");
        Label lblMonth = new Label(month);
        lblMonth.getStyleClass().add("date-month");
        dateBox.getChildren().addAll(lblDay, lblMonth);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label lblType = new Label(event.getTypeEvenement() != null ? event.getTypeEvenement().toUpperCase() : "EVENT");
        lblType.getStyleClass().add(isWorkshop ? "type-tag" : "type-tag-conf");

        header.getChildren().addAll(dateBox, spacer1, lblType);

        VBox content = new VBox();
        content.getStyleClass().add("card-content");
        content.setSpacing(10);

        Label lblTitle = new Label(event.getTitre());
        lblTitle.getStyleClass().add("card-title");

        Label lblMeta = new Label("📍 " + event.getLieu());
        lblMeta.getStyleClass().add("card-meta");

        HBox actions = new HBox();
        actions.setSpacing(10);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button btnAction = new Button();
        updateButtonStatus(btnAction, event);
        
        btnAction.setOnAction(e -> {
            e.consume(); 
            showDetails(event); // Redirect to details to get name/email
        });

        actions.getChildren().add(btnAction);
        content.getChildren().addAll(lblTitle, lblMeta, actions);
        card.getChildren().addAll(header, content);

        return card;
    }

    private void updateButtonStatus(Button btn, Event event) {
        boolean isEnded = event.getDateFin() != null && LocalDateTime.now().isAfter(event.getDateFin());
        String status = getInscriptionStatus(event.getId(), userId, null); // Pass null as we don't have email yet for global check
        
        if (isEnded) {
            btn.setText("Avis & Feedback");
            btn.setDisable(false);
            btn.getStyleClass().setAll("btn-feedback");
            btn.setOnAction(e -> {
                 e.consume();
                 gotoReviews(event);
            });
        } else if ("Confirme".equalsIgnoreCase(status)) {
            btn.setText("Inscrit (Confirmé)");
            btn.getStyleClass().setAll("btn-secondary");
            btn.setDisable(true);
            btn.setStyle("-fx-text-fill: green; -fx-border-color: green;");
        } else if ("En attente".equalsIgnoreCase(status)) {
            btn.setText("En attente...");
            btn.getStyleClass().setAll("btn-secondary");
            btn.setDisable(true);
            btn.setStyle("-fx-text-fill: #FFAA00; -fx-border-color: #FFAA00;");
        } else if (event.getCapacite() <= 0) {
            btn.setText("Complet / Full");
            btn.setDisable(true);
            btn.getStyleClass().setAll("btn-secondary");
            btn.setStyle("-fx-text-fill: #FF5252; -fx-border-color: #FF5252;");
        } else {
            btn.setText("S'inscrire");
            btn.getStyleClass().setAll("btn-primary");
            btn.setDisable(false);
            btn.setStyle("");
        }
    }

    @FXML
    public void showDetails(Event event) {
        lblModalTitle.setText(event.getTitre());
        lblModalType.setText(event.getTypeEvenement() != null ? event.getTypeEvenement().toUpperCase() : "EVENT");
        lblModalLieu.setText("📍 Lieu: " + event.getLieu());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
        String dateStr = event.getDateDebut() != null ? event.getDateDebut().format(formatter) : "Date non spécifiée";
        lblModalDate.setText("📅 Date: " + dateStr);
        
        lblModalCapacity.setText("👥 Places restantes: " + event.getCapacite());
        lblModalDesc.setText(event.getDescription());
        
        updateButtonStatus(btnModalAction, event);
        btnModalAction.setOnAction(e -> {
            inscrire(event);
        });
        
        overlayPane.setVisible(true);
    }

    @FXML
    public void closeDetails() {
        overlayPane.setVisible(false);
    }

    @FXML
    public void inscrire(Event selectedEvent) {
        if (selectedEvent == null) return;

        String name = tfUserName.getText().trim();
        String email = tfUserEmail.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention", "Veuillez renseigner votre nom et email.");
            return;
        }

        if (selectedEvent.getCapacite() <= 0) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention", "Il n'y a plus de places disponibles.");
            return;
        }
        
        if (getInscriptionStatus(selectedEvent.getId(), userId, email) != null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention", "Vous avez déjà une demande pour cet événement.");
            return;
        }

        InscriptionEvent inscriptionEvent = new InscriptionEvent();
        inscriptionEvent.setName(name);
        inscriptionEvent.setEmail(email);
        inscriptionEvent.setDateInscrit(LocalDateTime.now());
        inscriptionEvent.setStatus("En attente");
        inscriptionEvent.setEventId(selectedEvent.getId());
        inscriptionEvent.setUserId(userId);

        inscriptionEventService.addInscription(inscriptionEvent);
        selectedEvent.setCapacite(selectedEvent.getCapacite() - 1);
        eventService.updateEvent(selectedEvent);

        // Send confirmation that request is received
        new Thread(() -> {
            org.example.utils.EmailSender.sendConfirmationEmail(
                inscriptionEvent.getEmail(),
                inscriptionEvent.getName(),
                selectedEvent.getTitre() + " (Demande en attente)"
            );
        }).start();

        afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Votre demande d'inscription est en attente de validation. Un email a été envoyé.");
        closeDetails();
        chargerEvents();
    }

    public static Event selectedEventForReview;

    private void gotoReviews(Event event) {
        selectedEventForReview = event;
        try {
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/ReviewEvent.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) eventsGrid.getScene().getWindow();
            stage.setTitle("Avis sur l'événement");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page des avis.");
        }
    }

    @FXML
    void gotoMesInscriptions() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/InscriptionAdmin.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) eventsGrid.getScene().getWindow();
            stage.setTitle("Mes Inscriptions");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page.");
        }
    }

    @FXML
    void gotoAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/PageAccueil.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) eventsGrid.getScene().getWindow();
            stage.setTitle("InnoLearn - Accueil");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la page d'accueil.");
        }
    }

    private String getInscriptionStatus(int eventId, Integer userId, String email) {
        List<InscriptionEvent> inscriptions = inscriptionEventService.getAllInscriptions();
        for (InscriptionEvent inscription : inscriptions) {
            boolean sameEvent = inscription.getEventId() == eventId;
            boolean sameUserId = (userId != null && inscription.getUserId() != null && inscription.getUserId().equals(userId));
            boolean sameEmail = (email != null && inscription.getEmail() != null && inscription.getEmail().equalsIgnoreCase(email));
            
            if (sameEvent && (sameUserId || sameEmail)) {
                return inscription.getStatus();
            }
        }
        return null;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package org.example.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.MainFX;
import org.example.Entities.Event;
import org.example.Services.EventService;

import java.io.IOException;
import java.util.List;

public class AfficherEvent {

    @FXML
    private FlowPane eventsGrid;
    
    @FXML
    private Label lblTotalEvents;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox<String> cbSort;
    
    public static boolean modeModification=false;
    public static Event eventSelectionne=null;
    EventService eventService = new EventService();
    private List<Event> allEvents;

    @FXML
    public void initialize() {
        cbSort.getItems().addAll("Titre (A-Z)", "Date (Récent)", "Capacité (Max)");
        cbSort.setOnAction(e -> applyFilters());
        
        tfSearch.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        
        loadEvents();
    }

    private void loadEvents() {
        allEvents = eventService.getAllEvents();
        applyFilters();
    }

    private void applyFilters() {
        if (allEvents == null) return;

        String searchQuery = tfSearch.getText() != null ? tfSearch.getText().toLowerCase() : "";
        String sortOption = cbSort.getValue();

        List<Event> filteredList = allEvents.stream()
                .filter(e -> e.getTitre().toLowerCase().contains(searchQuery) || 
                             e.getLieu().toLowerCase().contains(searchQuery))
                .sorted((e1, e2) -> {
                    if ("Titre (A-Z)".equals(sortOption)) {
                        return e1.getTitre().compareToIgnoreCase(e2.getTitre());
                    } else if ("Date (Récent)".equals(sortOption)) {
                        if (e1.getDateDebut() == null || e2.getDateDebut() == null) return 0;
                        return e2.getDateDebut().compareTo(e1.getDateDebut());
                    } else if ("Capacité (Max)".equals(sortOption)) {
                        return Integer.compare(e2.getCapacite(), e1.getCapacite());
                    }
                    return 0;
                })
                .toList();

        renderEvents(filteredList);
    }

    private void renderEvents(List<Event> events) {
        eventsGrid.getChildren().clear();
        lblTotalEvents.setText(String.valueOf(events.size()));
        for(Event event : events) {
            VBox card = createEventCard(event);
            eventsGrid.getChildren().add(card);
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");
        card.setPrefWidth(240);
        card.setMaxWidth(240);

        HBox header = new HBox();
        boolean isWorkshop = "Workshop".equalsIgnoreCase(event.getTypeEvenement()) || "Formation".equalsIgnoreCase(event.getTypeEvenement());
        header.getStyleClass().add(isWorkshop ? "card-header-workshop" : "card-header-conference");
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox dateBox = new VBox();
        dateBox.getStyleClass().add("date-box");
        dateBox.setAlignment(Pos.CENTER);
        
        String day = "00";
        String month = "MMM";
        if(event.getDateDebut() != null) {
            day = String.format("%02d", event.getDateDebut().getDayOfMonth());
            month = event.getDateDebut().getMonth().name().substring(0,3).toUpperCase();
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

        Label lblMeta = new Label("📍 " + event.getLieu() + " | 👥 Cap: " + event.getCapacite());
        lblMeta.getStyleClass().add("card-meta");
        lblMeta.setStyle("-fx-font-weight: bold;");

        Label lblDesc = new Label(event.getDescription());
        lblDesc.getStyleClass().add("card-desc");
        lblDesc.setWrapText(true);
        lblDesc.setPrefHeight(50);

        HBox actions = new HBox();
        actions.setSpacing(15);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button btnEdit = new Button("Modifier");
        btnEdit.getStyleClass().add("btn-edit");
        btnEdit.setPrefWidth(100);
        btnEdit.setOnAction(e -> {
            eventSelectionne = event;
            modeModification = true;
            try {
                FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/AjouterEvent.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage=(Stage)eventsGrid.getScene().getWindow();
                stage.setTitle("Modifier Event");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) { 
                ex.printStackTrace();
            }
        });

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("btn-danger");
        btnDelete.setPrefWidth(100);
        btnDelete.setOnAction(e -> {
            eventService.deleteEvent(event.getId());
            loadEvents();
            showAlert(Alert.AlertType.INFORMATION,"Succès","Événement supprimé avec succès");
        });

        actions.getChildren().addAll(btnEdit, btnDelete);
        content.getChildren().addAll(lblTitle, lblMeta, lblDesc, actions);
        card.getChildren().addAll(header, content);

        return card;
    }

    @FXML
    void gotoajouter(ActionEvent event) {
        modeModification=false;
        eventSelectionne=null;
        try {
            MainFX.chargerPage("/AjouterEvent.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void gotoInscription() {
        try {
            MainFX.chargerPage("/InscriptionAdmin.fxml");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page.");
        }
    }

    @FXML
    public void gotoAccueil() {
        try {
            MainFX.chargerPage("/AdminDashboard.fxml");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au Dashboard Admin.");
        }
    }

    private void showAlert(Alert.AlertType type,String title,String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

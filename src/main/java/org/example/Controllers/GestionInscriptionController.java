package org.example.Controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.MainFX;
import org.example.Services.EventService;
import org.example.Entities.InscriptionEvent;
import org.example.Services.InscriptionEventService;

import java.io.IOException;
import java.util.List;

public class GestionInscriptionController {
    @FXML
    private ListView<InscriptionEvent> lvinscriptions;

    @FXML
    private Button btnconfirmer;

    @FXML
    private Button btnsupprimer;

    @FXML
    private Button btnretour;
    InscriptionEventService inscriptionEventService = new InscriptionEventService();
    @FXML
    public void initialize() {
        chargerInscriptions();
    }
    private void chargerInscriptions() {

            List<InscriptionEvent> inscriptions = inscriptionEventService.getAllInscriptions();
            ObservableList<InscriptionEvent> observableList = FXCollections.observableArrayList(inscriptions);
            lvinscriptions.setItems(observableList);

    }
    @FXML
    public void confirmerInscription() {
        InscriptionEvent selectedInscription = lvinscriptions.getSelectionModel().getSelectedItem();

        if (selectedInscription == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner une inscription à confirmer.");
            return;
        }
        if(selectedInscription.getStatus().equals("En attente")){
            selectedInscription.setStatus("Confirme");
            inscriptionEventService.updateInscription(selectedInscription);
            
            // Send Confirmation Email in a background thread to not block UI
            new Thread(() -> {
                EventService evS = new EventService();
                List<org.example.Entities.Event> events = evS.getAllEvents();
                String eventTitle = "votre événement";
                for(org.example.Entities.Event ev : events) {
                    if(ev.getId() == selectedInscription.getEventId()) {
                        eventTitle = ev.getTitre();
                        break;
                    }
                }
                org.example.utils.EmailSender.sendConfirmationEmail(
                    selectedInscription.getEmail(), 
                    selectedInscription.getName(), 
                    eventTitle
                );
            }).start();

            afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                    "Inscription confirmée avec succès. Un email de confirmation a été envoyé.");
            chargerInscriptions();
        }
    }
    @FXML
    public void supprimerInscription() {
        InscriptionEvent selectedInscription = lvinscriptions.getSelectionModel().getSelectedItem();

        if (selectedInscription == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner une inscription à supprimer.");
            return;
        }
        if(selectedInscription.getStatus().equals("En attente")){
            inscriptionEventService.deleteInscription(selectedInscription.getId());
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                    "Inscription supprimée avec succès.");
            chargerInscriptions();
        }
    }
    @FXML
    public void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/AfficherEvent.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) btnretour.getScene().getWindow();
            stage.setTitle("Gestion Event");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de retourner à la page des événements.");
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

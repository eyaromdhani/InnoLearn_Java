package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.MainFX;
import org.example.entity.Event;
import org.example.service.EventService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AjouterEvent {

    @FXML
    private Button btnajouter;

    @FXML
    private ComboBox<String> cbtype;

    @FXML
    private DatePicker dpdebut;

    @FXML
    private DatePicker dpfin;

    @FXML
    private Label ltitle;

    @FXML
    private TextArea tadesc;

    @FXML
    private TextField tfcapaciter;

    @FXML
    private TextField tflieux;

    @FXML
    private TextField tftitre;

    private ContextMenu suggestionsMenu = new ContextMenu();
    private HttpClient httpClient = HttpClient.newHttpClient();

    EventService eventService = new EventService();

    @FXML
    public void initialize()
    {
        cbtype.getItems().addAll("Formation","Conference","Hackathon");

        setupLocationAutocomplete();

        if(AfficherEvent.modeModification && AfficherEvent.eventSelectionne != null){
            ltitle.setText("Modifier Event");
            btnajouter.setText("Modifier");
            remplirChamps(AfficherEvent.eventSelectionne);
        }else{
            ltitle.setText("Ajouter Event");
            btnajouter.setText("Ajouter");
        }
    }

    private void setupLocationAutocomplete() {
        tflieux.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.length() < 3) {
                suggestionsMenu.hide();
                return;
            }
            fetchSuggestions(newValue);
        });
    }

    private void fetchSuggestions(String query) {
        String url = "https://photon.komoot.io/api/?q=" + query.replace(" ", "%20") + "&limit=5";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::handleApiResponse)
                .exceptionally(ex -> {
                    System.err.println("API Error: " + ex.getMessage());
                    return null;
                });
    }

    private void handleApiResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray features = json.getJSONArray("features");

            Platform.runLater(() -> {
                suggestionsMenu.getItems().clear();
                for (int i = 0; i < features.length(); i++) {
                    JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                    String name = properties.optString("name", "");
                    String city = properties.optString("city", "");
                    String country = properties.optString("country", "");

                    String fullAddress = name + (city.isEmpty() ? "" : ", " + city) + (country.isEmpty() ? "" : " (" + country + ")");

                    MenuItem item = new MenuItem(fullAddress);
                    item.setOnAction(e -> {
                        tflieux.setText(fullAddress);
                        suggestionsMenu.hide();
                    });
                    suggestionsMenu.getItems().add(item);
                }

                if (!suggestionsMenu.getItems().isEmpty()) {
                    if (!suggestionsMenu.isShowing()) {
                        suggestionsMenu.show(tflieux, Side.BOTTOM, 0, 0);
                    }
                } else {
                    suggestionsMenu.hide();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void remplirChamps(Event event){
        tftitre.setText(event.getTitre());
        tadesc.setText(event.getDescription());
        tflieux.setText(event.getLieu());
        tfcapaciter.setText(String.valueOf(event.getCapacite()));
        cbtype.setValue(event.getTypeEvenement());
        if(event.getDateDebut()!=null){
            dpdebut.setValue(event.getDateDebut().toLocalDate());
        }
        if(event.getDateFin()!=null){
            dpfin.setValue(event.getDateFin().toLocalDate());
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        if(!controleDeSaisie()){
            return;
        }
        if(AfficherEvent.modeModification && AfficherEvent.eventSelectionne != null){
            mettreAJourEventDepuisFormulaire(AfficherEvent.eventSelectionne);
            eventService.updateEvent(AfficherEvent.eventSelectionne);
            afficherAlerte(Alert.AlertType.INFORMATION,"Succes","Evenement modifier avec succes");
        }else{
            Event newEvent=construireEventDepuisFormulaire();
            eventService.addEvent(newEvent);
            afficherAlerte(Alert.AlertType.INFORMATION,"Succes","Evenement ajoute avec succes");
        }
        retourVersListe();

    }
    private boolean controleDeSaisie(){
        String erreurs="";
        String titre=tftitre.getText().trim();
        String description=tadesc.getText().trim();
        String type=cbtype.getValue();
        String lieu=tflieux.getText().trim();
        String capacite=tfcapaciter.getText().trim();
        if(titre.isEmpty()){
            erreurs+="- Le titre est obligatoire,\n";
        }
        if(description.isEmpty()){
            erreurs+="- Description est obligatoire,\n";
        }
        if(lieu.isEmpty()){
            erreurs+="- Le lieu est obligatoire,\n";
        }
        if(type==null ||  type.isEmpty()){
            erreurs+="- Veuillez selectionner un type\n";
        }
        if (capacite.isEmpty()){
            erreurs+="- La capacite est obligatoire,\n";
        }else{
            try{
                int capaciteNum =Integer.parseInt(capacite);
                if(capaciteNum<0){
                    erreurs+="- La capacite doit etre positive\n";
                }
            }catch (NumberFormatException e){
                erreurs+="- La capacite doit etre un nombre valide\n";
            }

        }
        LocalDate dateDebut=dpdebut.getValue();
        LocalDate dateFin=dpfin.getValue();
        if(dateFin != null && dateDebut!=null && dateFin.isBefore(dateDebut)){
            erreurs+="- La date de fin doit etre apres ou egale a la date de debut\n";
        }
        if(erreurs.length()>0){
            afficherAlerte(Alert.AlertType.WARNING,"Erreurs de saisie",erreurs);
            return false;
        }
        return true;

    }

    @FXML
    void retour(ActionEvent event) {
        retourVersListe();

    }
    private void retourVersListe(){
        AfficherEvent.eventSelectionne=null;
        AfficherEvent.modeModification=false;
        FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/AfficherEvent.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage=(Stage)btnajouter.getScene().getWindow();
            stage.setTitle("Gestion Event");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Event construireEventDepuisFormulaire() {
        String titre = tftitre.getText().trim();
        String lieu = tflieux.getText().trim();
        String description = tadesc.getText().trim();
        String type = cbtype.getValue();
        int capacite = Integer.parseInt(tfcapaciter.getText().trim());

        LocalDateTime dateDebut = convertirDatePickerEnLocalDateTime(dpdebut);
        LocalDateTime dateFin = convertirDatePickerEnLocalDateTime(dpfin);

        String statut = "Planifie";

        return new Event(
                titre,
                description,
                type,
                dateDebut,
                dateFin,
                lieu,
                capacite,
                statut
        );
    }

    private void mettreAJourEventDepuisFormulaire(Event event){
        event.setTitre(tftitre.getText().trim());
        event.setDescription(tadesc.getText().trim());
        event.setLieu(tflieux.getText().trim());
        event.setTypeEvenement(cbtype.getValue());
        event.setCapacite(Integer.parseInt(tfcapaciter.getText().trim()));
        event.setDateDebut(convertirDatePickerEnLocalDateTime(dpdebut));
        event.setDateFin(convertirDatePickerEnLocalDateTime(dpfin));
        event.setStatut("Planifie");
    }
    private LocalDateTime convertirDatePickerEnLocalDateTime(DatePicker datePicker) {
        if (datePicker.getValue() != null) {
            return datePicker.getValue().atStartOfDay();
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

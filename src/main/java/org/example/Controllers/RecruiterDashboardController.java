package org.example.Controllers;

import org.example.Entities.OffreStage;
import org.example.Services.ServiceOffreStage;
import org.example.Services.ServiceStageCondidature;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import org.example.utils.MyDataBase;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RecruiterDashboardController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> comboEnterprise;
    @FXML private ComboBox<String> comboDomaine;
    @FXML private ComboBox<String> comboDuration;
    @FXML private ComboBox<String> comboSort;
    @FXML private Label lblCount;
    @FXML private FlowPane cardsContainer;
    @FXML private Circle heroCircle;

    @FXML private Label lblStatMyOffers;
    @FXML private Label lblStatMyApps;
    @FXML private Label lblStatPending;

    private ServiceOffreStage serviceOffre;
    private final int MOCK_RECRUITER_ID = 8;
    private List<OffreStage> allMyOffres;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceOffre = new ServiceOffreStage(MyDataBase.getInstance().getConnection());
        
        loadData();
        setupFilters();
        loadStats();
    }

    private void setupFilters() {
        if (comboSort != null) {
            comboSort.getItems().addAll("Plus récents", "Plus anciens");
        }
        if (comboDuration != null) {
            comboDuration.getItems().addAll("Tous", "1-2 mois", "3-4 mois", "6 mois+");
        }

        // Fetch unique values for Entreprise and Domaine from allMyOffres
        if (allMyOffres != null) {
            List<String> entreprises = allMyOffres.stream().map(OffreStage::getEntreprise).distinct().sorted().toList();
            List<String> domaines = allMyOffres.stream().map(OffreStage::getDomaine).distinct().sorted().toList();

            comboEnterprise.getItems().add("Toutes les entreprises");
            comboEnterprise.getItems().addAll(entreprises);
            comboDomaine.getItems().add("Tous les domaines");
            comboDomaine.getItems().addAll(domaines);
        }

        // Real-time listeners
        txtSearch.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboSort.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboEnterprise.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboDomaine.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboDuration.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
    }

    private void applyFilters() {
        if (allMyOffres == null) return;

        String query = txtSearch.getText().toLowerCase();
        String ent = comboEnterprise.getValue();
        String dom = comboDomaine.getValue();
        String dur = comboDuration.getValue();
        String sort = comboSort.getValue();

        List<OffreStage> filtered = allMyOffres.stream().filter(o -> {
            boolean matchesSearch = query.isEmpty() || 
                o.getTitre().toLowerCase().contains(query) || 
                o.getEntreprise().toLowerCase().contains(query);
            
            boolean matchesEnt = ent == null || ent.equals("Toutes les entreprises") || ent.equals("Toutes") || o.getEntreprise().equals(ent);
            boolean matchesDom = dom == null || dom.equals("Tous les domaines") || o.getDomaine().equals(dom);
            
            boolean matchesDur = true;
            if (dur != null && !dur.equals("Tous") && !dur.equals("Toutes")) {
                if (dur.equals("1-2 mois")) matchesDur = o.getDuree() <= 2;
                else if (dur.equals("3-4 mois")) matchesDur = o.getDuree() >= 3 && o.getDuree() <= 4;
                else if (dur.equals("6 mois+")) matchesDur = o.getDuree() >= 6;
            }
            
            return matchesSearch && matchesEnt && matchesDom && matchesDur;
        }).toList();

        // Apply sorting
        if (sort != null) {
            boolean asc = sort.equals("Plus anciens");
            filtered = filtered.stream().sorted((a, b) -> {
                int res = a.getDate_publication().compareTo(b.getDate_publication());
                return asc ? res : -res;
            }).toList();
        }

        updateDisplay(filtered);
    }

    private void loadStats() {
        new Thread(() -> {
            try {
                ServiceStageCondidature serviceDemande = new ServiceStageCondidature(MyDataBase.getInstance().getConnection());
                
                // 1. Total My Offers (Filtered by Recruiter ID)
                int myOffersCount = serviceOffre.afficherParRecruteur(MOCK_RECRUITER_ID).size();
                
                // 2. Candidatures Stats for this recruiter's offers
                java.util.Map<String, Integer> stats = serviceDemande.getStatsCandidaturesForRecruiter(MOCK_RECRUITER_ID);

                javafx.application.Platform.runLater(() -> {
                    lblStatMyOffers.setText(String.valueOf(myOffersCount));
                    lblStatMyApps.setText(String.valueOf(stats.getOrDefault("total", 0)));
                    lblStatPending.setText(String.valueOf(stats.getOrDefault("pending", 0)));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadData() {
        try {
            // Filter to only show THIS recruiter's offers
            allMyOffres = serviceOffre.afficherParRecruteur(MOCK_RECRUITER_ID);
            updateDisplay(allMyOffres);
            
            // Populate combos
            List<String> enterprises = allMyOffres.stream()
                    .map(OffreStage::getEntreprise)
                    .distinct()
                    .collect(Collectors.toList());
            comboEnterprise.getItems().clear();
            comboEnterprise.getItems().add("Toutes les entreprises");
            comboEnterprise.getItems().addAll(enterprises);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterData(String query) {
        if (allMyOffres == null) return;
        
        List<OffreStage> filtered = allMyOffres.stream()
                .filter(o -> o.getTitre().toLowerCase().contains(query.toLowerCase()) || 
                            o.getEntreprise().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        
        updateDisplay(filtered);
    }

    private void updateDisplay(List<OffreStage> offres) {
        cardsContainer.getChildren().clear();
        lblCount.setText(offres.size() + " Offres");

        int colorIndex = 0;
        String[] colors = {"recruiter-card-blue", "recruiter-card-purple", "recruiter-card-pink", "recruiter-card-cyan"};

        for (OffreStage o : offres) {
            VBox card = createOfferCard(o, colors[colorIndex % colors.length]);
            cardsContainer.getChildren().add(card);
            colorIndex++;
        }
    }

    private VBox createOfferCard(OffreStage o, String colorClass) {
        VBox card = new VBox(15);
        card.getStyleClass().addAll("recruiter-offer-card", colorClass);
        
        HBox tagBox = new HBox();
        Label tag = new Label(o.getDomaine());
        tag.getStyleClass().add("card-tag");
        tagBox.getChildren().add(tag);
        
        VBox content = new VBox(5);
        Label title = new Label(o.getTitre());
        title.getStyleClass().add("card-title-white");
        title.setWrapText(true);
        
        Label company = new Label(o.getEntreprise());
        company.getStyleClass().add("card-company-white");
        
        content.getChildren().addAll(title, company);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        HBox footer = new HBox();
        footer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Button btnView = new Button("Voir Détails");
        btnView.setStyle("-fx-background-color: white; -fx-text-fill: #6358ff; -fx-background-radius: 15px; -fx-font-weight: bold;");
        btnView.setCursor(javafx.scene.Cursor.HAND);
        
        // Navigation to details
        btnView.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OffreDetail.fxml"));
                javafx.scene.Parent root = loader.load();
                
                OffreDetailController controller = loader.getController();
                // Pass null for StagesController as we are in Recruiter Dashboard context
                controller.setOffre(o, null);
                
                javafx.stage.Stage stage = (javafx.stage.Stage) cardsContainer.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        });
        
        footer.getChildren().add(btnView);
        
        card.getChildren().addAll(tagBox, content, spacer, footer);
        
        return card;
    }

    @FXML
    void handlePublish(ActionEvent event) {
        navigateTo("/RecruiterOffres.fxml");
    }

    @FXML
    void handleMyOffers(ActionEvent event) {
        navigateTo("/RecruiterOffres.fxml");
    }

    @FXML
    void handleCandidatures(ActionEvent event) {
        navigateTo("/RecruiterCandidatures.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlPath));
            javafx.stage.Stage stage = (javafx.stage.Stage) cardsContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

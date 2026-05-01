package org.example.Controllers;

import org.example.Entities.OffreStage;
import org.example.Entities.StageCondidature;
import org.example.Services.ServiceOffreStage;
import org.example.Services.ServiceStageCondidature;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.image.Image;
import org.example.Entities.ExternalOffer;
import org.example.Services.ServiceExternalOffer;
import org.example.utils.MyDataBase;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class StagesController implements Initializable {

    @FXML private Circle heroCircle;
    @FXML private FlowPane contentArea;
    @FXML private HBox tabOffres;
    @FXML private HBox tabDemandes;
    @FXML private HBox tabProfile;
    @FXML private HBox tabInsights;
    @FXML private HBox tabExternal;
    @FXML private Label lblStatTotalOffres;
    @FXML private Label lblStatMyApps;
    @FXML private Label lblStatAccepted;
    @FXML private Label lblStatPending;

    @FXML private NavbarController navbarController;
    @FXML private ScrollPane scrollPaneContent;
    @FXML private VBox filterArea;
    @FXML private javafx.scene.control.TextField txtSearch;
    @FXML private javafx.scene.control.ComboBox<String> comboSort;
    @FXML private javafx.scene.control.ComboBox<String> comboEntreprise;
    @FXML private javafx.scene.control.ComboBox<String> comboDomaine;
    @FXML private javafx.scene.control.ComboBox<String> comboDuree;
    @FXML private HBox filterRow2;
    @FXML private HBox containerEntreprise;
    @FXML private Button btnRecommendationAI;
    
    private Node listViewBackup;

    private ServiceOffreStage serviceOffre;
    private ServiceStageCondidature serviceDemande;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Highlight active link in navbar
        if (navbarController != null) {
            navbarController.setActiveLink("Stages");
        }

        // Initialize Services
        serviceOffre = new ServiceOffreStage(MyDataBase.getInstance().getConnection());
        serviceDemande = new ServiceStageCondidature(MyDataBase.getInstance().getConnection());
        
        loadHeaderStats();

        // Load Hero Image
        try {
            URL imageResource = getClass().getResource("/assets/stages_hero.png");
            if (imageResource != null) {
                heroCircle.setFill(new ImagePattern(new Image(imageResource.toExternalForm())));
            }
        } catch (Exception e) {
            System.err.println("Hero image error: " + e.getMessage());
        }

        // Show Offres by default
        handleShowOffres();
        
        // Backup the list view for later returning
        listViewBackup = scrollPaneContent.getContent();

        // Setup Sorting & Filters
        setupFilters();

        // Backup the list view for later returning
        listViewBackup = scrollPaneContent.getContent();
    }

    private void setupFilters() {
        if (comboSort != null) {
            comboSort.getItems().addAll("Plus récents", "Plus anciens");
        }
        if (comboDuree != null) {
            comboDuree.getItems().addAll("Tous", "1-2 mois", "3-4 mois", "6 mois+");
        }

        // Fetch unique values for Entreprise and Domaine
        new Thread(() -> {
            try {
                List<OffreStage> all = serviceOffre.afficherAll();
                List<String> entreprises = all.stream().map(OffreStage::getEntreprise).distinct().sorted().toList();
                List<String> domaines = all.stream().map(OffreStage::getDomaine).distinct().sorted().toList();

                javafx.application.Platform.runLater(() -> {
                    comboEntreprise.getItems().add("Toutes les entreprises");
                    comboEntreprise.getItems().addAll(entreprises);
                    comboDomaine.getItems().add("Tous les domaines");
                    comboDomaine.getItems().addAll(domaines);
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        // Listeners for real-time filtering
        txtSearch.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboSort.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboEntreprise.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboDomaine.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        comboDuree.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
    }

    private void applyFilters() {
        if (tabExternal.getStyleClass().contains("hero-tab-item-active")) {
            loadExternalOffers(txtSearch.getText(), comboDomaine.getValue());
            return;
        }

        boolean isDemandes = tabDemandes.getStyleClass().contains("hero-tab-item-active");
        String query = txtSearch.getText().toLowerCase();
        String dom = comboDomaine.getValue();
        String sort = comboSort.getValue();

        try {
            if (isDemandes) {
                List<StageCondidature> all = serviceDemande.afficherDemandes();
                List<StageCondidature> filtered = all.stream().filter(sc -> {
                    boolean matchesSearch = query.isEmpty() || sc.getTitre().toLowerCase().contains(query);
                    boolean matchesDom = dom == null || dom.equals("Tous les domaines") || sc.getDomaine().equals(dom);
                    return matchesSearch && matchesDom;
                }).toList();

                if (sort != null) {
                    boolean asc = sort.equals("Plus anciens");
                    filtered = filtered.stream().sorted((a, b) -> {
                        int res = a.getDate_publication().compareTo(b.getDate_publication());
                        return asc ? res : -res;
                    }).toList();
                }
                renderDemandes(filtered);
            } else {
                List<OffreStage> all = serviceOffre.afficherAll();
                String ent = comboEntreprise.getValue();
                String dur = comboDuree.getValue();

                List<OffreStage> filtered = all.stream().filter(o -> {
                    boolean matchesSearch = query.isEmpty() || 
                        o.getTitre().toLowerCase().contains(query) || 
                        o.getEntreprise().toLowerCase().contains(query);
                    
                    boolean matchesEnt = ent == null || ent.equals("Toutes les entreprises") || o.getEntreprise().equals(ent);
                    boolean matchesDom = dom == null || dom.equals("Tous les domaines") || o.getDomaine().equals(dom);
                    
                    boolean matchesDur = true;
                    if (dur != null && !dur.equals("Tous")) {
                        if (dur.equals("1-2 mois")) matchesDur = o.getDuree() <= 2;
                        else if (dur.equals("3-4 mois")) matchesDur = o.getDuree() >= 3 && o.getDuree() <= 4;
                        else if (dur.equals("6 mois+")) matchesDur = o.getDuree() >= 6;
                    }
                    
                    return matchesSearch && matchesEnt && matchesDom && matchesDur;
                }).toList();

                if (sort != null) {
                    boolean asc = sort.equals("Plus anciens");
                    filtered = filtered.stream().sorted((a, b) -> {
                        int res = a.getDate_publication().compareTo(b.getDate_publication());
                        return asc ? res : -res;
                    }).toList();
                }
                renderOffres(filtered);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void loadHeaderStats() {
        new Thread(() -> {
            try {
                // 1. Total Offers
                int totalOffres = serviceOffre.afficherAll().size();
                
                // 2. Student Apps Stats (Student 10)
                java.util.Map<String, Integer> stats = serviceDemande.getStatsCandidatures(10);
                
                javafx.application.Platform.runLater(() -> {
                    lblStatTotalOffres.setText(String.valueOf(totalOffres));
                    lblStatMyApps.setText(String.valueOf(stats.getOrDefault("total", 0)));
                    lblStatAccepted.setText(String.valueOf(stats.getOrDefault("accepted", 0)));
                    lblStatPending.setText(String.valueOf(stats.getOrDefault("pending", 0)));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void showOffresList() {
        if (listViewBackup != null) {
            scrollPaneContent.setContent(listViewBackup);
            handleShowOffres(); // Refresh
        }
    }

    private void showOffreDetail(OffreStage os) {
        System.out.println("Switching to Detail View for: " + os.getTitre());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OffreDetail.fxml"));
            Parent detailView = loader.load();
            
            OffreDetailController controller = loader.getController();
            controller.setOffre(os, this);
            
            scrollPaneContent.setContent(detailView);
            scrollPaneContent.setVvalue(0); // Scroll to top
        } catch (Exception e) {
            System.err.println("Error loading OffreDetail.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleShowOffres() {
        restoreListView();
        setActiveTab(tabOffres);
        if (filterArea != null) {
            filterArea.setVisible(true);
            filterArea.setManaged(true);
            
            // Local-specific filters
            containerEntreprise.setVisible(true);
            containerEntreprise.setManaged(true);
            btnRecommendationAI.setVisible(true);
            btnRecommendationAI.setManaged(true);
            filterRow2.setVisible(true);
            filterRow2.setManaged(true);
            
            txtSearch.setPromptText("Rechercher une offre...");
        }
        loadOffres();
    }

    @FXML
    public void handleShowDemandes() {
        restoreListView();
        setActiveTab(tabDemandes);
        if (filterArea != null) {
            filterArea.setVisible(true);
            filterArea.setManaged(true);

            // Local-specific filters (Entreprise and Duration don't apply to student demands)
            containerEntreprise.setVisible(false);
            containerEntreprise.setManaged(false);
            btnRecommendationAI.setVisible(false);
            btnRecommendationAI.setManaged(false);
            filterRow2.setVisible(true); // Keep sort
            filterRow2.setManaged(true);
            
            txtSearch.setPromptText("Rechercher un profil...");
        }
        loadDemandes();
    }

    private void restoreListView() {
        if (listViewBackup != null && scrollPaneContent.getContent() != listViewBackup) {
            scrollPaneContent.setContent(listViewBackup);
        }
    }

    @FXML
    public void handleShowProfile() {
        setActiveTab(tabProfile);
        loadProfile();
    }

    @FXML
    public void handleShowInsights() {
        setActiveTab(tabInsights);
        loadInsights();
    }

    @FXML
    public void handleShowExternal() {
        restoreListView();
        setActiveTab(tabExternal);
        if (filterArea != null) {
            filterArea.setVisible(true);
            filterArea.setManaged(true);
            
            // External specific: Hide entreprise, recommendation, and row 2 (not used for API yet)
            containerEntreprise.setVisible(false);
            containerEntreprise.setManaged(false);
            btnRecommendationAI.setVisible(false);
            btnRecommendationAI.setManaged(false);
            filterRow2.setVisible(false);
            filterRow2.setManaged(false);
            
            txtSearch.setPromptText("Lieu (ex: Paris, London, Remote)...");
        }
        loadExternalOffers(null, null);
    }

    private void loadExternalOffers(String category, String location) {
        contentArea.getChildren().clear();

        // Premium loading state
        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(javafx.geometry.Pos.CENTER);
        loadingBox.setStyle("-fx-padding: 60 0;");

        SVGPath globeIcon = new SVGPath();
        globeIcon.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z");
        globeIcon.setFill(Color.web("#6366f1"));
        globeIcon.setScaleX(2.0);
        globeIcon.setScaleY(2.0);

        Label loadingLabel = new Label("🌍 Chargement des opportunités internationales...");
        loadingLabel.getStyleClass().add("external-loading-label");

        loadingBox.getChildren().addAll(globeIcon, loadingLabel);
        contentArea.getChildren().add(loadingBox);

        new Thread(() -> {
            ServiceExternalOffer service = new ServiceExternalOffer();
            List<ExternalOffer> offers = service.fetchInternships(category, location);
            javafx.application.Platform.runLater(() -> renderExternalOffers(offers));
        }).start();
    }

    private void renderExternalOffers(List<ExternalOffer> offers) {
        contentArea.getChildren().clear();
        if (offers.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setStyle("-fx-padding: 60 0;");

            SVGPath emptyIcon = new SVGPath();
            emptyIcon.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z");
            emptyIcon.setFill(Color.web("#cbd5e1"));
            emptyIcon.setScaleX(2.5);
            emptyIcon.setScaleY(2.5);

            Label emptyLabel = new Label("Aucune opportunité internationale trouvée pour le moment.");
            emptyLabel.getStyleClass().add("external-empty-label");

            emptyBox.getChildren().addAll(emptyIcon, emptyLabel);
            contentArea.getChildren().add(emptyBox);
            return;
        }

        for (ExternalOffer offer : offers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ExternalOfferItem.fxml"));
                Node card = loader.load();
                ExternalOfferItemController controller = loader.getController();
                controller.setData(offer);
                contentArea.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadInsights() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StudentResponses.fxml"));
            Parent insightsView = loader.load();
            scrollPaneContent.setContent(insightsView);
            scrollPaneContent.setVvalue(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActiveTab(HBox active) {
        // Reset all tabs
        tabOffres.getStyleClass().remove("hero-tab-item-active");
        tabDemandes.getStyleClass().remove("hero-tab-item-active");
        tabProfile.getStyleClass().remove("hero-tab-item-active");
        tabInsights.getStyleClass().remove("hero-tab-item-active");
        tabExternal.getStyleClass().remove("hero-tab-item-active");
        
        // Activate selected
        active.getStyleClass().add("hero-tab-item-active");
    }

    private void loadProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
            Parent profileView = loader.load();
            scrollPaneContent.setContent(profileView);
            scrollPaneContent.setVvalue(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddNewDemand() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddDemandPopup.fxml"));
            Parent root = loader.load();
            
            AddDemandPopupController controller = loader.getController();
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Nouvelle Demande");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            // Apply CSS if needed
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error opening AddDemandPopup: " + e.getMessage());
        }
    }

    private void showDemandDetail(StageCondidature sc) {
        System.out.println("Switching to Demand Detail View for Student: " + sc.getId_etudiant());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandDetail.fxml"));
            Parent detailView = loader.load();
            
            System.out.println("FXML Loaded successfully, setting controller data...");
            DemandDetailController controller = loader.getController();
            controller.setDemand(sc, this);
            
            scrollPaneContent.setContent(detailView);
            scrollPaneContent.setVvalue(0);
            System.out.println("Content successfully updated to Demand Detail View.");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR loading DemandDetail.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void renderOffres(List<OffreStage> offres) {
        contentArea.getChildren().clear();
        for (OffreStage os : offres) {
            VBox card = createCard(
                os.getTitre(),
                os.getEntreprise(),
                os.getDomaine(),
                os.getLieu(),
                "M21 16.5c0 .38-.21.71-.53.88l-7.97 4.27a1.006 1.006 0 01-.94 0l-7.97-4.27A1 1 0 013 16.5V7.5c0-.38.21-.71.53-.88l7.97-4.27a1.006 1.006 0 01.94 0l7.97 4.27c.32.17.53.5.53.88v9z", // Briefcase icon
                "#3498db",
                "Voir Détails",
                e -> showOffreDetail(os)
            );
            
            card.setOnMouseClicked(e -> showOffreDetail(os));
            contentArea.getChildren().add(card);
        }
    }

    private void loadOffres() {
        try {
            List<OffreStage> offres = serviceOffre.afficherAll();
            renderOffres(offres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch() {
        applyFilters();
    }

    @FXML
    public void handleRecommendations() {
        restoreListView();
        setActiveTab(tabOffres);
        contentArea.getChildren().clear();
        
        Label loading = new Label("Analyse de votre profil en cours pour les recommandations...");
        loading.setStyle("-fx-text-fill: #6366f1; -fx-font-weight: bold;");
        contentArea.getChildren().add(loading);

        new Thread(() -> {
            try {
                // 1. Get student domain
                StageCondidature profile = serviceDemande.getProfileEtudiant(10); // Hardcoded ID 10
                if (profile == null || profile.getDomaine() == null) {
                    javafx.application.Platform.runLater(() -> {
                        contentArea.getChildren().clear();
                        contentArea.getChildren().add(new Label("Veuillez d'abord compléter votre domaine dans 'Mon Profil' pour recevoir des recommandations."));
                    });
                    return;
                }

                String domain = profile.getDomaine().toLowerCase();
                
                // 2. Fetch all local offers and filter
                List<OffreStage> allOffres = serviceOffre.afficherAll();
                List<OffreStage> filtered = allOffres.stream()
                        .filter(o -> o.getDomaine().toLowerCase().contains(domain) || domain.contains(o.getDomaine().toLowerCase()))
                        .toList();

                // 3. Render
                javafx.application.Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    if (filtered.isEmpty()) {
                        contentArea.getChildren().add(new Label("Désolé, aucune offre locale ne correspond exactement à votre domaine (" + domain + ") pour le moment."));
                    } else {
                        Label header = new Label("✨ Recommandations basées sur votre profil (" + domain + ") :");
                        header.setStyle("-fx-font-weight: bold; -fx-text-fill: #6366f1; -fx-padding: 0 0 10 0;");
                        contentArea.getChildren().add(header);
                        renderOffres(filtered);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void searchOffres(String query) {
        try {
            List<OffreStage> result = serviceOffre.search(query);
            renderOffres(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDemandes() {
        contentArea.getChildren().clear();
        try {
            // Add a special "Add New Demand" card at the beginning
            VBox addCard = createCard(
                "Publier une Demande",
                "Créez votre profil public",
                "Nouveau",
                "Ajouter",
                "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z", // Plus icon
                "#6358ff",
                "Commencer",
                e -> handleAddNewDemand()
            );
            addCard.setStyle(addCard.getStyle() + "; -fx-border-style: dashed; -fx-border-width: 2px; -fx-border-color: #6358ff;");
            contentArea.getChildren().add(addCard);

            // Updated to use the filtered method
            List<StageCondidature> demandes = serviceDemande.afficherDemandes();
            renderDemandes(demandes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void renderDemandes(List<StageCondidature> demandes) {
        // Find if addCard is already there (or clear and re-add if needed)
        // For simplicity when filtering, we might want to keep the "Add" card only if not filtering?
        // But usually it's better to keep it.
        
        contentArea.getChildren().clear();
        
        // Always show the Add card
        VBox addCard = createCard(
            "Publier une Demande",
            "Créez votre profil public",
            "Nouveau",
            "Ajouter",
            "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z", // Plus icon
            "#6358ff",
            "Commencer",
            e -> handleAddNewDemand()
        );
        addCard.setStyle(addCard.getStyle() + "; -fx-border-style: dashed; -fx-border-width: 2px; -fx-border-color: #6358ff;");
        contentArea.getChildren().add(addCard);

        for (StageCondidature sc : demandes) {
            VBox card = createCard(
                sc.getTitre(),
                "Profil Étudiant",
                sc.getDomaine(),
                sc.getStatut(),
                "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z", // Person icon
                "#2ecc71",
                "Voir Détails",
                e -> showDemandDetail(sc)
            );
            
            card.setOnMouseClicked(e -> showDemandDetail(sc));
            contentArea.getChildren().add(card);
        }
    }

    private VBox createCard(String title, String subtitle, String category, String meta, String svgContent, String color, String btnText, javafx.event.EventHandler<javafx.event.ActionEvent> onBtnAction) {
        VBox card = new VBox(15);
        card.getStyleClass().add("item-card");

        // Header: Icon + Badge
        HBox header = new HBox();
        header.setSpacing(10);
        
        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("item-card-icon-container");
        iconContainer.setStyle("-fx-background-color: " + color + "1A;"); // 10% opacity hex
        
        SVGPath icon = new SVGPath();
        icon.setContent(svgContent);
        icon.setFill(Color.web(color));
        icon.setScaleX(0.8);
        icon.setScaleY(0.8);
        
        iconContainer.getChildren().add(icon);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label badge = new Label(category);
        badge.getStyleClass().add("item-card-badge");
        
        header.getChildren().addAll(iconContainer, spacer, badge);

        // Content
        VBox metaBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("item-card-title");
        titleLabel.setWrapText(true);
        
        Label subLabel = new Label(subtitle);
        subLabel.getStyleClass().add("item-card-subtitle");
        
        metaBox.getChildren().addAll(titleLabel, subLabel);

        // Footer: Meta Info (Location or Status)
        HBox footer = new HBox(8);
        footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        SVGPath pinIcon = new SVGPath();
        pinIcon.setContent("M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5a2.5 2.5 0 010-5 2.5 2.5 0 010 5z");
        pinIcon.setFill(Color.web("#7f8c8d"));
        pinIcon.setScaleX(0.6);
        pinIcon.setScaleY(0.6);
        
        Label footerLabel = new Label(meta);
        footerLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        footer.getChildren().addAll(pinIcon, footerLabel);

        // Call to action button
        Button btnDetail = new Button(btnText);
        btnDetail.getStyleClass().add("btn-card-detail");
        btnDetail.setMaxWidth(Double.MAX_VALUE); // Full width button
        btnDetail.setCursor(javafx.scene.Cursor.HAND);
        
        if (onBtnAction != null) {
            btnDetail.setOnAction(onBtnAction);
        } else {
            btnDetail.setDisable(true); // Disable if no action
            btnDetail.setOpacity(0.5);
        }

        card.getChildren().addAll(header, metaBox, footer, btnDetail);
        
        return card;
    }
}

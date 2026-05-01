package org.example.Controllers;

import org.example.Entities.StageCondidature;
import org.example.Services.ServiceStageCondidature;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.utils.MyDataBase;

import java.sql.SQLException;
import java.util.List;

public class StudentResponsesController {

    @FXML private FlowPane containerCandidatures;
    @FXML private FlowPane containerDemandes;

    private ServiceStageCondidature serviceCandidature;
    private final int MOCK_STUDENT_ID = 10;

    @FXML
    public void initialize() {
        serviceCandidature = new ServiceStageCondidature(MyDataBase.getInstance().getConnection());
        loadData();
    }

    @FXML
    private void refresh() {
        loadData();
    }

    private void loadData() {
        containerCandidatures.getChildren().clear();
        containerDemandes.getChildren().clear();

        try {
            List<StageCondidature> all = serviceCandidature.afficherAll();
            
            // Filter candidatures for this student
            List<StageCondidature> myCandidatures = all.stream()
                .filter(c -> c.getId_etudiant() != null && c.getId_etudiant() == MOCK_STUDENT_ID && "CANDIDATURE".equalsIgnoreCase(c.getType_request()))
                .toList();

            // Filter demands for this student
            List<StageCondidature> myDemands = all.stream()
                .filter(c -> c.getId_etudiant() != null && c.getId_etudiant() == MOCK_STUDENT_ID && "DEMANDE".equalsIgnoreCase(c.getType_request()))
                .toList();

            for (StageCondidature sc : myCandidatures) {
                containerCandidatures.getChildren().add(createResponseCard(sc));
            }

            for (StageCondidature sc : myDemands) {
                containerDemandes.getChildren().add(createResponseCard(sc));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createResponseCard(StageCondidature sc) {
        VBox card = new VBox(15);
        card.getStyleClass().add("item-card");
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        // Header with status pill
        HBox header = new HBox();
        Label title = new Label(sc.getTitre());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        title.setWrapText(true);
        title.setPrefWidth(200);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label statusPill = new Label(sc.getStatut() == null ? "EN_ATTENTE" : sc.getStatut());
        styleStatusPill(statusPill, sc.getStatut());

        header.getChildren().addAll(title, spacer, statusPill);

        // Body
        VBox body = new VBox(5);
        Label domain = new Label("Domaine: " + sc.getDomaine());
        domain.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        
        Label date = new Label("Postulé le: " + (sc.getDate_publication() != null ? sc.getDate_publication().toString() : "—"));
        date.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        body.getChildren().addAll(domain, date);

        // Message context
        Label message = new Label();
        message.setWrapText(true);
        message.setStyle("-fx-font-style: italic; -fx-text-fill: #475569; -fx-padding: 10 0 0 0;");
        
        if ("Acceptée".equalsIgnoreCase(sc.getStatut())) {
            message.setText("✨ Félicitations ! Votre profil a retenu notre attention. Un recruteur vous contactera pour un entretien.");
        } else if ("Refusée".equalsIgnoreCase(sc.getStatut())) {
            message.setText("Merci pour votre intérêt. Malheureusement, nous ne pouvons pas donner suite à votre demande pour le moment.");
        } else {
            message.setText("⏳ Votre candidature est en cours de revue par l'équipe de recrutement.");
        }

        card.getChildren().addAll(header, body, message);
        return card;
    }

    private void styleStatusPill(Label pill, String statut) {
        if (statut == null) statut = "EN_ATTENTE";
        String style = "-fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 12; -fx-background-radius: 20px;";
        
        if (statut.toUpperCase().contains("ACC")) {
            pill.setStyle(style + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;");
        } else if (statut.toUpperCase().contains("REF")) {
            pill.setStyle(style + "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;");
        } else {
            pill.setStyle(style + "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04;");
        }
    }
}

class Region extends javafx.scene.layout.Region {}

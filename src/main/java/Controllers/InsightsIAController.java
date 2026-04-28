package Controllers;

import Entities.StageCondidature;
import Services.ServiceGroq;
import Services.ServiceStageCondidature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.MyDatabase;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class InsightsIAController implements Initializable {

    @FXML private Label lblOffresCount;
    @FXML private Label lblCandidaturesCount;
    @FXML private Label lblAcceptedCount;
    @FXML private Label lblPendingCount;
    @FXML private Label lblStanding;
    @FXML private Circle circleProgress;
    @FXML private FlowPane flowSkillGaps;
    @FXML private VBox vboxActionPlan;

    private ServiceGroq serviceGroq;
    private ServiceStageCondidature serviceCandidature;
    private final int STUDENT_ID = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceGroq = new ServiceGroq();
        Connection conn = MyDatabase.getInstance().getConnection();
        serviceCandidature = new ServiceStageCondidature(conn);

        loadHeaderStats();
        loadAIInsights();
    }

    private void loadHeaderStats() {
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            try (Statement st = conn.createStatement()) {
                // Offres
                ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM offrestage");
                if (rs1.next()) lblOffresCount.setText(String.valueOf(rs1.getInt(1)));

                // Candidatures
                ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM stagecondidature WHERE id_etudiant = " + STUDENT_ID + " AND type_request = 'CANDIDATURE'");
                if (rs2.next()) lblCandidaturesCount.setText(String.valueOf(rs2.getInt(1)));

                // Accepted
                ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM stagecondidature WHERE id_etudiant = " + STUDENT_ID + " AND statut = 'ACCEPTEE'");
                if (rs3.next()) lblAcceptedCount.setText(String.valueOf(rs3.getInt(1)));

                // Pending
                ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM stagecondidature WHERE id_etudiant = " + STUDENT_ID + " AND statut = 'EN_ATTENTE'");
                if (rs4.next()) lblPendingCount.setText(String.valueOf(rs4.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAIInsights() {
        System.out.println("DEBUG: Starting AI Insights load for student " + STUDENT_ID);
        new Thread(() -> {
            try {
                StageCondidature profile = serviceCandidature.getProfileEtudiant(STUDENT_ID);
                if (profile != null) {
                    System.out.println("DEBUG: Profile found for student " + STUDENT_ID + ": " + profile.getDomaine());
                    JSONObject advice = serviceGroq.generateCareerAdvice(
                            profile.getDomaine(),
                            "Master", 
                            profile.getCompetences()
                    );

                    if (advice != null) {
                        System.out.println("DEBUG: AI Advice received: " + advice.toString());
                        Platform.runLater(() -> updateUI(advice));
                    } else {
                        System.out.println("DEBUG: AI Advice was NULL from Groq.");
                        // Fallback dummy for testing if API fails
                        JSONObject dummy = new JSONObject();
                        dummy.put("standing", 82);
                        dummy.put("skillGaps", new JSONArray().put("Maîtrise de Docker").put("Soft Skills").put("Anglais Technique"));
                        dummy.put("actionPlan", new JSONArray().put("Suivre une formation sur l'IA").put("Participer à des Hackathons"));
                        Platform.runLater(() -> updateUI(dummy));
                    }
                } else {
                    System.out.println("DEBUG: Profile NOT found for student " + STUDENT_ID + " in 'DEMANDE' type.");
                    // Last resort dummy
                    JSONObject dummy = new JSONObject();
                    dummy.put("standing", 65);
                    dummy.put("skillGaps", new JSONArray().put("Compléter votre profil").put("Ajouter des compétences"));
                    dummy.put("actionPlan", new JSONArray().put("Remplir la section 'Mon Profil'").put("Ajouter des expériences passées"));
                    Platform.runLater(() -> updateUI(dummy));
                }
            } catch (Exception e) {
                System.err.println("DEBUG: Error in loadAIInsights thread: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUI(JSONObject advice) {
        int standing = advice.optInt("standing", 50);
        lblStanding.setText(standing + "%");
        
        // Progress ring anim (dash offset)
        // Full circumference = 2 * PI * 70 = 439.8
        double offset = 439.8 * (1.0 - (standing / 100.0));
        circleProgress.setStrokeDashOffset(offset);

        // Skill Gaps
        flowSkillGaps.getChildren().clear();
        JSONArray gaps = advice.optJSONArray("skillGaps");
        if (gaps != null) {
            for (int i = 0; i < gaps.length(); i++) {
                Label pill = new Label(gaps.getString(i));
                pill.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-padding: 8 15; -fx-background-radius: 20; -fx-font-size: 13; -fx-border-color: #6366f1; -fx-border-radius: 20; -fx-border-width: 0.5;");
                flowSkillGaps.getChildren().add(pill);
            }
        }

        // Action Plan
        vboxActionPlan.getChildren().clear();
        JSONArray plan = advice.optJSONArray("actionPlan");
        if (plan != null) {
            for (int i = 0; i < plan.length(); i++) {
                HBox step = new HBox(15);
                step.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                step.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
                
                Label num = new Label(String.valueOf(i + 1));
                num.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 50;");
                
                Label desc = new Label(plan.getString(i));
                desc.setWrapText(true);
                desc.setStyle("-fx-text-fill: #334155; -fx-font-size: 14;");
                
                step.getChildren().addAll(num, desc);
                vboxActionPlan.getChildren().add(step);
            }
        }
    }
}

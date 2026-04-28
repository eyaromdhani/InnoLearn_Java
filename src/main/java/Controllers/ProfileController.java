package Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import Entities.Experience;
import Entities.StageCondidature;
import Services.ServiceExperience;
import Services.ServiceStageCondidature;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import utils.MyDatabase;
import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private TextField txtDomaine;
    @FXML private ComboBox<String> comboNiveau;
    @FXML private TextArea txtCompetences;
    @FXML private TextField txtLangues;
    @FXML private TextArea txtDescription;
    @FXML private TextArea txtLettreMotivation;
    @FXML private Label lblFileName;
    @FXML private Button btnChooseFile;
    @FXML private Label lblWelcome;
    @FXML private Label lblUserMeta;
    
    // Experience Form
    @FXML private Button btnAddExperience;
    @FXML private VBox experienceForm;
    @FXML private VBox timelineList;
    @FXML private ComboBox<String> expType;
    @FXML private TextField expPeriode;
    @FXML private TextField expEtablissement;
    @FXML private TextField expDomaine;
    @FXML private TextField expNiveau;
    @FXML private TextArea expDesc;

    private ServiceStageCondidature serviceCandidature;
    private ServiceExperience serviceExperience;
    private StageCondidature existingProfile;
    private String currentCVPath;
    private final int MOCK_STUDENT_ID = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize Services
        serviceCandidature = new ServiceStageCondidature(MyDatabase.getInstance().getConnection());
        serviceExperience = new ServiceExperience(MyDatabase.getInstance().getConnection());

        // Initialize academic levels
        comboNiveau.setItems(FXCollections.observableArrayList(
            "Licence", "Master", "Doctorat", "Ingénierie", "Technicien", "Baccalauréat"
        ));
        comboNiveau.setValue("Master");

        // Initialize experience types
        expType.setItems(FXCollections.observableArrayList("Formation", "Expérience"));
        expType.setValue("Formation");

        // Load existing profile and experiences
        loadUserData();
        loadExistingProfile();
        loadExperiences();
    }

    private void loadUserData() {
        try {
            java.sql.Connection conn = MyDatabase.getInstance().getConnection();
            try (java.sql.Statement st = conn.createStatement()) {
                // Fetching from table 'user' (ID 10 = Jihene)
                try (java.sql.ResultSet rs = st.executeQuery("SELECT * FROM user WHERE id = " + MOCK_STUDENT_ID)) {
                    if (rs.next()) {
                        String nom = rs.getString("name");
                        String email = rs.getString("email");
                        lblWelcome.setText("Profil de " + (nom != null ? nom : "Jihene"));
                        lblUserMeta.setText("Étudiante Esprit — " + (email != null ? email : "jihen@esprit.tn"));
                    }
                } catch (Exception e) {
                    System.err.println("Note: Could not load user labels from DB: " + e.getMessage());
                    lblWelcome.setText("Mon Profil Professionnel (Jihene)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExperiences() {
        timelineList.getChildren().clear();
        try {
            java.util.List<Experience> experiences = serviceExperience.getParEtudiant(MOCK_STUDENT_ID);
            for (int i = 0; i < experiences.size(); i++) {
                Experience exp = experiences.get(i);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExperienceItem.fxml"));
                    Node node = loader.load();
                    ExperienceItemController controller = loader.getController();
                    
                    boolean isLast = (i == experiences.size() - 1);
                    controller.setData(exp, isLast, this::loadExperiences);
                    
                    timelineList.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadExistingProfile() {
        try {
            existingProfile = serviceCandidature.getProfileEtudiant(MOCK_STUDENT_ID);
            if (existingProfile != null) {
                txtDomaine.setText(existingProfile.getDomaine());
                txtCompetences.setText(existingProfile.getCompetences());
                txtDescription.setText(existingProfile.getDescription());
                txtLettreMotivation.setText(existingProfile.getLettre_motivation());
                
                if (existingProfile.getCv() != null && !existingProfile.getCv().isEmpty()) {
                    currentCVPath = existingProfile.getCv();
                    File cvFile = new File(currentCVPath);
                    lblFileName.setText(cvFile.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleToggleExperienceForm(ActionEvent event) {
        boolean isVisible = experienceForm.isVisible();
        experienceForm.setVisible(!isVisible);
        experienceForm.setManaged(!isVisible);
        
        if (!isVisible) {
            btnAddExperience.setText("✕ Fermer");
            btnAddExperience.getStyleClass().add("btn-close-mini");
        } else {
            btnAddExperience.setText("+ Ajouter");
            btnAddExperience.getStyleClass().remove("btn-close-mini");
        }
    }

    @FXML
    private void handleConfirmAddExperience(ActionEvent event) {
        // Collect data
        String type = expType.getValue();
        String annee = expPeriode.getText();
        String etablissement = expEtablissement.getText();
        String domaine = expDomaine.getText();
        String niveau = expNiveau.getText();
        String desc = expDesc.getText();

        if (annee.isEmpty() || etablissement.isEmpty() || domaine.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir les informations principales.");
            return;
        }

        try {
            Experience newExp = new Experience(MOCK_STUDENT_ID, type, annee, etablissement, domaine, niveau, desc);
            serviceExperience.ajouter(newExp);
            
            // Reload UI
            loadExperiences();
            
            // Hide form
            handleToggleExperienceForm(null);
            
            // Clear fields
            expPeriode.clear();
            expEtablissement.clear();
            expDomaine.clear();
            expNiveau.clear();
            expDesc.clear();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'élément a été ajouté à votre parcours !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'expérience : " + e.getMessage());
        }
    }

    @FXML
    private void handleUploadPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre CV (PDF)");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );
        
        File selectedFile = fileChooser.showOpenDialog(btnChooseFile.getScene().getWindow());
        
        if (selectedFile != null) {
            lblFileName.setText(selectedFile.getName());
            currentCVPath = selectedFile.getAbsolutePath();
            System.out.println("Fichier sélectionné : " + currentCVPath);
        }
    }

    @FXML
    private void handleGenerateCV(ActionEvent event) {
        if (txtDomaine.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez préciser votre domaine pour générer le CV.");
            return;
        }

        // Show FileChooser for save location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Télécharger votre CV généré");
        String defaultName = "CV_" + txtDomaine.getText().replaceAll("\\s+", "_") + ".pdf";
        fileChooser.setInitialFileName(defaultName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        
        File destFile = fileChooser.showSaveDialog(btnChooseFile.getScene().getWindow());
        if (destFile == null) {
            return; // User cancelled
        }

        try {
            // Générer Accroche IA
            Services.ServiceGroq aiService = new Services.ServiceGroq();
            String accroche = aiService.generateCvIntro(
                    txtDomaine.getText(),
                    comboNiveau.getValue() != null ? comboNiveau.getValue() : "Non précisé",
                    txtCompetences.getText()
            );

            // Fetch current experiences
            java.util.List<Experience> experiences = serviceExperience.getParEtudiant(MOCK_STUDENT_ID);
            
            // Temporary profile object
            StageCondidature tempProfile = new StageCondidature();
            tempProfile.setDomaine(txtDomaine.getText());
            tempProfile.setCompetences(txtCompetences.getText());

            // Fetch User Details from Real DB (User/Utilisateur)
            String sName = null, sEmail = null, sPhone = null, sAddress = null;
            try {
                 java.sql.Connection conn = utils.MyDatabase.getInstance().getConnection();
                 try (java.sql.Statement st = conn.createStatement()) {
                    // Query table `user`
                    try (java.sql.ResultSet rs = st.executeQuery("SELECT * FROM user WHERE id = " + MOCK_STUDENT_ID)) {
                        if (rs.next()) {
                            sName = rs.getString("name");
                            sEmail = rs.getString("email");
                            
                            // Check for phone_number (from screenshot) or telephone
                            try { sPhone = rs.getString("phone_number"); } catch(Exception e) {
                                try { sPhone = rs.getString("telephone"); } catch(Exception e2) {}
                            }
                            
                            // Check for adresse or address
                            try { sAddress = rs.getString("adresse"); } catch(Exception e) {
                                try { sAddress = rs.getString("address"); } catch(Exception e2) {}
                            }
                        }
                    } catch(Exception e) {
                        System.out.println("Error on 'user' table fetch: " + e.getMessage());
                    }
                }
            } catch(Exception dbE) {
                System.out.println("DB Connection error when fetching student: " + dbE.getMessage());
            }

            // Generate PDF
            String sLangues = txtLangues.getText();
            String sInterests = "Technologies Web, IA, Lecture, Voyage."; // Default or parsed from description
            
            Services.ServicePDF pdfService = new Services.ServicePDF();
            String pathResult = pdfService.genererCvPdf(tempProfile, experiences, accroche, destFile.getAbsolutePath(), 
                                                        sName, sEmail, sPhone, sAddress, sLangues, sInterests);

            // Update UI
            currentCVPath = pathResult;
            lblFileName.setText(destFile.getName());
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre CV a été téléchargé avec succès au chemin:\n" + pathResult);
            
            // Ouvrir le fichier automatiquement si possible
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(new File(pathResult));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la génération: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        String domaine = txtDomaine.getText();
        String competences = txtCompetences.getText();
        String description = txtDescription.getText();
        String motivation = txtLettreMotivation.getText();
        
        if (domaine == null || domaine.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez renseigner votre domaine d'études.");
            return;
        }

        try {
            boolean isNew = (existingProfile == null);
            if (isNew) {
                existingProfile = new StageCondidature();
                existingProfile.setType_request("DEMANDE");
                existingProfile.setId_etudiant(MOCK_STUDENT_ID);
                existingProfile.setStatut("ACTIF");
                existingProfile.setDate_publication(Date.valueOf(LocalDate.now()));
            }

            existingProfile.setTitre("Profil : " + domaine);
            existingProfile.setDomaine(domaine);
            existingProfile.setCompetences(competences);
            existingProfile.setDescription(description);
            existingProfile.setLettre_motivation(motivation);
            existingProfile.setCv(currentCVPath);

            if (isNew) {
                serviceCandidature.ajouter(existingProfile);
                // After adding, we should probably fetch it again to get the generated ID
                existingProfile = serviceCandidature.getProfileEtudiant(MOCK_STUDENT_ID);
            } else {
                serviceCandidature.modifier(existingProfile);
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre profil a été mis à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de sauvegarder le profil : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}

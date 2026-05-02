package org.example.Controllers;

import org.example.Entities.G_user;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Services.UserService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UsersListController implements Initializable {

    @FXML private TableView<G_user> usersTable;
    @FXML private TableColumn<G_user, String> colId;
    @FXML private TableColumn<G_user, String> colName;
    @FXML private TableColumn<G_user, String> colUsername;
    @FXML private TableColumn<G_user, String> colEmail;
    @FXML private TableColumn<G_user, String> colPhone;
    @FXML private TableColumn<G_user, String> colActive;
    @FXML private TableColumn<G_user, String> colBanned;
    @FXML private TableColumn<G_user, String> colActions;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;

    private final UserService us = new UserService();
    private final ObservableList<G_user> allUsers = FXCollections.observableArrayList();

    private static final String BASE = "src/main/java/viewer/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadUsers();
    }

    private void setupColumns() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));
        colUsername.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUsername()));
        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));
        colPhone.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPhoneNumber()));
        colActive.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getActive() ? "✅" : "❌"));
        colBanned.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getBanned() ? "🚫" : "✅"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final HBox box = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("action-btn", "action-primary");
                deleteBtn.getStyleClass().addAll("action-btn", "action-warning");

                editBtn.setOnAction(e ->
                        openForm(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e ->
                        confirmDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadUsers() {
        try {
            List<G_user> users = us.read();
            allUsers.setAll(users);
            usersTable.setItems(allUsers);
            statusLabel.setText("Total: " + users.size() + " users");
        } catch (SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML private void onRefresh() { loadUsers(); }

    @FXML private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.sizeToScene();
        } catch (Exception e) {
            statusLabel.setText("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearch() {
        String q = searchField.getText().toLowerCase().trim();
        if (q.isEmpty()) {
            usersTable.setItems(allUsers);
            statusLabel.setText("Total: " + allUsers.size() + " users");
            return;
        }
        ObservableList<G_user> filtered = FXCollections.observableArrayList();
        for (G_user u : allUsers) {
            if (u.getName().toLowerCase().contains(q)   ||
                    u.getEmail().toLowerCase().contains(q)  ||
                    u.getUsername().toLowerCase().contains(q)) {
                filtered.add(u);
            }
        }
        usersTable.setItems(filtered);
        statusLabel.setText("Found: " + filtered.size() + " users");
    }

    @FXML private void onAddUser() { openForm(null); }

    private void openForm(G_user u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_form.fxml"));
            Parent root = loader.load();

            UserFormController ctrl = loader.getController();
            ctrl.setUser(u);
            ctrl.setOnSaved(this::loadUsers);

            Stage stage = new Stage();
            stage.setTitle(u == null ? "Add User" : "Edit — " + u.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void confirmDelete(G_user u) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete \"" + u.getName() + "\"?");
        alert.setContentText("This cannot be undone.");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    us.delete(u.getId());
                    loadUsers();
                } catch (SQLException e) {
                    statusLabel.setText("Delete error: " + e.getMessage());
                }
            }
        });
    }
}
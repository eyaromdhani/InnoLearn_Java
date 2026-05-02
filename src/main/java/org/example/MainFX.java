package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Controllers.CoursController;

public class MainFX extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        chargerPage("/captcha.fxml");
        stage.setTitle("InnoLearn");
        stage.show();
    }

    public static void chargerPage(String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainFX.class.getResource(fxmlPath));
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(loader.load(), 1200, 800);
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(loader.load());
        }
    }

    public static void chargerPageAvecCategorie(String fxmlPath, int categorieId) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainFX.class.getResource(fxmlPath));
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(loader.load(), 1200, 800);
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(loader.load());
        }
        CoursController controller = loader.getController();
        controller.initAvecCategorie(categorieId);
    }

    public static void main(String[] args) {launch(args);}
}
package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
       FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/PageAccueil.fxml"));

        try {
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("InnoLearn - Événements");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

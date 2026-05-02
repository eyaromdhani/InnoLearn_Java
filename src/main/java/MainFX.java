import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/captcha.fxml"));
        Parent root = loader.load();

        // Créer la scène
        Scene scene = new Scene(root, 1200, 800);
        // We do not add login.css to homepage, as it might mess up its layout, unless needed.
        
        // Configurer la fenêtre
        primaryStage.setTitle("InnoLearn - CAPTCHA Verification");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
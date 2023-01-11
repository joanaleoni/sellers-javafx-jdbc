package main;

import javafx.scene.image.Image;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

/**
 *
 * @author joana
 */
public class Main extends Application {

    private static Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/MainView.fxml"));
            ScrollPane scrollPane = loader.load();

            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);

            mainScene = new Scene(scrollPane);
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Sample JavaFX application");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../icon/icon.png")));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

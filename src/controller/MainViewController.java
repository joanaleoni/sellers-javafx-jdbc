package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.Main;
import util.AlertDialog;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class MainViewController implements Initializable {
    @FXML private MenuItem menuItemSeller;
    @FXML private MenuItem menuItemDepartment;
    @FXML private MenuItem menuItemAbout;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleMenuItemSeller(ActionEvent event) {
    }

    @FXML
    private void handleMenuItemDepartment(ActionEvent event) {
    }

    @FXML
    private void handleMenuItemAbout(ActionEvent event) {
        loadView("../view/AboutView.fxml", x -> {
        });
    }

    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            Scene mainScene = Main.getMainScene();
            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            Node mainMenu = mainVBox.getChildren().get(0);
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(mainMenu);
            mainVBox.getChildren().addAll(newVBox.getChildren());

            T controller = loader.getController();
            initializingAction.accept(controller);
        } catch (IOException e) {
            AlertDialog.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }
}

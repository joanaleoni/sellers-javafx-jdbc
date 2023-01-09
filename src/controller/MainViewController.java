package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

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
    }   
}
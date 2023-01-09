package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Main;
import model.domain.Department;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class DepartmentListController  implements Initializable {
    @FXML private TableView<Department> tableViewDepartment;
    @FXML private TableColumn<Department, Integer> tableColumnId;
    @FXML private TableColumn<Department, String> tableColumnName;
    @FXML private Button btNew;

    @FXML
    public void handleBtNew() {
        System.out.println("handleBtNewAction");
    }
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }
}
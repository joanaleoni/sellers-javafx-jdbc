package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Main;
import model.domain.Department;
import model.service.DepartmentService;

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

    private DepartmentService service;
    private List<Department> listDepartments;
    private ObservableList<Department> observableListDepartments;
    
    public void setDepartmentService(DepartmentService service){
        this.service = service;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }
    
    @FXML
    public void handleBtNew() {
        System.out.println("handleBtNewAction");
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }
    
    public void updataTableView(){
        if(service == null){
            throw new IllegalStateException("Service was null");
        }
        
        listDepartments = service.findAll();
        observableListDepartments = FXCollections.observableArrayList(listDepartments);
        tableViewDepartment.setItems(observableListDepartments);
    }
}
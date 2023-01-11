package controller;

import exception.DatabaseIntegrityException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import model.domain.Department;
import model.service.DepartmentService;
import util.AlertDialog;
import util.Utils;
import view.listener.DataChangeListener;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class DepartmentListController implements Initializable, DataChangeListener {
    @FXML private TableView<Department> tableViewDepartment;
    @FXML private TableColumn<Department, Integer> tableColumnId;
    @FXML private TableColumn<Department, String> tableColumnName;
    @FXML private TableColumn<Department, Department> tableColumnEDIT;
    @FXML private TableColumn<Department, Department> tableColumnREMOVE;
    @FXML private Button btNew;

    private DepartmentService service;
    private ObservableList<Department> observableListDepartments;

    public void setDepartmentService(DepartmentService service) {
        this.service = service;
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
    
    @FXML
    public void handleBtNew(ActionEvent event) {
        Stage parentStage = Utils.currentStage(event);
        Department dep = new Department();
        createDialogForm(dep, "../view/DepartmentForm.fxml", parentStage);
    }
    
    private void createDialogForm(Department department, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            DepartmentFormController controller = setDepartmentFormController(loader, department);     
            controller.subscribeDataChangeListener(this);  
            controller.updateFormData();
            
            Stage dialogStage = setDialogStage(pane);  
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }
    
    private DepartmentFormController setDepartmentFormController(FXMLLoader loader, Department department){
        DepartmentFormController controller = loader.getController();
        
        controller.setDepartment(department);
        controller.setDepartmentService(new DepartmentService());
        
        return controller;
    }
    
    private Stage setDialogStage(Pane pane){
        Stage dialogStage = new Stage();
        
        dialogStage.setTitle("Enter Department data");
        dialogStage.setScene(new Scene(pane));
        dialogStage.setResizable(false);
                
        return dialogStage;
    }
    
    private void removeEntity(Department department){
        Optional<ButtonType> result = AlertDialog.showConfirmation("Confirmation", "Are you sure to delete?");
        
        if(btConfirmClicked(result)){
            if(service == null){
                throw new IllegalStateException("Service was null");
            }
            
            try {
                service.remove(department);
                updateTableView();
            } catch(DatabaseIntegrityException e){
                AlertDialog.showAlert("Error removing department", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }
    
    private boolean btConfirmClicked(Optional<ButtonType> button){
        return button.get() == ButtonType.OK;
    }
            
    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        List<Department> listDepartments = service.findAll();
        observableListDepartments = FXCollections.observableArrayList(listDepartments);
        tableViewDepartment.setItems(observableListDepartments);
        initEditButtons();
        initRemoveButtons();
    }
    
    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("Edit");
            
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                
                setGraphic(button);
                button.setOnAction(
                event -> createDialogForm(
                obj, "../view/DepartmentForm.fxml",Utils.currentStage(event)));
                }
            });
    }
    
    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("Remove");

            @Override
            protected void updateItem(Department department, boolean empty) {
                super.updateItem(department, empty);

                if (department == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> removeEntity(department));
            }
        });
    }
    
    @Override
    public void onDataChanged() {
        updateTableView();
    }
}
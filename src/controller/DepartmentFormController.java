package controller;

import exception.DatabaseException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.domain.Department;
import model.service.DepartmentService;
import util.AlertDialog;
import util.Constraint;
import util.Utils;
import view.listener.DataChangeListener;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class DepartmentFormController implements Initializable {
    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private Label labelErrorName;
    @FXML private Button btSave;
    @FXML private Button btCancel;
    
    private Department department;
    private DepartmentService service;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    public void setDepartment(Department department) {
        this.department = department;
    }
    
    public void setDepartmentService(DepartmentService service) {
        this.service = service;
    }
    
    public void subscriceDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraint.setTextFieldInteger(txtId);
        Constraint.setTextFieldMaxLength(txtName, 30);
    }
    
    @FXML
    public void handleBtSave(ActionEvent event) {
        if(department == null) {
            throw new IllegalStateException("Entity was null");
        }
        if(service == null) {
            throw new IllegalStateException("Service was null");
        }
        
        try {
            department = getFormData();
            service.saveOrUpdate(department);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch(DatabaseException e){
            AlertDialog.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleBtCancel(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    public void updateFormData() {
        if (department == null) {
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(department.getId()));
        txtName.setText(department.getName());
    }

    private Department getFormData() {
        Department dep = new Department();
        dep.setId(Utils.tryParseToInt(txtId.getText()));
        dep.setName(txtName.getText());
        return dep;
    }
    
    private void notifyDataChangeListeners(){
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }
}

package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.domain.Department;
import util.Constraint;

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

    public void setDepartment(Department department) {
        this.department = department;
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
    public void handleBtSave() {
        System.out.println("handleBtSave");
    }

    @FXML
    public void handleBtCancel() {
        System.out.println("handleBtCancel");
    }

    public void updateFormData() {
        if (department == null) {
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(department.getId()));
        txtName.setText(department.getName());
    }
}

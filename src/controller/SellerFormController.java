package controller;

import exception.DatabaseException;
import exception.ValidationException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.domain.Department;
import model.domain.Seller;
import model.service.DepartmentService;
import model.service.SellerService;
import util.AlertDialog;
import util.Constraint;
import util.Utils;
import view.listener.DataChangeListener;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class SellerFormController implements Initializable {
    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpBirthDate;
    @FXML private TextField txtBaseSalary;
    @FXML private ComboBox<Department> cbDepartments;
    @FXML private Label labelErrorName;
    @FXML private Label labelErrorEmail;
    @FXML private Label labelErrorBirthDate;
    @FXML private Label labelErrorBaseSalary;
    @FXML private Button btSave;
    @FXML private Button btCancel;
    
    private Seller seller;
    private SellerService sellerService;
    private DepartmentService departmentService;
    private ObservableList<Department> observableListDepartments;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
    
    public void setServices(SellerService sellerService, DepartmentService departmentService) {
        this.sellerService = sellerService;
        this.departmentService = departmentService;
    }
    
    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraint.setTextFieldInteger(txtId);
        Constraint.setTextFieldMaxLength(txtName, 70);
        Constraint.setTextFieldDouble(txtBaseSalary);
        Constraint.setTextFieldMaxLength(txtId, 60);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
        initializeComboBoxDepartment();
    }
    
    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        
        cbDepartments.setCellFactory(factory);
        cbDepartments.setButtonCell(factory.call(null));
    }
    
    @FXML
    public void handleBtSave(ActionEvent event) {
        if(seller == null) {
            throw new IllegalStateException("Entity was null");
        }
        if(sellerService == null) {
            throw new IllegalStateException("Service was null");
        }
        
        try {
            seller = getFormData();
            sellerService.saveOrUpdate(seller);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch(ValidationException e){
            setErrorMessages(e.getErrors());
        } catch(DatabaseException e){
            AlertDialog.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        } 
    }
    
    private Seller getFormData() {
        Seller seller = new Seller();        
        seller.setId(Utils.tryParseToInt(txtId.getText()));
        
        if(fieldsAreValid()){
            seller.setName(txtName.getText());
            seller.setEmail(txtEmail.getText());
            Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
            seller.setBirthDate(Date.from(instant));
            seller.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
            seller.setDepartment(cbDepartments.getValue()); 
        }
        
        return seller;
    }
       
    private boolean fieldsAreValid(){
        ValidationException exception = new ValidationException("Validation error");
        
        if (!fieldIsValid(txtName)){
            exception.addError("name", "Field name can't be empty");
        }
        
        if (!fieldIsValid(txtEmail)){
            exception.addError("email", "Field email can't be empty");
        }
         
        if(dpBirthDate.getValue() == null) {
            exception.addError("birthDate", "Field birth date can't be empty");
         }
         
        if (!fieldIsValid(txtBaseSalary)){
            exception.addError("baseSalary", "Field base salary can't be empty");
        }
        
        if(exception.getErrors().size() > 0){
            throw exception;
        } 
        
        return true;
    }
    
    private boolean fieldIsValid(TextField textField){
        return !(textField.getText() == null) && !(textField.getText().trim().equals(""));
    }
    
    private void notifyDataChangeListeners(){
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }

    public void updateFormData() {
        if (seller == null) {
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(seller.getId()));
        txtName.setText(seller.getName());
        txtEmail.setText(seller.getEmail());
        Locale.setDefault(Locale.US);
        txtBaseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
        
        if(seller.getBirthDate() != null) {
            dpBirthDate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }
        
        if(seller.getDepartment() == null) {
            cbDepartments.getSelectionModel().selectFirst();
        } else {
            cbDepartments.setValue(seller.getDepartment());
        }        
    }
    
    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();        

        labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
        labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
        labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
        labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
    }
    
    @FXML
    public void handleBtCancel(ActionEvent event) {
        Utils.currentStage(event).close();
    }
    
    public void loadAssociatedDepartments(){
        if(departmentService == null){
            throw new IllegalStateException("DepartmentService was null");
        }
        List<Department> listDepartments = departmentService.findAll();
        observableListDepartments = FXCollections.observableArrayList(listDepartments);
        cbDepartments.setItems(observableListDepartments);
    }
}
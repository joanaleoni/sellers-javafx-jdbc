package controller;

import exception.DatabaseException;
import exception.ValidationException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.domain.Seller;
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
    @FXML private Label labelErrorName;
    @FXML private Label labelErrorEmail;
    @FXML private Label labelErrorBirthDate;
    @FXML private Label labelErrorBaseSalary;
    @FXML private Button btSave;
    @FXML private Button btCancel;
    
    private Seller seller;
    private SellerService service;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
    
    public void setSellerService(SellerService service) {
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
        Constraint.setTextFieldMaxLength(txtName, 70);
        Constraint.setTextFieldDouble(txtBaseSalary);
        Constraint.setTextFieldMaxLength(txtId, 60);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
    }
    
    @FXML
    public void handleBtSave(ActionEvent event) {
        if(seller == null) {
            throw new IllegalStateException("Entity was null");
        }
        if(service == null) {
            throw new IllegalStateException("Service was null");
        }
        
        try {
            seller = getFormData();
            service.saveOrUpdate(seller);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch(ValidationException e){
            setErrorMessages(e.getErrors());
        } catch(DatabaseException e){
            AlertDialog.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        } 
    }

    @FXML
    public void handleBtCancel(ActionEvent event) {
        Utils.currentStage(event).close();
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
    }

    private Seller getFormData() {
        Seller seller = new Seller();
        
        ValidationException exception = new ValidationException("Validation error");
        
        seller.setId(Utils.tryParseToInt(txtId.getText()));
        
        if (!nameIsValid(txtName)){
            exception.addError("name", "Field can't be empty");
        }
        seller.setName(txtName.getText());
        
        if(exception.getErrors().size() > 0){
            throw exception;
        }
        return seller;
    }
    
    private boolean nameIsValid(TextField txtName){
        return !(txtName.getText() == null) && !(txtName.getText().trim().equals(""));
    }
    
    private void notifyDataChangeListeners(){
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }
    
    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();        
        if(fields.contains("name")){
            labelErrorName.setText(errors.get("name"));
        }
    }
}
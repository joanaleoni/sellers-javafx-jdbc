package controller;

import exception.DatabaseIntegrityException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Main;
import model.domain.Seller;
import model.service.SellerService;
import util.AlertDialog;
import util.Utils;
import view.listener.DataChangeListener;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class SellerListController implements Initializable, DataChangeListener {
    @FXML private TableView<Seller> tableViewSeller;
    @FXML private TableColumn<Seller, Integer> tableColumnId;
    @FXML private TableColumn<Seller, String> tableColumnName;
    @FXML private TableColumn<Seller, String> tableColumnEmail;
    @FXML private TableColumn<Seller, Date> tableColumnBirthDate;
    @FXML private TableColumn<Seller, Double> tableColumnBaseSalary;
    @FXML private TableColumn<Seller, Seller> tableColumnEDIT;
    @FXML private TableColumn<Seller, Seller> tableColumnREMOVE;
    @FXML private Button btNew;

    private SellerService service;
    private ObservableList<Seller> observableListSellers;

    public void setSellerService(SellerService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));

        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
        
        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }
    
    @FXML
    public void handleBtNew(ActionEvent event) {
        Stage parentStage = Utils.currentStage(event);
        Seller dep = new Seller();
        createDialogForm(dep, "../view/SellerForm.fxml", parentStage);
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        List<Seller> listSellers = service.findAll();
        observableListSellers = FXCollections.observableArrayList(listSellers);
        tableViewSeller.setItems(observableListSellers);
        initEditButtons();
        initRemoveButtons();
    }
    
    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("Edit");
            
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                
                setGraphic(button);
                button.setOnAction(
                event -> createDialogForm(
                obj, "../view/SellerForm.fxml",Utils.currentStage(event)));
                }
            });
    } 


    private void createDialogForm(Seller seller, String absoluteName, Stage parentStage) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//            Pane pane = loader.load();
//
//            SellerFormController controller = loader.getController();
//            controller.setSeller(seller);
//            controller.setSellerService(new SellerService());
//            controller.subscriceDataChangeListener(this);
//            controller.updateFormData();
//
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("Enter Seller data");
//            dialogStage.setScene(new Scene(pane));
//            dialogStage.setResizable(false);
//            dialogStage.initOwner(parentStage);
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.showAndWait();
//        } catch (IOException e) {
//            AlertDialog.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//        }
    }
    
    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("Remove");

            @Override
            protected void updateItem(Seller seller, boolean empty) {
                super.updateItem(seller, empty);

                if (seller == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> removeEntity(seller));
            }
        });
    }
    
    private void removeEntity(Seller seller){
        Optional<ButtonType> result = AlertDialog.showConfirmation("Confirmation", "Are you sure to delete?");
        
        if(result.get() == ButtonType.OK){
            if(service == null){
                throw new IllegalStateException("Service was null");
            }
            
            try {
                service.remove(seller);
                updateTableView();
            } catch(DatabaseIntegrityException e){
                AlertDialog.showAlert("Error removing seller", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }
}

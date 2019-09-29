/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;

/**
 *
 * @author portl
 */
public class ProductScreenController implements Initializable {
    
    ObservableList<Part> partsSearchResultsList = FXCollections.observableArrayList();
    
    Product placeHolderProduct = new Product(0, null, 0, 0, 0, 0);
    
    private boolean addingNewProduct = true;
    private int newIdNumber = 5000;
    private int indexToUpdate = -1;

    
    @FXML
    private TextField searchTextField;

    @FXML
    private TextField idTextField;
    
    @FXML
    private Label ProductTitleLabel;

    @FXML
    private TextField partNameTextField;

    @FXML
    private TextField inventoryTextField;

    @FXML
    private TextField costTextField;

    @FXML
    private TextField maxTextField;

    @FXML
    private TextField minTextField;
    
    @FXML
    private TableView<Part> availablePartsTableView;

    @FXML
    private TableColumn<Part, Integer> partIDAvailableColumn;

    @FXML
    private TableColumn<Part, String> partNameAvailableColumn;

    @FXML
    private TableColumn<Part, Integer> inventoryLevelAvailableColumn;

    @FXML
    private TableColumn<Part, Double> pricePerUnitAvailableColumn;

    @FXML
    private Button addProductButton;
    
    @FXML
    private TableView<Part> addedPartsTableView;

    @FXML
    private TableColumn<Part, Integer> partIdAddedColumn;

    @FXML
    private TableColumn<Part, String> partNameAddedColumn;

    @FXML
    private TableColumn<Part, Integer> inventoryLevelAddedColumn;

    @FXML
    private TableColumn<Part, Double> pricePerUnitAddedColumn;

    
    /********ACTION EVENTS*********/
    @FXML
    void onActionAddPartToProduct(ActionEvent event) {
        placeHolderProduct.addAssociatedPart(availablePartsTableView.getSelectionModel().getSelectedItem());
        addedPartsTableView.setItems(placeHolderProduct.getAllAssociatedParts());

        //Populate the Associated Parts table, sorting the parts first.
        placeHolderProduct.getAllAssociatedParts().sort((a, b) -> Integer.compare(a.getId(), b.getId()));

    }

    @FXML
    void onActionDeletePartFromAddedTable(ActionEvent event) {

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the added part?");
        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            placeHolderProduct.deleteAssociatedParts(addedPartsTableView.getSelectionModel().getSelectedItem());
            addedPartsTableView.setItems(placeHolderProduct.getAllAssociatedParts());

            //Populate the Associated Parts table, sorting the parts first.
            placeHolderProduct.getAllAssociatedParts().sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        }
    }

    @FXML
    void onActionDisplayMainScreen(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Canceling will cause all data entered to be removed, and your changes will not be saved."
                + " Are you sure you wish to proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == ButtonType.OK)
            displayMenu("/view/MainScreen.fxml", event);
    }

    @FXML
    void onActionSaveProduct(ActionEvent event) throws IOException {

        //Validates Inventory criteria is met.
        if (!isInventoryValid(Integer.parseInt(minTextField.getText()), Integer.parseInt(maxTextField.getText()), Integer.parseInt(inventoryTextField.getText()))) {
            Alert inventoryAlert = new Alert(Alert.AlertType.ERROR, "The inventory minimum, maximum, and quantities do not compute. Please re-check the figures "
                    + "and try again.\n"
                    + "Minimum: " + minTextField.getText() + "\n"
                    + "Maximum: " + maxTextField.getText() + "\n"
                    + "Inventory: " + inventoryTextField.getText());
            inventoryAlert.showAndWait();
            return;
        }
        
        //Validates at least one part is added to the Product
        if(placeHolderProduct.getAllAssociatedParts().isEmpty()){
            Alert noAssociatedPartsAlert = new Alert(Alert.AlertType.ERROR, "You must have, at a minimum, one part added to the Product. "
                    + "Please add a part and try saving again.");
            noAssociatedPartsAlert.showAndWait();
            return;
        }

        try {
            
            //Validates that the total cost of all parts is equal to or less than the total cost of the Product
            //This validation needed to take place here because of the potential for a NumberFormatException if the field was blank at Save.
            double partTotal = 0;
            
            for(Part part : placeHolderProduct.getAllAssociatedParts())
                partTotal += part.getPrice();
            
            if (Integer.parseInt(costTextField.getText()) >= partTotal)
                saveProduct();
            else{
                Alert priceLessThanPartTotalAlert = new Alert(Alert.AlertType.ERROR, "The price of the product is less than the combined price of "
                        + "all added parts. Please correct and try again.");
                priceLessThanPartTotalAlert.showAndWait();
                return;
            }
        } 
        
        catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please verify data entered is in the correct format and try again."
                    + " Please note that all fields are required, and all fields require numeric input, except for the name field.");
            alert.showAndWait();
            return;
        }
        
        //Sorts the list for easier navigation
        Inventory.getAllProducts().sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        displayMenu("/view/MainScreen.fxml", event);
    }

    @FXML
    void onActionSearch(ActionEvent event) {
        String searchText = searchTextField.getText();

        if (searchText.isEmpty()) {
            availablePartsTableView.setItems(Inventory.getAllParts());
            return;
        }

        partsSearchResultsList.clear();

        if (searchStringIsNumber(searchTextField.getText())) {
            partsSearchResultsList.add(Inventory.lookupPart(Integer.parseInt(searchTextField.getText())));
            System.out.println(String.valueOf(Integer.parseInt(searchTextField.getText())));
        } else {
            partsSearchResultsList = Inventory.lookupPart(searchTextField.getText());
        }

        if (partsSearchResultsList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The searched part was not found.");
            alert.showAndWait();
        } else {
            availablePartsTableView.setItems(partsSearchResultsList);
        }
    }
    
    /****** CUSTOM METHODS ********/
    
    //Determines if the minimum inventory level is less than or equal to the max inventory level, and if the inventory is within those limits
    private boolean isInventoryValid(int min, int max, int inventory) {
        if (min > max) {
            return false;
        } else {
            return inventory <= max && inventory >= min;
        }
    }
    
    //Determines if the search string is a number or string to determine which overloaded method to use
    private boolean searchStringIsNumber(String searchString) {
        try {
            int aNumber = Integer.parseInt(searchString);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    //Logic for saving the product, once all validation is checked/approved in the Action event.
    private void saveProduct(){
        int id = Integer.parseInt(idTextField.getText());
        String partName = partNameTextField.getText();
        int inventory = Integer.parseInt(inventoryTextField.getText());
        double cost = Double.parseDouble(costTextField.getText());
        int max = Integer.parseInt(maxTextField.getText());
        int min = Integer.parseInt(minTextField.getText());
        
        //Irregardless if the product is added or updated, it will be replaced with this new object
        Product newProduct = new Product(id, partName, cost, inventory, min, max);

        if(addingNewProduct){
            for(Part part : placeHolderProduct.getAllAssociatedParts())
                newProduct.addAssociatedPart(part);
            Inventory.addProduct(newProduct);
        }
        else{
            
            for (Part part : placeHolderProduct.getAllAssociatedParts()) {
                newProduct.addAssociatedPart(part);
            }
            Inventory.updateProduct(this.indexToUpdate, newProduct);
        }
        
    }
    
    //Method used to send Product selected from the Main Screen to the Product Screen
    public void sendProductToModifyProductWindow(Product product, int indexToUpdate) {

        ProductTitleLabel.setText("Modify Part");
        idTextField.setText(String.valueOf(product.getId()));
        partNameTextField.setText(product.getName());
        inventoryTextField.setText(String.valueOf(product.getStock()));
        costTextField.setText(String.valueOf(product.getPrice()));
        maxTextField.setText(String.valueOf(product.getMax()));
        minTextField.setText(String.valueOf(product.getMin()));
        this.indexToUpdate = indexToUpdate;
        addingNewProduct = false;

        //Populate the Associated Parts table, sorting the parts first.
        product.getAllAssociatedParts().sort((a,b ) -> Integer.compare(a.getId(), b.getId()));
        
        for(Part part: product.getAllAssociatedParts()){
            placeHolderProduct.addAssociatedPart(part);
        }
        
        addedPartsTableView.setItems(placeHolderProduct.getAllAssociatedParts());
        partIdAddedColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameAddedColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        inventoryLevelAddedColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        pricePerUnitAddedColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
    }
    
    //Gets the next available ID number (starting at 5,000 for Products)
    private String getAutoGeneratedId() {
        if (Inventory.getAllParts().isEmpty()) {
            return String.valueOf(newIdNumber);
        } else {
            newIdNumber = Inventory.getAllParts().get(0).getId();
        }
        for (Product product : Inventory.getAllProducts()) {
            if (product.getId() > newIdNumber) {
                newIdNumber = product.getId();
            }
        }
        newIdNumber += 1;
        return String.valueOf(newIdNumber);
    }
    
    //Used to display a screen, passing in the path as a parameter
    private void displayMenu(String path, ActionEvent event) throws IOException {

        Stage stage;
        Parent scene;

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(path));
        stage.setScene(new Scene(scene));
        stage.show();

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (addingNewProduct) {
            idTextField.setText(getAutoGeneratedId());
        }
        // Populate Available Parts Table
        availablePartsTableView.setItems(Inventory.getAllParts());
        partIDAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        inventoryLevelAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        pricePerUnitAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("price")); 
        
        //Populate a blank Added Parts Table a the start of the form
        addedPartsTableView.setItems(placeHolderProduct.getAllAssociatedParts());
        partIdAddedColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameAddedColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        inventoryLevelAddedColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        pricePerUnitAddedColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

    }    

}

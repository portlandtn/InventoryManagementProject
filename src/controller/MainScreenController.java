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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;

/**
 *
 * @author portl
 */
public class MainScreenController implements Initializable {
        
    @FXML
    private TextField partsSearchTextField;
    
    @FXML
    private TableView<Part> partsTableView;

    @FXML
    private TableColumn<Part, Integer> partIdPartsTableColumn;

    @FXML
    private TableColumn<Part, String> partNamePartsTableColumn;

    @FXML
    private TableColumn<Part, Integer> inventoryLevelPartsTableColumn;

    @FXML
    private TableColumn<Part, Double> pricePerUnitPartsTableColumn;

    @FXML
    private TextField productsSearchTextField;
    
    @FXML
    private TableView<Product> productsTableView;

    @FXML
    private TableColumn<Product, Integer> productIdProductTableColumn;

    @FXML
    private TableColumn<Product, String> productNameProductTableColumn;

    @FXML
    private TableColumn<Product, Integer> inventoryLevelProductTableColumn;

    @FXML
    private TableColumn<Product, String> pricePerUnitProductTableColumn;

    
    //ACTION EVENTS
    @FXML
    void onActionDeleteParts(ActionEvent event) {
        
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the selected part?");
        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK)
            Inventory.deletePart(partsTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onActionDeleteProducts(ActionEvent event) {
        
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the selected product?");
        Optional<ButtonType> result = deleteAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK)
            Inventory.deleteProduct(productsTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onActionDisplayAddPartsScreen(ActionEvent event) throws IOException {
        //PartScreenController.isAddingPart = true;
        displayScreen("/view/PartScreen.fxml", event);
    }

    @FXML
    void onActionDisplayAddProductsScreen(ActionEvent event) throws IOException {

        //ProductScreenController.isAddingProduct = true;
        displayScreen("/view/ProductScreen.fxml", event);

    }

    @FXML
    void onActionDisplayModifyPartsScreen(ActionEvent event) throws IOException {
        
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/PartScreen.fxml"));
            loader.load();

            PartScreenController PartController = loader.getController();
            PartController.sendPartToModifyPartWindow((Part) partsTableView.getSelectionModel().getSelectedItem(),partsTableView.getSelectionModel().getSelectedIndex());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (NullPointerException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a part to modify.");
            alert.showAndWait();
        }
    }

    @FXML
    void onActionDisplayModifyProductsScreen(ActionEvent event) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/ProductScreen.fxml"));
            loader.load();

            ProductScreenController ProductController = loader.getController();
            ProductController.sendProductToModifyProductWindow((Product) productsTableView.getSelectionModel().getSelectedItem(), productsTableView.getSelectionModel().getSelectedIndex());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (NullPointerException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a product to modify.");
            alert.showAndWait();
        }
    }

    @FXML
    void onActionSearchParts(ActionEvent event) {
        ObservableList<Part> partsSearchResultsList = FXCollections.observableArrayList();

        String searchText = partsSearchTextField.getText();
        
        if(searchText.isEmpty()){
            partsTableView.setItems(Inventory.getAllParts());
            return;
        }
        
        partsSearchResultsList.clear();
        
        if(searchStringIsNumber(partsSearchTextField.getText())){
            partsSearchResultsList.add(Inventory.lookupPart(Integer.parseInt(partsSearchTextField.getText())));
            System.out.println(String.valueOf(Integer.parseInt(partsSearchTextField.getText())));
        }
        else{
            partsSearchResultsList = Inventory.lookupPart(partsSearchTextField.getText());
        }

        if(partsSearchResultsList.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The searched part was not found.");
            alert.showAndWait();
        }
        else{
            partsTableView.setItems(partsSearchResultsList);
        }

    }

    @FXML
    void onActionSearchProducts(ActionEvent event) {
        ObservableList<Product> productsSearchResultsList = FXCollections.observableArrayList();
        
        String searchText = productsSearchTextField.getText();

        if (searchText.isEmpty()) {
            productsTableView.setItems(Inventory.getAllProducts());
            return;
        }

        productsSearchResultsList.clear();

        if (searchStringIsNumber(productsSearchTextField.getText())) {
            productsSearchResultsList.add(Inventory.lookupProduct(Integer.parseInt(productsSearchTextField.getText())));
            System.out.println(String.valueOf(Integer.parseInt(productsSearchTextField.getText())));
        } else {
            productsSearchResultsList = Inventory.lookupProduct(productsSearchTextField.getText());
        }

        if (productsSearchResultsList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The searched product was not found.");
            alert.showAndWait();
        } else {
            productsTableView.setItems(productsSearchResultsList);
        }
    }
    
    @FXML
    void onActionExit(ActionEvent event) {
        System.exit(0);
    }
    
    /*** CUSTOM METHODS ***/
    
    //Determines if the search string is a number or string to determine which overloaded method to use
    private boolean searchStringIsNumber(String searchString){
        try{
            int aNumber = Integer.parseInt(searchString);
            return true;
        }
        catch(NumberFormatException ex){
            return false;
        }
    }
        
    //Used to display a screen, passing in the path as a parameter
    private void displayScreen(String path, ActionEvent event) throws IOException {

        Stage stage;
        Parent scene;

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(path));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Sorts the parts list for easier navigation
        Inventory.getAllParts().sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        
        //Sorts the products list for easier navigation
        Inventory.getAllProducts().sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        
        // Populate Parts Table
        partsTableView.setItems(Inventory.getAllParts());
        partIdPartsTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNamePartsTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        inventoryLevelPartsTableColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        pricePerUnitPartsTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        //Populate Products Table
        productsTableView.setItems(Inventory.getAllProducts());
        productIdProductTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameProductTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        inventoryLevelProductTableColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        pricePerUnitProductTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

    }    
}

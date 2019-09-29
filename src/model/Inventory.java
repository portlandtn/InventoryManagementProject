/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author portl
 */
public class Inventory {
    
    //collections holding all parts and products
    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();
    
    
    public static void addPart(Part part){
        allParts.add(part);
    }
    
    public static void addProduct(Product product){
        allProducts.add(product);
    }
    
    //Looks up parts and displays them in the tableview on the Main Screen, based on Part ID entered.
    public static Part lookupPart(int partId){
                
        for (Part part : Inventory.getAllParts())
        {
            if(part.getId() == partId){
                return part;
            }
        }
        return null;
    }
    
    //Looks up products and displays them in the tableview on the Main Screen, based on Product ID entered.
    public static Product lookupProduct(int productId){
        
        for (Product product : Inventory.getAllProducts()) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
    
    //Looks up parts and displays them in the tableview on the Main Screen, based on Part name entered.
    public static ObservableList<Part> lookupPart(String partName){
        
        ObservableList<Part> filteredParts = FXCollections.observableArrayList();
        
        for (Part part : Inventory.getAllParts()){
            if(part.getName().toUpperCase().contains(partName.toUpperCase())){
                filteredParts.add(part);
            }
        }
        return filteredParts;
    }
    
    //Looks up products and displays them in the tableview on the Main Screen, based on Product Name entered.
    public static ObservableList<Product> lookupProduct(String productName){
        
        ObservableList<Product> filteredProducts = FXCollections.observableArrayList();

        for (Product product : Inventory.getAllProducts()) {
            if (product.getName().toUpperCase().contains(productName.toUpperCase())) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }
    
    public static void updatePart(int index, Part selectedPart){
        allParts.set(index, selectedPart);
    }
    
    public static void updateProduct (int index, Product selectedProduct){
        allProducts.set(index, selectedProduct);
        
    }
    
    public static void deletePart (Part selectedPart){
        getAllParts().remove(selectedPart);
    }
    
    public static void deleteProduct(Product selectedProduct){
        getAllProducts().remove(selectedProduct);
    }
    
    public static ObservableList<Part> getAllParts(){
        return allParts;
    }
    
    public static ObservableList<Product> getAllProducts () {
        return allProducts;
    }
}

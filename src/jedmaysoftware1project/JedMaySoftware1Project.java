/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jedmaysoftware1project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.InHouse;
import model.Inventory;
import model.Product;
import model.Outsourced;

/**
 *
 * @author portl
 */
public class JedMaySoftware1Project extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //This method call is to pouplate sample data for testing only. It should be removed prior to release for Production.
        populateSampleData();
        
        launch(args);

    }
    
    /**
     * * This method exists to only populate the project with sample data. 
     * * Simply comment out the call to this method in the main method to prevent data from being populated.
     */
    private static void populateSampleData(){
        
        //Populate Sample Data
        
        //Products
        Product halfInchBoltAssembly = new Product(5000, "1/2-Inch Hardened Bolt Assembly", 19.28, 5, 1, 10);
        Product threeQuarterInchBoltAssembly = new Product(5001, "3/4-Inch Hardened Bolt Assembly", 325.00, 1005, 100, 10000);

        //In-House Parts
        InHouse halfInchWasher = new InHouse(1, "1/2-Inch Washer", 0.03 , 28, 1, 15000, 0502);
        InHouse halfInchNut = new InHouse(2, "1/2-Inch Nut", 0.015, 228, 1, 1000, 0501);
        InHouse threeQuarterInchWasher = new InHouse(3, "3/4-Inch Washer", 0.08, 50, 1, 10000, 1202);
        InHouse threeQuarterInchNut = new InHouse(4, "3/4-Inch Nut", 0.05, 5, 1, 10000, 1201);
        
        //Add parts to Inventory
        Inventory.addPart(halfInchWasher);
        Inventory.addPart(halfInchNut);
        Inventory.addPart(threeQuarterInchWasher);
        Inventory.addPart(threeQuarterInchNut);

        //Outsourced Parts
        Outsourced halfInchThreadedBolt = new Outsourced(6, "1/2-Inch Threaded Bolt", 12.31, 280, 11, 1000, "Bolt Company");
        Outsourced threeQuarterInchThreadedBolt = new Outsourced(5, "3/4-Inch Threaded Bolt", 12.4831, 24, 12, 1000, "Bolted");
        
        //Add parts to Inventory
        Inventory.addPart(halfInchThreadedBolt);
        Inventory.addPart(threeQuarterInchThreadedBolt);
        
        //Add associated parts to the Products
        halfInchBoltAssembly.addAssociatedPart(halfInchWasher);
        halfInchBoltAssembly.addAssociatedPart(halfInchNut);
        halfInchBoltAssembly.addAssociatedPart(halfInchThreadedBolt);
        
        threeQuarterInchBoltAssembly.addAssociatedPart(threeQuarterInchThreadedBolt);
        threeQuarterInchBoltAssembly.addAssociatedPart(threeQuarterInchNut);
        
        //Add Aseembled Products to Inventory
        Inventory.addProduct(halfInchBoltAssembly);        
        Inventory.addProduct(threeQuarterInchBoltAssembly);

    }
    
}

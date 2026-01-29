package com.munna;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        // ===== Shop Name =====
    	Label shopName = new Label("ABC MOBILE SHOP");
    	shopName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    	Label shopAddress = new Label("Main Road, Hyderabad | Phone: 9XXXXXXXXX");
    	shopAddress.setStyle("-fx-font-size: 12px;");


        // ===== Invoice Info =====
        Label invoiceNo = new Label("Invoice No: 1001");
        Label date = new Label("Date: " + LocalDate.now());

        // ===== Customer Fields =====
        Label customerNameLabel = new Label("Customer Name:");
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Enter customer name");

        Label mobileLabel = new Label("Mobile Number:");
        TextField mobileField = new TextField();
        mobileField.setPromptText("Enter mobile number");

        // ===== Customer Grid =====
        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(10);
        customerGrid.setVgap(10);

        customerGrid.add(customerNameLabel, 0, 0);
        customerGrid.add(customerNameField, 1, 0);
        customerGrid.add(mobileLabel, 0, 1);
        customerGrid.add(mobileField, 1, 1);

        // ===== Main Layout =====
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                shopName,
                shopAddress,
                invoiceNo,
                date,
                customerGrid
        );


        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Offline Billing System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

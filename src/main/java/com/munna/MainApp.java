package com.munna;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MainApp extends Application {

    private final ObservableList<Item> items = FXCollections.observableArrayList();
    private double grandTotal = 0.0;
    private Label totalLabel = new Label("Grand Total: 0.00");

    @Override
    public void start(Stage stage) {

        // ===== Shop Header =====
        Label shopName = new Label("ABC MOBILE SHOP");
        shopName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label shopAddress = new Label("Main Road, Hyderabad | Phone: 9XXXXXXXXX");

        Label invoiceNo = new Label("Invoice No: 1001");
        Label date = new Label("Date: " + LocalDate.now());

        // ===== Customer Fields =====
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");

        TextField mobileField = new TextField();
        mobileField.setPromptText("Mobile Number");

        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(10);
        customerGrid.setVgap(10);
        customerGrid.add(new Label("Customer Name:"), 0, 0);
        customerGrid.add(customerNameField, 1, 0);
        customerGrid.add(new Label("Mobile:"), 0, 1);
        customerGrid.add(mobileField, 1, 1);

        // ===== Item Entry Fields =====
        TextField itemNameField = new TextField();
        itemNameField.setPromptText("Item Name");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Qty");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button addItemBtn = new Button("Add Item");

        HBox itemEntryBox = new HBox(10, itemNameField, qtyField, priceField, addItemBtn);

        // ===== Items Table =====
        TableView<Item> table = new TableView<>();
        table.setItems(items);
        table.setPrefHeight(250);

        TableColumn<Item, String> nameCol = new TableColumn<>("Item Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);

        TableColumn<Item, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(80);

        TableColumn<Item, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(120);

        TableColumn<Item, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(120);

        table.getColumns().addAll(nameCol, qtyCol, priceCol, amountCol);

        // ===== Add Item Logic =====
        addItemBtn.setOnAction(e -> {
            try {
                String name = itemNameField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                double price = Double.parseDouble(priceField.getText());

                if (name.isEmpty() || qty <= 0 || price <= 0) {
                    return;
                }

                Item item = new Item(name, qty, price);
                items.add(item);

                grandTotal += item.getAmount();
                totalLabel.setText("Grand Total: " + grandTotal);

                itemNameField.clear();
                qtyField.clear();
                priceField.clear();

            } catch (Exception ex) {
                // ignore for now (we’ll add validation later)
            }
        });

        // ===== Main Layout =====
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                shopName,
                shopAddress,
                invoiceNo,
                date,
                customerGrid,
                new Label("Add Item"),
                itemEntryBox,
                table,
                totalLabel
        );

        Scene scene = new Scene(root, 900, 650);
        stage.setTitle("Offline Billing System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

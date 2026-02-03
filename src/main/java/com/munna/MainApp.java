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

        System.out.println("APP STARTED");
        DBUtil.initDatabase();

        /* ================= SHOP HEADER ================= */
        Label shopName = new Label("ABC MOBILE SHOP");
        shopName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label shopAddress = new Label("Main Road, Hyderabad | Phone: 9XXXXXXXXX");

        Label invoiceNo = new Label("Invoice No: 1001");
        Label date = new Label("Date: " + LocalDate.now());

        /* ================= CUSTOMER DETAILS ================= */
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");
        // Customer Name: letters and space only
        customerNameField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("[a-zA-Z ]*") ? change : null
        ));


        TextField mobileField = new TextField();
        mobileField.setPromptText("Mobile Number");

        // Mobile → digits only while typing
        mobileField.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d*") ? c : null
        ));

        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(10);
        customerGrid.setVgap(10);
        customerGrid.add(new Label("Customer Name:"), 0, 0);
        customerGrid.add(customerNameField, 1, 0);
        customerGrid.add(new Label("Mobile:"), 0, 1);
        customerGrid.add(mobileField, 1, 1);

        /* ================= ITEM ENTRY ================= */
        TextField itemNameField = new TextField();
        itemNameField.setPromptText("Item Name");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Qty");
        qtyField.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d*") ? c : null
        ));

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d*(\\.\\d*)?") ? c : null
        ));

        Button addItemBtn = new Button("Add Item");
        Button deleteItemBtn = new Button("Delete Selected");
        Button saveBillBtn = new Button("Save Bill");

        HBox itemEntryBox = new HBox(10,
                itemNameField, qtyField, priceField, addItemBtn, deleteItemBtn
        );

        /* ================= TABLE ================= */
        TableView<Item> table = new TableView<>(items);
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

        /* ================= ADD ITEM LOGIC ================= */
        addItemBtn.setOnAction(e -> {

            String name = itemNameField.getText().trim();
            String qtyText = qtyField.getText().trim();
            String priceText = priceField.getText().trim();

            if (!name.matches("[a-zA-Z0-9 _\\-/]+")) {
                showAlert("Item name can contain letters, numbers, space, -, _, / only.");
                return;
            }

            if (qtyText.isEmpty() || priceText.isEmpty()) {
                showAlert("Quantity and Price are required.");
                return;
            }

            int qty = Integer.parseInt(qtyText);
            double price = Double.parseDouble(priceText);

            if (qty <= 0 || price <= 0) {
                showAlert("Quantity and Price must be greater than zero.");
                return;
            }

            Item item = new Item(name, qty, price);
            items.add(item);

            grandTotal += item.getAmount();
            updateTotalLabel();

            itemNameField.clear();
            qtyField.clear();
            priceField.clear();
        });

        /* ================= DELETE ITEM ================= */
        deleteItemBtn.setOnAction(e -> {
            Item selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Please select an item to delete.");
                return;
            }
            items.remove(selected);
            grandTotal -= selected.getAmount();
            updateTotalLabel();
        });

        /* ================= SAVE BILL ================= */
        saveBillBtn.setOnAction(e -> {

            String customerName = customerNameField.getText().trim();
            String mobile = mobileField.getText().trim();

            if (!customerName.matches("[a-zA-Z ]+")) {
                showAlert("Customer name must contain only letters.");
                return;
            }

            if (!mobile.matches("\\d{10}")) {
                showAlert("Mobile number must be exactly 10 digits.");
                return;
            }

            if (items.isEmpty()) {
                showAlert("Add at least one item.");
                return;
            }

            int billId = DBUtil.saveBill(
                    LocalDate.now().toString(),
                    customerName,
                    mobile,
                    grandTotal
            );

            if (billId == -1) {
                showAlert("Failed to save bill.");
                return;
            }

            DBUtil.saveBillItems(billId, items);


            showInfo("Bill saved successfully!");

            items.clear();
            grandTotal = 0;
            updateTotalLabel();
        });

        /* ================= LAYOUT ================= */
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
                totalLabel,
                saveBillBtn
        );

        stage.setScene(new Scene(root, 900, 650));
        stage.setTitle("Offline Billing System");
        stage.show();
    }

    /* ================= HELPERS ================= */
    private void updateTotalLabel() {
        totalLabel.setText("Grand Total: " + String.format("%.2f", grandTotal));
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Success");
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}

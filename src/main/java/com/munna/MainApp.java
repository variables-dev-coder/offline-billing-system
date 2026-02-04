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

    private TableView<Bill> billTable = new TableView<>();
    private TableView<Item> billItemTable = new TableView<>();

    @Override
    public void start(Stage stage) {

        DBUtil.initDatabase();

        Label shopName = new Label("ABC MOBILE SHOP");
        shopName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label shopAddress = new Label("Main Road, Hyderabad");
        Label date = new Label("Date: " + LocalDate.now());

        /* ===== CUSTOMER ===== */
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");
        customerNameField.setTextFormatter(
                new TextFormatter<>(c ->
                        c.getControlNewText().matches("[a-zA-Z ]*") ? c : null)
        );

        TextField mobileField = new TextField();
        mobileField.setPromptText("Mobile");
        mobileField.setTextFormatter(
                new TextFormatter<>(c ->
                        c.getControlNewText().matches("\\d*") ? c : null)
        );

        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(10);
        customerGrid.setVgap(10);
        customerGrid.add(new Label("Customer:"), 0, 0);
        customerGrid.add(customerNameField, 1, 0);
        customerGrid.add(new Label("Mobile:"), 0, 1);
        customerGrid.add(mobileField, 1, 1);

        /* ===== ITEM ENTRY ===== */
        TextField itemNameField = new TextField();
        itemNameField.setPromptText("Item");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Qty");
        qtyField.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d*") ? c : null));

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d*(\\.\\d*)?") ? c : null));

        Button addItemBtn = new Button("Add Item");
        Button deleteItemBtn = new Button("Delete");
        Button saveBillBtn = new Button("Save Bill");
        Button pdfBtn = new Button("Generate PDF");

        HBox itemBox = new HBox(10, itemNameField, qtyField, priceField, addItemBtn, deleteItemBtn);

        /* ===== CURRENT ITEMS TABLE ===== */
        TableView<Item> itemTable = new TableView<>(items);

        TableColumn<Item, String> iName = new TableColumn<>("Item");
        iName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Item, Integer> iQty = new TableColumn<>("Qty");
        iQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Item, Double> iPrice = new TableColumn<>("Price");
        iPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Item, Double> iAmt = new TableColumn<>("Amount");
        iAmt.setCellValueFactory(new PropertyValueFactory<>("amount"));

        itemTable.getColumns().setAll(iName, iQty, iPrice, iAmt);
        itemTable.setPrefHeight(200);

        addItemBtn.setOnAction(e -> {
            if (itemNameField.getText().isEmpty()
                    || qtyField.getText().isEmpty()
                    || priceField.getText().isEmpty()) {
                alert("Fill all item fields");
                return;
            }

            Item item = new Item(
                    itemNameField.getText(),
                    Integer.parseInt(qtyField.getText()),
                    Double.parseDouble(priceField.getText())
            );

            items.add(item);
            grandTotal += item.getAmount();
            updateTotal();

            itemNameField.clear();
            qtyField.clear();
            priceField.clear();
        });

        deleteItemBtn.setOnAction(e -> {
            Item selected = itemTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                items.remove(selected);
                grandTotal -= selected.getAmount();
                updateTotal();
            }
        });

        saveBillBtn.setOnAction(e -> {
            int billId = DBUtil.saveBill(
                    LocalDate.now().toString(),
                    customerNameField.getText(),
                    mobileField.getText(),
                    grandTotal
            );
            DBUtil.saveBillItems(billId, items);
            billTable.setItems(DBUtil.getAllBills());
            info("Bill saved");
            items.clear();
            grandTotal = 0;
            updateTotal();
        });

        pdfBtn.setOnAction(e -> {
            PDFUtil.generateSamplePDF();
            info("PDF generated");
        });

        /* ===== BILL HISTORY ===== */
        TableColumn<Bill, Integer> bId = new TableColumn<>("ID");
        bId.setCellValueFactory(new PropertyValueFactory<>("billId"));

        TableColumn<Bill, String> bDate = new TableColumn<>("Date");
        bDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Bill, String> bCust = new TableColumn<>("Customer");
        bCust.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Bill, Double> bTotal = new TableColumn<>("Total");
        bTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        billTable.getColumns().setAll(bId, bDate, bCust, bTotal);
        billTable.setItems(DBUtil.getAllBills());

        billTable.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
            if (c != null) {
                billItemTable.setItems(DBUtil.getItemsByBillId(c.getBillId()));
            }
        });

        TableColumn<Item, String> biName = new TableColumn<>("Item");
        biName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Item, Integer> biQty = new TableColumn<>("Qty");
        biQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Item, Double> biAmt = new TableColumn<>("Amount");
        biAmt.setCellValueFactory(new PropertyValueFactory<>("amount"));

        billItemTable.getColumns().setAll(biName, biQty, biAmt);

        VBox content = new VBox(15,
                shopName, shopAddress, date,
                customerGrid,
                itemBox,
                itemTable,
                totalLabel,
                saveBillBtn,
                pdfBtn,
                new Label("Bills"),
                billTable,
                new Label("Bill Items"),
                billItemTable
        );
        content.setPadding(new Insets(20));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);

        stage.setScene(new Scene(scroll, 900, 700));
        stage.setTitle("Offline Billing System");
        stage.show();
    }

    private void updateTotal() {
        totalLabel.setText("Grand Total: " + String.format("%.2f", grandTotal));
    }

    private void alert(String m) {
        new Alert(Alert.AlertType.ERROR, m).showAndWait();
    }

    private void info(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}

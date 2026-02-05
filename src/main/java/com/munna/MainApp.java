package com.munna;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MainApp extends Application {

    private final ObservableList<Item> items = FXCollections.observableArrayList();
    private double grandTotal = 0.0;
    private Label totalLabel = new Label("Grand Total: 0.00");

    private TableView<Bill> billTable = new TableView<>();

    @Override
    public void start(Stage stage) {

        DBUtil.initDatabase();

        /* ================= SHOP HEADER ================= */
        Label shopName = new Label(ShopConfig.get("shop.name"));
        Label shopAddress = new Label(
            ShopConfig.get("shop.address") + " | Phone: " + ShopConfig.get("shop.phone")
        );

        Label dateLabel = new Label("Date: " + LocalDate.now());

        /* ================= CUSTOMER ================= */
        TextField customerName = new TextField();
        customerName.setPromptText("Customer Name");
        customerName.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("[a-zA-Z ]*") ? c : null
        ));

        TextField mobile = new TextField();
        mobile.setPromptText("Mobile (10 digits)");
        mobile.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d{0,10}") ? c : null
        ));

        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(10);
        customerGrid.setVgap(10);
        customerGrid.add(new Label("Customer:"), 0, 0);
        customerGrid.add(customerName, 1, 0);
        customerGrid.add(new Label("Mobile:"), 0, 1);
        customerGrid.add(mobile, 1, 1);

        /* ================= ITEM ENTRY ================= */
        TextField itemName = new TextField();
        itemName.setPromptText("Item Name");
        itemName.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("[a-zA-Z0-9 _\\-/]*") ? c : null
        ));

        TextField qty = new TextField();
        qty.setPromptText("Qty");
        qty.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d*") ? c : null
        ));

        TextField price = new TextField();
        price.setPromptText("Price");
        price.setTextFormatter(new TextFormatter<>(c ->
                c.getControlNewText().matches("\\d*(\\.\\d*)?") ? c : null
        ));

        Button addItem = new Button("Add Item");
        Button deleteItem = new Button("Delete Selected");
        Button saveBill = new Button("Save Bill");
        Button printBill = new Button("Print Bill");

        /* ================= ITEM TABLE ================= */
        TableView<Item> itemTable = new TableView<>(items);

        TableColumn<Item, String> c1 = new TableColumn<>("Item");
        c1.setCellValueFactory(new PropertyValueFactory<>("name"));
        c1.setPrefWidth(200);

        TableColumn<Item, Integer> c2 = new TableColumn<>("Qty");
        c2.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Item, Double> c3 = new TableColumn<>("Price");
        c3.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Item, Double> c4 = new TableColumn<>("Amount");
        c4.setCellValueFactory(new PropertyValueFactory<>("amount"));

        itemTable.getColumns().addAll(c1, c2, c3, c4);

        /* ================= ADD ITEM ================= */
        addItem.setOnAction(e -> {

            if (itemName.getText().isEmpty() ||
                qty.getText().isEmpty() ||
                price.getText().isEmpty()) {
                showAlert("Fill all item fields");
                return;
            }

            int q = Integer.parseInt(qty.getText());
            double p = Double.parseDouble(price.getText());

            if (q <= 0 || p <= 0) {
                showAlert("Qty and Price must be greater than zero");
                return;
            }

            Item it = new Item(itemName.getText(), q, p);
            items.add(it);

            grandTotal += it.getAmount();
            updateTotal();

            itemName.clear();
            qty.clear();
            price.clear();
        });

        /* ================= DELETE ITEM ================= */
        deleteItem.setOnAction(e -> {
            Item selected = itemTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Select item to delete");
                return;
            }
            items.remove(selected);
            grandTotal -= selected.getAmount();
            updateTotal();
        });

        /* ================= SAVE BILL ================= */
        saveBill.setOnAction(e -> {

            if (customerName.getText().isEmpty()) {
                showAlert("Customer name required");
                return;
            }

            if (mobile.getText().length() != 10) {
                showAlert("Mobile number must be 10 digits");
                return;
            }

            if (items.isEmpty()) {
                showAlert("Add at least one item");
                return;
            }

            int billId = DBUtil.saveBill(
                    LocalDate.now().toString(),
                    customerName.getText(),
                    mobile.getText(),
                    grandTotal
            );

            DBUtil.saveBillItems(billId, items);
            billTable.setItems(DBUtil.getAllBills());

            items.clear();
            grandTotal = 0;
            updateTotal();
        });

        /* ================= BILL HISTORY ================= */
        TableColumn<Bill, Integer> b1 = new TableColumn<>("Bill ID");
        b1.setCellValueFactory(new PropertyValueFactory<>("billId"));

        TableColumn<Bill, String> b2 = new TableColumn<>("Customer");
        b2.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Bill, Double> b3 = new TableColumn<>("Total");
        b3.setCellValueFactory(new PropertyValueFactory<>("total"));

        billTable.getColumns().addAll(b1, b2, b3);
        billTable.setItems(DBUtil.getAllBills());

        /* ================= PRINT ================= */
        printBill.setOnAction(e -> {
            Bill bill = billTable.getSelectionModel().getSelectedItem();
            if (bill == null) {
                showAlert("Select bill to print");
                return;
            }
            PDFUtil.generateAndPrintInvoice(
                    bill,
                    DBUtil.getItemsByBillId(bill.getBillId())
            );
        });

        /* ================= LAYOUT ================= */
        VBox root = new VBox(15,
                shopName,
                shopAddress,
                dateLabel,
                customerGrid,
                new HBox(10, itemName, qty, price, addItem, deleteItem),
                itemTable,
                totalLabel,
                new HBox(10, saveBill, printBill),
                new Label("Bill History"),
                billTable
        );

        root.setPadding(new Insets(20));

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);

        stage.setScene(new Scene(sp, 900, 700));
        stage.setTitle("Offline Billing System");
        stage.show();
    }

    private void updateTotal() {
        totalLabel.setText("Grand Total: " + String.format("%.2f", grandTotal));
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}

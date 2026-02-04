package com.munna;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DBUtil {

    private static final String DB_URL = "jdbc:sqlite:billing.db";

    public static void initDatabase() {

        String createBillTable =
                "CREATE TABLE IF NOT EXISTS bill (" +
                "bill_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bill_date TEXT, " +
                "customer_name TEXT, " +
                "customer_mobile TEXT, " +
                "total_amount REAL" +
                ");";

        String createBillItemTable =
                "CREATE TABLE IF NOT EXISTS bill_item (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bill_id INTEGER, " +
                "item_name TEXT, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "amount REAL" +
                ");";

        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement stmt = con.createStatement()) {

            stmt.execute(createBillTable);
            stmt.execute(createBillItemTable);

            System.out.println("TABLES CREATED / VERIFIED");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int saveBill(String date, String name, String mobile, double total) {

        String sql =
                "INSERT INTO bill (bill_date, customer_name, customer_mobile, total_amount) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, date);
            ps.setString(2, name);
            ps.setString(3, mobile);
            ps.setDouble(4, total);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void saveBillItems(int billId, ObservableList<Item> items) {

        String sql =
                "INSERT INTO bill_item (bill_id, item_name, quantity, price, amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (Item i : items) {
                ps.setInt(1, billId);
                ps.setString(2, i.getName());
                ps.setInt(3, i.getQuantity());
                ps.setDouble(4, i.getPrice());
                ps.setDouble(5, i.getAmount());
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Bill> getAllBills() {

        ObservableList<Bill> list = FXCollections.observableArrayList();

        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bill")) {

            while (rs.next()) {
                list.add(new Bill(
                        rs.getInt("bill_id"),
                        rs.getString("bill_date"),
                        rs.getString("customer_name"),
                        rs.getString("customer_mobile"),
                        rs.getDouble("total_amount")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ObservableList<Item> getItemsByBillId(int billId) {

        ObservableList<Item> list = FXCollections.observableArrayList();

        String sql = "SELECT * FROM bill_item WHERE bill_id = ?";

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Item(
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

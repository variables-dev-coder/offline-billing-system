package com.munna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.sql.ResultSet;
import javafx.collections.ObservableList;



public class DBUtil {

    private static final String DB_URL =
            "jdbc:sqlite:D:/Java project/offline-billing-system/billing.db";

    public static void initDatabase() {

        System.out.println("DB INIT STARTED");
        System.out.println("DB PATH = " + DB_URL);

        String createBillItemTable =
                "CREATE TABLE IF NOT EXISTS bill_item (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bill_id INTEGER," +
                "item_name TEXT," +
                "quantity INTEGER," +
                "price REAL," +
                "amount REAL," +
                "FOREIGN KEY (bill_id) REFERENCES bill(bill_id)" +
                ");";


        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement stmt = con.createStatement()) {

            System.out.println("DB CONNECTED SUCCESSFULLY");
            
            stmt.execute(createBillItemTable);

            System.out.println("TABLE CREATED / VERIFIED");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== SAVE BILL HEADER =====
    public static int saveBill(String date,
            					String customerName,
            					String mobile,
            					double totalAmount) {

    	String insertSql =
    			"INSERT INTO bill (bill_date, customer_name, customer_mobile, total_amount) " +
    			"VALUES (?, ?, ?, ?)";

    	try (Connection con = DriverManager.getConnection(DB_URL);
    			PreparedStatement ps = con.prepareStatement(
    					insertSql, Statement.RETURN_GENERATED_KEYS)) {

    		ps.setString(1, date);
    		ps.setString(2, customerName);
    		ps.setString(3, mobile);
    		ps.setDouble(4, totalAmount);

    		ps.executeUpdate();

    		ResultSet rs = ps.getGeneratedKeys();
    		if (rs.next()) {
    			return rs.getInt(1); // bill_id
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return -1;
    	}
    
    public static void saveBillItems(int billId, ObservableList<Item> items) {

        String insertSql =
                "INSERT INTO bill_item (bill_id, item_name, quantity, price, amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = con.prepareStatement(insertSql)) {

            for (Item item : items) {
                ps.setInt(1, billId);
                ps.setString(2, item.getName());
                ps.setInt(3, item.getQuantity());
                ps.setDouble(4, item.getPrice());
                ps.setDouble(5, item.getAmount());
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("Bill items saved");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  }

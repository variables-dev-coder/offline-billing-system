package com.munna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBUtil {

    private static final String DB_URL =
            "jdbc:sqlite:D:/Java project/offline-billing-system/billing.db";

    public static void initDatabase() {

        System.out.println("DB INIT STARTED");
        System.out.println("DB PATH = " + DB_URL);

        String createBillTable =
                "CREATE TABLE IF NOT EXISTS bill (" +
                "bill_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bill_date TEXT," +
                "customer_name TEXT," +
                "customer_mobile TEXT," +
                "total_amount REAL" +
                ");";

        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement stmt = con.createStatement()) {

            System.out.println("DB CONNECTED SUCCESSFULLY");
            stmt.execute(createBillTable);
            System.out.println("TABLE CREATED / VERIFIED");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

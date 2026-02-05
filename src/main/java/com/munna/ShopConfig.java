package com.munna;

import java.io.FileInputStream;
import java.util.Properties;

public class ShopConfig {

    private static final Properties props = new Properties();

    static {
        try {
            props.load(new FileInputStream("config/shop.properties"));
        } catch (Exception e) {
            System.out.println("shop.properties not found");
        }
    }

    public static String get(String key) {
        return props.getProperty(key, "");
    }
}

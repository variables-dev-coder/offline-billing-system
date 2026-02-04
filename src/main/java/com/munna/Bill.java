package com.munna;

public class Bill {

    private int billId;
    private String date;
    private String customerName;
    private String mobile;
    private double total;

    public Bill(int billId, String date, String customerName, String mobile, double total) {
        this.billId = billId;
        this.date = date;
        this.customerName = customerName;
        this.mobile = mobile;
        this.total = total;
    }

    public int getBillId() {
        return billId;
    }

    public String getDate() {
        return date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public double getTotal() {
        return total;
    }
}

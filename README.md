# 🧾 Offline Billing System (JavaFX)

An **offline desktop billing application** built using **JavaFX and Maven**, designed for local shops (mobile shops, coffee shops, small retail stores) to generate and **print A4 invoices without internet connectivity**.

This project focuses on **real-world billing requirements**, clean UI, and simplicity for non-technical users.

---

## ✨ Features

- Fully **offline** desktop application  
- Simple and clean **billing UI**
- Add **customer name & mobile number**
- Add multiple items with quantity and price
- Automatic **total calculation**
- **A4 invoice printing** (HP / normal printers)
- No internet, no cloud dependency
- Built using **JavaFX + Maven**

---

## 🛠 Tech Stack

- **Java 17**
- **JavaFX** (UI)
- **Maven** (Build & Dependency Management)
- **Spring Tool Suite (STS) / Eclipse**
- **Git & GitHub**

---

## 📸 Invoice Demo (A4 Format)

# 🧾 INVOICE

---

## **ABC MOBILE SHOP**  
Main Road, Hyderabad  
📞 Phone: 9XXXXXXXXX  

---

### **Invoice Details**

| Field | Value |
|-----|------|
| **Invoice No** | 1023 |
| **Date** | 29-01-2026 |
| **Time** | 06:45 PM |

---

### **Customer Details**

| Field | Value |
|-----|------|
| **Name** | Rahul Kumar |
| **Mobile** | 98XXXXXXXX |

---

### **Item Details**

| Item Name | Quantity | Price | Amount |
|----------|----------|-------|--------|
| Mobile Charger | 1 | 500 | 500 |
| Mobile Cover | 2 | 200 | 400 |
| Earphones | 1 | 300 | 300 |

---

### **Summary**

| Description | Amount |
|------------|--------|
| **Sub Total** | 1200 |
| **Discount** | 0 |
| **Grand Total** | **1200** |

---

### **Payment Mode**
Cash

---

### **Thank You!**

Thank you for shopping with us.  
Please visit again 😊  

---

**Authorized Signature:** ______________________  

---

## 📂 Project Structure

offline-billing-system
├── pom.xml
└── src
└── main
└── java
└── com
└── munna
└── MainApp.java


---

## ▶️ How to Run the Project

### Prerequisites
- Java **17**
- Maven
- STS / Eclipse / IntelliJ IDEA

---

### Run using Maven

```bash
mvn javafx:run
A window titled "Offline Billing System" will open.

🖨 Printing Support
Supports A4 printers (HP / Inkjet / Laser)

Uses system-installed printer drivers

Same invoice UI is used for screen preview and print

No extra configuration required

🎯 Target Users
Mobile shops

Coffee shops

Small retail stores

Local businesses needing simple offline billing

🚀 Planned Enhancements
User authentication

SQLite local database

Bill history & search

PDF invoice export

Thermal printer (POS) support

Daily / monthly sales reports

👨‍💻 Developer
Munna
Java Backend & Desktop Application Developer

GitHub: https://github.com/variables-dev-coder

LinkedIn: (add your LinkedIn profile link)

📌 Note
This project was built as a real-world desktop application to gain hands-on experience in:

JavaFX UI development

Offline billing workflows

Maven project structure

Printer integration

⭐ Feedback
If you find this project useful, feel free to ⭐ star the repository or share feedback.

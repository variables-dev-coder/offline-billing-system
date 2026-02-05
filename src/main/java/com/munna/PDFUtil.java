package com.munna;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import javafx.collections.ObservableList;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

public class PDFUtil {

	public static void generateAndPrintInvoice(Bill bill, ObservableList<Item> items) {

	    try {
	        String dir = "invoices/";
	        new File(dir).mkdirs();

	        String filePath = dir + "Invoice_" + bill.getBillId() + ".pdf";

	        // --------- CREATE PDF ----------
	        Document doc = new Document(PageSize.A4);
	        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
	        doc.open();

	        Font header = new Font(Font.HELVETICA, 16, Font.BOLD);
	        Font bold = new Font(Font.HELVETICA, 20, Font.BOLD);

	        doc.add(new Paragraph(ShopConfig.get("shop.name"), header));
	        doc.add(new Paragraph(ShopConfig.get("shop.address")));
	        doc.add(new Paragraph("Phone: " + ShopConfig.get("shop.phone")));
	        doc.add(new Paragraph("\n"));

	        doc.add(new Paragraph("Bill ID: " + bill.getBillId()));
	        doc.add(new Paragraph("Date: " + bill.getDate()));
	        doc.add(new Paragraph("Customer: " + bill.getCustomerName()));
	        doc.add(new Paragraph("Mobile: " + bill.getMobile()));
	        doc.add(new Paragraph("\n"));

	        PdfPTable table = new PdfPTable(4);
	        table.setWidthPercentage(100);
	        table.setWidths(new float[]{4, 1, 2, 2});

	        table.addCell("Item");
	        table.addCell("Qty");
	        table.addCell("Price");
	        table.addCell("Amount");

	        for (Item i : items) {
	            table.addCell(i.getName());
	            table.addCell(String.valueOf(i.getQuantity()));
	            table.addCell(String.valueOf(i.getPrice()));
	            table.addCell(String.valueOf(i.getAmount()));
	        }

	        doc.add(table);
	        doc.add(new Paragraph("\nGrand Total: " + bill.getTotal(), bold));
	        doc.close();

	        // --------- WINDOWS DIRECT PRINT ----------
	        File pdfFile = new File(filePath);

	        if (!pdfFile.exists()) {
	            System.out.println("PDF not found for printing");
	            return;
	        }

	        // Windows print command (RELIABLE)
	        String command = "cmd /c start /min \"\" \"" + pdfFile.getAbsolutePath() + "\"";
	        Runtime.getRuntime().exec(command);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}

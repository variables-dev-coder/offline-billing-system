package com.munna;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;

public class PDFUtil {

    public static void generateSamplePDF() {
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("invoice-sample.pdf"));
            doc.open();
            doc.add(new Paragraph("ABC MOBILE SHOP"));
            doc.add(new Paragraph("Invoice generated successfully"));
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

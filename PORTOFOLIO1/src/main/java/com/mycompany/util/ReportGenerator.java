package com.mycompany.util;

import com.mycompany.dao.ProductDAO;
import com.mycompany.dao.SupplierDAO;
import com.mycompany.model.Product;
import com.mycompany.model.Supplier;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    public static void generateSuppliersPdf(JFrame parent, String outputPath) {
        SupplierDAO dao = new SupplierDAO();
        try {
            List<Supplier> list = dao.findAll();
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            cs.newLineAtOffset(50, 700);
            cs.showText("Supplier Report");
            cs.newLineAtOffset(0, -20);
            cs.setFont(PDType1Font.HELVETICA, 10);

            for (Supplier s : list) {
                String line = String.format("%d | %s | %s | %s", s.getId(), s.getName(), s.getPhone(), s.getEmail());
                cs.showText(line);
                cs.newLineAtOffset(0, -14);
            }
            cs.endText();
            cs.close();

            doc.save(new File(outputPath));
            doc.close();

            JOptionPane.showMessageDialog(parent, "Laporan dibuat: " + outputPath, "Laporan", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Gagal membuat laporan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void generateProductsPdf(JFrame parent, String outputPath) {
        ProductDAO dao = new ProductDAO();
        try {
            List<Product> list = dao.findAll();
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            cs.newLineAtOffset(50, 700);
            cs.showText("Product Report");
            cs.newLineAtOffset(0, -20);
            cs.setFont(PDType1Font.HELVETICA, 10);

            for (Product p : list) {
                String line = String.format("%d | %s | %s | %s | %d",
                        p.getId(),
                        p.getSku(),
                        p.getName(),
                        p.getPrice() != null ? p.getPrice().toString() : "0",
                        p.getQuantity() != null ? p.getQuantity() : 0);
                cs.showText(line);
                cs.newLineAtOffset(0, -14);
            }
            cs.endText();
            cs.close();

            doc.save(new File(outputPath));
            doc.close();

            JOptionPane.showMessageDialog(parent, "Laporan dibuat: " + outputPath, "Laporan", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Gagal membuat laporan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


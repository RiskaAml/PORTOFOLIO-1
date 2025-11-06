package com.mycompany.util;

import com.mycompany.dao.ProductDAO;
import com.mycompany.dao.SupplierDAO;
import com.mycompany.model.Product;
import com.mycompany.model.Supplier;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

public class CSVImporter {

    public static void importSuppliersFromCsv(JFrame parent, String csvPath) {
        int success = 0;
        int failed = 0;
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] header = reader.readNext(); // skip header
            String[] line;
            SupplierDAO dao = new SupplierDAO();
            while ((line = reader.readNext()) != null) {
                try {
                    Supplier s = new Supplier();
                    s.setName(line.length > 0 ? line[0] : "");
                    s.setContactPerson(line.length > 1 ? line[1] : "");
                    s.setPhone(line.length > 2 ? line[2] : "");
                    s.setEmail(line.length > 3 ? line[3] : "");
                    s.setAddress(line.length > 4 ? line[4] : "");
                    dao.insert(s);
                    success++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    failed++;
                }
            }
            JOptionPane.showMessageDialog(parent,
                    "Import selesai. Berhasil: " + success + ", Gagal: " + failed,
                    "Import CSV", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | CsvValidationException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error membaca CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void importProductsFromCsv(JFrame parent, String csvPath) {
        int success = 0;
        int failed = 0;
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] header = reader.readNext(); // skip header
            String[] line;
            ProductDAO pdao = new ProductDAO();
            SupplierDAO sdao = new SupplierDAO();
            while ((line = reader.readNext()) != null) {
                try {
                    String sku = line.length > 0 ? line[0] : "";
                    String name = line.length > 1 ? line[1] : "";
                    String supplierField = line.length > 2 ? line[2] : "";
                    BigDecimal price = line.length > 3 && !line[3].isEmpty() ? new BigDecimal(line[3]) : BigDecimal.ZERO;
                    int qty = line.length > 4 && !line[4].isEmpty() ? Integer.parseInt(line[4]) : 0;

                    Supplier supplier = null;
                    try {
                        int sid = Integer.parseInt(supplierField);
                        supplier = sdao.findById(sid);
                    } catch (NumberFormatException ex) {
                        supplier = sdao.findByName(supplierField);
                    }
                    if (supplier == null) throw new Exception("Supplier tidak ditemukan: " + supplierField);

                    Product p = new Product();
                    p.setSku(sku);
                    p.setName(name);
                    p.setSupplierId(supplier);
                    p.setPrice(price);
                    p.setQuantity(qty);
                    pdao.insert(p);
                    success++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    failed++;
                }
            }
            JOptionPane.showMessageDialog(parent,
                    "Import selesai. Berhasil: " + success + ", Gagal: " + failed,
                    "Import CSV Produk", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | CsvValidationException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error membaca CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
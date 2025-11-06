package com.mycompany.ui;

import com.mycompany.dao.ProductDAO;
import com.mycompany.dao.SupplierDAO;
import com.mycompany.model.Product;
import com.mycompany.model.Supplier;
import com.mycompany.util.CSVImporter;
import com.mycompany.util.ReportGenerator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class MainForm extends JFrame {

    private JTabbedPane tabbedPane;

    // Suppliers components
    private JTable tblSuppliers;
    private DefaultTableModel supTableModel;
    private SupplierDAO supplierDAO = new SupplierDAO();

    // Products components
    private JTable tblProducts;
    private DefaultTableModel prodTableModel;
    private ProductDAO productDAO = new ProductDAO();
    private SupplierDAO sdao = new SupplierDAO();

    public MainForm() {
        setTitle("Inventory App (Suppliers & Products)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        initComponents();
        loadSuppliers();
        loadProducts();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Suppliers tab
        JPanel suppliersPanel = new JPanel(new BorderLayout());
        supTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact Person", "Phone", "Email", "Address"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblSuppliers = new JTable(supTableModel);
        tblSuppliers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suppliersPanel.add(new JScrollPane(tblSuppliers), BorderLayout.CENTER);

        JPanel supButtons = new JPanel();
        JButton btnSupAdd = new JButton("Add");
        JButton btnSupEdit = new JButton("Edit");
        JButton btnSupDelete = new JButton("Delete");
        JButton btnSupImport = new JButton("Import CSV");
        JButton btnSupReport = new JButton("Print Report (PDF)");
        JButton btnSupRefresh = new JButton("Refresh");
        supButtons.add(btnSupAdd);
        supButtons.add(btnSupEdit);
        supButtons.add(btnSupDelete);
        supButtons.add(btnSupImport);
        supButtons.add(btnSupReport);
        supButtons.add(btnSupRefresh);
        suppliersPanel.add(supButtons, BorderLayout.SOUTH);

        // Suppliers listeners
        btnSupAdd.addActionListener(e -> addSupplier());
        btnSupEdit.addActionListener(e -> editSupplier());
        btnSupDelete.addActionListener(e -> deleteSupplier());
        btnSupImport.addActionListener(e -> importSuppliersCsv());
        btnSupReport.addActionListener(e -> generateSuppliersReport());
        btnSupRefresh.addActionListener(e -> loadSuppliers());

        // Products tab
        JPanel productsPanel = new JPanel(new BorderLayout());
        prodTableModel = new DefaultTableModel(new Object[]{"ID", "SKU", "Name", "Supplier", "Price", "Qty"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProducts = new JTable(prodTableModel);
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsPanel.add(new JScrollPane(tblProducts), BorderLayout.CENTER);

        JPanel prodButtons = new JPanel();
        JButton btnProdAdd = new JButton("Add");
        JButton btnProdEdit = new JButton("Edit");
        JButton btnProdDelete = new JButton("Delete");
        JButton btnProdImport = new JButton("Import CSV");
        JButton btnProdReport = new JButton("Print Report (PDF)");
        JButton btnProdRefresh = new JButton("Refresh");
        prodButtons.add(btnProdAdd);
        prodButtons.add(btnProdEdit);
        prodButtons.add(btnProdDelete);
        prodButtons.add(btnProdImport);
        prodButtons.add(btnProdReport);
        prodButtons.add(btnProdRefresh);
        productsPanel.add(prodButtons, BorderLayout.SOUTH);

        // Products listeners
        btnProdAdd.addActionListener(e -> addProduct());
        btnProdEdit.addActionListener(e -> editProduct());
        btnProdDelete.addActionListener(e -> deleteProduct());
        btnProdImport.addActionListener(e -> importProductsCsv());
        btnProdReport.addActionListener(e -> generateProductsReport());
        btnProdRefresh.addActionListener(e -> loadProducts());

        tabbedPane.addTab("Suppliers", suppliersPanel);
        tabbedPane.addTab("Products", productsPanel);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    // --- Suppliers operations ---

    private void loadSuppliers() {
        SwingUtilities.invokeLater(() -> {
            supTableModel.setRowCount(0);
            try {
                List<Supplier> list = supplierDAO.findAll();
                for (Supplier s : list) {
                    supTableModel.addRow(new Object[]{
                            s.getId(),
                            s.getName(),
                            s.getContactPerson(),
                            s.getPhone(),
                            s.getEmail(),
                            s.getAddress()
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat suppliers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addSupplier() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField tfName = new JTextField();
        JTextField tfContact = new JTextField();
        JTextField tfPhone = new JTextField();
        JTextField tfEmail = new JTextField();
        JTextField tfAddress = new JTextField();
        panel.add(new JLabel("Name:")); panel.add(tfName);
        panel.add(new JLabel("Contact Person:")); panel.add(tfContact);
        panel.add(new JLabel("Phone:")); panel.add(tfPhone);
        panel.add(new JLabel("Email:")); panel.add(tfEmail);
        panel.add(new JLabel("Address:")); panel.add(tfAddress);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name tidak boleh kosong", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Supplier s = new Supplier();
            s.setName(name);
            s.setContactPerson(tfContact.getText().trim());
            s.setPhone(tfPhone.getText().trim());
            s.setEmail(tfEmail.getText().trim());
            s.setAddress(tfAddress.getText().trim());
            boolean ok = supplierDAO.insert(s);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Supplier berhasil ditambahkan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan supplier", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSupplier() {
        int sel = tblSuppliers.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Pilih supplier terlebih dahulu", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Integer id = (Integer) supTableModel.getValueAt(sel, 0);
        Supplier s = supplierDAO.findById(id);
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Supplier tidak ditemukan", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField tfName = new JTextField(s.getName());
        JTextField tfContact = new JTextField(s.getContactPerson());
        JTextField tfPhone = new JTextField(s.getPhone());
        JTextField tfEmail = new JTextField(s.getEmail());
        JTextField tfAddress = new JTextField(s.getAddress());
        panel.add(new JLabel("Name:")); panel.add(tfName);
        panel.add(new JLabel("Contact Person:")); panel.add(tfContact);
        panel.add(new JLabel("Phone:")); panel.add(tfPhone);
        panel.add(new JLabel("Email:")); panel.add(tfEmail);
        panel.add(new JLabel("Address:")); panel.add(tfAddress);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Supplier", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            s.setName(tfName.getText().trim());
            s.setContactPerson(tfContact.getText().trim());
            s.setPhone(tfPhone.getText().trim());
            s.setEmail(tfEmail.getText().trim());
            s.setAddress(tfAddress.getText().trim());
            boolean ok = supplierDAO.update(s);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Supplier berhasil diupdate", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate supplier", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSupplier() {
        int sel = tblSuppliers.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Pilih supplier terlebih dahulu", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Integer id = (Integer) supTableModel.getValueAt(sel, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus supplier ID=" + id + " ?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = supplierDAO.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Supplier berhasil dihapus", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSuppliers();
                loadProducts(); // refresh produk
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus supplier. Periksa apakah ada produk yang bergantung.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importSuppliersCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            CSVImporter.importSuppliersFromCsv(this, f.getAbsolutePath());
            loadSuppliers();
        }
    }

    private void generateSuppliersReport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Suppliers Report");
        fc.setSelectedFile(new File("suppliers_report.pdf"));
        int res = fc.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            ReportGenerator.generateSuppliersPdf(this, f.getAbsolutePath());
        }
    }

    // --- Products operations ---

    private void loadProducts() {
        SwingUtilities.invokeLater(() -> {
            prodTableModel.setRowCount(0);
            try {
                List<Product> list = productDAO.findAll();
                for (Product p : list) {
                    String supplierName = p.getSupplierId() != null ? p.getSupplierId().getName() : "";
                    prodTableModel.addRow(new Object[]{
                            p.getId(),
                            p.getSku(),
                            p.getName(),
                            supplierName,
                            p.getPrice() != null ? p.getPrice().toString() : "0",
                            p.getQuantity() != null ? p.getQuantity() : 0
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addProduct() {
        try {
            List<Supplier> suppliers = sdao.findAll();
            if (suppliers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada supplier. Tambahkan supplier terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            JTextField tfSku = new JTextField();
            JTextField tfName = new JTextField();
            JComboBox<String> cbSup = new JComboBox<>();
            for (Supplier s : suppliers) cbSup.addItem(s.getName());
            JTextField tfPrice = new JTextField();
            JTextField tfQty = new JTextField();
            panel.add(new JLabel("SKU:")); panel.add(tfSku);
            panel.add(new JLabel("Name:")); panel.add(tfName);
            panel.add(new JLabel("Supplier:")); panel.add(cbSup);
            panel.add(new JLabel("Price:")); panel.add(tfPrice);
            panel.add(new JLabel("Quantity:")); panel.add(tfQty);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String sku = tfSku.getText().trim();
                String name = tfName.getText().trim();
                String supName = (String) cbSup.getSelectedItem();
                String priceStr = tfPrice.getText().trim();
                String qtyStr = tfQty.getText().trim();
                if (sku.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "SKU dan Name tidak boleh kosong", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Supplier supplier = sdao.findByName(supName);
                if (supplier == null) {
                    JOptionPane.showMessageDialog(this, "Supplier tidak ditemukan: " + supName, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal price = BigDecimal.ZERO;
                if (!priceStr.isEmpty()) {
                    try { price = new BigDecimal(priceStr); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Format price tidak valid", "Validation", JOptionPane.WARNING_MESSAGE); return; }
                }
                int qty = 0;
                if (!qtyStr.isEmpty()) {
                    try { qty = Integer.parseInt(qtyStr); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Format quantity tidak valid", "Validation", JOptionPane.WARNING_MESSAGE); return; }
                }
                Product p = new Product();
                p.setSku(sku);
                p.setName(name);
                p.setSupplierId(supplier);
                p.setPrice(price);
                p.setQuantity(qty);
                boolean ok = productDAO.insert(p);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Product berhasil ditambahkan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan product (cek SKU duplicate)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat menambahkan product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProduct() {
        int sel = tblProducts.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Pilih product terlebih dahulu", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Integer id = (Integer) prodTableModel.getValueAt(sel, 0);
        Product p = productDAO.findById(id);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Product tidak ditemukan", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<Supplier> suppliers = sdao.findAll();
            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            JTextField tfSku = new JTextField(p.getSku());
            JTextField tfName = new JTextField(p.getName());
            JComboBox<String> cbSup = new JComboBox<>();
            for (Supplier s : suppliers) cbSup.addItem(s.getName());
            if (p.getSupplierId() != null) cbSup.setSelectedItem(p.getSupplierId().getName());
            JTextField tfPrice = new JTextField(p.getPrice() != null ? p.getPrice().toString() : "0");
            JTextField tfQty = new JTextField(p.getQuantity() != null ? p.getQuantity().toString() : "0");
            panel.add(new JLabel("SKU:")); panel.add(tfSku);
            panel.add(new JLabel("Name:")); panel.add(tfName);
            panel.add(new JLabel("Supplier:")); panel.add(cbSup);
            panel.add(new JLabel("Price:")); panel.add(tfPrice);
            panel.add(new JLabel("Quantity:")); panel.add(tfQty);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String sku = tfSku.getText().trim();
                String name = tfName.getText().trim();
                String supName = (String) cbSup.getSelectedItem();
                String priceStr = tfPrice.getText().trim();
                String qtyStr = tfQty.getText().trim();
                if (sku.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "SKU dan Name tidak boleh kosong", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Supplier supplier = sdao.findByName(supName);
                if (supplier == null) {
                    JOptionPane.showMessageDialog(this, "Supplier tidak ditemukan: " + supName, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal price = BigDecimal.ZERO;
                if (!priceStr.isEmpty()) {
                    try { price = new BigDecimal(priceStr); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Format price tidak valid", "Validation", JOptionPane.WARNING_MESSAGE); return; }
                }
                int qty = 0;
                if (!qtyStr.isEmpty()) {
                    try { qty = Integer.parseInt(qtyStr); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Format quantity tidak valid", "Validation", JOptionPane.WARNING_MESSAGE); return; }
                }
                p.setSku(sku);
                p.setName(name);
                p.setSupplierId(supplier);
                p.setPrice(price);
                p.setQuantity(qty);
                boolean ok = productDAO.update(p);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Product berhasil diupdate", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengupdate product", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int sel = tblProducts.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Pilih product terlebih dahulu", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Integer id = (Integer) prodTableModel.getValueAt(sel, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus product ID=" + id + " ?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = productDAO.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Product berhasil dihapus", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus product", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importProductsCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            CSVImporter.importProductsFromCsv(this, f.getAbsolutePath());
            loadProducts();
        }
    }

    private void generateProductsReport() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Products Report");
        fc.setSelectedFile(new File("products_report.pdf"));
        int res = fc.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            ReportGenerator.generateProductsPdf(this, f.getAbsolutePath());
        }
    }
}
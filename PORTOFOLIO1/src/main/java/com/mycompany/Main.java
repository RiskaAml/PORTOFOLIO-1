package com.mycompany;

import com.mycompany.ui.MainForm;
import com.mycompany.util.JPAUtil;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Tidak ada login — langsung buka MainForm
            MainForm mainForm = new MainForm();
            mainForm.setLocationRelativeTo(null);
            mainForm.setVisible(true);

            // register shutdown hook untuk menutup JPA EntityManagerFactory
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                JPAUtil.close();
            }));
        });
    }
}
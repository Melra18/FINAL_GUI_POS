package com.pos.ui;

import com.pos.config.ConnectDb;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductPanel extends JPanel {

    public interface FilterListener {
        void onFilterChanged(int categoryId, String searchText);
    }

    private JComboBox<CategoryItem> cbCategory;
    private JTextField txtSearch;
    private FilterListener listener;

    public ProductPanel() {
        setBackground(Color.WHITE);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setFont(new Font("Segoe UI", Font.BOLD, 12));

        cbCategory = new JComboBox<>();
        cbCategory.setPreferredSize(new Dimension(180, 28));
        cbCategory.addActionListener(e -> fireFilterChanged());

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(220, 28));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { fireFilterChanged(); }
            public void removeUpdate(DocumentEvent e) { fireFilterChanged(); }
            public void changedUpdate(DocumentEvent e) { fireFilterChanged(); }
        });

        add(lblCategory);
        add(cbCategory);
        add(Box.createHorizontalStrut(20));
        add(lblSearch);
        add(txtSearch);

        loadCategories();
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    public void reloadCategories() {
        loadCategories();
    }

    private void loadCategories() {
        cbCategory.removeAllItems();
        cbCategory.addItem(new CategoryItem(0, "All Categories"));

        String sql = "SELECT category_id, category_name FROM categories";
        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbCategory.addItem(new CategoryItem(rs.getInt("category_id"), rs.getString("category_name")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void fireFilterChanged() {
        if (listener != null) {
            CategoryItem selected = (CategoryItem) cbCategory.getSelectedItem();
            int categoryId = (selected != null) ? selected.getId() : 0;
            String searchText = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
            listener.onFilterChanged(categoryId, searchText);
        }
    }

    private static class CategoryItem {
        private final int id;
        private final String name;
        CategoryItem(int id, String name) { this.id = id; this.name = name; }
        int getId() { return id; }
        @Override public String toString() { return name; }
    }
}
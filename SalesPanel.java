/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pos.ui;

import com.pos.config.ConnectDb;
import com.pos.model.Product;
import com.pos.model.ProductSorter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SalesPanel extends javax.swing.JPanel {

    private static final String[] SORT_OPTIONS = {
        "Default", "Price: Low to High", "Price: High to Low", "Name: A-Z", "Stock: Low to High"
    };

    private Dashboard parentDashboard;
    private ProductPanel filterPanel;
    private JComboBox<String> cbSort;

    // ArrayList: lahat ng products sa memory
    private final List<Product> allProducts = new ArrayList<>();
    private int lastCategoryId = 0;
    private String lastSearch = "";

    public SalesPanel(Dashboard parentDashboard) {
        initComponents();
        this.parentDashboard = parentDashboard;

        // Product cards are generated at runtime from the database.
        // The surrounding panel, header, filters and scroll area are
        // designed in NetBeans GUI Builder (SalesPanel.form).
        pnlProductList = new ScrollablePanel();
        pnlProductList.setBackground(Color.WHITE);
        pnlProductList.setLayout(new WrapLayout(WrapLayout.LEFT, 14, 14));
        jScrollPane1.setViewportView(pnlProductList);
        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setBackground(Color.WHITE);
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane1.getVerticalScrollBar().setBlockIncrement(80);

        filterPanel = new ProductPanel();
        filterPanel.setFilterListener((categoryId, searchText) -> {
            lastCategoryId = categoryId;
            lastSearch = (searchText == null) ? "" : searchText;
            applyFilters();
        });

        cbSort = new JComboBox<>(SORT_OPTIONS);
        cbSort.setPreferredSize(new Dimension(180, 28));
        cbSort.addActionListener(e -> applyFilters());

        JLabel lblSort = new JLabel("Sort:");
        lblSort.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        sortPanel.setBackground(Color.WHITE);
        sortPanel.add(lblSort);
        sortPanel.add(cbSort);

        pnlFilterHost.removeAll();
        pnlFilterHost.add(filterPanel);
        pnlFilterHost.add(sortPanel);
        pnlFilterHost.revalidate();
        pnlFilterHost.repaint();

        loadFromDatabase();
        applyFilters();
    }

    public void refreshProducts() {
        loadFromDatabase();
        filterPanel.reloadCategories();
        applyFilters();
    }

    private void loadFromDatabase() {
        allProducts.clear();
        String sql = "SELECT product_id, product_name, unit_price, stock_quantity, "
                   + "category_id, supplier_id, image_path FROM products WHERE is_archived = 0";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            boolean hasPiecesPerBulk = hasColumn(meta, "pieces_per_bulk");
            boolean hasPricePerBulk = hasColumn(meta, "price_per_bulk");

            while (rs.next()) {
                try {
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("category_id"),
                        rs.getInt("supplier_id"),
                        rs.getString("image_path")
                    );
                    if (hasPiecesPerBulk) {
                        product.setPiecesPerBulk(rs.getInt("pieces_per_bulk"));
                    }
                    if (hasPricePerBulk) {
                        product.setPricePerBulk(rs.getDouble("price_per_bulk"));
                    }
                    allProducts.add(product);
                } catch (IllegalArgumentException bad) {
                    System.err.println("Skipped invalid product row: " + bad.getMessage());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private boolean hasColumn(ResultSetMetaData meta, String columnName) throws SQLException {
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(meta.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    // Filter (linear search) -> Sort (insertion sort) -> Render
    private void applyFilters() {
        List<Product> view = new ArrayList<>();
        String keyword = lastSearch.trim().toLowerCase();

        for (Product p : allProducts) {
            boolean categoryOk = (lastCategoryId == 0) || (p.getCategoryId() == lastCategoryId);
            boolean nameOk = keyword.isEmpty() || p.getProductName().toLowerCase().contains(keyword);
            if (categoryOk && nameOk) {
                view.add(p);
            }
        }

        sortView(view);

        pnlProductList.removeAll();
        for (Product p : view) {
            pnlProductList.add(createProductCard(p));
        }
        pnlProductList.revalidate();
        pnlProductList.repaint();
    }

    private void sortView(List<Product> view) {
        switch (cbSort.getSelectedIndex()) {
            case 1:
                ProductSorter.insertionSort(view, Comparator.comparingDouble(Product::getUnitPrice));
                break;
            case 2:
                ProductSorter.insertionSort(view, Comparator.comparingDouble(Product::getUnitPrice).reversed());
                break;
            case 3:
                ProductSorter.insertionSort(view, (a, b) -> a.getProductName().compareToIgnoreCase(b.getProductName()));
                break;
            case 4:
                ProductSorter.insertionSort(view, Comparator.comparingInt(Product::getStockQuantity));
                break;
            default:
                break;
        }
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 80; }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
}
    
    
    private void setCardBorder(JPanel card, Color color) {
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(9, 9, 9, 9)
        ));
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setPreferredSize(new Dimension(220, 285));
        card.setMinimumSize(new Dimension(220, 285));
        card.setMaximumSize(new Dimension(220, 285));

        JLabel imageLabel = new JLabel();
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(248, 248, 248));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(202, 130));
        imageLabel.setMinimumSize(new Dimension(202, 130));
        imageLabel.setMaximumSize(new Dimension(202, 130));
        setProductImage(imageLabel, product.getImagePath());

        JLabel nameLabel = new JLabel(
            "<html><div style='text-align:center; width:195px;'>" +
            escapeHtml(product.getProductName()) + "</div></html>",
            SwingConstants.CENTER
        );
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(35, 35, 35));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        JLabel pieceLabel = new JLabel("Piece  •  ₱" + df.format(product.getPricePerPiece()), SwingConstants.CENTER);
        pieceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pieceLabel.setForeground(new Color(0, 102, 204));
        pieceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String bulkText = product.hasBulkPricing()
                ? "Bulk (" + product.getPiecesPerBulk() + " pcs)  •  ₱" + df.format(product.getPricePerBulk())
                : "Bulk  •  Not set";
        JLabel bulkLabel = new JLabel(bulkText, SwingConstants.CENTER);
        bulkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bulkLabel.setForeground(product.hasBulkPricing() ? new Color(70, 70, 70) : new Color(145, 145, 145));
        bulkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stockLabel = new JLabel(
            product.isInStock() ? product.getStockQuantity() + " pcs available" : "OUT OF STOCK",
            SwingConstants.CENTER
        );
        stockLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        stockLabel.setForeground(product.isInStock() ? new Color(70, 120, 70) : new Color(190, 35, 35));
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addButton = new JButton(product.isInStock() ? "ADD TO ORDER" : "OUT OF STOCK");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addButton.setBackground(product.isInStock() ? new Color(27, 31, 92) : new Color(210, 210, 210));
        addButton.setForeground(product.isInStock() ? Color.WHITE : new Color(100, 100, 100));
        addButton.setEnabled(product.isInStock());
        addButton.setCursor(product.isInStock()
                ? new Cursor(Cursor.HAND_CURSOR)
                : new Cursor(Cursor.DEFAULT_CURSOR));
        addButton.addActionListener(e -> showAddToOrderDialog(product));

        card.add(imageLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(pieceLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(bulkLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(stockLabel);
        card.add(Box.createVerticalGlue());
        card.add(addButton);

        if (product.isInStock()) {
            MouseAdapter hover = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                        BorderFactory.createEmptyBorder(7, 7, 7, 7)
                    ));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    ));
                }
            };
            card.addMouseListener(hover);
            imageLabel.addMouseListener(hover);
            nameLabel.addMouseListener(hover);
            pieceLabel.addMouseListener(hover);
            bulkLabel.addMouseListener(hover);
            stockLabel.addMouseListener(hover);
        }

        return card;
    }

    private void setProductImage(JLabel label, String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            setImagePlaceholder(label);
            return;
        }

        try {
            java.awt.Image image = null;
            File file = new File(imagePath);
            if (file.exists()) {
                image = ImageIO.read(file);
            } else {
                URL url = getClass().getResource(imagePath.startsWith("/") ? imagePath : "/" + imagePath);
                if (url != null) {
                    image = ImageIO.read(url);
                }
            }

            if (image == null) {
                setImagePlaceholder(label);
                return;
            }

            int boxW = 202;
            int boxH = 130;
            double scale = Math.min((double) boxW / image.getWidth(null),
                                    (double) boxH / image.getHeight(null));
            int w = Math.max(1, (int) Math.round(image.getWidth(null) * scale));
            int h = Math.max(1, (int) Math.round(image.getHeight(null) * scale));
            Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setText("");
        } catch (Exception ex) {
            setImagePlaceholder(label);
        }
    }

    private void setImagePlaceholder(JLabel label) {
        label.setIcon(null);
        label.setText("NO IMAGE");
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(150, 150, 150));
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }

    private void showAddToOrderDialog(Product product) {
        if (!product.isInStock()) {
            JOptionPane.showMessageDialog(this, "Out of stock: " + product.getProductName());
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel(product.getProductName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel priceInfo = new JLabel(String.format("Piece: ₱%.2f", product.getPricePerPiece()));
        gbc.gridy++;
        panel.add(priceInfo, gbc);

        JComboBox<String> unitBox = new JComboBox<>();
        unitBox.addItem("Piece");
        if (product.hasBulkPricing()) {
            unitBox.addItem("Bulk (" + product.getPiecesPerBulk() + " pcs)");
        }

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1;
        panel.add(unitBox, gbc);

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, product.getStockQuantity(), 1));
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        panel.add(quantitySpinner, gbc);

        JLabel availableLabel = new JLabel();
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(availableLabel, gbc);

        JLabel selectedPriceLabel = new JLabel();
        selectedPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy++;
        panel.add(selectedPriceLabel, gbc);

        Runnable update = () -> {
            boolean bulk = unitBox.getSelectedIndex() == 1;
            int max = bulk ? product.getStockQuantity() / Math.max(1, product.getPiecesPerBulk())
                           : product.getStockQuantity();
            if (max < 1) max = 1;
            SpinnerNumberModel model = (SpinnerNumberModel) quantitySpinner.getModel();
            model.setMaximum(max);
            if ((Integer) model.getNumber() > max) model.setValue(max);

            double price = bulk ? product.getPricePerBulk() : product.getPricePerPiece();
            availableLabel.setText(bulk
                    ? "Available: " + max + " bulk(s)"
                    : "Available: " + product.getStockQuantity() + " piece(s)");
            selectedPriceLabel.setText(String.format("Unit price: ₱%.2f   |   Line total: ₱%.2f",
                    price, price * ((Integer) model.getNumber())));
        };

        unitBox.addActionListener(e -> update.run());
        quantitySpinner.addChangeListener(e -> update.run());
        update.run();

        int result = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(this),
            panel,
            "Add to Order",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            boolean bulk = unitBox.getSelectedIndex() == 1;
            String unitType = bulk ? "Bulk" : "Piece";
            int quantity = (Integer) quantitySpinner.getValue();
            parentDashboard.addProductToCart(product, unitType, quantity);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblProducts = new javax.swing.JLabel();
        pnlFilterHost = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlProductList = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblProducts.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblProducts.setForeground(new java.awt.Color(35, 35, 35));
        lblProducts.setText("Products");
        lblProducts.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 10));
        pnlHeader.add(lblProducts, java.awt.BorderLayout.WEST);

        pnlFilterHost.setBackground(new java.awt.Color(255, 255, 255));
        pnlFilterHost.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 8));
        pnlHeader.add(pnlFilterHost, java.awt.BorderLayout.CENTER);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        pnlProductList.setBackground(new java.awt.Color(255, 255, 255));
        pnlProductList.setAutoscrolls(true);
        jScrollPane1.setViewportView(pnlProductList);

        setLayout(new java.awt.BorderLayout());
        add(pnlHeader, java.awt.BorderLayout.NORTH);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblProducts;
    private javax.swing.JPanel pnlFilterHost;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlProductList;
    // End of variables declaration//GEN-END:variables
}

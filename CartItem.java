package com.pos.model;

public class CartItem {
    private int productId;
    private String productName;
    private String unitType; // "Piece" or "Bulk"
    private int quantity;
    private double unitPrice;
    private int piecesPerBulk;
    private Product product;

    public CartItem(int productId, String productName, String unitType,
                    int quantity, double unitPrice, int piecesPerBulk) {
        this.productId = productId;
        this.productName = productName;
        this.unitType = unitType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.piecesPerBulk = piecesPerBulk > 0 ? piecesPerBulk : 1;
    }

    public CartItem(Product product, int quantity) {
        this(product, "Piece", quantity);
    }

    public CartItem(Product product, String unitType, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        this.product = product;
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.unitType = "Bulk".equalsIgnoreCase(unitType) ? "Bulk" : "Piece";
        this.quantity = quantity;
        this.piecesPerBulk = product.getPiecesPerBulk();
        this.unitPrice = "Bulk".equalsIgnoreCase(this.unitType)
                ? product.getPricePerBulk()
                : product.getPricePerPiece();
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getUnitType() { return unitType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }

    public Product getProduct() { return product; }

    public double getSubtotal() {
        return quantity * unitPrice;
    }

    public int getTotalPiecesToDeduct() {
        if ("Bulk".equalsIgnoreCase(unitType)) {
            return quantity * piecesPerBulk;
        }
        return quantity;
    }
}

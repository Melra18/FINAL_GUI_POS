package com.pos.model;

/**
 * Product model used by Inventory and the Sales Register.
 *
 * The project historically used unit_price while the newer bulk workflow
 * uses pricePerPiece/pricePerBulk.  The compatibility methods below allow
 * both parts of the application to work with the same Product object.
 */
public class Product {
    private int productId;
    private String barcode;
    private String productName;
    private int categoryId;
    private int supplierId;
    private int piecesPerBulk;
    private double pricePerPiece;
    private double pricePerBulk;
    private String status;
    private String imagePath;
    private int stockQuantity;

    public Product() {
        this.piecesPerBulk = 1;
    }

    public Product(int productId, String barcode, String productName, int categoryId,
                   int supplierId, int piecesPerBulk, double pricePerPiece,
                   double pricePerBulk, String status) {
        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.piecesPerBulk = piecesPerBulk > 0 ? piecesPerBulk : 1;
        this.pricePerPiece = pricePerPiece;
        this.pricePerBulk = pricePerBulk;
        this.status = status;
    }

    /** Compatibility constructor used by the existing Sales Register query. */
    public Product(int productId, String productName, double unitPrice,
                   int stockQuantity, int categoryId, int supplierId,
                   String imagePath) {
        this.productId = productId;
        this.productName = productName;
        this.pricePerPiece = unitPrice;
        this.pricePerBulk = 0.0;
        this.piecesPerBulk = 1;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.imagePath = imagePath;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getPiecesPerBulk() { return piecesPerBulk; }
    public void setPiecesPerBulk(int piecesPerBulk) {
        this.piecesPerBulk = piecesPerBulk > 0 ? piecesPerBulk : 1;
    }

    public double getPricePerPiece() { return pricePerPiece; }
    public void setPricePerPiece(double pricePerPiece) { this.pricePerPiece = pricePerPiece; }

    public double getPricePerBulk() { return pricePerBulk; }
    public void setPricePerBulk(double pricePerBulk) { this.pricePerBulk = pricePerBulk; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = Math.max(0, stockQuantity); }

    /** Existing code calls this the unit price. */
    public double getUnitPrice() { return pricePerPiece; }
    public void setUnitPrice(double unitPrice) { this.pricePerPiece = unitPrice; }

    public boolean isInStock() { return stockQuantity > 0; }

    public boolean hasBulkPricing() {
        return piecesPerBulk > 1 && pricePerBulk > 0;
    }
}

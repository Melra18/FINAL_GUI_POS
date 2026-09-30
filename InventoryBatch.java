package com.pos.model;

import java.sql.Date;

public class InventoryBatch {
    private int batchId;
    private int productId;
    private String batchNumber;
    private Date dateReceived;
    private Date expirationDate;
    private int bulkAdded;
    private int initialPieces;
    private int remainingPieces;
    private String status;

    public InventoryBatch() {}

    public InventoryBatch(int batchId, int productId, String batchNumber, Date dateReceived, 
                          Date expirationDate, int bulkAdded, int initialPieces, int remainingPieces, String status) {
        this.batchId = batchId;
        this.productId = productId;
        this.batchNumber = batchNumber;
        this.dateReceived = dateReceived;
        this.expirationDate = expirationDate;
        this.bulkAdded = bulkAdded;
        this.initialPieces = initialPieces;
        this.remainingPieces = remainingPieces;
        this.status = status;
    }

    // Getters and Setters
    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public Date getDateReceived() { return dateReceived; }
    public void setDateReceived(Date dateReceived) { this.dateReceived = dateReceived; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }

    public int getBulkAdded() { return bulkAdded; }
    public void setBulkAdded(int bulkAdded) { this.bulkAdded = bulkAdded; }

    public int getInitialPieces() { return initialPieces; }
    public void setInitialPieces(int initialPieces) { this.initialPieces = initialPieces; }

    public int getRemainingPieces() { return remainingPieces; }
    public void setRemainingPieces(int remainingPieces) { this.remainingPieces = remainingPieces; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
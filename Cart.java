package com.pos.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {

    private static final double VAT_RATE = 0.12;

    // Product + unit type is the cart key so Piece and Bulk can coexist.
    private final Map<String, CartItem> items = new LinkedHashMap<>();
    private final Deque<String> undoStack = new ArrayDeque<>();
    private Discount discount = new NoDiscount();

    public boolean add(Product product) {
        return add(product, "Piece", 1);
    }

    public boolean add(Product product, String unitType, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        String type = "Bulk".equalsIgnoreCase(unitType) ? "Bulk" : "Piece";
        int piecesRequired = "Bulk".equals(type) ? product.getPiecesPerBulk() : 1;
        int additionalPieces = quantity * piecesRequired;
        String key = makeKey(product, type);

        CartItem existing = items.get(key);
        int currentPieces = existing == null ? 0 : existing.getTotalPiecesToDeduct();

        if (currentPieces + additionalPieces > product.getStockQuantity()) {
            return false;
        }

        if (existing == null) {
            items.put(key, new CartItem(product, type, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }

        // One undo action represents the quantity just added.
        undoStack.push(key);
        return true;
    }

    private String makeKey(Product product, String unitType) {
        return product.getProductId() + "|" + unitType;
    }

    public boolean undoLast() {
        if (undoStack.isEmpty()) return false;

        String key = undoStack.pop();
        CartItem item = items.get(key);
        if (item == null) return false;

        if (item.getQuantity() <= 1) {
            items.remove(key);
        } else {
            item.setQuantity(item.getQuantity() - 1);
        }
        return true;
    }

    public boolean remove(int index) {
        if (index < 0 || index >= items.size()) return false;
        String key = items.keySet().toArray(new String[0])[index];
        items.remove(key);
        undoStack.removeIf(key::equals);
        return true;
    }

    public void clear() {
        items.clear();
        undoStack.clear();
        discount = new NoDiscount();
    }

    public void setDiscount(Discount discount) {
        this.discount = discount == null ? new NoDiscount() : discount;
    }

    public Discount getDiscount() { return discount; }
    public Collection<CartItem> getItems() { return items.values(); }
    public boolean isEmpty() { return items.isEmpty(); }

    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : items.values()) subtotal += item.getSubtotal();
        return subtotal;
    }

    public double getDiscountAmount() { return discount.compute(getSubtotal()); }
    public double getVat() { return getSubtotal() * VAT_RATE; }
    public double getTotal() { return getSubtotal() - getDiscountAmount() + getVat(); }
}

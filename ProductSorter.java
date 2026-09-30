package com.pos.model;

import java.util.Comparator;
import java.util.List;

public class ProductSorter {

    // Insertion sort: simple at madaling ipaliwanag, O(n^2)
    public static void insertionSort(List<Product> list, Comparator<Product> cmp) {
        for (int i = 1; i < list.size(); i++) {
            Product key = list.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    // Binary search by name (kailangang sorted by name muna), O(log n)
    public static int binarySearchByName(List<Product> sortedByName, String name) {
        int low = 0, high = sortedByName.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int c = sortedByName.get(mid).getProductName().compareToIgnoreCase(name);
            if (c == 0) return mid;
            if (c < 0) low = mid + 1; else high = mid - 1;
        }
        return -1;
    }
}
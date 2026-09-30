package com.pos.model;

public class User {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String role;
    private int storeId;
    private boolean isArchived;

    public User(String username, String password, String fullName, String email,
                String phone, String address, String role, int storeId, boolean isArchived) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.storeId = storeId;
        this.isArchived = isArchived;
    }

    public String getUsername() {
    return username;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;

    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public int getStoreId() { return storeId; }
    public boolean isArchived() { return isArchived; }
    
}
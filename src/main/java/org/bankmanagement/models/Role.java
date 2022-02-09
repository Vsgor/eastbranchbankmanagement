package org.bankmanagement.models;

public enum Role {
    ROLE_CLIENT, ROLE_ADMIN;

    public String getRole() {
        String role = this.toString();
        return role.split("_")[1];
    }
}

package org.bankmanagement.enums;

public enum Role {
    ROLE_CLIENT,
    ROLE_ADMIN
    ;

    public String getRoleWithNoPrefix() {
        return this.name().replace("ROLE_", "");
    }
}

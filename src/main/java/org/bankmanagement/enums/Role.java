package org.bankmanagement.enums;

public enum Role {
    ROLE_CLIENT("CLIENT"),
    ROLE_ADMIN("ADMIN");

    private final String shortName;

    Role(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}

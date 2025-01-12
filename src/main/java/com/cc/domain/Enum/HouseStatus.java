package com.cc.domain.Enum;

public enum HouseStatus {
    NOT_LIVED("未入住", "0"),
    LIVED("入住", "1");

    private String displayName;
    private String code;

    HouseStatus(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
}

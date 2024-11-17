package com.hanna.second.springbootprj.support.enums;

public enum AssetType {
    CARD,
    CASH;

    public String getAssetType() {
        return name();
    }
}

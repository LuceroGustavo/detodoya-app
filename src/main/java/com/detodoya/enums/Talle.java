package com.detodoya.enums;

public enum Talle {
    XS("XS"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL"),
    XXXL("XXXL"),
    T32("32"),
    T34("34"),
    T36("36"),
    T38("38"),
    T40("40"),
    T42("42"),
    T44("44"),
    T46("46"),
    T48("48"),
    T50("50"),
    T52("52"),
    T54("54"),
    T56("56");

    private final String displayName;

    Talle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

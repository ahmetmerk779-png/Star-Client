package com.hackclient.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player"),
    MISC("Misc");

    private final String label;

    Category(String label) { this.label = label; }

    public String getLabel() { return label; }
}

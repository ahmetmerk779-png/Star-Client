package com.hackclient.setting;

import com.google.gson.JsonElement;

public abstract class Setting {
    private final String name;
    private final String description;

    protected Setting(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement element);
}

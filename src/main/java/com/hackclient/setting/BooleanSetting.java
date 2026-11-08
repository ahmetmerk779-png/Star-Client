package com.hackclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, String description, boolean value) {
        super(name, description);
        this.value = value;
    }

    public boolean get() { return value; }
    public void set(boolean value) { this.value = value; }
    public void toggle() { this.value = !this.value; }

    @Override
    public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override
    public void fromJson(JsonElement element) {
        try { value = element.getAsBoolean(); } catch (Exception ignored) {}
    }
}

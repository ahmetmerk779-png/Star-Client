package com.hackclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ModeSetting extends Setting {
    private final String[] modes;
    private int index;

    public ModeSetting(String name, String description, String defaultMode, String... modes) {
        super(name, description);
        this.modes = modes;
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equalsIgnoreCase(defaultMode)) index = i;
        }
    }

    public String get() { return modes[index]; }
    public boolean is(String mode) { return modes[index].equalsIgnoreCase(mode); }

    public void next() { index = (index + 1) % modes.length; }
    public void previous() { index = (index - 1 + modes.length) % modes.length; }

    @Override
    public JsonElement toJson() { return new JsonPrimitive(get()); }

    @Override
    public void fromJson(JsonElement element) {
        try {
            String s = element.getAsString();
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].equalsIgnoreCase(s)) { index = i; return; }
            }
        } catch (Exception ignored) {}
    }
}

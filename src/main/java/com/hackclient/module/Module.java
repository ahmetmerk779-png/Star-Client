package com.hackclient.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private final String name;
    private final int defaultKey;
    private boolean enabled;

    protected Module(String name, int defaultKey) {
        this.name = name;
        this.defaultKey = defaultKey;
    }

    public String getName() { return name; }
    public int getDefaultKey() { return defaultKey; }
    public boolean isEnabled() { return enabled; }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean value) {
        if (this.enabled == value) return;
        this.enabled = value;
        if (value) onEnable(); else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick(MinecraftClient client) {}
}

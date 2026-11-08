package com.hackclient.module.impl;

import com.hackclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import org.lwjgl.glfw.GLFW;

/** Adim yuksekligini arttirir (0.6 -> 1.0 blok). */
public class StepModule extends Module {
    private static final double STEP = 1.0;

    public StepModule() {
        super("Step", GLFW.GLFW_KEY_H);
    }

    @Override
    public void onTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;
        var attr = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attr != null) attr.setBaseValue(STEP);
    }

    @Override
    protected void onDisable() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        var attr = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attr != null) attr.setBaseValue(0.6);
    }
}

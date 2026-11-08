package com.hackclient.module.impl;

import com.hackclient.module.Category;
import com.hackclient.module.Module;
import com.hackclient.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class FlyModule extends Module {
    private final NumberSetting speed =
            add(new NumberSetting("Speed", "Uçuş hızı", 0.05, 0.01, 0.5, 0.01));

    public FlyModule() {
        super("Fly", "Creative tarzı uçuş", Category.MOVEMENT, GLFW.GLFW_KEY_G);
    }

    @Override
    public void onTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;
        player.getAbilities().allowFlying = true;
        player.getAbilities().setFlySpeed(speed.getFloat());
    }

    @Override
    protected void onDisable() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        player.getAbilities().setFlySpeed(0.05f);
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
        }
    }
}

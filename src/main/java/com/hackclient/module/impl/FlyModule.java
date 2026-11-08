package com.hackclient.module.impl;

import com.hackclient.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Creative tarzi ucus (yalnizca sunucu izin veriyorsa / tek kisilik dunyada calisir). */
public class FlyModule extends Module {
    public FlyModule() {
        super("Fly", GLFW.GLFW_KEY_G);
    }

    @Override
    public void onTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;
        player.getAbilities().allowFlying = true;
        player.getAbilities().setFlySpeed(0.05f);
    }

    @Override
    protected void onDisable() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
        }
    }
}

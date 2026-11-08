package com.hackclient.module.impl;

import com.hackclient.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Ileri tusuna basarken otomatik kosar. */
public class SprintModule extends Module {
    public SprintModule() {
        super("Sprint", GLFW.GLFW_KEY_R);
    }

    @Override
    public void onTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;
        if (client.options.forwardKey.isPressed()
                && !player.isSneaking()
                && !player.horizontalCollision
                && player.getHungerManager().getFoodLevel() > 6) {
            player.setSprinting(true);
        }
    }
}

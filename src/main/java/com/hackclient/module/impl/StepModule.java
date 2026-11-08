package com.hackclient.module.impl;

import com.hackclient.module.Category;
import com.hackclient.module.Module;
import com.hackclient.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import org.lwjgl.glfw.GLFW;

public class StepModule extends Module {
    private final NumberSetting height =
            add(new NumberSetting("Height", "Adım yüksekliği (blok)", 1.0, 0.6, 2.5, 0.1));

    public StepModule() {
        super("Step", "Bloklara zıplamadan çıkar", Category.MOVEMENT, GLFW.GLFW_KEY_H);
    }

    @Override
    public void onTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;
        var attr = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attr != null) attr.setBaseValue(height.get());
    }

    @Override
    protected void onDisable() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        var attr = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attr != null) attr.setBaseValue(0.6);
    }
}

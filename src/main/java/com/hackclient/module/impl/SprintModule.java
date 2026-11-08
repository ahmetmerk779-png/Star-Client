package com.hackclient.module.impl;

import com.hackclient.module.Category;
import com.hackclient.module.Module;
import com.hackclient.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class SprintModule extends Module {
    private final BooleanSetting hungerCheck =
            add(new BooleanSetting("Hunger Check", "Açlık düşükken koşma", true));

    public SprintModule() {
        super("Sprint", "İleri giderken otomatik koşar", Category.MOVEMENT, GLFW.GLFW_KEY_R);
    }

    @Override
    public void onTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;
        boolean hungerOk = !hungerCheck.get() || player.getHungerManager().getFoodLevel() > 6;
        if (client.options.forwardKey.isPressed()
                && !player.isSneaking()
                && !player.horizontalCollision
                && hungerOk) {
            player.setSprinting(true);
        }
    }
}

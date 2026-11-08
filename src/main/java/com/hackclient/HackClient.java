package com.hackclient;

import com.hackclient.module.Module;
import com.hackclient.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HackClient implements ClientModInitializer {
    public static final String MOD_ID = "hackclient";
    public static final String NAME = "HackClient";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

    private final Map<Module, KeyBinding> binds = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        for (Module m : ModuleManager.all()) {
            KeyBinding kb = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key." + MOD_ID + "." + m.getName().toLowerCase(),
                    InputUtil.Type.KEYSYM,
                    m.getDefaultKey(),
                    CATEGORY
            ));
            binds.put(m, kb);
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Map.Entry<Module, KeyBinding> e : binds.entrySet()) {
                while (e.getValue().wasPressed()) {
                    e.getKey().toggle();
                }
            }
            if (client.player == null) return;
            for (Module m : ModuleManager.all()) {
                if (m.isEnabled()) m.onTick(client);
            }
        });

        // Sag ust kosede acik modullerin listesi (HUD)
        HudElementRegistry.addLast(Identifier.of(MOD_ID, "arraylist"), (context, tickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            int y = 4;
            int width = mc.getWindow().getScaledWidth();
            for (Module m : ModuleManager.all()) {
                if (!m.isEnabled()) continue;
                int w = mc.textRenderer.getWidth(m.getName());
                context.drawTextWithShadow(mc.textRenderer, m.getName(), width - w - 4, y, 0xFF55FF55);
                y += 10;
            }
        });

        LOGGER.info("{} yuklendi. Moduller: {}", NAME, ModuleManager.all().size());
    }
}

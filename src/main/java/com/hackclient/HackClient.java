package com.hackclient;

import com.hackclient.gui.ClickGuiScreen;
import com.hackclient.gui.HudRenderer;
import com.hackclient.module.Module;
import com.hackclient.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
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
    public static final String NAME = "StarClient";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

    private final Map<Module, KeyBinding> binds = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ConfigManager.load();

        KeyBinding guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + MOD_ID + ".gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY
        ));

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
            while (guiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
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

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save());

        HudElementRegistry.addLast(Identifier.of(MOD_ID, "hud"),
                (context, tickCounter) -> HudRenderer.render(context));

        LOGGER.info("{} yuklendi. Moduller: {}", NAME, ModuleManager.all().size());
    }
}

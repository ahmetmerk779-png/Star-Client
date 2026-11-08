package com.hackclient.module.impl;

import com.hackclient.module.Category;
import com.hackclient.module.Module;
import com.hackclient.setting.BooleanSetting;
import com.hackclient.setting.ModeSetting;
import org.lwjgl.glfw.GLFW;

public class HudModule extends Module {
    public final BooleanSetting watermark =
            add(new BooleanSetting("Watermark", "Sol üstte logo göster", true));
    public final BooleanSetting arrayList =
            add(new BooleanSetting("ArrayList", "Açık modül listesi", true));
    public final ModeSetting sort =
            add(new ModeSetting("Sort", "Liste sıralaması", "Length", "Length", "Alphabet"));

    public HudModule() {
        super("HUD", "Ekran üstü bilgi arayüzü", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
    }
}

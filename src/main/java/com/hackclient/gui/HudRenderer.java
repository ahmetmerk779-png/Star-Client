package com.hackclient.gui;

import com.hackclient.module.Module;
import com.hackclient.module.ModuleManager;
import com.hackclient.module.impl.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HudRenderer {
    private HudRenderer() {}

    public static void render(DrawContext ctx) {
        HudModule hud = ModuleManager.get(HudModule.class);
        if (!hud.isEnabled()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;
        TextRenderer tr = mc.textRenderer;

        if (hud.watermark.get()) {
            String a = "STAR";
            String b = "CLIENT";
            int w = tr.getWidth(a) + tr.getWidth(" " + b) + 14;
            Draw.round(ctx, 6, 6, w, 17, 5, 0xB0111118);
            Draw.round(ctx, 6, 8, 2, 13, 1, Theme.ACCENT);
            ctx.drawTextWithShadow(tr, a, 13, 11, Theme.ACCENT);
            ctx.drawTextWithShadow(tr, " " + b, 13 + tr.getWidth(a), 11, Theme.TEXT);
        }

        if (hud.arrayList.get()) {
            List<Module> active = new ArrayList<>();
            for (Module m : ModuleManager.all()) {
                if (m.isEnabled() && m != hud) active.add(m);
            }
            if (hud.sort.is("Alphabet")) {
                active.sort(Comparator.comparing(Module::getName));
            } else {
                active.sort(Comparator.comparingInt((Module m) -> tr.getWidth(m.getName())).reversed());
            }
            int sw = mc.getWindow().getScaledWidth();
            int y = 6;
            for (Module m : active) {
                int w = tr.getWidth(m.getName()) + 10;
                int x = sw - w - 6;
                Draw.round(ctx, x, y, w, 13, 4, 0xA0111118);
                ctx.fill(sw - 7, y + 2, sw - 6, y + 11, Theme.ACCENT);
                ctx.drawTextWithShadow(tr, m.getName(), x + 4, y + 3, Theme.TEXT);
                y += 14;
            }
        }
    }
}

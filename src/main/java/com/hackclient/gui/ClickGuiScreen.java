package com.hackclient.gui;

import com.hackclient.ConfigManager;
import com.hackclient.module.Category;
import com.hackclient.module.Module;
import com.hackclient.module.ModuleManager;
import com.hackclient.setting.BooleanSetting;
import com.hackclient.setting.ModeSetting;
import com.hackclient.setting.NumberSetting;
import com.hackclient.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Kategorili, animasyonlu ClickGUI. Fare/klavye girisi GLFW uzerinden
 * dogrudan okunur (surumler arasi Screen input API farklarindan etkilenmemek icin).
 */
public class ClickGuiScreen extends Screen {
    private static final int W = 580, H = 350, SIDE = 128, HEAD = 34, ROW = 26;

    private Category selected = Category.MOVEMENT;
    private float scroll, scrollTarget;
    private float barY = Float.NaN;
    private final Map<Module, Anim> anims = new HashMap<>();

    private NumberSetting dragging;
    private int dragX, dragW;

    private boolean prevLeft, prevRight, prevShift = true;
    private long lastNs = System.nanoTime();
    private float openAnim = 0f;

    private static final class Anim {
        float expand, on;
        boolean open;
    }

    public ClickGuiScreen() {
        super(Text.literal("Star Client"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        ConfigManager.save();
        super.close();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        scrollTarget -= (float) vertical * 28f;
        return true;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer tr = mc.textRenderer;
        long handle = mc.getWindow().getHandle();

        boolean left = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean shift = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        boolean lClick = left && !prevLeft;
        boolean rClick = right && !prevRight;
        boolean shiftEdge = shift && !prevShift;
        prevLeft = left;
        prevRight = right;
        prevShift = shift;
        if (shiftEdge) {
            close();
            return;
        }
        if (!left) dragging = null;

        long now = System.nanoTime();
        float dt = Math.min(0.05f, (now - lastNs) / 1_000_000_000f);
        lastNs = now;
        float k = Math.min(1f, dt * 14f);
        openAnim += (1f - openAnim) * Math.min(1f, dt * 12f);

        // arka plan karartma
        ctx.fill(0, 0, width, height, Draw.alpha(0x000000, (int) (140 * openAnim)));

        int x0 = (width - W) / 2;
        int y0 = (height - H) / 2 + (int) ((1f - openAnim) * 14);

        // golge + ana pencere
        Draw.round(ctx, x0 - 3, y0 - 3, W + 6, H + 6, 10, 0x30000000);
        Draw.round(ctx, x0, y0, W, H, 9, Theme.BG);

        // ---------- kenar cubugu ----------
        Draw.round(ctx, x0, y0, SIDE, H, 9, Theme.SIDEBAR);
        ctx.fill(x0 + SIDE - 9, y0, x0 + SIDE, y0 + H, Theme.SIDEBAR);
        ctx.fill(x0 + SIDE - 1, y0 + 10, x0 + SIDE, y0 + H - 10, 0x22FFFFFF);

        ctx.drawTextWithShadow(tr, "STAR", x0 + 16, y0 + 16, Theme.ACCENT);
        ctx.drawText(tr, "CLIENT", x0 + 16 + tr.getWidth("STAR ") + 1, y0 + 16, Theme.TEXT, false);
        ctx.drawText(tr, "1.21.11 · v0.1", x0 + 16, y0 + 29, Theme.MUTED, false);
        ctx.fill(x0 + 14, y0 + 46, x0 + SIDE - 14, y0 + 47, 0x22FFFFFF);

        Category[] cats = Category.values();
        int catTop = y0 + 58;
        for (int i = 0; i < cats.length; i++) {
            Category cat = cats[i];
            int by = catTop + i * 30;
            boolean hov = Draw.inside(mouseX, mouseY, x0 + 8, by, SIDE - 16, 26);
            boolean sel = cat == selected;
            if (sel) {
                if (Float.isNaN(barY)) barY = by;
                barY += (by - barY) * k;
            }
            if (sel) {
                Draw.round(ctx, x0 + 8, (int) barY, SIDE - 16, 26, 6, Draw.alpha(Theme.ACCENT, 60));
                Draw.round(ctx, x0 + 8, (int) barY + 6, 3, 14, 1, Theme.ACCENT);
            } else if (hov) {
                Draw.round(ctx, x0 + 8, by, SIDE - 16, 26, 6, 0x18FFFFFF);
            }
            ctx.drawText(tr, cat.getLabel(), x0 + 20, by + 9, sel ? Theme.TEXT : Theme.MUTED, false);

            int total = 0, active = 0;
            for (Module m : ModuleManager.inCategory(cat)) {
                total++;
                if (m.isEnabled()) active++;
            }
            if (total > 0) {
                String badge = active + "/" + total;
                ctx.drawText(tr, badge, x0 + SIDE - 14 - tr.getWidth(badge), by + 9,
                        active > 0 ? Theme.ACCENT : 0xFF5A5A6E, false);
            }
            if (lClick && hov && !sel) {
                selected = cat;
                scroll = scrollTarget = 0;
            }
        }
        ctx.drawText(tr, "RSHIFT  Aç/Kapa", x0 + 16, y0 + H - 18, 0xFF5A5A6E, false);

        // ---------- icerik basligi ----------
        int cx = x0 + SIDE + 16;
        List<Module> mods = ModuleManager.inCategory(selected);
        int activeCount = 0;
        for (Module m : mods) if (m.isEnabled()) activeCount++;
        ctx.drawTextWithShadow(tr, selected.getLabel(), cx, y0 + 14, Theme.TEXT);
        String sub = mods.size() + " modül · " + activeCount + " aktif";
        ctx.drawText(tr, sub, x0 + W - 16 - tr.getWidth(sub), y0 + 14, Theme.MUTED, false);
        ctx.fill(cx, y0 + 30, x0 + W - 16, y0 + 31, 0x22FFFFFF);

        // ---------- modul listesi ----------
        int vx = cx;
        int vy = y0 + 40;
        int vw = W - SIDE - 32;
        int vh = H - 52;

        if (mods.isEmpty()) {
            String msg = "Bu kategoride henüz modül yok";
            ctx.drawText(tr, msg, vx + (vw - tr.getWidth(msg)) / 2, vy + vh / 2 - 4, Theme.MUTED, false);
            return;
        }

        // toplam yukseklik (kaydirma siniri icin)
        int total = 0;
        for (Module m : mods) {
            Anim a = anim(m);
            total += cardHeight(m, a) + 6;
        }
        float maxScroll = Math.max(0, total - vh);
        scrollTarget = Math.max(0, Math.min(maxScroll, scrollTarget));
        scroll += (scrollTarget - scroll) * Math.min(1f, dt * 16f);

        ctx.enableScissor(vx, vy - 2, vx + vw, vy + vh);
        int yy = vy - (int) scroll;
        boolean mouseInView = Draw.inside(mouseX, mouseY, vx, vy, vw, vh);

        for (Module m : mods) {
            Anim a = anim(m);
            a.expand += ((a.open ? 1f : 0f) - a.expand) * k;
            a.on += ((m.isEnabled() ? 1f : 0f) - a.on) * k;

            int cardH = cardHeight(m, a);
            boolean headHover = mouseInView && Draw.inside(mouseX, mouseY, vx, yy, vw, HEAD);

            int bg = Draw.lerp(Theme.CARD, Theme.CARD_ON, a.on);
            if (headHover) bg = Draw.lerp(bg, 0xFF2A2A38, 0.35f);
            Draw.round(ctx, vx, yy, vw, cardH, 7, bg);
            if (a.on > 0.02f) {
                Draw.round(ctx, vx, yy + 7, 3, HEAD - 14, 1, Draw.alpha(Theme.ACCENT, (int) (255 * a.on)));
            }

            // isim + aciklama
            ctx.drawTextWithShadow(tr, m.getName(), vx + 14, yy + 7, Theme.TEXT);
            ctx.drawText(tr, m.getDescription(), vx + 14, yy + 19, Theme.MUTED, false);

            // tus etiketi
            int swX = vx + vw - 42;
            String key = keyName(m.getDefaultKey());
            if (!key.isEmpty()) {
                int kw = tr.getWidth(key) + 8;
                Draw.round(ctx, swX - kw - 8, yy + 9, kw, 15, 4, 0xFF0F0F16);
                ctx.drawText(tr, key, swX - kw - 4, yy + 13, Theme.MUTED, false);
            }

            // ac/kapa anahtari
            int track = Draw.lerp(Theme.OFF, Theme.ACCENT, a.on);
            Draw.round(ctx, swX, yy + 9, 30, 16, 8, track);
            int knobX = swX + 2 + (int) (14 * a.on);
            Draw.round(ctx, knobX, yy + 11, 12, 12, 6, 0xFFFFFFFF);

            // ayar oku
            if (!m.getSettings().isEmpty()) {
                String arrow = a.open ? "-" : "+";
                ctx.drawText(tr, arrow, swX - 8 - (key.isEmpty() ? 0 : tr.getWidth(key) + 16) - 10,
                        yy + 13, Theme.MUTED, false);
            }

            if (headHover) {
                if (lClick) m.toggle();
                else if (rClick && !m.getSettings().isEmpty()) a.open = !a.open;
            }

            // ayar satirlari
            int shown = (int) (m.getSettings().size() * a.expand + 0.001f);
            int sy = yy + HEAD + 2;
            for (int i = 0; i < m.getSettings().size(); i++) {
                if (i >= shown) break;
                Setting s = m.getSettings().get(i);
                int ry = sy + i * ROW;
                int rx = vx + 8;
                int rw = vw - 16;
                Draw.round(ctx, rx, ry, rw, ROW - 3, 5, Theme.ROW);
                boolean rowHover = mouseInView && Draw.inside(mouseX, mouseY, rx, ry, rw, ROW - 3);

                if (s instanceof BooleanSetting b) {
                    ctx.drawText(tr, b.getName(), rx + 10, ry + 8, Theme.TEXT, false);
                    int bx = rx + rw - 32;
                    Draw.round(ctx, bx, ry + 4, 24, 13, 6, b.get() ? Theme.ACCENT : Theme.OFF);
                    Draw.round(ctx, bx + (b.get() ? 13 : 2), ry + 6, 9, 9, 4, 0xFFFFFFFF);
                    if (rowHover && lClick) b.toggle();
                } else if (s instanceof ModeSetting md) {
                    ctx.drawText(tr, md.getName(), rx + 10, ry + 8, Theme.TEXT, false);
                    String val = md.get();
                    int pw = tr.getWidth(val) + 16;
                    Draw.round(ctx, rx + rw - pw - 8, ry + 3, pw, 15, 5, Draw.alpha(Theme.ACCENT, 70));
                    ctx.drawText(tr, val, rx + rw - pw, ry + 7, Theme.TEXT, false);
                    if (rowHover && lClick) md.next();
                    else if (rowHover && rClick) md.previous();
                } else if (s instanceof NumberSetting n) {
                    ctx.drawText(tr, n.getName(), rx + 10, ry + 4, Theme.TEXT, false);
                    String val = format(n);
                    ctx.drawText(tr, val, rx + rw - 10 - tr.getWidth(val), ry + 4, Theme.ACCENT, false);
                    int tx = rx + 10, tw = rw - 20, ty = ry + 15;
                    Draw.round(ctx, tx, ty, tw, 3, 1, Theme.OFF);
                    int fill = (int) (tw * n.ratio());
                    if (fill > 0) Draw.round(ctx, tx, ty, Math.max(fill, 2), 3, 1, Theme.ACCENT);
                    Draw.round(ctx, tx + fill - 3, ty - 2, 7, 7, 3, 0xFFFFFFFF);
                    if (rowHover && lClick) {
                        dragging = n;
                        dragX = tx;
                        dragW = tw;
                    }
                }
            }

            yy += cardH + 6;
        }

        // slider surukleme
        if (dragging != null && left) {
            dragging.setFromRatio((mouseX - dragX) / (double) dragW);
        }
        ctx.disableScissor();

        // kaydirma cubugu
        if (maxScroll > 0) {
            int trackH = vh;
            int thumbH = Math.max(24, (int) (trackH * (vh / (float) total)));
            int thumbY = vy + (int) ((trackH - thumbH) * (scroll / maxScroll));
            Draw.round(ctx, vx + vw + 4, thumbY, 3, thumbH, 1, Draw.alpha(Theme.ACCENT, 140));
        }
    }

    private Anim anim(Module m) {
        return anims.computeIfAbsent(m, x -> new Anim());
    }

    private int cardHeight(Module m, Anim a) {
        int n = m.getSettings().size();
        int extra = (int) (n * ROW * a.expand);
        return HEAD + (extra > 0 ? extra + 3 : 0);
    }

    private static String format(NumberSetting n) {
        double step = n.getStep();
        if (step >= 1) return String.valueOf((int) Math.round(n.get()));
        int decimals = step >= 0.1 ? 1 : 2;
        return String.format(Locale.ROOT, "%." + decimals + "f", n.get());
    }

    private static String keyName(int key) {
        if (key <= 0) return "";
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase(Locale.ROOT);
        return switch (key) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            default -> "";
        };
    }
}

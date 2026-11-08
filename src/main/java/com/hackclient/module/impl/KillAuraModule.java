package com.hackclient.module.impl;

import com.hackclient.module.Category;
import com.hackclient.module.Module;
import com.hackclient.setting.BooleanSetting;
import com.hackclient.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

/** Menzildeki en yakin hedefe otomatik saldirir. */
public class KillAuraModule extends Module {
    private final NumberSetting range =
            add(new NumberSetting("Range", "Saldırı menzili", 3.0, 2.0, 6.0, 0.1));
    private final BooleanSetting players =
            add(new BooleanSetting("Players", "Oyunculara saldır", true));
    private final BooleanSetting mobs =
            add(new BooleanSetting("Mobs", "Canlılara/mob'lara saldır", true));
    private final BooleanSetting cooldown =
            add(new BooleanSetting("Cooldown Check", "Silah dolmadan vurma", true));

    public KillAuraModule() {
        super("KillAura", "Yakındaki hedeflere otomatik vurur", Category.COMBAT, GLFW.GLFW_KEY_K);
    }

    @Override
    public void onTick(MinecraftClient client) {
        var player = client.player;
        var world = client.world;
        var manager = client.interactionManager;
        if (player == null || world == null || manager == null) return;
        if (client.currentScreen != null) return;
        if (cooldown.get() && player.getAttackCooldownProgress(0f) < 0.95f) return;

        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : world.getEntities()) {
            if (!(e instanceof LivingEntity living) || living == player || !living.isAlive()) continue;
            if (living instanceof ArmorStandEntity) continue;
            boolean isPlayer = living instanceof PlayerEntity;
            if (isPlayer && !players.get()) continue;
            if (!isPlayer && !mobs.get()) continue;
            double d = player.distanceTo(living);
            if (d > range.get()) continue;
            if (d < bestDist) {
                bestDist = d;
                best = living;
            }
        }

        if (best != null) {
            manager.attackEntity(player, best);
            player.swingHand(Hand.MAIN_HAND);
        }
    }
}

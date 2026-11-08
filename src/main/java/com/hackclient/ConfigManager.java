package com.hackclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hackclient.module.Module;
import com.hackclient.module.ModuleManager;
import com.hackclient.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigManager() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("starclient.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ModuleManager.all()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("enabled", m.isEnabled());
                JsonObject settings = new JsonObject();
                for (Setting s : m.getSettings()) {
                    settings.add(s.getName(), s.toJson());
                }
                obj.add("settings", settings);
                root.add(m.getName(), obj);
            }
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception e) {
            HackClient.LOGGER.error("Config kaydedilemedi", e);
        }
    }

    public static void load() {
        Path path = file();
        if (!Files.exists(path)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            for (Module m : ModuleManager.all()) {
                JsonElement el = root.get(m.getName());
                if (el == null || !el.isJsonObject()) continue;
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("settings") && obj.get("settings").isJsonObject()) {
                    JsonObject settings = obj.getAsJsonObject("settings");
                    for (Setting s : m.getSettings()) {
                        if (settings.has(s.getName())) s.fromJson(settings.get(s.getName()));
                    }
                }
                if (obj.has("enabled")) m.setEnabled(obj.get("enabled").getAsBoolean());
            }
        } catch (Exception e) {
            HackClient.LOGGER.error("Config okunamadi", e);
        }
    }
}

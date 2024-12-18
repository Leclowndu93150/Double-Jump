package com.leclowndu93150.double_jump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DoubleJumpConfig {
    public Map<String, Float> lootTables = new HashMap<>();

    public DoubleJumpConfig() {
        lootTables.put("minecraft:chests/woodland_mansion", 5.0f);
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDirectory().toPath()
            .resolve("double_jump.json");

    public static DoubleJumpConfig load() {
        try {
            if (!CONFIG_PATH.toFile().exists()) {
                DoubleJumpConfig defaultConfig = new DoubleJumpConfig();
                save(defaultConfig);
                return defaultConfig;
            }

            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                Type configType = new TypeToken<DoubleJumpConfig>(){}.getType();
                return GSON.fromJson(reader, configType);
            }
        } catch (IOException e) {
            System.err.println("Could not read Double Jump config: " + e.getMessage());
            return new DoubleJumpConfig();
        }
    }

    public static void save(DoubleJumpConfig config) {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            System.err.println("Could not save Double Jump config: " + e.getMessage());
        }
    }

    public void modifyLootTables() {
        for (Map.Entry<String, Float> entry : lootTables.entrySet()) {
            try {
                ResourceLocation lootTableId = ResourceLocation.tryParse(entry.getKey());
                System.out.println("Modifying loot table: " + lootTableId + " with weight: " + entry.getValue() + "%");
            } catch (Exception e) {
                System.err.println("Error processing loot table: " + entry.getKey());
            }
        }
    }
}
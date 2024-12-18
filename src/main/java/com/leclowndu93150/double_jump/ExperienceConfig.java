package com.leclowndu93150.double_jump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExperienceConfig {
    public Map<String, Float> dimensionMultipliers = new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("experience_config.json");

    public ExperienceConfig() {
        // Default multipliers
        dimensionMultipliers.put("minecraft:overworld", 1.0f);
        dimensionMultipliers.put("minecraft:the_nether", 1.0f);
        dimensionMultipliers.put("minecraft:the_end", 1.0f);
    }

    public static ExperienceConfig load() {
        try {
            if (!CONFIG_PATH.toFile().exists()) {
                ExperienceConfig defaultConfig = new ExperienceConfig();
                save(defaultConfig);
                return defaultConfig;
            }

            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                Type configType = new TypeToken<ExperienceConfig>() {}.getType();
                return GSON.fromJson(reader, configType);
            }
        } catch (IOException e) {
            System.err.println("Could not read Experience config: " + e.getMessage());
            return new ExperienceConfig();
        }
    }

    public static void save(ExperienceConfig config) {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            System.err.println("Could not save Experience config: " + e.getMessage());
        }
    }

    public float getMultiplier(String dimensionId) {
        return dimensionMultipliers.getOrDefault(dimensionId, 1.0f);
    }

}

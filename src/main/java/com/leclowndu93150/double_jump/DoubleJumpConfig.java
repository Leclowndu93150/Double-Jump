package com.leclowndu93150.double_jump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DoubleJumpConfig {
    public Map<String, Float> lootTables = new HashMap<>();

    public DoubleJumpConfig() {

    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDirectory().toPath()
            .resolve("double_jump-config.json");

    private static final ResourceLocation DEFAULT_CONFIG_LOCATION =
            new ResourceLocation("double_jump", "config/default_config.json");

    public static DoubleJumpConfig load() {
        try {
            if (!CONFIG_PATH.toFile().exists()) {
                DoubleJumpConfig defaultConfig = loadDefaultConfig();
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

    private static DoubleJumpConfig loadDefaultConfig() {
        String githubUrl = "https://raw.githubusercontent.com/Leclowndu93150/Double-Jump/refs/heads/master/src/main/resources/config/default_config.json";

        try {
            System.out.println("Fetching default config from: " + githubUrl);
            URL url = new URL(githubUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed to fetch default config: HTTP " + connection.getResponseCode());
            }

            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream)) {

                JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);

                JsonObject lootTablesJson = jsonObject.getAsJsonObject("lootTables");

                Map<String, Float> uniqueLootTables = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : lootTablesJson.entrySet()) {
                    uniqueLootTables.put(entry.getKey(), entry.getValue().getAsFloat());
                }

                DoubleJumpConfig config = new DoubleJumpConfig();
                config.lootTables = uniqueLootTables;
                return config;
            }
        } catch (IOException e) {
            System.err.println("Could not load default config from GitHub: " + e.getMessage());
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

}

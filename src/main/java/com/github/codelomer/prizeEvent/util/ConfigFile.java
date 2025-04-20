package com.github.codelomer.prizeEvent.util;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile {
    private final JavaPlugin plugin;
    private final String configPath;
    @Getter
    private FileConfiguration config;
    private File file;

    public ConfigFile(@NonNull JavaPlugin plugin, @NonNull String configPath) {

        this.plugin = plugin;
        this.configPath = configPath;
    }



    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveDefaultConfig() {
        file = new File(plugin.getDataFolder(), configPath);
        if (!file.exists()){
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(configPath, true);
        }
        loadYamlConfiguration(file);
    }


    public void reloadConfig(){
        if(fileInvalid()) createNewFile();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Failed to reload configuration", e);
        }
    }

    public void loadYamlConfiguration(File file){
        if(file != null) config = YamlConfiguration.loadConfiguration(file);
    }

    private void createNewFile(){
        file = new File(plugin.getDataFolder(),configPath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create new configuration file", e);
            }
        }
        loadYamlConfiguration(file);
    }

    private boolean fileInvalid(){
        return file == null || !file.exists() || config == null;
    }
}

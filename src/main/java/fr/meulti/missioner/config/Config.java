package fr.meulti.missioner.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        COMMON_SPEC = builder.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        createConfigFile();
    }

    private static void createConfigFile() {
        Path configDir = Paths.get("config", "Missioner");
        Path backgroundsDir = configDir.resolve("backgrounds");
        Path soundsDir = configDir.resolve("sounds");

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            if (!Files.exists(backgroundsDir)) {
                Files.createDirectories(backgroundsDir);
            }

            if (!Files.exists(soundsDir)) {
                Files.createDirectories(soundsDir);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

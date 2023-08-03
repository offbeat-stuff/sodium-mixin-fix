package me.jellysquid.mods.sodium.client;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.gui.console.Console;
import me.jellysquid.mods.sodium.client.gui.console.message.MessageLevel;
import me.jellysquid.mods.sodium.client.util.FlawlessFrames;
import me.jellysquid.mods.sodium.client.util.workarounds.PostLaunchChecks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SodiumClientMod implements ClientModInitializer {
    private static SodiumGameOptions CONFIG;
    private static Logger LOGGER;

    private static String MOD_VERSION;

    @Override
    public void onInitializeClient() {
        ModContainer mod = FabricLoader.getInstance()
                .getModContainer("pojavium")
                .orElseThrow(NullPointerException::new);

        MOD_VERSION = mod.getMetadata()
                .getVersion()
                .getFriendlyString();

        LOGGER = LoggerFactory.getLogger("Pojavium");
        CONFIG = loadConfig();

        logStartupMessages();

        FlawlessFrames.onClientInitialization();

        PostLaunchChecks.checkDrivers();
    }

    public static SodiumGameOptions options() {
        if (CONFIG == null) {
            throw new IllegalStateException("Config not yet available");
        }

        return CONFIG;
    }

    public static Logger logger() {
        if (LOGGER == null) {
            throw new IllegalStateException("Logger not yet available");
        }

        return LOGGER;
    }

    private static SodiumGameOptions loadConfig() {
        try {
            return SodiumGameOptions.load();
        } catch (Exception e) {
            LOGGER.error("Failed to load configuration file", e);
            LOGGER.error("Using default configuration file in read-only mode");

            var config = new SodiumGameOptions();
            config.setReadOnly();

            return config;
        }
    }

    public static void restoreDefaultOptions() {
        CONFIG = SodiumGameOptions.defaults();

        try {
            CONFIG.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write config file", e);
        }
    }

    public static String getVersion() {
        if (MOD_VERSION == null) {
            throw new NullPointerException("Mod version hasn't been populated yet");
        }

        return MOD_VERSION;
    }

    private static void logStartupMessages() {
        var name = Text.literal("Sodium Renderer (Nope)")
                .setStyle(Style.EMPTY.withFormatting(Formatting.GREEN));

        var version = Text.literal(" (version %s) loaded...".formatted(SodiumClientMod.getVersion()))
                .setStyle(Style.EMPTY.withFormatting(Formatting.WHITE));

        Console.instance()
                .logMessage(MessageLevel.INFO, name.append(version), 9.0);

        {
            Console.instance()
                    .logMessage(MessageLevel.INFO, Text.literal("* Official website: ").append(Text.literal("Does not exist")
                            .setStyle(Style.EMPTY.withFormatting(Formatting.AQUA))), 9.0);

            Console.instance()
                    .logMessage(MessageLevel.INFO, Text.literal("* Help support development: ").append(Text.literal("Contribute code")
                            .setStyle(Style.EMPTY.withFormatting(Formatting.AQUA))), 9.0);
        }
    }
}

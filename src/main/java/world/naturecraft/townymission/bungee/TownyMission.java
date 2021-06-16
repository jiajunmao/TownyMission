package world.naturecraft.townymission.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import world.naturecraft.townymission.bungee.listener.PluginMessageListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class TownyMission extends Plugin {

    Configuration configuration;

    @Override
    public void onEnable() {
        getLogger().info("===> Enabling TownyMission");
        // This is loading in the config file
        try {
            createPlugin();
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Registering plugin messaging channel
        getLogger().info("===> Registering Listeners and PMC");
        getProxy().registerChannel("townymission:main");
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
    }

    @Override
    public void onDisable() {
        getLogger().info("===> Disabling TownyMission");
        getProxy().unregisterChannel("townymission:main");
    }

    public void createPlugin() throws IOException {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            InputStream in = getResourceAsStream("bungee/config.yml");
            Files.copy(in, file.toPath());
        }
    }

    public void sendData(ProxiedPlayer player, String data1, int data2) {

    }
}

package masa3mc.storage;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Storage extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    public static Storage plugin;
    public static FileConfiguration config;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        getServer().getLogger().info("");
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
        getCommand("storage").setExecutor(new OpenStorageo());
        getCommand("st").setExecutor(new OpenStorageo());
        getCommand("addStorage").setExecutor(new SetStorage());
        getCommand("ast").setExecutor(new SetStorage());
        getCommand(("locstorage")).setExecutor(new ChangeLocation());
        getCommand(("lst")).setExecutor(new ChangeLocation());

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!www", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Storage getPlugin(){
        return plugin;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("");
    }
}

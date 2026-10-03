package masa3mc.storage;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Storage extends JavaPlugin {

    private static Economy econ = null;
    public static Storage plugin;


    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        Messages.load(this);
        if (!setupEconomy()) {
            getLogger().info("Vault economy not found, item registration will be free.");
        }
        reYaml.setup();
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        OpenStorageo open = new OpenStorageo();
        getCommand("storage").setExecutor(open);
        getCommand("storage").setTabCompleter(open);
        getCommand("addstorage").setExecutor(new SetStorage());
        getCommand("locstorage").setExecutor(new ChangeLocation());

        // /reload 等でオンライン中のプレイヤーがいる場合も自動回収を復元する
        for (Player player : Bukkit.getOnlinePlayers()) {
            Listeners.restoreAutoCollectState(player);
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

    /** Vaultの経済プラグインが無い場合は null */
    public static Economy getEconomy() {
        return econ;
    }

    public static Storage getPlugin() {
        return plugin;
    }

    public static File getStorageFile(Player player) {
        return new File(plugin.getDataFolder(), "Storages/" + player.getUniqueId() + ".yml");
    }

    public static int getMaxPages() {
        return Math.max(1, Math.min(9, plugin.getConfig().getInt("max-pages", 9)));
    }

    public static int getMaxStorages() {
        return GUI.PAGE_SIZE * getMaxPages();
    }

    public static String formatMoney(double amount) {
        return econ != null ? econ.format(amount) : String.valueOf(amount);
    }

    public void reload() {
        reloadConfig();
        Messages.load(this);
    }


    @Override
    public void onDisable() {
        if (reYaml.get() == null) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            Listeners.closeIfStorageOpen(player);
            Listeners.saveAutoCollectState(player);
        }
    }
}

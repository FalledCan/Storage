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
        saveDefaultConfig();
        plugin = this;
        if (!setupEconomy()) {
            getLogger().severe("Vault(または経済プラグイン)が見つからないため無効化します。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        reYaml.setup();
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        getCommand("storage").setExecutor(new OpenStorageo());
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

    public static Economy getEconomy() {
        return econ;
    }

    public static Storage getPlugin() {
        return plugin;
    }

    public static File getStorageFile(Player player) {
        return new File(plugin.getDataFolder(), "Storages/" + player.getUniqueId() + ".yml");
    }


    @Override
    public void onDisable() {
        if (reYaml.get() == null) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof StorageHolder) {
                player.closeInventory();
            }
            Listeners.saveAutoCollectState(player);
        }
    }
}

package masa3mc.storage;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SetStorage implements CommandExecutor {

    /** ブロックリストの1項目と一致するか。'*' は任意の文字列 (例: *_SPAWN_EGG) */
    static boolean matches(String pattern, String name) {
        StringBuilder regex = new StringBuilder();
        String[] parts = pattern.trim().toUpperCase(Locale.ROOT).split("\\*", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                regex.append(".*");
            }
            regex.append(Pattern.quote(parts[i]));
        }
        return name.matches(regex.toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Messages.send(sender, "player-only");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("storage.add")) {
            Messages.send(player, "no-permission", "permission", "storage.add");
            return true;
        }

        Material material = player.getInventory().getItemInMainHand().getType();
        String item = material.name();

        if (material == Material.AIR || !material.isItem()) {
            Messages.send(player, "hold-item");
            return true;
        }

        // スタックできない/上限が64未満のアイテムは取り出し時に溢れるため登録不可
        if (material.getMaxStackSize() < 64) {
            Messages.send(player, "cannot-register", "item", item);
            return true;
        }

        for (String s : Storage.getPlugin().getConfig().getStringList("blocklist")) {
            if (matches(s, item)) {
                Messages.send(player, "cannot-register", "item", item);
                return true;
            }
        }

        File f = Storage.getStorageFile(player);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        if (c.get("Storage." + item) != null) {
            Messages.send(player, "already-registered", "item", item);
            return true;
        }

        List<String> storage = c.getStringList("Storages");
        if (storage.size() >= Storage.getMaxStorages()) {
            Messages.send(player, "limit-reached", "max", Storage.getMaxStorages());
            return true;
        }

        Economy economy = Storage.getEconomy();
        double cost = Math.max(0, Storage.getPlugin().getConfig().getDouble("registration-cost", 0));
        boolean paid = economy != null && cost > 0 && !player.hasPermission("storage.free");
        if (paid) {
            if (economy.getBalance(player) < cost) {
                Messages.send(player, "not-enough-money", "cost", Storage.formatMoney(cost));
                return true;
            }
            if (!economy.withdrawPlayer(player, cost).transactionSuccess()) {
                Messages.send(player, "payment-failed");
                return true;
            }
        }

        storage.add(item);
        c.set("Storages", storage);
        c.set("Storage." + item, 0);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            if (paid) {
                economy.depositPlayer(player, cost);
                Messages.send(player, "save-failed-refund");
            }
            return true;
        }
        if (paid) {
            Messages.send(player, "registered-paid", "item", item, "cost", Storage.formatMoney(cost));
        } else {
            Messages.send(player, "registered", "item", item);
        }
        return true;
    }
}

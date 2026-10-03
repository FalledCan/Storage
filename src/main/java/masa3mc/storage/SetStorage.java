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

public class SetStorage implements CommandExecutor {

    private static final int MAX_STORAGES = GUI.PAGE_SIZE * GUI.MAX_PAGE;
    private static final double PRICE = 30000;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行できます。");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("storage.add")) {
            player.sendMessage("§6[§7Storage§6] §cあなたはstorage.addを持っていません。");
            return true;
        }

        Material material = player.getInventory().getItemInMainHand().getType();
        String item = material.name();

        if (material.isAir() || !material.isItem()) {
            player.sendMessage("§6[§7Storage§6] §c登録したいアイテムをメインハンドに持ってください。");
            return true;
        }

        // スタックできない/上限が64未満のアイテムは取り出し時に溢れるため登録不可
        if (material.getMaxStackSize() < 64) {
            player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
            return true;
        }

        for (String s : Storage.getPlugin().getConfig().getStringList("blocklist")) {
            if (item.contains(s)) {
                player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                return true;
            }
        }

        File f = Storage.getStorageFile(player);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        if (c.get("Storage." + item) != null) {
            player.sendMessage("§6[§7Storage§6] §cすでに登録されています!!");
            return true;
        }

        List<String> storage = c.getStringList("Storages");
        if (storage.size() >= MAX_STORAGES) {
            player.sendMessage("§6[§7Storage§6] §c登録上限のため登録できません。");
            return true;
        }

        Economy economy = Storage.getEconomy();
        if (economy.getBalance(player) < PRICE) {
            player.sendMessage("§6[§7Storage§6] §c登録するには3万円が必要です。");
            return true;
        }
        if (!economy.withdrawPlayer(player, PRICE).transactionSuccess()) {
            player.sendMessage("§6[§7Storage§6] §c支払いに失敗しました。");
            return true;
        }

        storage.add(item);
        c.set("Storages", storage);
        c.set("Storage." + item, 0);
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            economy.depositPlayer(player, PRICE);
            player.sendMessage("§6[§7Storage§6] §c保存に失敗したため返金しました。");
            return true;
        }
        player.sendMessage("§6[§7Storage§6] §aStorageに§b" + item + "§aを追加しました。");
        return true;
    }
}

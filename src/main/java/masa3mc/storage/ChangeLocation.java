package masa3mc.storage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChangeLocation implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行できます。");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("storage.open")) {
            player.sendMessage("§6[§7Storage§6] §cあなたはstorage.openを持っていません。");
            return true;
        }

        File f = Storage.getStorageFile(player);
        if (!f.exists()) {
            player.sendMessage("§6[§7Storage§6] §c登録がされていないかファイルが存在しません!");
            return true;
        }
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        List<String> list = c.getStringList("Storages");

        int loc1;
        int loc2;
        try {
            if (args.length != 2) {
                throw new NumberFormatException();
            }
            loc1 = Integer.parseInt(args[0]);
            loc2 = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§6[§7Storage§6] §c/" + label + " [移動元Number] [移動先Number]");
            return true;
        }
        if (loc1 < 0 || loc2 < 0 || loc1 >= list.size() || loc2 >= list.size()) {
            player.sendMessage("§6[§7Storage§6] §cNumberは0～" + (list.size() - 1) + "で指定してください。");
            return true;
        }

        String c1 = list.get(loc1);
        String c2 = list.get(loc2);
        list.set(loc1, c2);
        list.set(loc2, c1);
        c.set("Storages", list);

        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage("§6[§7Storage§6] §7" + c1 + "§6と§7" + c2 + "§6の位置の変更が完了しました。");
        return true;
    }
}

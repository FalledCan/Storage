package masa3mc.storage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenStorageo implements CommandExecutor {
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

        if (!Storage.getStorageFile(player).exists()) {
            player.sendMessage("§6[§7Storage§6] §c登録がされていないかファイルが存在しません!");
            return true;
        }

        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        new GUI().OpenGui(player, page);
        return true;
    }
}

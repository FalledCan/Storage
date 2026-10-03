package masa3mc.storage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OpenStorageo implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("storage.admin")) {
                Messages.send(sender, "no-permission", "permission", "storage.admin");
                return true;
            }
            Storage.getPlugin().reload();
            Messages.send(sender, "reloaded");
            return true;
        }

        if (!(sender instanceof Player)) {
            Messages.send(sender, "player-only");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("storage.open")) {
            Messages.send(player, "no-permission", "permission", "storage.open");
            return true;
        }

        if (!Storage.getStorageFile(player).exists()) {
            Messages.send(player, "not-registered");
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length != 1) {
            return list;
        }
        for (int i = 1; i <= Storage.getMaxPages(); i++) {
            list.add(String.valueOf(i));
        }
        if (sender.hasPermission("storage.admin")) {
            list.add("reload");
        }
        list.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
        return list;
    }
}

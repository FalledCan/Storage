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
            Messages.send(sender, "player-only");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("storage.open")) {
            Messages.send(player, "no-permission", "permission", "storage.open");
            return true;
        }

        File f = Storage.getStorageFile(player);
        if (!f.exists()) {
            Messages.send(player, "not-registered");
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
            Messages.send(player, "loc-usage", "label", label);
            return true;
        }
        if (loc1 < 0 || loc2 < 0 || loc1 >= list.size() || loc2 >= list.size()) {
            Messages.send(player, "loc-range", "max", list.size() - 1);
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

        Messages.send(player, "loc-done", "item1", c1, "item2", c2);
        return true;
    }
}

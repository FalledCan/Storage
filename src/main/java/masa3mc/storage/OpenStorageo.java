package masa3mc.storage;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class OpenStorageo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();

            if(!player.hasPermission("storage.open")) {
                player.sendMessage("§6[§7Storage§6] §cあなたはstorage.openも持っていません。");
                return true;
            }
            File f = new File(Storage.getPlugin().getDataFolder().getAbsolutePath(),"/Storages/" + player.getUniqueId() + ".yml");

            if(!f.exists()){
                player.sendMessage("§6[§7Storege§6]§6 §c登録がされていないかファイルが存在しません!");
                return true;
            }
                GUI gui = new GUI();
                gui.OpenGui(player,1);
                return true;
        }
        return false;
    }
}

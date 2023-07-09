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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();

            if(!player.hasPermission("storage.add")){
                player.sendMessage("§6[§7Storage§6] §cあなたはstorage.addも持っていません。");
                return true;
            }

            Material material = player.getInventory().getItemInMainHand().getType();
            String item = material.name();

            for(String s:Storage.getPlugin().getConfig().getStringList("blocklist")){
                if(item.contains(s)){
                    player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                    return true;
                }
            }

                File f = new File(Storage.getPlugin().getDataFolder(),"/Storages/" + player.getUniqueId() + ".yml");
                FileConfiguration c = YamlConfiguration.loadConfiguration(f);

                if(c.get("Storage." + item) == null){

                    if(c.getStringList("Storages").size() == 405){
                        player.sendMessage("§6[§7Storage§6] §c登録上限のため登録できません。");
                        return true;
                    }

                    Economy economy = Storage.getEconomy();

                    if(economy.getBalance(player) < 30000){
                        player.sendMessage("§6[§7Storage§6] §c登録するには3万円が必要です。");
                        return true;
                    }

                    economy.withdrawPlayer(player, 30000);

                    List<String> storeage = c.getStringList("Storages");
                    storeage.add(item);
                    c.set("Storages",storeage);
                    c.set("Storage." + item, 0);
                    try {
                        c.save(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage("§6[§7Storage§6] §aStorageに§b" + item + "§aを追加しました。");
                }else {
                    player.sendMessage("§6[§7Storage§6] §cすでに登録されています!!");
                }


            return true;

        }
        return false;
    }
}

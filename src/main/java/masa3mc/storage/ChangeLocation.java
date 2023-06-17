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

        if(sender instanceof Player) {

            Player player = ((Player) sender).getPlayer();
            if(!player.hasPermission("storage.open")) {
                player.sendMessage("§6[§7Storage§6] §cあなたはstorage.openも持っていません。");
                return true;
            }

                File f = new File(Storage.getPlugin().getDataFolder(),"/Storages/" + player.getUniqueId() + ".yml");
                FileConfiguration c = YamlConfiguration.loadConfiguration(f);

            if(!f.exists()){
                player.sendMessage("§6[§7Storege§6]§6 §c登録がされていないかファイルが存在しません!");
                return true;
            }

            if(args.length == 2){

                int loc1 = 0;
                int loc2 = 0;

                try{
                    loc1 = Integer.parseInt(args[0]);
                    loc2 = Integer.parseInt(args[1]);
                    if(!(c.getStringList("Storages").size() > loc1) || !(loc1 > -1) || !(c.getStringList("Storages").size() > loc2) || !(loc2 > -1)) {
                        player.sendMessage("§6[§7Storage§6] §c/locstorage [移動元Number] [移動先Number]");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("§6[§7Storage§6] §c/locstorage [移動元Number] [移動先Number]");
                    return true;
                }

                List<String> list = c.getStringList("Storages");
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
            }else{
                player.sendMessage("§6[§7Storage§6] §c/locstorage [移動元Number] [移動先Number]");
            }
        }
        return true;
    }
}

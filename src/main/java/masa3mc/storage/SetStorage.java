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

            for(String s:Storage.config.getStringList("Blocklist")){
                if(item.contains(s)){
                    player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                    return true;
                }
            }
/*
            if(item.equals("SPAWNER")){
                player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                return true;
            }
            int size = item.length();
            if(size >= 3){
                if (item.substring(size - 3).equals("BED") || item.substring(size - 3).equals("MAP") || item.substring(size - 3).equals("BOW") || item.substring(size - 3).equals("AXE") || item.substring(size - 3).equals("HOE")) {
                    player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                    return true;
                }
                if(size >= 4) {
                    if (item.substring(size - 4).equals("SIGN") || item.substring(size - 4).equals("BOAT") || item.substring(size - 4).equals("BOOK")) {
                        player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                        return true;
                    }
                    if (size >= 5) {
                        if (item.substring(size - 5).equals("BOOTS") || item.substring(size - 5).equals("SWORD")) {
                            player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                            return true;
                        }
                        if (size >= 6) {
                            if (item.substring(size - 6).equals("BANNER") || item.substring(size - 6).equals("BUCKET") || item.substring(size - 6).equals("HELMET") || item.substring(size - 6).equals("SHOVEL") || item.substring(size - 6).equals("POTION")) {
                                player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                                return true;
                            }
                            if(size >= 7){
                                if(item.substring(size - 7).equals("pickaxe") || item.substring(size - 7).equals("A_STICK")){
                                    player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                                    return true;
                                }
                                if (size >= 8) {
                                    if (item.substring(size - 8).equals("MINECART") || item.substring(size - 8).equals("LEGGINGS")) {
                                        player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                                        return true;
                                    }
                                    if (size >= 10) {
                                        if (item.substring(size - 10).equals("CHESTPLATE")) {
                                            player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                                            return true;
                                        }
                                        if (size >= 11) {
                                            if (item.substring(size - 11).equals("SHULKER_BOX") || item.substring(size - 11).equals("HORSE_ARMOR")) {
                                                player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                                                return true;
                                            }
                                            if (size >= 14) {
                                                if (item.substring(size - 14).equals("BANNER_PATTERN")) {
                                                    player.sendMessage("§6[§7Storage§6] §c" + item + "は登録できません!");
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if(size >= 10){
                if(item.startsWith("MUSIC_DISC")){
                    return true;
                }
            }

 */
            //if(!item.equals("FISHING_ROD") && !item.equals("FLINT_AND_STEEL") && !item.equals("RABBIT_STEW") && !item.equals("BEETROOT_SOUP") && !item.equals("MUSHROOM_STEW") && !item.equals("SUSPICIOUS_STEW") && !item.equals("HONEY_BOTTLE") && !item.equals("CAKE") && !item.equals("ARMOR_STAND") && !item.equals("TRIDENT") && !item.equals("AIR") && !item.equals("SADDLE") && !item.equals("ELYTRA") && !item.equals("EGG") && !item.equals("ENDER_PEARL") && !item.equals("FIREWORK_ROCKET") && !item.equals("SHEARS") && !item.equals("SHIELD") && !item.equals("TOTEM_OF_UNDYING")){

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

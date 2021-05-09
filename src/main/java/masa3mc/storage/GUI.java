package masa3mc.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;

public class GUI {

    public void OpenGui(Player player,int n){

        File f = new File(Storage.getPlugin().getDataFolder().getAbsolutePath(),"/Storages/" + player.getUniqueId() + ".yml");

            FileConfiguration c = YamlConfiguration.loadConfiguration(f);

            Inventory inv = Bukkit.createInventory(null, 54, "§6[§7Storege§6]");

            int number = 0;
            int slot = 0;
            for (String item : c.getStringList("Storages")) {
                if(n == 2){
                    if(number <= 44) {
                        number++;
                        continue;
                    }else
                    if(number == 90){
                        break;
                    }
                }else if(n == 3){
                    if(number < 90) {
                        number++;
                        continue;
                    }else
                    if(number == 135)
                        break;
                }else if(n == 4){
                    if(number < 135){
                        number++;
                        continue;
                    }else
                    if(number == 180)
                        break;
                }else if(n == 5){
                    if(number < 180){
                        number++;
                        continue;
                    }else
                    if(number == 225)
                        break;
                }else if(n == 6){
                    if(number < 225){
                        number++;
                        continue;
                    }else
                    if(number == 270)
                        break;
                }else if(n == 7) {
                    if (number < 270){
                        number++;
                        continue;
                    }else
                    if(number == 315)
                        break;
                }else if(n == 8){
                    if(number < 315){
                        number++;
                        continue;
                    }else
                    if(number == 360)
                        break;
                }else if(n == 9){
                    if(number < 360) {
                        number++;
                        continue;
                    }else
                    if(number == 405)
                        break;
                }else
                if(number == 45){
                    break;
                }
                ItemStack itemStack = new ItemStack(Material.valueOf(item));
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName("§6" + item);
                ArrayList<String> list = new ArrayList<String>();
                list.add("§7Storage capacity");
                int items = c.getInt("Storage." + item);
                if (items < 64) {
                    list.add("§7" + items + " items");
                } else {
                    int stack = items / 64;
                    int stackitems = items % 64;
                    list.add("§7" + stack + " stack");
                    list.add("§7" + stackitems + " items");
                }
                meta.setLore(list);
                itemStack.setItemMeta(meta);
                inv.setItem(slot, itemStack);
                number++;
                slot++;
            }

            for(int i = 0; i < 9; i++){
                String[] sh = {"WHITE_SHULKER_BOX","RED_SHULKER_BOX"};
                String item;
                if(n == i+1){
                    item = sh[1];
                }else {
                    item = sh[0];
                }
                ItemStack itemStack = new ItemStack(Material.valueOf(item));
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName("§6[§7Storage§6] - " + (i+1));
                itemStack.setItemMeta(meta);
                inv.setItem(i+45, itemStack);
            }
            player.openInventory(inv);
    }


    public void OpenItemGui(Player player, String item){
        Inventory inv = Bukkit.createInventory(null, 27,"§6[§7Storege§6] items");

        File f = new File(Storage.getPlugin().getDataFolder(),"/Storages/" + player.getUniqueId() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        ItemStack item1 = new ItemStack(Material.valueOf(item));
        ItemMeta meta1 = item1.getItemMeta();
        meta1.setDisplayName("§6" + item);
        ArrayList<String> list = new ArrayList<String>();
        list.add("§7Storage capacity");
        int items = c.getInt("Storage." + item);
        if(items < 64){
            list.add("§7" + items + " items");
        }else {
            int stack = items / 64;
            int stackitems = items % 64;
            list.add("§7" + stack + " stack");
            list.add("§7" + stackitems + " items");
        }
        meta1.setLore(list);
        item1.setItemMeta(meta1);

        ItemStack item2 = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta meta2 = item2.getItemMeta();
        meta2.setDisplayName("§6すべて保存する");
        item2.setItemMeta(meta2);

        ItemStack item3 = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta3 = item3.getItemMeta();
        meta3.setDisplayName("§61stack保存する");
        item3.setItemMeta(meta3);

        ItemStack back = new ItemStack(Material.RED_CONCRETE);
        ItemMeta backmeta = back.getItemMeta();
        backmeta.setDisplayName("§c戻る");
        back.setItemMeta(backmeta);

        ItemStack change = new ItemStack(Material.ORANGE_CONCRETE);
        ItemMeta changemeta = change.getItemMeta();
        changemeta.setDisplayName("§6並び替え");
        change.setItemMeta(changemeta);

        if(c.getInt("Storage." + item) > 63){
            ItemStack item4 = new ItemStack(Material.LIGHT_BLUE_CONCRETE);
            ItemMeta meta4 = item4.getItemMeta();
            meta4.setDisplayName("§61stack取り出す");
            item4.setItemMeta(meta4);

            ItemStack item5 = new ItemStack(Material.BLUE_CONCRETE);
            ItemMeta meta5 = item5.getItemMeta();
            meta5.setDisplayName("§6インベントリの空き分だけ取り出す");
            item5.setItemMeta(meta5);

            inv.setItem(10, item2);
            inv.setItem(11, item3);
            inv.setItem(13, item1);
            inv.setItem(15, item4);
            inv.setItem(16, item5);
            inv.setItem(18,change);
            inv.setItem(26, back);
        }else {
            inv.setItem(10, item2);
            inv.setItem(11, item3);
            inv.setItem(13, item1);
            inv.setItem(18,change);
            inv.setItem(26, back);
        }

        player.openInventory(inv);
    }

    public void chengeGUI(Player player,String item){
        Inventory inv = Bukkit.createInventory(null, 54, "§6[§7Storege§6] - change");

    }
}

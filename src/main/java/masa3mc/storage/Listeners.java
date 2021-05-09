package masa3mc.storage;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class Listeners implements Listener {

    @EventHandler
    public void ClickEventStorege(InventoryClickEvent event){

        if(event.getView().getTitle().equals("§6[§7Storege§6]")){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            File f = new File(Storage.getPlugin().getDataFolder(),"/Storages/" + player.getUniqueId() + ".yml");
            FileConfiguration c = YamlConfiguration.loadConfiguration(f);
            int raw = event.getRawSlot();
            GUI gui = new GUI();
            if(raw == 45) {
                gui.OpenGui(player, 1);
                return;
            }
            if(raw == 46) {
                gui.OpenGui(player, 2);
                return;
            }if(raw == 47) {
                gui.OpenGui(player, 3);
                return;
            }if(raw == 48) {
                gui.OpenGui(player, 4);
                return;
            }if(raw == 49) {
                gui.OpenGui(player, 5);
                return;
            }if(raw == 50) {
                gui.OpenGui(player, 6);
                return;
            }if(raw == 51) {
                gui.OpenGui(player, 7);
                return;
            }if(raw == 52) {
                gui.OpenGui(player, 8);
                return;
            }if(raw == 53) {
                gui.OpenGui(player, 9);
                return;
            }
            if(raw >= 45){
                return;
            }
            String item = player.getOpenInventory().getItem(raw).getType().toString();
            gui.OpenItemGui(player, item);

        }

    }

    @EventHandler
    public void ClickEventStoregeItems(InventoryClickEvent event){

        if(event.getView().getTitle().equals("§6[§7Storege§6] items")){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            File f = new File(Storage.getPlugin().getDataFolder(),"/Storages/" + player.getUniqueId() + ".yml");
            FileConfiguration c = YamlConfiguration.loadConfiguration(f);
            int raw = event.getRawSlot();
            if(raw >= 27){
                return;
            }

            ItemStack itemStack = player.getOpenInventory().getItem(13);
            String item = itemStack.getType().name();
            GUI gui = new GUI();
            if(raw == 10){
                int items = 0;

                for(ItemStack is : player.getInventory().getContents()){
                    if(is != null && is.getType() == Material.valueOf(item)){
                        items = items + is.getAmount();
                    }
                }

                ItemStack removeitem = new ItemStack(Material.valueOf(item),items);
                player.getInventory().removeItem(removeitem);

                c.set("Storage." + item,c.getInt("Storage." + item) + items);
                try {
                    c.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gui.OpenItemGui(player, item);
            }else if(raw == 11){

                if(player.getInventory().containsAtLeast(new ItemStack(Material.valueOf(item)),64)){
                    ItemStack removeitem = new ItemStack(Material.valueOf(item),64);
                    player.getInventory().removeItem(removeitem);

                    c.set("Storage." + item,c.getInt("Storage." + item) + 64);
                    try {
                        c.save(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    if(player.getInventory().containsAtLeast(new ItemStack(Material.valueOf(item)),1)){

                        int items = 0;
                        for(ItemStack is : player.getInventory().getContents()){
                            if(is != null && is.getType() == Material.valueOf(item)){
                                items = items + is.getAmount();
                            }
                        }
                        ItemStack removeitem = new ItemStack(Material.valueOf(item),items);
                        player.getInventory().removeItem(removeitem);

                        c.set("Storage." + item,c.getInt("Storage." + item) + items);
                        try {
                            c.save(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                gui.OpenItemGui(player, item);
            }else if(raw == 15){
                if(c.getInt("Storage." + item) < 64){
                    return;
                }
                if(player.getInventory().firstEmpty() != -1){
                    ItemStack additem = new ItemStack(Material.valueOf(item),64);
                    player.getInventory().addItem(additem);

                    c.set("Storage." + item,c.getInt("Storage." + item) - 64);
                    try {
                        c.save(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                gui.OpenItemGui(player, item);
            }else if(raw == 16){
                if(c.getInt("Storage." + item) < 64){
                    return;
                }
                for(int a = 0; a < 36; a++){
                    if(c.getInt("Storage." + item) < 64){
                        break;
                    }
                    if(player.getInventory().firstEmpty() != -1){
                        ItemStack additem = new ItemStack(Material.valueOf(item),64);
                        player.getInventory().addItem(additem);

                        c.set("Storage." + item,c.getInt("Storage." + item) - 64);
                        try {
                            c.save(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                gui.OpenItemGui(player, item);
            }else if(raw == 18) {
                gui.chengeGUI(player,item);
            }else if(raw == 26){
                gui.OpenGui(player, 1);
            }

        }

    }

}

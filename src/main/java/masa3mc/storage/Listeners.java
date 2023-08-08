package masa3mc.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static HashMap<String,Integer> hopper = new HashMap<>();
    public static ArrayList<String> hopperl = new ArrayList<>();

    @EventHandler
    public void onHopper(EntityPickupItemEvent event){
        if(event.getEntity() instanceof Player){
            Player player = ((Player) event.getEntity()).getPlayer();

            if(player.hasPermission("sub.large")) {
                String item = event.getItem().getItemStack().getType().name();
                String hoppername = player.getName() + item;
                if(hopper.get(hoppername) != null){
                    hopper.put(hoppername, hopper.get(hoppername) + event.getItem().getItemStack().getAmount());
                    event.setCancelled(true);
                    event.getItem().remove();
                    if(hopper.get(hoppername) > 63){
                        File f = new File(Storage.getPlugin().getDataFolder(),"/Storages/" + player.getUniqueId() + ".yml");
                        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
                        c.set("Storage." + item,c.getInt("Storage." + item) + hopper.get(hoppername));
                        hopper.put(hoppername,0);
                        try {
                            c.save(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("sub.large")){
            Bukkit.getLogger().info("[Storage] tester");
            if(reYaml.get().get(player.getUniqueId().toString()) != null){
                for(String item: reYaml.get().getStringList(player.getUniqueId().toString())) {
                    String hpi = player.getName() + item;
                    Bukkit.getLogger().info("[Storage] tester2");
                    if (hopper.get(hpi) == null) {
                        hopperl.add(hpi);
                        hopper.put(hpi, 0);
                        Bukkit.getLogger().info("[Storage] " + player.getName() + "の" + item + "の自動回収がONにしました。");
                        player.sendMessage("§6[§7Storege§6] " + item + "§bの自動回収機能が§aon§bになりました。");
                    }
                }
                reYaml.get().set(player.getUniqueId().toString(),null);
                reYaml.save();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("sub.large")) {
            List<String> strings = new ArrayList<>();
            List<String> hop = new ArrayList<>();
            File f = new File(Storage.getPlugin().getDataFolder(), "/Storages/" + player.getUniqueId() + ".yml");
            FileConfiguration c = YamlConfiguration.loadConfiguration(f);
            for (String s : hopperl) {
                if (s.contains(player.getName())) {
                    String item = s.replace(player.getName(), "");
                    c.set("Storage." + item, c.getInt("Storage." + item) + hopper.get(player.getName() + item));
                    strings.add(item);
                    hop.add(s);
                }
            }
            for (String s:hop){
                hopperl.remove(s);
                hopper.remove(s);
            }
            try {
                c.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            reYaml.get().set(player.getUniqueId().toString(), strings);
            reYaml.save();

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
            if(raw == 8){
             if(player.hasPermission("sub.large")){
                  String hpi = player.getName() + item;
                  if(hopper.get(hpi) == null){
                      hopperl.add(hpi);
                      hopper.put(hpi, 0);
                      Bukkit.getLogger().info("[Storage] " + player.getName() + "の" + item + "の自動回収がONになりました。");
                      player.sendMessage("§6[§7Storege§6] " + item + "§bの自動回収機能が§aon§bになりました。");
                  }else {
                      hopperl.remove(hpi);
                      c.set("Storage." + item,c.getInt("Storage." + item) + hopper.get(hpi));
                      try {
                          c.save(f);
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                      hopperl.remove(hpi);
                      hopper.remove(hpi);
                      Bukkit.getLogger().info("[Storage] " + player.getName() + "の" + item + "の自動回収がOFFになりました。");
                      player.sendMessage("§6[§7Storege§6] " + item + "§bの自動回収機能が§coff§bになりました。");
                  }
                 gui.OpenItemGui(player, item);
              }else {
                  player.sendMessage("§6[§7Storege§6] §cこの機能はメンバーシッププラン専用です。");
                  player.sendMessage("§6[§7Storege§6] §cサブスクはこちらから: §astore.masa3mc.xyz");
              }
            } else if(raw == 10){
                int items = 0;

                for(ItemStack is : player.getInventory().getContents()){
                    if(is != null && is.getType() == Material.valueOf(item) && is.getItemMeta().getDisplayName().equals("")){
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

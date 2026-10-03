package masa3mc.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUI {

    public static final int PAGE_SIZE = 45;
    public static final int MAX_PAGE = 9;

    public void OpenGui(Player player, int n) {
        if (n < 1 || n > MAX_PAGE) {
            n = 1;
        }

        FileConfiguration c = YamlConfiguration.loadConfiguration(Storage.getStorageFile(player));

        StorageHolder holder = new StorageHolder(StorageHolder.Type.MAIN, n, null);
        Inventory inv = Bukkit.createInventory(holder, 54, "§6[§7Storage§6]");
        holder.setInventory(inv);

        List<String> storages = c.getStringList("Storages");
        int start = (n - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, storages.size());
        for (int number = start; number < end; number++) {
            String item = storages.get(number);
            Material material = Material.matchMaterial(item);
            ItemStack itemStack = new ItemStack(material != null && material.isItem() ? material : Material.BARRIER);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§7No." + number + "-§6" + item);
            List<String> list = capacityLore(c.getInt("Storage." + item));
            if (material == null || !material.isItem()) {
                list.add("§c(このアイテムは現在のバージョンに存在しません)");
            }
            meta.setLore(list);
            itemStack.setItemMeta(meta);
            inv.setItem(number - start, itemStack);
        }

        for (int i = 0; i < MAX_PAGE; i++) {
            ItemStack itemStack = new ItemStack(n == i + 1 ? Material.RED_SHULKER_BOX : Material.WHITE_SHULKER_BOX);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§6[§7Storage§6] - " + (i + 1));
            itemStack.setItemMeta(meta);
            inv.setItem(i + PAGE_SIZE, itemStack);
        }
        player.openInventory(inv);
    }


    public void OpenItemGui(Player player, String item) {
        Material material = Material.matchMaterial(item);
        if (material == null || !material.isItem()) {
            player.sendMessage("§6[§7Storage§6] §c" + item + "は現在のバージョンに存在しないため操作できません。");
            return;
        }

        StorageHolder holder = new StorageHolder(StorageHolder.Type.ITEM, 1, material.name());
        Inventory inv = Bukkit.createInventory(holder, 27, "§6[§7Storage§6] items");
        holder.setInventory(inv);

        FileConfiguration c = YamlConfiguration.loadConfiguration(Storage.getStorageFile(player));
        int items = c.getInt("Storage." + material.name());

        ItemStack item1 = new ItemStack(material);
        ItemMeta meta1 = item1.getItemMeta();
        meta1.setDisplayName("§6" + material.name());
        meta1.setLore(capacityLore(items));
        item1.setItemMeta(meta1);

        ItemStack toggle = new ItemStack(Material.HOPPER);
        ItemMeta togglem = toggle.getItemMeta();
        togglem.setDisplayName("§6アイテム自動回収");
        ArrayList<String> toggle_list = new ArrayList<>();
        if (Listeners.isAutoCollect(player, material.name())) {
            toggle_list.add("§6現在: §aon");
        } else {
            toggle_list.add("§6現在: §coff");
        }
        togglem.setLore(toggle_list);
        toggle.setItemMeta(togglem);

        inv.setItem(8, toggle);
        inv.setItem(10, button(Material.GREEN_CONCRETE, "§6すべて保存する"));
        inv.setItem(11, button(Material.LIME_CONCRETE, "§61stack保存する"));
        inv.setItem(13, item1);
        if (items > 0) {
            inv.setItem(15, button(Material.LIGHT_BLUE_CONCRETE, "§61stack取り出す"));
            inv.setItem(16, button(Material.BLUE_CONCRETE, "§6インベントリの空き分だけ取り出す"));
        }
        inv.setItem(26, button(Material.RED_CONCRETE, "§c戻る"));
        player.openInventory(inv);
    }

    private static ItemStack button(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private static List<String> capacityLore(int items) {
        List<String> list = new ArrayList<>();
        list.add("§7Storage capacity");
        if (items < 64) {
            list.add("§7" + items + " items");
        } else {
            list.add("§7" + (items / 64) + " stack");
            list.add("§7" + (items % 64) + " items");
        }
        return list;
    }
}

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

    public void OpenGui(Player player, int n) {
        int maxPages = Storage.getMaxPages();
        if (n < 1 || n > maxPages) {
            n = 1;
        }

        FileConfiguration c = YamlConfiguration.loadConfiguration(Storage.getStorageFile(player));

        StorageHolder holder = new StorageHolder(StorageHolder.Type.MAIN, n, null);
        Inventory inv = Bukkit.createInventory(holder, 54, Messages.get("gui.title-main"));
        holder.setInventory(inv);

        List<String> storages = c.getStringList("Storages");
        int start = (n - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, storages.size());
        for (int number = start; number < end; number++) {
            String item = storages.get(number);
            Material material = Material.matchMaterial(item);
            boolean exists = material != null && material.isItem();
            ItemStack itemStack = new ItemStack(exists ? material : Material.BARRIER);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(Messages.get("gui.item-name", "number", number, "item", item));
            List<String> list = capacityLore(c.getInt("Storage." + item));
            if (!exists) {
                list.add(Messages.get("gui.missing-material"));
            }
            meta.setLore(list);
            itemStack.setItemMeta(meta);
            inv.setItem(number - start, itemStack);
        }

        for (int i = 0; i < maxPages; i++) {
            ItemStack itemStack = new ItemStack(n == i + 1 ? Material.RED_SHULKER_BOX : Material.WHITE_SHULKER_BOX);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(Messages.get("gui.page", "page", i + 1));
            itemStack.setItemMeta(meta);
            inv.setItem(i + PAGE_SIZE, itemStack);
        }
        player.openInventory(inv);
    }


    public void OpenItemGui(Player player, String item) {
        Material material = Material.matchMaterial(item);
        if (material == null || !material.isItem()) {
            Messages.send(player, "item-missing", "item", item);
            return;
        }

        StorageHolder holder = new StorageHolder(StorageHolder.Type.ITEM, 1, material.name());
        Inventory inv = Bukkit.createInventory(holder, 27, Messages.get("gui.title-item"));
        holder.setInventory(inv);

        FileConfiguration c = YamlConfiguration.loadConfiguration(Storage.getStorageFile(player));
        int items = c.getInt("Storage." + material.name());

        ItemStack item1 = new ItemStack(material);
        ItemMeta meta1 = item1.getItemMeta();
        meta1.setDisplayName(Messages.get("gui.item-detail-name", "item", material.name()));
        meta1.setLore(capacityLore(items));
        item1.setItemMeta(meta1);

        if (Storage.getPlugin().getConfig().getBoolean("auto-collect.enabled", true)) {
            ItemStack toggle = new ItemStack(Material.HOPPER);
            ItemMeta togglem = toggle.getItemMeta();
            togglem.setDisplayName(Messages.get("gui.autocollect"));
            ArrayList<String> toggle_list = new ArrayList<>();
            toggle_list.add(Messages.get(Listeners.isAutoCollect(player, material.name())
                    ? "gui.autocollect-status-on" : "gui.autocollect-status-off"));
            togglem.setLore(toggle_list);
            toggle.setItemMeta(togglem);
            inv.setItem(8, toggle);
        }

        inv.setItem(10, button(Material.GREEN_CONCRETE, Messages.get("gui.store-all")));
        inv.setItem(11, button(Material.LIME_CONCRETE, Messages.get("gui.store-stack")));
        inv.setItem(13, item1);
        if (items > 0) {
            inv.setItem(15, button(Material.LIGHT_BLUE_CONCRETE, Messages.get("gui.take-stack")));
            inv.setItem(16, button(Material.BLUE_CONCRETE, Messages.get("gui.take-all")));
        }
        inv.setItem(26, button(Material.RED_CONCRETE, Messages.get("gui.back")));
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
        return Messages.getList("gui.capacity", "stacks", items / 64, "items", items % 64, "total", items);
    }
}

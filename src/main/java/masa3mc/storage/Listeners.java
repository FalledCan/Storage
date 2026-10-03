package masa3mc.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Listeners implements Listener {

    /** 自動回収中のアイテム: プレイヤーUUID -> (アイテム名 -> 未保存の回収数) */
    private static final Map<UUID, Map<String, Integer>> hopper = new HashMap<>();

    /** 自動回収の一時バッファがこの数を超えたらファイルへ書き込む */
    private static final int FLUSH_THRESHOLD = 63;

    /** Storage GUIを開いているプレイヤー */
    private static final Set<UUID> viewers = new HashSet<>();

    /** sub.large は旧バージョンとの互換用 */
    public static boolean canAutoCollect(Player player) {
        return Storage.getPlugin().getConfig().getBoolean("auto-collect.enabled", true)
                && (player.hasPermission("storage.autocollect") || player.hasPermission("sub.large"));
    }

    public static void closeIfStorageOpen(Player player) {
        if (viewers.remove(player.getUniqueId())) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof StorageHolder) {
            viewers.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof StorageHolder) {
            viewers.remove(event.getPlayer().getUniqueId());
        }
    }

    public static boolean isAutoCollect(Player player, String item) {
        Map<String, Integer> map = hopper.get(player.getUniqueId());
        return map != null && map.containsKey(item);
    }

    private static void enableAutoCollect(Player player, String item) {
        hopper.computeIfAbsent(player.getUniqueId(), k -> new LinkedHashMap<>()).putIfAbsent(item, 0);
    }

    /**
     * 自動回収で溜まった分をストレージへ書き込み、回収対象のアイテム一覧を返す。
     * remove が true の場合はメモリ上の自動回収状態も破棄する。
     */
    public static List<String> flushAutoCollect(Player player, boolean remove) {
        Map<String, Integer> map = remove ? hopper.remove(player.getUniqueId()) : hopper.get(player.getUniqueId());
        List<String> items = new ArrayList<>();
        if (map == null || map.isEmpty()) {
            return items;
        }
        File f = Storage.getStorageFile(player);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            c.set("Storage." + e.getKey(), c.getInt("Storage." + e.getKey()) + e.getValue());
            e.setValue(0);
            items.add(e.getKey());
        }
        save(c, f);
        return items;
    }

    /** 自動回収状態をre.ymlに退避する(ログアウト/サーバー停止時) */
    public static void saveAutoCollectState(Player player) {
        if (!hopper.containsKey(player.getUniqueId())) {
            return;
        }
        List<String> items = flushAutoCollect(player, true);
        reYaml.get().set(player.getUniqueId().toString(), items.isEmpty() ? null : items);
        reYaml.save();
    }

    /** re.ymlから自動回収状態を復元する(ログイン/リロード時) */
    public static void restoreAutoCollectState(Player player) {
        String key = player.getUniqueId().toString();
        // 権限が無い間は状態を残しておき、再度権限を得たときに復元する
        if (reYaml.get().get(key) == null || !canAutoCollect(player)) {
            return;
        }
        for (String item : reYaml.get().getStringList(key)) {
            if (!isAutoCollect(player, item)) {
                enableAutoCollect(player, item);
                Messages.send(player, "autocollect-on", "item", item);
            }
        }
        reYaml.get().set(key, null);
        reYaml.save();
    }

    private static void save(FileConfiguration c, File f) {
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** エンチャント・名前・NBT等が付いていない素のアイテムか */
    private static boolean isPlain(ItemStack is, Material material) {
        return is != null && is.getType() == material && is.isSimilar(new ItemStack(material));
    }

    private static int countPlain(PlayerInventory inv, Material material) {
        int items = 0;
        for (ItemStack is : inv.getStorageContents()) {
            if (isPlain(is, material)) {
                items += is.getAmount();
            }
        }
        return items;
    }

    /** 素のアイテムを最大amount個インベントリから取り除き、実際に取り除いた数を返す */
    private static int removePlain(PlayerInventory inv, Material material, int amount) {
        int removed = 0;
        ItemStack[] contents = inv.getStorageContents();
        for (int i = 0; i < contents.length && removed < amount; i++) {
            ItemStack is = contents[i];
            if (!isPlain(is, material)) {
                continue;
            }
            int take = Math.min(is.getAmount(), amount - removed);
            removed += take;
            if (take == is.getAmount()) {
                contents[i] = null;
            } else {
                is.setAmount(is.getAmount() - take);
            }
        }
        inv.setStorageContents(contents);
        return removed;
    }

    /** 最大amount個インベントリへ追加し、実際に追加できた数を返す(溢れた分は追加しない) */
    private static int addItems(PlayerInventory inv, Material material, int amount) {
        int added = 0;
        int max = material.getMaxStackSize();
        while (added < amount) {
            int size = Math.min(max, amount - added);
            Map<Integer, ItemStack> left = inv.addItem(new ItemStack(material, size));
            int notAdded = 0;
            for (ItemStack is : left.values()) {
                notAdded += is.getAmount();
            }
            added += size - notAdded;
            if (notAdded > 0) {
                break;
            }
        }
        return added;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof StorageHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof StorageHolder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        StorageHolder holder = (StorageHolder) event.getInventory().getHolder();
        int raw = event.getRawSlot();
        if (raw < 0 || raw >= event.getInventory().getSize()) {
            return;
        }
        // インベントリ操作中にGUIを開き直すとクライアントと不整合が起きるため1tick遅らせる
        Bukkit.getScheduler().runTask(Storage.getPlugin(), () -> {
            if (!player.isOnline()) {
                return;
            }
            if (holder.getType() == StorageHolder.Type.MAIN) {
                clickMain(player, holder, raw);
            } else {
                clickItem(player, holder, raw);
            }
        });
    }

    private void clickMain(Player player, StorageHolder holder, int raw) {
        GUI gui = new GUI();
        if (raw >= GUI.PAGE_SIZE) {
            gui.OpenGui(player, raw - GUI.PAGE_SIZE + 1);
            return;
        }
        FileConfiguration c = YamlConfiguration.loadConfiguration(Storage.getStorageFile(player));
        List<String> storages = c.getStringList("Storages");
        int index = (holder.getPage() - 1) * GUI.PAGE_SIZE + raw;
        if (index >= storages.size()) {
            return;
        }
        gui.OpenItemGui(player, storages.get(index));
    }

    private void clickItem(Player player, StorageHolder holder, int raw) {
        String item = holder.getItem();
        Material material = Material.matchMaterial(item);
        if (material == null) {
            return;
        }
        File f = Storage.getStorageFile(player);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.get("Storage." + item) == null) {
            player.closeInventory();
            return;
        }
        PlayerInventory inv = player.getInventory();
        GUI gui = new GUI();
        int stored = c.getInt("Storage." + item);

        switch (raw) {
            case 8:
                if (!Storage.getPlugin().getConfig().getBoolean("auto-collect.enabled", true)) {
                    return;
                }
                if (!canAutoCollect(player)) {
                    Messages.send(player, "autocollect-no-permission");
                    return;
                }
                if (!isAutoCollect(player, item)) {
                    enableAutoCollect(player, item);
                    Messages.send(player, "autocollect-on", "item", item);
                } else {
                    Map<String, Integer> map = hopper.get(player.getUniqueId());
                    int buffered = map.remove(item);
                    if (map.isEmpty()) {
                        hopper.remove(player.getUniqueId());
                    }
                    c.set("Storage." + item, stored + buffered);
                    save(c, f);
                    Messages.send(player, "autocollect-off", "item", item);
                }
                break;
            case 10: {
                int removed = removePlain(inv, material, countPlain(inv, material));
                c.set("Storage." + item, stored + removed);
                save(c, f);
                break;
            }
            case 11: {
                int removed = removePlain(inv, material, 64);
                c.set("Storage." + item, stored + removed);
                save(c, f);
                break;
            }
            case 15: {
                int added = addItems(inv, material, Math.min(stored, material.getMaxStackSize()));
                c.set("Storage." + item, stored - added);
                save(c, f);
                break;
            }
            case 16: {
                int added = addItems(inv, material, stored);
                c.set("Storage." + item, stored - added);
                save(c, f);
                break;
            }
            case 26:
                gui.OpenGui(player, 1);
                return;
            default:
                return;
        }
        gui.OpenItemGui(player, item);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHopper(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Map<String, Integer> map = hopper.get(player.getUniqueId());
        if (map == null) {
            return;
        }
        ItemStack itemStack = event.getItem().getItemStack();
        String item = itemStack.getType().name();
        if (!map.containsKey(item) || !isPlain(itemStack, itemStack.getType())) {
            return;
        }
        if (!canAutoCollect(player)) {
            return;
        }
        event.setCancelled(true);
        event.getItem().remove();
        int buffered = map.get(item) + itemStack.getAmount();
        if (buffered > FLUSH_THRESHOLD) {
            File f = Storage.getStorageFile(player);
            FileConfiguration c = YamlConfiguration.loadConfiguration(f);
            c.set("Storage." + item, c.getInt("Storage." + item) + buffered);
            save(c, f);
            buffered = 0;
        }
        map.put(item, buffered);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        restoreAutoCollectState(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // 権限を失っていても回収済みの分は必ず保存する
        saveAutoCollectState(event.getPlayer());
    }
}

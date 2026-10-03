package masa3mc.storage;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Storage GUIを識別するためのHolder。
 * タイトル文字列ではなくHolderで判定することで、同名の別インベントリとの誤判定を防ぐ。
 */
public class StorageHolder implements InventoryHolder {

    public enum Type { MAIN, ITEM }

    private final Type type;
    private final int page;
    private final String item;
    private final int selected;
    private Inventory inventory;

    public StorageHolder(Type type, int page, String item) {
        this(type, page, item, -1);
    }

    /** selected: 並び替えのために選択中のアイテム番号 (未選択は -1) */
    public StorageHolder(Type type, int page, String item, int selected) {
        this.type = type;
        this.page = page;
        this.item = item;
        this.selected = selected;
    }

    public Type getType() {
        return type;
    }

    public int getPage() {
        return page;
    }

    public int getSelected() {
        return selected;
    }

    public String getItem() {
        return item;
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}

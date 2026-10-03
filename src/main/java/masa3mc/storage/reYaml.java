package masa3mc.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class reYaml {

    private static File file;
    private static FileConfiguration cf;

    public static void setup() {
        Storage.getPlugin().getDataFolder().mkdirs();
        file = new File(Storage.getPlugin().getDataFolder(), "re.yml");
        cf = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return cf;
    }

    public static void save() {
        try {
            cf.save(file);
        } catch (IOException e) {
            Storage.getPlugin().getLogger().warning("re.ymlの保存に失敗しました: " + e.getMessage());
        }
    }

}

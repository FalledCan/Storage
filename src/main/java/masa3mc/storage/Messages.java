package masa3mc.storage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * lang/&lt;language&gt;.yml からメッセージを読み込む。
 * 足りないキーはjar内の同じ言語ファイル、さらに en.yml の値で補う。
 */
public final class Messages {

    private static final String[] BUNDLED = {"en", "ja"};

    private static FileConfiguration lang;

    private Messages() {
    }

    public static void load(Storage plugin) {
        for (String code : BUNDLED) {
            if (!new File(plugin.getDataFolder(), "lang/" + code + ".yml").exists()) {
                plugin.saveResource("lang/" + code + ".yml", false);
            }
        }

        String code = plugin.getConfig().getString("language", "en");
        File file = new File(plugin.getDataFolder(), "lang/" + code + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("lang/" + code + ".yml not found, falling back to en.");
            code = "en";
            file = new File(plugin.getDataFolder(), "lang/en.yml");
        }

        lang = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration defaults = bundled(plugin, code);
        if (!code.equals("en")) {
            YamlConfiguration en = bundled(plugin, "en");
            if (defaults == null) {
                defaults = en;
            } else if (en != null) {
                defaults.setDefaults(en);
            }
        }
        if (defaults != null) {
            lang.setDefaults(defaults);
        }
    }

    private static YamlConfiguration bundled(Storage plugin, String code) {
        InputStream in = plugin.getResource("lang/" + code + ".yml");
        if (in == null) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    /** key のメッセージを取得し、{name} 形式のプレースホルダーを置換する (placeholders は name, value の順) */
    public static String get(String key, Object... placeholders) {
        String s = lang.getString(key, key);
        s = s.replace("{prefix}", lang.getString("prefix", ""));
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            s = s.replace("{" + placeholders[i] + "}", String.valueOf(placeholders[i + 1]));
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> getList(String key, Object... placeholders) {
        List<String> list = new ArrayList<>();
        for (String line : lang.getStringList(key)) {
            for (int i = 0; i + 1 < placeholders.length; i += 2) {
                line = line.replace("{" + placeholders[i] + "}", String.valueOf(placeholders[i + 1]));
            }
            list.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return list;
    }

    public static void send(CommandSender sender, String key, Object... placeholders) {
        String s = get(key, placeholders);
        if (!s.isEmpty()) {
            sender.sendMessage(s);
        }
    }
}

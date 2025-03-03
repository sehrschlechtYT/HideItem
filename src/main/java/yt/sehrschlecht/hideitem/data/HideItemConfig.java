package yt.sehrschlecht.hideitem.data;

import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import yt.sehrschlecht.hideitem.HideItem;
import yt.sehrschlecht.hideitem.data.database.Database;
import yt.sehrschlecht.hideitem.data.database.MySQL;
import yt.sehrschlecht.hideitem.data.database.SQLite;
import yt.sehrschlecht.hideitem.util.NBTTags;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HideItemConfig {
    private final HideItem plugin;
    private YamlConfiguration config;


    /* Data storage */
    private String STORAGE_METHOD;
    private Database database;

    /* Disabled features */
    private Boolean DISABLE_ITEMS;
    private Boolean DISABLE_COMMANDS;

    /* Item settings */
    private ItemStack HIDE_ITEM;
    private ItemStack SHOW_ITEM;
    private Boolean FIRST_FREE_SLOT;
    private Integer ITEM_SLOT;
    private Boolean FIXED_ITEM;

    /* Messages */
    private String SHOW_MESSAGE;
    private String HIDE_MESSAGE;
    private String COOLDOWN_MESSAGE;
    private String NO_PERMISSION_MESSAGE;
    private String SHOWN_FOR_MESSAGE;
    private String HIDDEN_FOR_MESSAGE;
    private String TOGGLED_FOR_MESSAGE;

    /* Commands */
    private Boolean USE_ALIASES;
    private String HIDE_ALIAS;
    private String SHOW_ALIAS;
    private String TOGGLE_ALIAS;

    /* Permissions */
    private Boolean REQUIRE_PERMISSION_FOR_ITEMS;
    private Boolean REQUIRE_PERMISSION_FOR_COMMANDS;

    /* Other */
    private Integer COOLDOWN;
    private Boolean DEFAULT_SHOWN;

    public HideItemConfig(HideItem plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /* Getting data from config */
    private void loadConfig() {

        try {

            final File file = new File(plugin.getDataFolder(), "config.yml");

            // If config doesn't exist create new one
            if (!file.exists()) {

                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            config = new YamlConfiguration();
            config.load(file);


            // If config is empty copy default one
            if (config.getConfigurationSection("").getKeys(true).isEmpty()) {
                Reader defConfigStream = new InputStreamReader(this.plugin.getResource("config.yml"), StandardCharsets.UTF_8);
                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    config.setDefaults(defConfig);

                    plugin.saveResource("config.yml", true);

                    config.load(file);
                }

            } else if (!config.getString("version", "").equalsIgnoreCase(plugin.getDescription().getVersion())) {
                if (config.getBoolean("rename-old-config", true)) {
                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &f| &cYour config file in '/plugins/HideItem/' is outdated. A new one will be created! (Your current one will be saved as config.old.yml)"));

                    File oldFile = new File(plugin.getDataFolder(), "config.old.yml");

                    if (oldFile.exists()) {
                        plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &f| &cconfig.old.yml already exists in '/plugins/HideItem/'! Please delete it so the new config file can be generated!"));

                    } else {
                        file.renameTo(oldFile);

                        Reader defConfigStream = new InputStreamReader(this.plugin.getResource("config.yml"), StandardCharsets.UTF_8);
                        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                        config.setDefaults(defConfig);

                        plugin.saveResource("config.yml", true);

                        config.load(file);
                    }
                } else {
                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lHideItem &f| &cYour config file in '/plugins/HideItem/' is outdated. Please update it!"));
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        //
        // Storage settings
        //

        // Storage method
        STORAGE_METHOD = config.getString("storage-method", "none").toLowerCase();

        // Setup data File if not exists
        if (STORAGE_METHOD.equals("file")) {

            final File file = new File(plugin.getDataFolder(),"data.yml");

            try {

                // Create new if doesn't exist
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // Setup MySQL if storage method is database
        if(STORAGE_METHOD.equals("mysql")) database = new MySQL(config);
        if(STORAGE_METHOD.equals("sqlite")) database = new SQLite(plugin);
        //if(STORAGE_METHOD.equals("mongodb")) database = new MongoDB(config);

        // Disabled features
        DISABLE_ITEMS = config.getBoolean("disable-items", false);
        DISABLE_COMMANDS = config.getBoolean("disable-commands", false);

        //
        // Items
        //

        // Hide item
        Material hideMaterial = Material.getMaterial(config.getString("hide-item.material", "GRAY_DYE"));
        if (hideMaterial == null) hideMaterial = Material.GRAY_DYE;

        if(config.getBoolean("hide-item.custom-head.enabled", false)) {
            if(config.getString("hide-item.custom-head.mode", "value").equalsIgnoreCase("name")) {
                HIDE_ITEM = SkullCreator.itemFromName(config.getString("hide-item.custom-head.name", "Notch"));
            } else {
                HIDE_ITEM = SkullCreator.itemFromBase64(config.getString("hide-item.custom-head.value", ""));
            }
        } else {
            HIDE_ITEM = new ItemStack(hideMaterial, 1);
        }


        ItemMeta hideItemMeta = HIDE_ITEM.getItemMeta();

        hideItemMeta.setDisplayName(config.getString("hide-item.name", "&eHide Players &7(Shown)").replace("&", "§"));

        ArrayList<String> hideItemLore = (ArrayList<String>) config.getStringList("hide-item.lore");

        for (int i = 0; i < hideItemLore.size(); i++) {
            hideItemLore.set(i, hideItemLore.get(i).replace("&", "§"));
        }

        hideItemMeta.setLore(hideItemLore);

        if (config.getBoolean("hide-item.enchanted", false)) hideItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        HIDE_ITEM.setItemMeta(hideItemMeta);

        if (config.getBoolean("hide-item.enchanted", false)) HIDE_ITEM.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        HIDE_ITEM = NBTTags.setBoolean(HIDE_ITEM, "HIDE_ITEM", true);

        // Show Item
        Material showMaterial = Material.getMaterial(config.getString("show-item.material", "LIME_DYE"));
        if (showMaterial == null) showMaterial = Material.LIME_DYE;

        if(config.getBoolean("show-item.custom-head.enabled", false)) {
            if(config.getString("show-item.custom-head.mode", "value").equalsIgnoreCase("name")) {
                SHOW_ITEM = SkullCreator.itemFromName(config.getString("show-item.custom-head.name", "Notch"));
            } else {
                SHOW_ITEM = SkullCreator.itemFromBase64(config.getString("show-item.custom-head.value", ""));
            }
        } else {
            SHOW_ITEM = new ItemStack(showMaterial, 1);
        }

        ItemMeta showItemMeta = SHOW_ITEM.getItemMeta();

        showItemMeta.setDisplayName(config.getString("show-item.name", "&eHide Players &7(Hidden)").replace("&", "§"));

        ArrayList<String> showItemLore = (ArrayList<String>) config.getStringList("show-item.lore");

        for (int i = 0; i < showItemLore.size(); i++) {
            showItemLore.set(i, showItemLore.get(i).replace("&", "§"));
        }

        showItemMeta.setLore(showItemLore);

        if (config.getBoolean("show-item.enchanted", false)) showItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        SHOW_ITEM.setItemMeta(showItemMeta);

        if (config.getBoolean("show-item.enchanted", false)) SHOW_ITEM.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);


        SHOW_ITEM = NBTTags.setBoolean(SHOW_ITEM, "SHOW_ITEM", true);

        //
        // Other settings
        //

        FIRST_FREE_SLOT = config.getBoolean("first-free-slot", false);
        ITEM_SLOT = config.getInt("slot", 9);

        if (ITEM_SLOT <= 0 || ITEM_SLOT >= 9) ITEM_SLOT = 9;

        FIXED_ITEM = config.getBoolean("fixed-item", true);

        //
        // Messages
        //

        SHOW_MESSAGE = config.getString("show-message", "&aAll players are now visible!");
        HIDE_MESSAGE = config.getString("hide-message", "&aAll players are now hidden!");
        COOLDOWN_MESSAGE = config.getString("cooldown-message", "&eYou are in cooldown for %cooldown% more seconds.");
        NO_PERMISSION_MESSAGE = config.getString("no-permission-message", "&cYou don't have permission to do this!");
        SHOWN_FOR_MESSAGE = config.getString("shown-for-message", "&aYou have successfully hidden other players for %player%!");
        HIDDEN_FOR_MESSAGE = config.getString("hidden-for-message", "&aYou have successfully shown other players for %player%!");
        TOGGLED_FOR_MESSAGE = config.getString("toggled-for-message", "&aYou have successfully toggled visibility of other players for %player%!");

        //
        // Commands & aliases
        //

        USE_ALIASES = config.getBoolean("use-aliases", true);
        SHOW_ALIAS = config.getString("show-command-alias", "/show");
        HIDE_ALIAS = config.getString("hide-command-alias", "/hide");
        TOGGLE_ALIAS = config.getString("toggle-command-alias", "/toggleplayers");

        //
        // Permissions
        //

        REQUIRE_PERMISSION_FOR_COMMANDS = config.getBoolean("require-permission-for-commands", false);
        REQUIRE_PERMISSION_FOR_ITEMS = config.getBoolean("require-permission-for-items", false);

        //
        // Other settings
        //
        COOLDOWN = config.getInt("cooldown", 5);
        DEFAULT_SHOWN = config.getBoolean("default-state-shown", true);
    }

    public void reload() {
        loadConfig();
    }

    //
    // Disabled features
    //
    public Boolean DISABLE_ITEMS() {
        return DISABLE_ITEMS;
    }

    public Boolean DISABLE_COMMANDS() {
        return DISABLE_COMMANDS;
    }

    //
    // Data Storage
    //

    public String STORAGE_METHOD() {
        return STORAGE_METHOD;
    }

    public Database DATABASE() {
        return database;
    }

    //
    // Items
    //

    public ItemStack HIDE_ITEM() {
        return HIDE_ITEM;
    }

    public ItemStack SHOW_ITEM() {
        return SHOW_ITEM;
    }

    public Boolean FIRST_FREE_SLOT() {
        return FIRST_FREE_SLOT;
    }

    public Integer ITEM_SLOT() {
        return ITEM_SLOT;
    }

    public Boolean FIXED_ITEM() {
        return FIXED_ITEM;
    }

    //
    // Messages
    //

    public String SHOW_MESSAGE() {
        return SHOW_MESSAGE;
    }

    public String HIDE_MESSAGE() {
        return HIDE_MESSAGE;
    }

    public String COOLDOWN_MESSAGE() {
        return COOLDOWN_MESSAGE;
    }

    public String NO_PERMISSION_MESSAGE() {
        return NO_PERMISSION_MESSAGE;
    }

    public String SHOWN_FOR_MESSAGE() {
        return SHOWN_FOR_MESSAGE;
    }

    public String HIDDEN_FOR_MESSAGE() {
        return HIDDEN_FOR_MESSAGE;
    }

    public String TOGGLED_FOR_MESSAGE() {
        return TOGGLED_FOR_MESSAGE;
    }

    //
    // Commands & aliases
    //

    public Boolean USE_ALIASES() {
        return USE_ALIASES;
    }

    public String SHOW_ALIAS() {
        return SHOW_ALIAS;
    }

    public String HIDE_ALIAS() {
        return HIDE_ALIAS;
    }

    public String TOGGLE_ALIAS() {
        return TOGGLE_ALIAS;
    }

    //
    // Permissions
    //

    public Boolean REQUIRE_PERMISSION_FOR_ITEMS() {
        return REQUIRE_PERMISSION_FOR_ITEMS;
    }

    public Boolean REQUIRE_PERMISSION_FOR_COMMANDS() {
        return REQUIRE_PERMISSION_FOR_COMMANDS;
    }

    //
    // Other settings
    //

    public Integer COOLDOWN() {
        return COOLDOWN;
    }

    public Boolean DEFAULT_SHOWN() {
        return DEFAULT_SHOWN;
    }


}

package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.enums.RewardsType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RewardManager {

    private DatabaseManager db;

    public RewardManager(DatabaseManager db) {
        this.db = db;
        db.connect();

        if (!db.tableExists(db.fixName("rewards"))) {
            if (!db.isMysql()) {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("rewards") + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "lobbyID INTEGER (11)," +
                        "reward_type VARCHAR (255)," +
                        "reward_command VARCHAR (255)," +
                        "reward_item_type VARCHAR (255)," +
                        "reward_item_amount INTEGER (64)," +
                        "reward_item_meta TEXT," +
                        "UNIQUE (id)" +
                        ");");
            } else {
                db.execute("CREATE TABLE IF NOT EXISTS " + db.fixName("rewards") + " (" +
                        "  `id` int(11) NOT NULL," +
                        "  `lobbyID` int(11) NULL," +
                        "  `reward_type` varchar(255) NOT NULL," +
                        "  `reward_command` varchar(255) NULL," +
                        "  `reward_item_type` varchar(255) NULL," +
                        "  `reward_item_amount` int(64) NULL," +
                        "  `reward_item_meta` text NULL," +
                        ");");
                db.execute("ALTER TABLE " + db.fixName("rewards") + " ADD PRIMARY KEY (`id`);");
                db.execute("ALTER TABLE " + db.fixName("rewards") + " MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;");

            }
        }
        db.disconnect();
    }

    public List<String> getRewards(int lobbyID) {
        List<String> tabComplite = new ArrayList<>();
        db.connect();
        if (db.tableExists(db.fixName("rewards"))) {
            String selectAllQuery = "SELECT id FROM " + db.fixName("rewards") + " WHERE lobbyID=?";
            List<Object[]> results = db.executeQuery(selectAllQuery, lobbyID);

            if (results != null) {
                for (Object[] data : results) {
                    tabComplite.add(String.valueOf(data[0]));
                }
            }
        }
        db.disconnect();
        return tabComplite;
    }

    private static String itemMetaToString(ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            YamlConfiguration config = new YamlConfiguration();
            config.set("item-meta", itemMeta);
            return config.saveToString();
        }
        return null;
    }

    private static ItemMeta stringToItemMeta(String serializedItemMeta) {
        if (serializedItemMeta != null && !serializedItemMeta.isEmpty()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.loadFromString(serializedItemMeta);
                return (ItemMeta) config.get("item-meta");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean addCommandReward(int lobbyID, String command) {
        db.connect();
        String insertSql = "INSERT INTO " + db.fixName("rewards") + " (lobbyID, reward_type, reward_command) VALUES (?, ?, ?)";
        if (db.execute(insertSql, lobbyID, "command", command)) {
            return true;
        }
        return false;
    }

    public boolean addItemReward(int lobbyID, ItemStack item) {
        db.connect();
        String insertSql = "INSERT INTO " + db.fixName("rewards") + " (lobbyID, reward_type, reward_item_type, reward_item_amount, reward_item_meta) VALUES (?, ?, ?, ?, ?)";
        if (db.execute(insertSql, lobbyID, "item", item.getType().toString(), item.getAmount(), this.itemMetaToString(item))) {
           return true;
        }
        return false;
    }

    public boolean removeItem(int lobbyID, int itemID) {
        db.connect();
        String removeSql = "DELETE FROM " + db.fixName("rewards") + " WHERE id=? AND lobbyID=?";
        if (db.execute(removeSql, itemID, lobbyID)) {
            return true;
        }
        return false;
    }

    public void giveRewards(int lobbyID, Player player) {
        db.connect();
        if (db.tableExists(db.fixName("rewards"))) {
            String selectAllQuery = "SELECT reward_type, reward_command, reward_item_type, reward_item_amount, reward_item_meta FROM " + db.fixName("rewards") + " WHERE lobbyID=?";
            List<Object[]> results = db.executeQuery(selectAllQuery, lobbyID);

            if (results != null) {
                for (Object[] data : results) {
                    if (data[0].equals("command")) {
                        String cmd = (String) data[1];
                        GeneralUtils.runCommand(Bukkit.getConsoleSender(), GeneralUtils.fixColors(cmd.replace("%player%", player.getName())));
                    } else if (data[0].equals("item")) {
                        String material = (String) data[2];
                        int amount = (int) data[3];
                        String itemMeta = (String) data[4];
                        ItemStack item = new ItemStack(Material.valueOf(material), amount);
                        item.setItemMeta(this.stringToItemMeta(itemMeta));
                        PlayerChestReward.addItem(item, player);
                    }
                }
            }
            db.disconnect();
        }
    }

    public StringBuilder getList(int lobbyID) {
        StringBuilder outputList = new StringBuilder();
        outputList.append(Messages.getMessage("items-listing-lobby", "&bRewards in Lobby: &6&l%lobby%\n", false).replace("%lobby%", lobbyID + ""));
        outputList.append("------------------------\n");

        db.connect();
        if (db.tableExists(db.fixName("rewards"))) {
            String selectAllQuery = "SELECT id, reward_type, reward_command, reward_item_type, reward_item_amount, reward_item_meta FROM " + db.fixName("rewards") + " WHERE lobbyID=?";
            List<Object[]> results = db.executeQuery(selectAllQuery, lobbyID);

            if (results != null) {
                for (Object[] data : results) {
                    int id = (int) data[0];
                    String type = (String) data[1];
                    String command = (String) data[2];

                    outputList.append(Messages.getMessage("items-listing-id", "&eID: &c%id%\n", false).replace("%id%", String.valueOf(id)));
                    outputList.append(Messages.getMessage("items-listing-type", "&eType: &c%type%\n", false).replace("%type%", type));

                    if (type.equals("command")) {
                        outputList.append(Messages.getMessage("items-listing-command", "&eCommand: &c%command%\n", false).replace("%command%", command));
                    } else if (type.equals("item")) {
                        String material = (String) data[3];
                        int amount = (int) data[4];
                        outputList.append(Messages.getMessage("items-listing-material", "&eMaterial: &c%material%\n", false).replace("%material%", material));
                        outputList.append(Messages.getMessage("items-listing-amount", "&eAmount: &c%amount%\n", false).replace("%amount%", String.valueOf(amount)));
                    }
                    outputList.append("------------------------\n");
                }
            }
        }
        db.disconnect();

        return outputList;
    }
}
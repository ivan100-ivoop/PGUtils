package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LobbyMenu {

    public  LobbyMenu(){}
    private List<ItemStack> items = new ArrayList<>();
    private static int updateTask = -1;
    private ItemStack itemStack = null;
    private ItemMeta itemStackMeta = null;
    private Material material = Material.getMaterial(PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-menu.material", "NETHER_STAR"));
    public static String LobbyGuiTitle = GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-menu.title", "&7Lobby"));
    private String getName(String lobbyID){
        return GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-menu.name", "&cLobby &e%id%").replace("%id%", lobbyID));
    }

    private List<String> fixLore(List<String> content, String status, String min, String max){
        List<String> lore = new ArrayList<>();

        for(String item : content){
            if(item.contains("%status%")){
                lore.add(GeneralUtils.fixColors(item.replace("%status%", status)));
            }
            if(item.contains("%min%") && item.contains("%max%")){
                String out = item.replace("%min%", min);
                out = out.replace("%max%", max);
                lore.add(GeneralUtils.fixColors(out));
            }
        }

        return lore;
    }

    public LobbyMenu prepareMenu(){
        items.clear();

        for (Lobby lobby: Lobby.lobbies) {
            if(lobby.isLocked()){
                itemStack = new ItemStack(Material.BARRIER);
                itemStackMeta = itemStack.getItemMeta();
                itemStackMeta.setDisplayName(this.getName("is Locsked"));
                itemStackMeta.setCustomModelData(Integer.parseInt("0"));
            } else {
                itemStack = new ItemStack(material);
                itemStackMeta = itemStack.getItemMeta();
                itemStackMeta.setDisplayName(this.getName("" + lobby.getID()));
                itemStackMeta.setLore(this.fixLore(PGUtils.getPlugin(PGUtils.class).getConfig().getStringList("lobby-menu.lore"), lobby.getStatus(), "" + lobby.getCurrentPlayersAmount(), ""+lobby.getMaxPlayers()));
                itemStackMeta.setCustomModelData(Integer.parseInt(lobby.getID() + ""));
            }

            if(PGUtils.getPlugin(PGUtils.class).getConfig().getBoolean("lobby-menu.glow", false)){
                itemStackMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
                itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            itemStack.setItemMeta(itemStackMeta);

            items.add(itemStack);
        }
        return this;
    }

    private Inventory getDisplayMenu(){
        Inventory inv = Bukkit.createInventory(null, InventoryType.PLAYER, this.LobbyGuiTitle);
        ItemStack[] chestContents = this.items.toArray(new ItemStack[0]);
        inv.setContents(chestContents);

        for(HumanEntity view : inv.getViewers()){
            view.setCanPickupItems(false);
            view.dropItem(false);
        }

        return inv;
    }

    public void getLobby(Player player){

        if(Lobby.lobbies.size() < 1){
            player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby is not found!")));
        } else {
            updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(PGUtils.getPlugin(PGUtils.class), new Runnable(){
                @Override
                public void run() {
                    prepareMenu();
                }
            }, 10l, 20l).getTaskId();
            player.openInventory(getDisplayMenu());
        }
    }

    public static void JoinLobbyClick(InventoryClickEvent e){
        if(e.getClickedInventory().getViewers().get(0).getOpenInventory().getTitle().equals(LobbyMenu.LobbyGuiTitle)){
            if(e.getClick().isLeftClick() || e.getClick().isRightClick() ){
                Player player = ((Player) e.getWhoClicked());
                Bukkit.getScheduler().runTask(PGUtils.getPlugin(PGUtils.class), () -> {
                    int id = e.getCurrentItem().getItemMeta().getCustomModelData();
                    Lobby lobby = Lobby.lobbies.stream()
                            .filter(lobby_ -> lobby_.getID() == id)
                            .findFirst()
                            .orElse(null);
                    if (lobby == null) {
                        player.sendMessage(GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).prefix + PGUtils.getPlugin(PGUtils.class).getConfig().getString("missing-lobby-message", "&cLobby is not found!")));
                    } else {
                        e.setCancelled(true);
                        PlayerChestReward.saveInv(player);
                        lobby.addPlayer(player);
                    }
                });
            }

            if(updateTask > -1){
                Bukkit.getScheduler().cancelTask(updateTask);
                updateTask = -1;
            }

            e.setCancelled(true);
        }
    }

}

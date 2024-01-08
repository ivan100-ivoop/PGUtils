package com.github.pgutils;

import com.github.pgutils.entities.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
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
    private Material material = Material.getMaterial(PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-menu.material", "NETHER_STAR"));
    private String LobbyGuiTitle = GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-menu.title", "&7Lobby"));
    private String getName(String lobbyID){
        return GeneralUtils.fixColors(PGUtils.getPlugin(PGUtils.class).getConfig().getString("lobby-menu.name", "&cLobby &e%id%").replace("%id%", lobbyID));
    }

    private List<String> fixLore(List<String> content, String status, String min, String max){
        List<String> lore = new ArrayList<>();

        for(String item : content){
            if(item.contains("%status%")){
                lore.add(GeneralUtils.fixColors(item.replace("%status%", status)));
            }
            if(item.contains("%min%")){
                lore.add(GeneralUtils.fixColors(item.replace("%min%", min)));
            }
            if(item.contains("%max%")){
                lore.add(GeneralUtils.fixColors(item.replace("%max%", max)));
            }
        }

        return lore;
    }

    public void prepareMenu(){
        for (Lobby lobby: Lobby.lobbies) {
            int currentPlayers = lobby.getPlayers().size() - 1;

            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemStackMeta = itemStack.getItemMeta();
            itemStackMeta.setDisplayName(this.getName("" + lobby.getID()));
            itemStackMeta.setLore(this.fixLore(PGUtils.getPlugin(PGUtils.class).getConfig().getStringList("lobby-menu.lore"), "", "" + lobby.getPlayers().size(), "10"));

            if(PGUtils.getPlugin(PGUtils.class).getConfig().getBoolean("lobby-menu.glow", false)){
                itemStackMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
                itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            itemStack.setItemMeta(itemStackMeta);
            items.add(itemStack);
        }
    }

    public Inventory getLobby(){
        Inventory inv = Bukkit.createInventory(null, InventoryType.PLAYER, this.LobbyGuiTitle);
        ItemStack[] chestContents = this.items.toArray(new ItemStack[0]);
        inv.setContents(chestContents);

        for(HumanEntity view : inv.getViewers()){
            view.setCanPickupItems(false);
            view.dropItem(false);
        }

        return inv;
    }

}

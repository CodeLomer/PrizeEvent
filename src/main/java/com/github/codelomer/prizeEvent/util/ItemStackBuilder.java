package com.github.codelomer.prizeEvent.util;

import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class ItemStackBuilder {
    private final ItemStack itemStack;

    public ItemStackBuilder(@NonNull ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        if(itemStack.getType().isAir()) throw new IllegalArgumentException("ItemStack must not be air");
    }


    public void modifyMeta(@NonNull ItemMetaBuilder metaBuilder) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            metaBuilder.modify(itemMeta);
            itemStack.setItemMeta(itemMeta);
        }
    }
    public void modifyContainer(@NonNull PersistentContainerBuilder containerBuilder){
        modifyMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            containerBuilder.modify(container);
        });
    }

    public ItemStackBuilder setDisplayName(String displayName){
        if(displayName != null) modifyMeta(meta -> meta.setDisplayName(SimpleUtil.toColorText(displayName)));
        return this;
    }
    public ItemStackBuilder setLore(List<String> lore){
        if(lore != null) modifyMeta(meta -> meta.setLore(SimpleUtil.toColorListText(lore)));
        return this;
    }
    public ItemStackBuilder setModelData(Integer modelData){
        if(modelData != null) modifyMeta(meta -> meta.setCustomModelData(modelData));
        return this;
    }

    public ItemStackBuilder addItemFlags(ItemFlag... flags){
        if(flags != null) modifyMeta(meta -> meta.addItemFlags(flags));
        return this;
    }

    public <T, Z> ItemStackBuilder setAttribute(NamespacedKey key, PersistentDataType <T,Z> persistentDataType, Z value, boolean replace){
        if (key != null && persistentDataType != null && value != null){
           modifyContainer(container -> {
               if(container.has(key,persistentDataType) && !replace) return;
               container.set(key,persistentDataType,value);
           });
        }
        return this;
    }

    public ItemStack build(){
        return itemStack;
    }

    public String getDisplayName() {
        String name = null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            name = itemMeta.getDisplayName();
        }
        return name;
    }

    public void addEnchants(Map<Enchantment, Integer> enchantments) {
       if(enchantments != null){
           itemStack.addEnchantments(enchantments);
       }
    }

    @FunctionalInterface
    public interface ItemMetaBuilder {
        void modify(@NonNull ItemMeta meta);
    }

    @FunctionalInterface
    public interface PersistentContainerBuilder{
        void modify(@NonNull PersistentDataContainer container);
    }

}

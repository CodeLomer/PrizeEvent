package com.github.codelomer.prizeEvent.gui;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

@RequiredArgsConstructor
public class GuiListener implements Listener {

    private final @NonNull GuiManager guiManager;

    @EventHandler
    public void onOpen(InventoryOpenEvent event){
        guiManager.onOpen(event);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        guiManager.onClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        guiManager.onClose(event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        guiManager.onDrag(event);
    }
}

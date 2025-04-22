package com.github.codelomer.prizeEvent.gui;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.model.DecorateButton;
import com.github.codelomer.prizeEvent.util.ItemStackBuilder;
import com.github.codelomer.prizeEvent.util.SimpleUtil;
import lombok.NonNull;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class EventGui implements GuiHandler{
    @NonNull
    private final GuiManager manager;
    @NonNull
    private final String title;
    private final int size;
    private final List<Integer> animationSlots;
    private final List<DecorateButton> decorateButtons;
    @NonNull
    private final JavaPlugin plugin;
    @NonNull
    private final EventConfig config;
    private boolean closable = false;
    private final ItemStack winnerHead;
    @NonNull
    private final UUID winner;
    private final List<ItemStack> memberHeads;
    @NonNull
    private final EventManager eventManager;
    private final Economy economy;
    private boolean tryClose =false;

    public EventGui(@NonNull GuiManager manager, @NonNull String title, int size, List<Integer> animationSlots,
                    List<DecorateButton> decorateButtons,
                    @NonNull JavaPlugin plugin, @NonNull EventConfig config,
                    @NonNull UUID winner, @NonNull List<UUID> members, @NonNull EventManager eventManager, Economy economy){
        this.manager = manager;
        this.title = title;
        this.size = size;
        this.animationSlots = animationSlots;
        this.decorateButtons = decorateButtons;
        this.plugin = plugin;
        this.config = config;
        winnerHead = createHead(winner);
        this.winner = winner;
        memberHeads = createHeads(members);
        this.eventManager = eventManager;
        this.economy = economy;
    }

    private List<ItemStack> createHeads(@NonNull List<UUID> members) {
        return members.stream().map(this::createHead).toList();
    }

    public void open(Player player){
        if(player != null){
            Inventory inventory = Bukkit.createInventory(null,size, SimpleUtil.toColorText(title));
            manager.register(this,inventory);
            player.openInventory(inventory);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        for (DecorateButton decorateButton : decorateButtons) {
            for (int slot : decorateButton.getSlots()) {
                inventory.setItem(slot, decorateButton.getItemStack());
            }
        }
        if(tryClose){
            tryClose = false;
            return;
        }
        Player player = (Player) event.getPlayer();
        AtomicInteger seconds = new AtomicInteger(config.getAnimationTime());

        // Оборачиваем task в AtomicReference
        AtomicReference<BukkitTask> taskRef = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            seconds.getAndDecrement();
            if (seconds.get() <= 0) {
                fillWinner(inventory);
                player.playSound(player, config.getWinSound(), 1, 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    closable = true;
                    player.closeInventory();
                }, 3 * 20);
                BukkitTask t = taskRef.get();
                if (t != null && !t.isCancelled()) {
                    t.cancel();
                }
                if(economy != null){
                    economy.depositPlayer(Bukkit.getOfflinePlayer(winner), eventManager.getPrizeAmount());
                }
                return;
            }
            closable = false;
            fill(inventory);
            player.playSound(player, config.getAnimationSound(), 1, 1);
            eventManager.clearWinnerTickets(winner);
            eventManager.setPrizeAmount(0);
        }, 0, 20);

        taskRef.set(task);

    }



    private void fill(Inventory inventory){
        for(int slot : animationSlots){
            if(slot >= inventory.getSize()) continue;
            inventory.setItem(slot, getRandomHead());
        }
    }

    private void fillWinner(Inventory inventory){
        for(int slot : animationSlots){
            if(slot >= inventory.getSize()) continue;
            inventory.setItem(slot, winnerHead);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if(closable){
            manager.unregister(event.getInventory());
            return;
        }
        tryClose = true;
        Bukkit.getScheduler().runTaskLater(plugin, () -> event.getPlayer().openInventory(event.getInventory()),1);
    }

    private ItemStack createHead(UUID uuid){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        ItemStackBuilder builder = new ItemStackBuilder(itemStack);
        builder.modifyMeta(meta -> {
            meta.setDisplayName(SimpleUtil.toColorText(offlinePlayer.getName()));
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwningPlayer(offlinePlayer);
        });
        return builder.build();
    }

    private ItemStack getRandomHead(){
        return memberHeads.get((int) (Math.random() * memberHeads.size()));
    }
}

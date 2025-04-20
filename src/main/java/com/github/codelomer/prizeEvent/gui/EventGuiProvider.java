package com.github.codelomer.prizeEvent.gui;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.model.DecorateButton;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class EventGuiProvider implements InventoryProvider {
    private final @NonNull EventConfig config;
    private final @NonNull List<UUID> members;
    private final @NonNull UUID winner;
    @Override
    public void init(Player player, InventoryContents contents) {
        List<DecorateButton> decorateButtons = config.getDecorateButtons();
        List<Integer> animationSlots = config.getPrizeRouletteGuiAnimationSlots();
        int winSlot = config.getPrizeRouletteGuiWinSlot();

        for(DecorateButton decorateButton: decorateButtons) {
            //TODO заполнение
        }
        
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}

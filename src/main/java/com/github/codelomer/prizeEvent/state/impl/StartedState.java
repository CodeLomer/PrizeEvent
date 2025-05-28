package com.github.codelomer.prizeEvent.state.impl;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.gui.EventGui;
import com.github.codelomer.prizeEvent.gui.GuiManager;
import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.state.EventState;
import com.github.codelomer.prizeEvent.state.EventStatus;
import com.github.codelomer.prizeEvent.model.EventTicket;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
@RequiredArgsConstructor
public class StartedState implements EventState {
    private final @NonNull EventConfig config;
    private final Economy economy;
    private final JavaPlugin plugin;
    private final @NonNull GuiManager guiManager;


    @Override
    public void enterState(EventManager eventManager) {
        Set<EventTicket> tickets = eventManager.getTickets();

        if (tickets.isEmpty()) {
            for(String line: config.getMessage("no-one-payed")) {
                Bukkit.broadcastMessage(line);
            }
            eventManager.setState(EventStatus.WAITING,0);
            return;
        }

       OfflinePlayer winner = getRandomWinner(tickets, eventManager);

        if (winner == null) {
            eventManager.setState(EventStatus.WAITING,0);
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            EventGui gui = new EventGui(guiManager,
                    config.getPrizeRouletteGuiTitle(),
                    config.getPrizeRouletteGuiSize(),
                    config.getPrizeRouletteGuiAnimationSlots(),
                    config.getDecorateButtons(),
                    plugin,
                    config,
                    winner.getUniqueId(),
                    eventManager.getMembers(),eventManager,economy);
            gui.open(player);
        }

        eventManager.setState(EventStatus.WAITING,0);
    }

    @Override
    public void leaveState(EventManager eventManager) {
    }

    @Override
    public EventStatus getStatus() {
        return EventStatus.STARTED;
    }

    @Override
    public long timeLeft() {
        return 0;
    }

    private OfflinePlayer getRandomWinner(@NonNull Set<EventTicket> tickets, @NonNull EventManager eventManager) {
        int randomIndex = (int) (Math.random() * tickets.size());
        EventTicket winnerTicket = tickets.stream().skip(randomIndex).findFirst().orElse(null);

        if (winnerTicket == null) {
            Bukkit.broadcastMessage("§cПроизошла неизвестная ошибка.");
            eventManager.setState(EventStatus.WAITING,0);
            return null;
        }
        tickets.remove(winnerTicket);
        return Bukkit.getOfflinePlayer(winnerTicket.getOwner());
    }
}

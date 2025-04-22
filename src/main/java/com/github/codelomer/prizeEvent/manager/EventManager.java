package com.github.codelomer.prizeEvent.manager;


import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.gui.GuiManager;
import com.github.codelomer.prizeEvent.state.EventState;
import com.github.codelomer.prizeEvent.state.EventStatus;
import com.github.codelomer.prizeEvent.model.EventTicket;
import com.github.codelomer.prizeEvent.state.impl.NotStartedState;
import com.github.codelomer.prizeEvent.state.impl.StartedState;
import com.github.codelomer.prizeEvent.state.impl.WaitingState;
import com.github.codelomer.prizeEvent.util.ConfigFile;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class EventManager {
    private int prizeAmount;
    private final EventConfig config;
    private int index = 0;
    private EventState state;
    private final JavaPlugin plugin;
    private final Economy economy;
    private final Multimap<UUID,EventTicket> tickets = ArrayListMultimap.create();
    private final GuiManager guiManager;
    private ConfigFile dataFile;

    public int getEventOverTime() {
        List<Integer> timers = config.getEventTimes();
        if(timers.isEmpty()) return 0;
        if(index >= timers.size()) index = 0;
        return timers.get(index);
    }

    public void giveTicket(@NonNull UUID uuid, int amount, boolean save) {
        for (int i = 0; i < amount; i++) {
            UUID ticketId = UUID.randomUUID();
            EventTicket ticket = new EventTicket(uuid, ticketId);
            tickets.put(uuid, ticket);
        }
        if(save){
            saveData(uuid);
        }
    }

    public void removeTicket(@NonNull UUID uuid, int amount) {
        Collection<EventTicket> userTickets = tickets.get(uuid);

        if (userTickets.isEmpty()) return;

        int removed = 0;
        Iterator<EventTicket> iterator = userTickets.iterator();
        while (iterator.hasNext() && removed < amount) {
            iterator.next();
            iterator.remove();
            removed++;
        }

        saveData(uuid);

    }

    public void initData(){
        dataFile = new ConfigFile(plugin,"data.yml");
        dataFile.saveConfig();

        ConfigurationSection playerSection = dataFile.getConfig().getConfigurationSection("players");
        if(playerSection != null){
            for(String uuidName: playerSection.getKeys(false)){
                try {
                    UUID uuid = UUID.fromString(uuidName);
                    int ticketCount = dataFile.getConfig().getInt(uuidName);
                    giveTicket(uuid,ticketCount,false);
                }catch (IllegalArgumentException e){
                    throw new RuntimeException("[PrizeEvent] invalid player uuid from data.yml");
                }
            }
        }
        this.prizeAmount = dataFile.getConfig().getInt("prize-sum");
    }

    public void saveData(UUID uuid){
        dataFile.getConfig().set("players."+uuid.toString(),tickets.get(uuid).size());
        dataFile.saveConfig();

    }


    public void setState(@NonNull EventStatus eventStatus) {
        if(state == null) changeState(eventStatus);
        if(state.getStatus() == eventStatus) return;
        state.leaveState(this);
        changeState(eventStatus);
    }

    private void changeState(@NonNull EventStatus status){
        switch (status) {
            case WAITING -> state = new WaitingState(plugin, config);
            case STARTED -> state = new StartedState(config,economy,plugin,guiManager);
            case NOT_STARTED -> state = new NotStartedState();
        }
        state.enterState(this);
    }

    public Set<EventTicket> getTickets() {
        return new HashSet<>(tickets.values());
    }

    public void updateEventOverTime() {
        index++;
    }

    public void reload() {
        setState(EventStatus.WAITING);

    }

    public int getTicketsAmount(@NonNull UUID uniqueId) {
        return tickets.get(uniqueId).size();
    }

    public void setPrizeAmount(int amount) {
        if(amount < 0) amount = 0;
        if(config.getMaxPrizeBalance() < 0){
            this.prizeAmount = prizeAmount+amount;
            return;
        }
        this.prizeAmount = Math.min(prizeAmount+amount,config.getMaxPrizeBalance());
        dataFile.getConfig().set("prize-sum",prizeAmount);
        dataFile.saveConfig();
    }

    public @NonNull List<UUID> getMembers() {
        return new ArrayList<>(tickets.keySet());
    }

    public void clearWinnerTickets(@NonNull UUID winner) {
        tickets.removeAll(winner);
    }
}

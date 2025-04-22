package com.github.codelomer.prizeEvent.config;

import com.github.codelomer.prizeEvent.model.DecorateButton;
import com.github.codelomer.prizeEvent.util.ConfigFile;
import com.github.codelomer.prizeEvent.util.ItemStackBuilder;
import com.github.codelomer.prizeEvent.util.SimpleUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public class EventConfig {
    private final ConfigFile configFile;
    private String defaultBarTitle;
    private BarColor defaultColor;
    private BarStyle defaultStyle;

    private String bossBarTitleAfter;
    private BarColor bossBarColorAfter;
    private BarStyle bossBarStyleAfter;
    private int bossBarUpdateSecondsAfter;

    private int maxPrizeBalance;
    private int balanceGive;

    private List<Integer> eventTimes;

    private Sound winSound;
    private Sound animationSound;
    private int animationTime;

    private String prizeRouletteGuiTitle;
    private int prizeRouletteGuiSize;
    private List<Integer> prizeRouletteGuiAnimationSlots;
    private List<DecorateButton> decorateButtons;

    private String timeFormat;

    private boolean reversed;



    public EventConfig(@NonNull JavaPlugin plugin){
        configFile = new ConfigFile(plugin, "config.yml");
        configFile.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig(){
        ConfigurationSection section = configFile.getConfig();
        defaultBarTitle = SimpleUtil.toColorText(section.getString("boss-bar-info.default-text"));
        bossBarTitleAfter = SimpleUtil.toColorText(section.getString("boss-bar-info.after.text"));

        defaultColor = getEnum(section, "boss-bar-info.default-color", BarColor.class, BarColor.BLUE);
        bossBarColorAfter = getEnum(section, "boss-bar-info.after.color", BarColor.class, BarColor.BLUE);

        defaultStyle = getEnum(section, "boss-bar-info.default-style", BarStyle.class, BarStyle.SOLID);
        bossBarStyleAfter = getEnum(section, "boss-bar-info.after.style", BarStyle.class, BarStyle.SOLID);

        maxPrizeBalance = section.getInt("max-prize-balance",-1);
        balanceGive = section.getInt("balance-give",0);

        reversed = section.getBoolean("boss-bar-info.after.reversed");

        bossBarUpdateSecondsAfter = SimpleUtil.getSecondsTime(section.getString("boss-bar-info.after.time",""));

        eventTimes = section.getStringList("event-times").stream().map(SimpleUtil::getSecondsTime).toList();

        prizeRouletteGuiTitle = SimpleUtil.toColorText(section.getString("prize-roulette-gui.title",""));
        prizeRouletteGuiSize = section.getInt("prize-roulette-gui.size",54);
        prizeRouletteGuiAnimationSlots = section.getIntegerList("prize-roulette-gui.animation-slots");
        decorateButtons = new ArrayList<>();

        ConfigurationSection decorateSection = getOrCreateSection(section, "decorate");

        for(String buttonKey : decorateSection.getKeys(false)){
            decorateButtons.add(loadButton(decorateSection,buttonKey));
        }
        timeFormat = section.getString("placeholder-time-format","%s часов, %s минут, %s секунд");
        winSound = getEnum(section,"prize-roulette-gui.win-sound",Sound.class,Sound.AMBIENT_BASALT_DELTAS_LOOP);
        animationSound = getEnum(section,"prize-roulette-gui.sound",Sound.class,Sound.BLOCK_NOTE_BLOCK_PLING);
        animationTime = section.getInt("prize-roulette-gui.animation-time",7);
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public List<String> getMessage(@NonNull String key){
        List<String> text = SimpleUtil.toColorListText(configFile.getConfig().getStringList("messages." +key));
        if(text.isEmpty() && configFile.getConfig().getBoolean("log-messages")){
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"[PrizeEvent] не найдено сообщение в секции <<messages."+key+">>");
        }
        return text;
    }

    private <T extends Enum<T>> T getEnum(ConfigurationSection section, String key, Class<T> enumClass, T defaultValue) {
        {
            try {
                return Enum.valueOf(enumClass, section.getString(key, "").toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                return defaultValue;
            }
        }
    }

    private <T extends Enum<T>> List<T> getEnumList(ConfigurationSection section, String key, Class<T> enumClass){
        return section.getStringList(key).stream().map(s -> getEnum(section, key, enumClass, null)).filter(Objects::nonNull).toList();
    }

    private ConfigurationSection getOrCreateSection(@NonNull ConfigurationSection s, @NonNull String key){
        ConfigurationSection section = s.getConfigurationSection(key);
        if(section == null || !section.isConfigurationSection(key)) section = s.createSection(key);
        return section;
    }



    private DecorateButton loadButton(@NonNull ConfigurationSection section, @NonNull String key){
        Material material = Material.getMaterial(section.getString(key+".material",""));
        if(material == null) material = Material.BARRIER;
        String displayName = SimpleUtil.toColorText(section.getString(key+".display-name",""));
        int modelData = section.getInt("model-data",-1);
        List<String> lore = SimpleUtil.toColorListText(section.getStringList(key+".lore"));
        List<Integer> slots = section.getIntegerList(key+".slots");
        List<ItemFlag> flags = getEnumList(section, "flags", ItemFlag.class);
        ConfigurationSection enchantSection = getOrCreateSection(section, key+".enchants");
        Map<Enchantment,Integer> enchantments = new HashMap<>();
        for(String enchantKey : enchantSection.getKeys(false)){
            Enchantment enchantment = Enchantment.getByName(enchantKey);
            int level = enchantSection.getInt(enchantKey);
            if(level < 1 || enchantment == null) continue;
            enchantments.put(enchantment,level);
        }
        ItemStack itemStack = new ItemStack(material);
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(itemStack);
        itemStackBuilder.setDisplayName(displayName);
        itemStackBuilder.setLore(lore);
        itemStackBuilder.addItemFlags(flags.toArray(new ItemFlag[0]));
        if(modelData != -1) itemStackBuilder.setModelData(modelData);
        itemStackBuilder.addEnchants(enchantments);
        itemStackBuilder.build();
        return new DecorateButton(itemStack, slots);
    }




}

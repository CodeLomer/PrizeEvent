package com.github.codelomer.prizeEvent.util;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SimpleUtil {
    private SimpleUtil(){}
    
    public static String toColorText(String from) {
        if(from == null) return "";
        Pattern pattern = Pattern.compile("&#[a-fA-F\0-9]{6}");
        Matcher matcher = pattern.matcher(from);
        while (matcher.find()) {
            String hexCode = from.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace("&#", "x");
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch)
                builder.append("&").append(c);
            from = from.replace(hexCode, builder.toString());
            matcher = pattern.matcher(from);
        }
        return ChatColor.translateAlternateColorCodes('&', from);
    }
    public static int getSecondsTime(@NonNull String time) {
        time = time.toLowerCase().trim();

        int multiplier;
        String numberPart = time.replaceAll("[^0-9]", "");
        String unitPart = time.replaceAll("[0-9]", "");

        if (numberPart.isEmpty() || unitPart.isEmpty()) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        int value = Integer.parseInt(numberPart);

        multiplier = switch (unitPart) {
            case "s" -> 1;
            case "m" -> 60;
            case "h" -> 60 * 60;
            case "d" -> 60 * 60 * 24;
            case "w" -> 60 * 60 * 24 * 7;
            case "mouth" -> 60 * 60 * 24 * 30;
            default -> throw new IllegalArgumentException("Unknown time unit: " + unitPart);
        };

        return value * multiplier;
    }


    public static @NotNull String parseTime(long input, String format) {
        if(format == null) return "Invalid format";
        int placeholders = format.split("%s").length - 1;
        if(placeholders == 6){
            return String.format(format, input / 2592000, (input % 2592000) / 604800, (input % 604800) / 86400, (input % 86400) / 3600, (input % 3600) / 60, input % 60);
        }
        if(placeholders == 5){
            return String.format(format, input / 604800, (input % 604800) / 86400, (input % 86400) / 3600, (input % 3600) / 60, input % 60);
        }
        if(placeholders == 4){
            return String.format(format, input / 86400, (input % 86400) / 3600, (input % 3600) / 60, input % 60);
        }
        if(placeholders == 3){
            return String.format(format, input / 3600, (input % 3600) / 60, input % 60);
        }
        if(placeholders == 2){
            return String.format(format, input / 60, input % 60);
        }
        if(placeholders == 1){
            return String.format(format, input);
        }
        return "Invalid format";
    }

    public static List<String> toColorListText(List<String> text) {
        if(text == null) return Collections.emptyList();
        return text.stream().filter(Objects::nonNull).map(SimpleUtil::toColorText).toList();
    }


    public static boolean hasPermission(@NonNull Permissible permissible, @NonNull String permission) {
        return permissible.getEffectivePermissions().stream().anyMatch(permissionAttachmentInfo ->
                permissionAttachmentInfo.getPermission().equalsIgnoreCase(permission));
    }

    public static void sendMessage(@NonNull CommandSender sender, @NonNull List<String> message) {
        message.forEach(sender::sendMessage);
    }
}

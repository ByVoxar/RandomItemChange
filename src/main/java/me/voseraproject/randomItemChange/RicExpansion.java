package me.voseraproject.randomItemChange;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class RicExpansion extends PlaceholderExpansion {

    private final RandomItemChange plugin;

    public RicExpansion(RandomItemChange plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "ByVoxar";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ric";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("situation")) {
            return plugin.isActive() ? plugin.getRawLangMessage("placeholder-active") : plugin.getRawLangMessage("placeholder-inactive");
        }

        if (params.equalsIgnoreCase("duration")) {
            if (!plugin.isActive()) {
                return "0 sn";
            }
            int totalSeconds = plugin.getTimeLeft();
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            if (minutes > 0) {
                return minutes + " dk " + seconds + " sn";
            }
            return seconds + " sn";
        }

        return null;
    }
}
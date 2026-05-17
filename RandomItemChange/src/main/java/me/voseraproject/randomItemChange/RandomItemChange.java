package me.voseraproject.randomItemChange;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public final class RandomItemChange extends JavaPlugin implements Listener {

    private File langFile;
    private FileConfiguration langConfig;

    private boolean isActive = false;
    private int duration;
    private int timeLeft;
    private BukkitTask itemTask;
    private BossBar bossBar;
    private final Random random = new Random();
    private final List<Material> validItems = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadFiles();
        loadValidItems();

        if (getCommand("ric") != null) {
            getCommand("ric").setExecutor(new RicCommand(this));
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RicExpansion(this).register();
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("RandomItemChange eklentisi aktif edildi!");
    }

    @Override
    public void onDisable() {
        stopEvent();
    }

    public void loadFiles() {
        reloadConfig();
        duration = getConfig().getInt("duration", 30);
        File langFolder = new File(getDataFolder(), "languages");
        if (!langFolder.exists()) langFolder.mkdirs();
        String langFileName = getConfig().getString("lang-file", "lang_tr.yml");
        langFile = new File(langFolder, langFileName);
        if (!langFile.exists()) saveResource("languages/" + langFileName, false);
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    private void loadValidItems() {
        validItems.clear();
        for (Material material : Material.values()) {
            if (material.isItem() && !material.isAir() && !material.name().startsWith("LEGACY_")) {
                validItems.add(material);
            }
        }
    }

    public void startEvent() {
        if (isActive) return;
        isActive = true;
        timeLeft = duration;

        String title = getRawLangMessage("bossbar-title").replace("{time}", String.valueOf(timeLeft));
        bossBar = Bukkit.createBossBar(title, BarColor.BLUE, BarStyle.SOLID);
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        itemTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActive) {
                    cancel();
                    return;
                }

                if (timeLeft <= 0) {
                    giveRandomItemToEachPlayer();
                    timeLeft = duration;
                }

                updateBossBar();
                timeLeft--;
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    public void stopEvent() {
        if (!isActive) return;
        isActive = false;
        if (itemTask != null) {
            itemTask.cancel();
            itemTask = null;
        }
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }

    private void updateBossBar() {
        if (bossBar == null) return;
        String title = getRawLangMessage("bossbar-title").replace("{time}", String.valueOf(timeLeft));
        bossBar.setTitle(title);
        double progress = (double) timeLeft / duration;
        if (progress >= 0 && progress <= 1) {
            bossBar.setProgress(progress);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (isActive && bossBar != null) {
            bossBar.addPlayer(event.getPlayer());
        }
    }

    private void giveRandomItemToEachPlayer() {
        if (validItems.isEmpty()) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Material randomMaterial = validItems.get(random.nextInt(validItems.size()));
            ItemStack item = new ItemStack(randomMaterial, 1);
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(item);
            if (!leftOver.isEmpty()) {
                leftOver.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
            }
        }
    }

    public void sendTitle(Player player, String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty()) return;

        String[] parts = rawMessage.split("\\|");
        String title = parts[0];
        String subtitle = parts.length > 1 ? parts[1] : "";

        player.sendTitle(title, subtitle, 10, 40, 10);
    }

    public void sendTitleToAll(String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty()) return;

        String[] parts = rawMessage.split("\\|");
        String title = parts[0];
        String subtitle = parts.length > 1 ? parts[1] : "";

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 50, 10);
        }
    }

    public String getRawLangMessage(String key) {
        String msg = langConfig.getString(key, "");
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public boolean isActive() { return isActive; }
    public int getTimeLeft() { return timeLeft; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) {
        this.duration = duration;
        getConfig().set("duration", duration);
        saveConfig();
        if (isActive) this.timeLeft = duration;
    }
}
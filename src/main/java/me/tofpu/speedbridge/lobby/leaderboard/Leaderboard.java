package me.tofpu.speedbridge.lobby.leaderboard;

import com.google.common.collect.Lists;
import me.tofpu.speedbridge.data.file.config.Config;
import me.tofpu.speedbridge.lobby.leaderboard.data.BoardUser;
import me.tofpu.speedbridge.user.User;
import me.tofpu.speedbridge.user.properties.timer.Timer;
import me.tofpu.speedbridge.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Leaderboard {
    private final List<BoardUser> mainLeaderboard;
    private final List<BoardUser> cacheLeaderboard;

    private BukkitTask update;

    public Leaderboard(final int limitSize) {
        mainLeaderboard = new ArrayList<>(limitSize);
        cacheLeaderboard = new ArrayList<>(limitSize);
    }

    public void initialize(final Plugin plugin) {
        update = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            mainLeaderboard.clear();
            List<BoardUser> users = sortGet();
            mainLeaderboard.addAll(users.subList(0, Math.min(users.size(), 10)));
        }, 20 * 5, 20 * 10);
    }

    public void cancel() {
        update.cancel();
    }

    public void check(final User user) {
        final Player player = Bukkit.getPlayer(user.getUuid());
        if (player == null) return;
        final Timer timer = user.getProperties().getTimer();

        BoardUser boardUser = get(user.getUuid());
        if (boardUser == null) {
            boardUser = new BoardUser(player.getName(), user.getUuid(), timer == null ? null : timer.getResult());
        } else {
            if (boardUser.getScore() == null || boardUser.getScore() > timer.getResult()) {
                boardUser.setScore(timer.getResult());
            }
        }
        add(boardUser);
    }

    public List<BoardUser> sortGet() {
        final List<BoardUser> players = new ArrayList<>(getCacheLeaderboard());
        if (players.isEmpty()) return players;

        int max;
        for (int i = 0; i < players.size(); i++) {
            max = i;
            BoardUser playerMax = players.get(max);
            BoardUser playerJ;

            for (int j = i; j < players.size(); j++) {
                playerJ = players.get(j);
                if (playerJ.getScore() < playerMax.getScore()) {
                    max = j;
                    playerMax = players.get(max);
                }
            }
            //Swap biggest and current locations
            final BoardUser placeholder = players.get(i);
            players.set(i, playerMax);
            if (!placeholder.equals(playerMax)) players.set(max, placeholder);
        }

        return players;
    }

    public BoardUser get(final UUID uuid) {
        for (final BoardUser user : getCacheLeaderboard()) {
            if (user.getUuid().equals(uuid)) return user;
        }
        return null;
    }

    public void add(final BoardUser boardUser) {
        if (boardUser == null || boardUser.getScore() == null) return;
        for (final BoardUser user : getCacheLeaderboard()) {
            if (user.equals(boardUser)) {
                if (user.getScore() > boardUser.getScore()) {
                    user.setScore(boardUser.getScore());
                }
                return;
            }
        }
        cacheLeaderboard.add(boardUser);
    }

    public void addAll(final List<BoardUser> users) {
        for (final BoardUser user : users) {
            add(user);
        }
    }

    public String printLeaderboard() {
        final StringBuilder builder = new StringBuilder();
        builder.append("&eLeaderboard");

        int length = 1;
        String format = Config.get("settings").getConfiguration().getString("leaderboard-format", "%player% &a(%time%)");
        for (final BoardUser user : getMainLeaderboard()) {
            String text = format.replace("%player%", user.getName())
                    .replace("%time%", user.getScore() + "");
            if (Util.isPlaceholderHooked) {
                text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(user.getUuid()), text);
            }
            builder.append("\n").append("&e").append(length).append(". ").append(text);
            length++;
        }

        return Util.colorize(builder.toString());
    }

    public String parseAndGet(final int slot){
        if (getMainLeaderboard().size() <= slot) return "N/A";
        final BoardUser user = getMainLeaderboard().get(slot);
        if (user == null) return "N/A";
        String format = Config.get("settings").getConfiguration().getString("leaderboard-format", "%player% &a(%time%)")
                .replace("%player%", user.getName())
                .replace("%time%", user.getScore() + "");
        if (Util.isPlaceholderHooked) {
            format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(user.getUuid()), format);
        }
        return Util.colorize(format);
    }

    public List<BoardUser> getMainLeaderboard() {
        return Lists.newArrayList(mainLeaderboard);
    }

    public List<BoardUser> getCacheLeaderboard() {
        return Lists.newArrayList(cacheLeaderboard);
    }
}

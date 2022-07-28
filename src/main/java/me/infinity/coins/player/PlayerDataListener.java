package me.infinity.coins.player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import me.infinity.coins.Coins;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class PlayerDataListener implements Listener {

  private final Coins instance;
  private final Map<UUID, PlayerData> cache = new HashMap<>();

  public PlayerDataListener(Coins instance) {
    this.instance = instance;

    instance.getServer().getPluginManager().registerEvents(this, instance);
  }

  @EventHandler
  public void onJoinEvent(PlayerJoinEvent event) {
    final PlayerData data;
    try {
      data = new PlayerData(event.getPlayer().getUniqueId());
      data.load();
    } catch (Exception ex) {
      event.getPlayer().kickPlayer(ChatColor.RED + "Failed to load your data! contact an admin...");
      ex.printStackTrace();
      return;
    }
    cache.put(event.getPlayer().getUniqueId(), data);
  }

  @EventHandler
  public void onLeaveEvent(PlayerQuitEvent event) {
    final UUID uniqueID = event.getPlayer().getUniqueId();
    final PlayerData data = cache.get(uniqueID);
    if (data == null) return;
    data.save();
  }
}

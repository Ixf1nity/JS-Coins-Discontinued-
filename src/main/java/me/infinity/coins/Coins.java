package me.infinity.coins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.infinity.coins.manager.DatabaseManager;
import me.infinity.coins.player.PlayerData;
import me.infinity.coins.player.PlayerDataListener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Coins extends JavaPlugin {

  @Getter
  private static Coins instance;
  private final Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .disableHtmlEscaping()
      .excludeFieldsWithoutExposeAnnotation()
      .create();

  private DatabaseManager databaseManager;

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();

    this.databaseManager = new DatabaseManager(this);
    this.databaseManager.init();

    PlayerData.init();
    new PlayerDataListener(this);
  }

  @Override
  public void onDisable() {
    this.databaseManager.disconnect();
  }
}

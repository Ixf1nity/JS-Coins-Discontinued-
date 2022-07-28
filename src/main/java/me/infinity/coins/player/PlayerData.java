package me.infinity.coins.player;

import com.google.gson.annotations.Expose;
import java.sql.ResultSet;
import java.util.UUID;
import lombok.Data;
import me.infinity.coins.Coins;
import me.infinity.coins.player.data.UserStatistics;

@Data
public class PlayerData {

  private final Coins instance = Coins.getInstance();

  @Expose private final UUID uniqueID;
  @Expose private String statistics;

  public PlayerData(UUID uniqueID) {
    this.uniqueID = uniqueID;
  }

  public UserStatistics userStatistics() {
    return instance.getGson().fromJson(statistics, UserStatistics.class);
  }

  public String toJson() {
    return instance.getGson().toJson(this);
  }

  private boolean exists() {
    return instance
        .getDatabaseManager()
        .getDatabaseConnection()
        .query(
            "SELECT * FROM jscoins_data WHERE uniqueID = ?",
            filler -> filler.setString(1, uniqueID.toString()),
            ResultSet::next);
  }

  public void load() {

    if (exists()) {
      instance
          .getDatabaseManager()
          .getDatabaseConnection()
          .query(
              "SELECT * FROM jscoins_data WHERE uniqueID = ?",
              filler -> filler.setString(1, uniqueID.toString()),
              resultSet -> {
                this.statistics = resultSet.getString("statistics");
                return this;
              });
    } else {
      this.save();
    }
  }

  public void save() {
    instance.getDatabaseManager().getDatabaseExecutor().execute(() -> {
    if (exists()) {
      instance
          .getDatabaseManager()
          .getDatabaseConnection()
          .prepare(
              "UPDATE jscoins_data SET statistics = ? WHERE uniqueID = ?",
              filler -> {
                filler.setString(1, statistics);
                filler.setString(2, uniqueID.toString());
              });
    } else {
      instance
          .getDatabaseManager()
          .getDatabaseConnection()
          .keyInsert(
              "INSERT INTO jscoins_data (uniqueID, statistics) VALUES (?,?)",
              preparedStatement -> {
                preparedStatement.setString(1, uniqueID.toString());
                preparedStatement.setString(2, statistics);
              });
      }
    });
  }

  public PlayerData setStatistics(String jsonString) {
    this.statistics = jsonString;
    return this;
  }

  public static void init() {
    Coins.getInstance().getDatabaseManager().getDatabaseConnection().prepare("CREATE TABLE IF NOT EXISTS jscoins_data ("
        + "uniqueID VARCHAR(32) PRIMARY KEY,"
        + "statistics LONGTEXT)");
  }
}

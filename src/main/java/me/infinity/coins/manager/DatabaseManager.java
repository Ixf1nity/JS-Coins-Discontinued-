package me.infinity.coins.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.simplix.core.database.sql.SqlDatabaseConnection;
import dev.simplix.core.database.sql.handlers.HikariConnectionHandler;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.AccessLevel;
import lombok.Getter;
import me.infinity.coins.Coins;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class DatabaseManager {

  @Getter(AccessLevel.NONE)
  private final Coins instance;

  private SqlDatabaseConnection databaseConnection;
  private final Executor databaseExecutor = Executors.newFixedThreadPool(2);

  private final String host, database, username, password;
  private final boolean enabled, useSSL;
  private final int port;

  public DatabaseManager(Coins instance) {
    this.instance = instance;

    final ConfigurationSection section = instance.getConfig().getConfigurationSection("DATABASE");

    this.enabled = section.getBoolean("ENABLED");
    this.host = section.getString("ADDRESS");
    this.port = section.getInt("PORT");
    this.database = section.getString("DATABASE");
    this.username = section.getString("USERNAME");
    this.password = section.getString("PASSWORD");
    this.useSSL = section.getBoolean("SSL");
  }

  public void init() {
    HikariConfig hikariConfig = new HikariConfig();
    if (enabled) {
      hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
    } else {
      final File database = new File(instance.getDataFolder(), "player-data.db");
      try {
        if (!database.exists()) database.createNewFile();
      } catch (IOException ex) {
        ex.printStackTrace();
        instance.getPluginLoader().disablePlugin(instance);
      }
      hikariConfig.setJdbcUrl("jdbc:sqlite:" + database);
      hikariConfig.setDriverClassName("org.sqlite.JDBC");
    }

    hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
    hikariConfig.addDataSourceProperty("useUnicode", "true");
    hikariConfig.addDataSourceProperty("useSSL", String.valueOf(useSSL));
    hikariConfig.setMaximumPoolSize(10);
    hikariConfig.setUsername(username);
    hikariConfig.setPassword(password);
    hikariConfig.setConnectionTestQuery("SELECT 1;");
    hikariConfig.setPoolName("JSCoins-Pool");

    try {
      databaseConnection =
          new SqlDatabaseConnection(
              new HikariDataSource(hikariConfig), host, username, password, String.valueOf(port), database,
              new HikariConnectionHandler());
    } catch (Exception e) {
      e.printStackTrace();
      instance.getPluginLoader().disablePlugin(instance);
    }
  }

  public void disconnect() {
    try {
      this.databaseConnection.getConnectionHandler().connection().close();
      this.databaseConnection.getDataSource().getConnection().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}

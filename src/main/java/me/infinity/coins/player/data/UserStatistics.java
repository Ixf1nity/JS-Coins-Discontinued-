package me.infinity.coins.player.data;

import com.google.gson.annotations.Expose;
import lombok.Data;
import me.infinity.coins.Coins;

@Data
public class UserStatistics {

  @Expose
  private double coins;

  public String toJson() {
    return Coins.getInstance().getGson().toJson(this);
  }

}

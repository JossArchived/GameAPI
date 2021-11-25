package test;

import cn.nukkit.utils.TextFormat;
import java.util.UUID;
import net.josscoder.gameapi.Game;

public class SkyWars extends Game {

  @Override
  public String getId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String getGameName() {
    return TextFormat.AQUA + "SkyWars: " + TextFormat.LIGHT_PURPLE + "Solo";
  }

  @Override
  public String getInstruction() {
    return "You have to be the last person standing! For this you have to equip yourself with the best of the loot found in the chests!";
  }

  @Override
  public void init() {}

  @Override
  public void close() {}
}

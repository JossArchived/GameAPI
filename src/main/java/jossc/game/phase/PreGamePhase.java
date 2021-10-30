package jossc.game.phase;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;

public class PreGamePhase extends GamePhase {

  private int countdown = 6;

  public PreGamePhase(PluginBase plugin) {
    super(plugin, Duration.ZERO);
  }

  @Override
  protected void onStart() {
    broadcastMessage(TextFormat.GREEN + "The game will begin in 5 seconds");

    spawnPlayers();
  }

  private void spawnPlayers() {}

  @Override
  public void onUpdate() {
    if (countdown > 0) {
      countdown--;

      broadcastSound("note.snare", 1, 2);
      broadcastMessage(
        "The game starting in " +
        countdown +
        " second" +
        (countdown == 1 ? "" : "s")
      );

      return;
    }

    if (countdown == 0) {
      broadcastSound("liquid.lavapop", 2, 2);
      broadcastMessage(TextFormat.GREEN + "The game has begun!");
    }
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() && countdown == 0;
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    event.setTo(event.getFrom());
  }
}

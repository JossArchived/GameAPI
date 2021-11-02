package jossc.game.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.math.Vector3;
import lombok.Getter;

public class PlayerSpawnEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();

  @Getter
  private final Vector3 spawn;

  public PlayerSpawnEvent(Player player, Vector3 spawn) {
    this.player = player;
    this.spawn = spawn;
  }

  public static HandlerList getHandlers() {
    return handlers;
  }
}

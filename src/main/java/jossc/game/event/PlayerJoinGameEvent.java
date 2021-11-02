package jossc.game.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class PlayerJoinGameEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();

  public PlayerJoinGameEvent(Player player) {
    this.player = player;
  }

  public static HandlerList getHandlers() {
    return handlers;
  }
}

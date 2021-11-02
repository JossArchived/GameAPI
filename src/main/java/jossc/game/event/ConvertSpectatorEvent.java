package jossc.game.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.Getter;

public class ConvertSpectatorEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();

  @Getter
  private final boolean hasLost;

  public ConvertSpectatorEvent(Player player, boolean hasLost) {
    this.player = player;
    this.hasLost = hasLost;
  }

  public static HandlerList getHandlers() {
    return handlers;
  }
}
package jossc.game.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.Getter;

public class ConvertPlayerEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();

  @Getter
  private final boolean forcedToEnd;

  public ConvertPlayerEvent(Player player, boolean forcedToEnd) {
    this.player = player;
    this.forcedToEnd = forcedToEnd;
  }

  public static HandlerList getHandlers() {
    return handlers;
  }
}

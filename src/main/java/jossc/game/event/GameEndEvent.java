package jossc.game.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import java.util.Collection;
import lombok.Getter;

public class GameEndEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  @Getter
  private final Collection<? extends Player> players;

  public GameEndEvent(Collection<? extends Player> players) {
    this.players = players;
  }

  public static HandlerList getHandlers() {
    return handlers;
  }
}

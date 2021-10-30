package jossc.game.phase.lobby;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import jossc.game.phase.GamePhase;

public abstract class LobbyPhase extends GamePhase {

  public LobbyPhase(PluginBase plugin) {
    super(plugin);
  }

  public LobbyPhase(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }
}

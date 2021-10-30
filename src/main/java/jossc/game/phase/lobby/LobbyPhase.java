package jossc.game.phase.lobby;

import cn.nukkit.plugin.PluginBase;
import jossc.game.phase.GamePhase;

import java.time.Duration;

public abstract class LobbyPhase extends GamePhase {

  public LobbyPhase(PluginBase plugin) {
    super(plugin);
  }

  public LobbyPhase(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }
}

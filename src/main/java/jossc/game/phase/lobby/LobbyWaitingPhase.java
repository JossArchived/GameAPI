package jossc.game.phase.lobby;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;

public class LobbyWaitingPhase extends LobbyPhase {

  public LobbyWaitingPhase(PluginBase plugin) {
    super(plugin);
  }

  public LobbyWaitingPhase(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }

  @Override
  public void onUpdate() {}
}
